package uk.gov.companieshouse.api.accounts.service.impl;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import com.mongodb.MongoException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
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
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorsafteroneyearentity.CreditorsAfterOneYearEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsafteroneyear.CreditorsAfterOneYear;
import uk.gov.companieshouse.api.accounts.repository.CreditorsAfterOneYearRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.CreditorsAfterOneYearTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class CreditorsAfterOneYearService implements ResourceService<CreditorsAfterOneYear> {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    private CreditorsAfterOneYearRepository repository;
    private CreditorsAfterOneYearTransformer transformer;
    private KeyIdGenerator keyIdGenerator;
    private SmallFullService smallFullService;

    @Autowired
    public CreditorsAfterOneYearService(CreditorsAfterOneYearRepository repository,
            CreditorsAfterOneYearTransformer transformer,
            KeyIdGenerator keyIdGenerator,
            SmallFullService smallFullService) {

        this.repository = repository;
        this.transformer = transformer;
        this.keyIdGenerator = keyIdGenerator;
        this.smallFullService = smallFullService;
    }

    @Override
    public ResponseObject<CreditorsAfterOneYear> create(CreditorsAfterOneYear rest,
            Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {

        setMetadataOnRestObject(rest, transaction, companyAccountId);

        CreditorsAfterOneYearEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {
            repository.insert(entity);
        } catch (DuplicateKeyException e) {
            LOGGER.errorRequest(request, e, getDebugMap(transaction, companyAccountId, entity.getId()));
            return  new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {
            DataException dataException = new DataException("Failed to insert "
                    + ResourceName.CREDITORS_AFTER_ONE_YEAR.getName(), e);
            LOGGER.errorRequest(request, dataException, getDebugMap(transaction, companyAccountId, entity.getId()));
            throw dataException;
        }

        smallFullService.addLink(companyAccountId, SmallFullLinkType.CREDITORS_AFTER_MORE_THAN_ONE_YEAR_NOTE,
                getSelfLinkFromCreditorsAfterOneYearEntity(entity), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<CreditorsAfterOneYear> update(CreditorsAfterOneYear rest,
            Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public ResponseObject<CreditorsAfterOneYear> findById(String id, HttpServletRequest request) throws DataException {
        CreditorsAfterOneYearEntity entity;

        try {
            entity = repository.findById(id).orElse(null);
        } catch (MongoException e) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("id", id);
            DataException dataException = new DataException("Failed to find creditors after one " +
                    "year", e);
            LOGGER.errorRequest(request, dataException, debugMap);

            throw dataException;
        }

        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, transformer.transform(entity));

    }

    @Override
    public ResponseObject<CreditorsAfterOneYear> delete(String companyAccountsId,
            HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public String generateID(String companyAccountId) {
        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.CREDITORS_AFTER_ONE_YEAR.getName());
    }


    private void setMetadataOnRestObject(CreditorsAfterOneYear rest, Transaction transaction,
            String companyAccountsId) {

        rest.setLinks(createSelfLink(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.CREDITORS_AFTER_ONE_YEAR_NOTE.getValue());
    }

    private Map<String, String> createSelfLink(Transaction transaction, String companyAccountsId) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId));
        return map;
    }

    private String generateSelfLink(Transaction transaction, String companyAccountId) {

        return transaction.getLinks().get(TransactionLinkType.SELF.getLink()) + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/"
                + companyAccountId + "/" + ResourceName.SMALL_FULL.getName() + "/notes/"
                + ResourceName.CREDITORS_AFTER_ONE_YEAR.getName();
    }

    public String getSelfLinkFromCreditorsAfterOneYearEntity(CreditorsAfterOneYearEntity entity) {
        return entity.getData().getLinks().get(BasicLinkType.SELF.getLink());
    }
}