package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.CicReportLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CicStatementsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CicStatements;
import uk.gov.companieshouse.api.accounts.model.rest.ReportStatements;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.CicStatementsRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.CicStatementsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.CicStatementsValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class CicStatementsService implements ResourceService<CicStatements> {

    private CicStatementsRepository repository;

    private CicStatementsTransformer transformer;

    private CicStatementsValidator validator;

    private CicReportService cicReportService;

    private KeyIdGenerator keyIdGenerator;

    private static final String NO_CONSULTATION_WITH_STAKEHOLDERS = "No consultation with stakeholders";
    private static final String NO_DIRECTORS_REMUNERATION = "No remuneration was received";
    private static final String NO_TRANSFER_OF_ASSETS = "No transfer of assets other than for full consideration";

    @Autowired
    public CicStatementsService(CicStatementsRepository repository,
                              CicStatementsTransformer transformer,
                              CicStatementsValidator validator,
                              CicReportService cicReportService,
                              KeyIdGenerator keyIdGenerator) {

        this.repository = repository;
        this.transformer = transformer;
        this.validator = validator;
        this.cicReportService = cicReportService;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<CicStatements> create(CicStatements rest, Transaction transaction,
            String companyAccountId, HttpServletRequest request) throws DataException {

        populateMetadataOnRestObject(rest, transaction, companyAccountId);
        populateDefaultStatementsIfNotProvided(rest);

        CicStatementsEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {
            repository.insert(entity);

        } catch (DuplicateKeyException dke) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        cicReportService.addLink(companyAccountId, CicReportLinkType.STATEMENTS, getSelfLink(rest), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<CicStatements> update(CicStatements rest, Transaction transaction,
            String companyAccountId, HttpServletRequest request) throws DataException {

        Errors errors = validator.validateCicStatementsUpdate(rest);
        if (errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        populateMetadataOnRestObject(rest, transaction, companyAccountId);

        CicStatementsEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {
            repository.save(entity);

        } catch (MongoException e) {

            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<CicStatements> find(String companyAccountsId, HttpServletRequest request)
            throws DataException {

        CicStatementsEntity entity;

        try {
            entity = repository.findById(generateID(companyAccountsId)).orElse(null);

        } catch (MongoException e) {

            throw new DataException(e);
        }

        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, transformer.transform(entity));
    }

    @Override
    public ResponseObject<CicStatements> delete(String companyAccountsId, HttpServletRequest request)
            throws DataException {

        String cicStatementsId = generateID(companyAccountsId);

        try {
            if (repository.existsById(cicStatementsId)) {
                repository.deleteById(cicStatementsId);

                cicReportService.removeLink(companyAccountsId, CicReportLinkType.STATEMENTS, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {
                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    private void populateMetadataOnRestObject(CicStatements cicStatements, Transaction transaction, String companyAccountsId) {

        initLinks(cicStatements, transaction, companyAccountsId);
        cicStatements.setEtag(GenerateEtagUtil.generateEtag());
        cicStatements.setKind(Kind.CIC_STATEMENTS.getValue());
    }

    private void populateDefaultStatementsIfNotProvided(CicStatements cicStatements) {

        ReportStatements reportStatements = cicStatements.getReportStatements();

        if (reportStatements.getConsultationWithStakeholders() == null) {
            reportStatements.setConsultationWithStakeholders(NO_CONSULTATION_WITH_STAKEHOLDERS);
        }
        if (reportStatements.getDirectorsRemuneration() == null) {
            reportStatements.setDirectorsRemuneration(NO_DIRECTORS_REMUNERATION);
        }
        if (reportStatements.getTransferOfAssets() == null) {
            reportStatements.setTransferOfAssets(NO_TRANSFER_OF_ASSETS);
        }
    }

    private String generateID(String companyAccountId) {
        return keyIdGenerator.generate(companyAccountId + "-"
                                        + ResourceName.CIC_STATEMENTS.getName());
    }

    private String createSelfLink(Transaction transaction, String companyAccountId) {
        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/"
                + companyAccountId + "/"
                + ResourceName.CIC_REPORT.getName() + "/"
                + ResourceName.CIC_STATEMENTS.getName();
    }

    private void initLinks(CicStatements cicStatements, Transaction transaction, String companyAccountsId) {
        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), createSelfLink(transaction, companyAccountsId));
        cicStatements.setLinks(map);
    }

    private String getSelfLink(CicStatements cicStatements) {

        return cicStatements.getLinks().get(BasicLinkType.SELF.getLink());
    }
}
