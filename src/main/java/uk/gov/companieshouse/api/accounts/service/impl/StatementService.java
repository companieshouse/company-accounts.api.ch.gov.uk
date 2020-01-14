package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingPeriod;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.StatementEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.rest.Statement;
import uk.gov.companieshouse.api.accounts.repository.StatementRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.StatementTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;

@Service
public class StatementService implements ResourceService<Statement> {

    private static final String PERIOD_END_ON_PLACE_HOLDER = "{period_end_on}";
    private static final String LEGAL_STATEMENT_SECTION_477_KEY = "section_477";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
        .ofPattern("d MMMM yyyy");
    private static final String NO_PROFIT_AND_LOSS = "no_profit_and_loss";

    @Autowired
    private StatementTransformer transformer;

    @Autowired
    private StatementRepository statementRepository;

    @Autowired
    private KeyIdGenerator keyIdGenerator;

    @Autowired
    private SmallFullService smallFullService;

    @Autowired
    private StatementsServiceProperties statementsServiceProperties;

    @Autowired
    private ProfitAndLossService profitAndLossService;


    @Override
    public ResponseObject<Statement> create(Statement rest, Transaction transaction,
        String companyAccountId, HttpServletRequest request) throws DataException {

        CompanyAccount companyAccount =
            ((CompanyAccount) request.getAttribute(AttributeName.COMPANY_ACCOUNT.getValue()));

        setMetadataOnRestObject(rest, transaction, companyAccountId,
            getPeriodEndOn(companyAccount));

        StatementEntity statementEntity = transformer.transform(rest);
        statementEntity.setId(generateID(companyAccountId));

        try {
            statementRepository.insert(statementEntity);

        } catch (DuplicateKeyException ex) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);

        } catch (MongoException ex) {

            throw new DataException(ex);
        }

        smallFullService.addLink(companyAccountId,
            SmallFullLinkType.STATEMENTS,
            statementEntity.getData().getLinks().get(BasicLinkType.SELF.getLink()),
            request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<Statement> update(Statement rest, Transaction transaction,
        String companyAccountId, HttpServletRequest request) throws DataException {

        CompanyAccount companyAccount =
            ((CompanyAccount) request.getAttribute(AttributeName.COMPANY_ACCOUNT.getValue()));

        setMetadataOnRestObject(rest, transaction, companyAccountId,
            getPeriodEndOn(companyAccount));

        StatementEntity statementEntity = transformer.transform(rest);
        statementEntity.setId(generateID(companyAccountId));

        try {
            statementRepository.save(statementEntity);

        } catch (MongoException ex) {

            throw new DataException(ex);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<Statement> find(String companyAccountsId, HttpServletRequest request)
        throws DataException {

        StatementEntity statementEntity;

        try {
            statementEntity = statementRepository.findById(generateID(companyAccountsId)).orElse(null);

        } catch (MongoException ex) {

            throw new DataException(ex);
        }

        if (statementEntity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, transformer.transform(statementEntity));
    }

    @Override
    public ResponseObject<Statement> delete(String companyAccountsId, HttpServletRequest request) throws DataException {
        return null;
    }

    private String generateID(String companyAccountId) {
        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.STATEMENTS.getName());
    }

    /**
     * Get the period end on stored in CompanyAccount
     *
     * @param companyAccount
     * @return period end on formatted.
     */
    private LocalDate getPeriodEndOn(CompanyAccount companyAccount) {

        return companyAccount.getNextAccounts().getPeriodEndOn();
    }

    /**
     * Sets the links, the etag, kind and statements(after replacing the placeholder) in the rest
     * object.
     *
     * @param rest
     * @param transaction
     * @param companyAccountId
     * @param periodEndOn
     */
    private void setMetadataOnRestObject(Statement rest,
        Transaction transaction,
        String companyAccountId,
        LocalDate periodEndOn) throws DataException {

        rest.setLinks(createSelfLink(transaction, companyAccountId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.SMALL_FULL_STATEMENT.getValue());
        rest.setLegalStatements(getLegalStatements(periodEndOn, companyAccountId));

    }

    private Map<String, String> createSelfLink(Transaction transaction, String companyAccountId) {
        Map<String, String> selfLink = new HashMap<>();
        selfLink.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountId));

        return selfLink;
    }

    private String generateSelfLink(Transaction transaction, String companyAccountId) {
        return transaction.getLinks().getSelf() + "/"
            + ResourceName.COMPANY_ACCOUNT.getName() + "/"
            + companyAccountId + "/"
            + ResourceName.SMALL_FULL.getName() + "/"
            + ResourceName.STATEMENTS.getName();
    }

    /**
     * Gets the Legal Statements. Replace period_end_on placeholder in the statements for
     * periodEndOn value
     *
     * @return
     */
    private Map<String, String> getLegalStatements(LocalDate periodEndOn, String companyAccountsId)
            throws DataException {
        Map<String, String> legalStatements = getLegalStatementsFromProperties();

        if (legalStatements.containsKey(LEGAL_STATEMENT_SECTION_477_KEY)) {
            updateLegalStatementByReplacingPlaceHolder(
                legalStatements,
                LEGAL_STATEMENT_SECTION_477_KEY,
                PERIOD_END_ON_PLACE_HOLDER,
                convertDateToString(periodEndOn));
        }

            if (profitAndLossService.find(companyAccountsId, AccountingPeriod.CURRENT_PERIOD).getStatus().equals(ResponseStatus.FOUND)) {
                legalStatements.remove(NO_PROFIT_AND_LOSS);
            }

        return legalStatements;
    }

    /**
     * Replace the place holder in the legal statement passed in, with the placeHolderReplacements
     * value.
     *
     * @param statements
     * @param legalStatementKey
     * @param placeHolder
     * @param placeHolderReplacement
     */
    private void updateLegalStatementByReplacingPlaceHolder(Map<String, String> statements,
        String legalStatementKey,
        String placeHolder,
        String placeHolderReplacement) {

        String statementUpdated =
            statements.get(legalStatementKey).replace(placeHolder, placeHolderReplacement);

        statements.replace(legalStatementKey, statementUpdated);
    }

    /**
     * Get the legal statements from LegalStatements.properties file.
     *
     * @return
     */
    private Map<String, String> getLegalStatementsFromProperties() {
        return statementsServiceProperties.getCloneOfStatements();
    }

    /**
     * Converts the date to format d MMMM yyyy. E.g: 1 January 2018.
     *
     * @param date - date to be formatted
     * @return
     */
    private String convertDateToString(LocalDate date) {
        return DATE_TIME_FORMATTER.format(date);
    }
}
