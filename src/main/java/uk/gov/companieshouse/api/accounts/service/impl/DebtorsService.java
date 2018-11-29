package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.links.TransactionLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.debtors.DebtorsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.Debtors.Debtors;
import uk.gov.companieshouse.api.accounts.repository.DebtorsRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.DebtorsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class DebtorsService implements ResourceService<Debtors> {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private DebtorsRepository repository;

    private DebtorsTransformer transformer;

    private SmallFullService smallFullService;

    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public DebtorsService(DebtorsRepository repository,
                          DebtorsTransformer transformer,
                          SmallFullService smallFullService,
                          KeyIdGenerator keyIdGenerator) {

        this.repository = repository;
        this.transformer = transformer;
        this.smallFullService = smallFullService;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<Debtors> create(Debtors rest, Transaction transaction,
                                          String companyAccountsId, HttpServletRequest request)
            throws DataException {

        setMetadataOnRestObject(rest, transaction, companyAccountsId);

        DebtorsEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountsId));

        try {

            repository.insert(entity);
        } catch (DuplicateKeyException e) {

            LOGGER.errorRequest(request, e, getDebugMap(transaction, companyAccountsId, entity.getId()));

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {

            DataException dataException = new DataException("Failed to insert " + ResourceName.ACCOUNTING_POLICIES.getName(), e);
            LOGGER.errorRequest(request, dataException, getDebugMap(transaction, companyAccountsId, entity.getId()));

            throw dataException;
        }

        smallFullService
                .addLink(companyAccountsId, SmallFullLinkType.ACCOUNTING_POLICY_NOTE,
                        getSelfLinkFromDebtors(entity), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<Debtors> update(Debtors rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public ResponseObject<Debtors> findById(String id, HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public String generateID(String companyAccountId) {
        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.DEBTORS.getName());
    }

    private String getSelfLink(Transaction transaction, String companyAccountId) {

        return transaction.getLinks().get(TransactionLinkType.SELF.getLink()) + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/"
                + companyAccountId + "/" + ResourceName.SMALL_FULL.getName() + "/"
                + ResourceName.DEBTORS.getName();
    }

    public String getSelfLinkFromDebtors(DebtorsEntity entity) {

        return entity.getData().getLinks().get(BasicLinkType.SELF.getLink());
    }

    private void setLinksOnDebtors(Debtors rest, String link) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), link);
        rest.setLinks(map);
    }

    private void setMetadataOnRestObject(Debtors rest, Transaction transaction, String companyAccountsId) {

        setLinksOnDebtors(rest, getSelfLink(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.DEBTORS_NOTE.getValue());
    }
}
