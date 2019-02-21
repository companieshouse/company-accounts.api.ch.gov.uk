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
import uk.gov.companieshouse.api.accounts.model.entity.notes.fixedassetsinvestments.FixedAssetsInvestmentsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.fixedassetsinvestments.FixedAssetsInvestments;
import uk.gov.companieshouse.api.accounts.repository.FixedAssetsInvestmentsRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.FixedAssetsInvestmentsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class FixedAssetsInvestmentsService implements ResourceService<FixedAssetsInvestments> {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    private FixedAssetsInvestmentsRepository repository;
    private FixedAssetsInvestmentsTransformer transformer;
    private KeyIdGenerator keyIdGenerator;
    private SmallFullService smallFullService;

    @Autowired
    public FixedAssetsInvestmentsService(FixedAssetsInvestmentsRepository repository,
            FixedAssetsInvestmentsTransformer transformer,
            KeyIdGenerator keyIdGenerator,
            SmallFullService smallFullService) {

        this.repository = repository;
        this.transformer = transformer;
        this.keyIdGenerator = keyIdGenerator;
        this.smallFullService = smallFullService;
    }

    @Override
    public ResponseObject<FixedAssetsInvestments> create(FixedAssetsInvestments rest,
            Transaction transaction,
            String companyAccountId,
            HttpServletRequest request) throws DataException {

        setMetadataOnRestObject(rest, transaction, companyAccountId);

        FixedAssetsInvestmentsEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {
            repository.insert(entity);
        } catch (DuplicateKeyException e) {
            LOGGER.errorRequest(request, e, getDebugMap(transaction, companyAccountId,
                entity.getId()));
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {
            DataException dataException = new DataException("Failed to insert "
                + ResourceName.FIXED_ASSETS_INVESTMENTS.getName(), e);
            LOGGER.errorRequest(request, dataException, getDebugMap(transaction, companyAccountId
                , entity.getId()));
            throw dataException;
        }

        smallFullService.addLink(companyAccountId, SmallFullLinkType.FIXED_ASSETS_INVESTMENTS_NOTE,
                getSelfLinkFromFixedAssetsInvestmentsEntity(entity), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<FixedAssetsInvestments> update(FixedAssetsInvestments rest,
            Transaction transaction,
            String companyAccountId,
            HttpServletRequest request) throws DataException {

        setMetadataOnRestObject(rest, transaction, companyAccountId);

        FixedAssetsInvestmentsEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {
            repository.save(entity);
        } catch (MongoException me) {
            DataException dataException = new DataException("Failed to update " + 
                ResourceName.FIXED_ASSETS_INVESTMENTS.getName(), me);
            LOGGER.errorRequest(request, dataException, getDebugMap(transaction,
                companyAccountId, entity.getId()));

            throw dataException;
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<FixedAssetsInvestments> findById(String id,
            HttpServletRequest request) throws DataException {

        FixedAssetsInvestmentsEntity entity;

        try {
            entity = repository.findById(id).orElse(null);
        } catch (MongoException e) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("id", id);
            DataException dataException = new DataException("Failed to find " + 
                ResourceName.FIXED_ASSETS_INVESTMENTS.getName(), e);
            LOGGER.errorRequest(request, dataException, debugMap);

            throw dataException;
        }

        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, transformer.transform(entity));
    }

    @Override
    public ResponseObject<FixedAssetsInvestments> delete(String companyAccountsId,
                                                             HttpServletRequest request) throws DataException {

        String fixedAssetsInvestmentsId = generateID(companyAccountsId);

        try {
            if (repository.existsById(fixedAssetsInvestmentsId)) {
                repository.deleteById(fixedAssetsInvestmentsId);

                smallFullService.removeLink(companyAccountsId,
                    SmallFullLinkType.FIXED_ASSETS_INVESTMENTS_NOTE, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {
                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException me) {
            final Map<String, Object> debugMap = new HashMap<>();

            debugMap.put("id", fixedAssetsInvestmentsId);
            DataException dataException = new DataException("Failed to delete " + 
                ResourceName.FIXED_ASSETS_INVESTMENTS.getName(), me);

            LOGGER.errorRequest(request, dataException, debugMap);

            throw dataException;
        }
    }

    @Override
    public String generateID(String companyAccountId) {
        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.FIXED_ASSETS_INVESTMENTS.getName());
    }

    private void setMetadataOnRestObject(FixedAssetsInvestments rest, Transaction transaction,
            String companyAccountsId) {

        rest.setLinks(createSelfLink(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.FIXED_ASSETS_INVESTMENTS_NOTE.getValue());
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
                + ResourceName.FIXED_ASSETS_INVESTMENTS.getName();
    }

    public String getSelfLinkFromFixedAssetsInvestmentsEntity(FixedAssetsInvestmentsEntity entity) {
        return entity.getData().getLinks().get(BasicLinkType.SELF.getLink());
    }
}
