package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.links.TransactionLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.CreditorsWithinOneYearEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.CreditorsWithinOneYear;
import uk.gov.companieshouse.api.accounts.repository.CreditorsWithinOneYearRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.CreditorsWithinOneYearTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

@Service
public class CreditorsWithinOneYearService implements ResourceService<CreditorsWithinOneYear> {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    private CreditorsWithinOneYearRepository repository;
    private CreditorsWithinOneYearTransformer transformer;
    private KeyIdGenerator keyIdGenerator;
    private SmallFullService smallFullService;

    @Autowired
    public CreditorsWithinOneYearService (CreditorsWithinOneYearRepository repository,
                                          CreditorsWithinOneYearTransformer transformer,
                                          KeyIdGenerator keyIdGenerator,
                                          SmallFullService smallFullService) {

        this.repository = repository;
        this.transformer = transformer;
        this.keyIdGenerator = keyIdGenerator;
        this.smallFullService = smallFullService;
    }

    @Override
    public ResponseObject<CreditorsWithinOneYear> create(CreditorsWithinOneYear rest,
                                                         Transaction transaction,
                                                         String companyAccountId,
                                                         HttpServletRequest request) throws DataException {

        setMetadataOnRestObject(rest, transaction, companyAccountId);

        CreditorsWithinOneYearEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {
            repository.insert(entity);
        } catch (DuplicateKeyException e) {
            LOGGER.errorRequest(request, e, getDebugMap(transaction, companyAccountId, entity.getId()));
            return  new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {
            DataException dataException = new DataException("Failed to insert "
                + ResourceName.CREDITORS_WITHIN_ONE_YEAR.getName());
            LOGGER.errorRequest(request, dataException, getDebugMap(transaction, companyAccountId, entity.getId()));
            throw dataException;
        }

        smallFullService.addLink(companyAccountId, SmallFullLinkType.CREDITORS_WITHIN_ONE_YEAR_NOTE,
            getSelfLinkFromCreditorsWithinOneYearEntity(entity), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<CreditorsWithinOneYear> update(CreditorsWithinOneYear rest,
                                                         Transaction transaction,
                                                         String companyAccountId,
                                                         HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public ResponseObject<CreditorsWithinOneYear> findById(String id,
                                                           HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public ResponseObject<CreditorsWithinOneYear> deleteById(String id,
                                                             HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public String generateID(String companyAccountId) {
        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.CREDITORS_WITHIN_ONE_YEAR.getName());
    }

    private void setMetadataOnRestObject(CreditorsWithinOneYear rest, Transaction transaction,
                                         String companyAccountsId) {

        rest.setLinks(createSelfLink(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.CREDITORS_DUE_WITHIN_ONE_YEAR_NOTE.getValue());
    }

    private Map<String, String> createSelfLink(Transaction transaction, String companyAccountsId) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId));
        return map;
    }

    private String generateSelfLink(Transaction transaction, String companyAccountId) {

        return transaction.getLinks().get(TransactionLinkType.SELF.getLink()) + "/"
            + ResourceName.COMPANY_ACCOUNT.getName() + "/"
            + companyAccountId + "/" + ResourceName.SMALL_FULL.getName() + "/"
            + ResourceName.CREDITORS_WITHIN_ONE_YEAR.getName();
    }

    public String getSelfLinkFromCreditorsWithinOneYearEntity(CreditorsWithinOneYearEntity entity) {
        return entity.getData().getLinks().get(BasicLinkType.SELF.getLink());
    }
}
