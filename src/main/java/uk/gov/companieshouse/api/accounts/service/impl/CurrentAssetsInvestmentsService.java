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
import uk.gov.companieshouse.api.accounts.model.entity.notes.currentassetsinvestments.CurrentAssetsInvestmentsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.CurrentAssetsInvestments;
import uk.gov.companieshouse.api.accounts.repository.CurrentAssetsInvestmentsRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.CurrentAssetsInvestmentsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

@Service
public class CurrentAssetsInvestmentsService implements ResourceService<CurrentAssetsInvestments> {

        private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

        private CurrentAssetsInvestmentsRepository repository;
        private CurrentAssetsInvestmentsTransformer transformer;
        private KeyIdGenerator keyIdGenerator;
        private SmallFullService smallFullService;

        @Autowired
        public CurrentAssetsInvestmentsService(CurrentAssetsInvestmentsRepository repository,
                                               CurrentAssetsInvestmentsTransformer transformer,
                                               KeyIdGenerator keyIdGenerator,
                                               SmallFullService smallFullService) {

            this.repository = repository;
            this.transformer = transformer;
            this.keyIdGenerator = keyIdGenerator;
            this.smallFullService = smallFullService;
        }

        @Override
        public ResponseObject<CurrentAssetsInvestments> create(CurrentAssetsInvestments rest,
                                                               Transaction transaction,
                                                               String companyAccountId,
                                                               HttpServletRequest request) throws DataException {

            setMetadataOnRestObject(rest, transaction, companyAccountId);

            CurrentAssetsInvestmentsEntity entity = transformer.transform(rest);
            entity.setId(generateID(companyAccountId));

            try {
                repository.insert(entity);
            } catch (DuplicateKeyException e) {
                LOGGER.errorRequest(request, e, getDebugMap(transaction, companyAccountId,
                    entity.getId()));
                return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
            } catch (MongoException e) {
                DataException dataException = new DataException("Failed to insert "
                    + ResourceName.CURRENT_ASSETS_INVESTMENTS.getName(), e);
                LOGGER.errorRequest(request, dataException, getDebugMap(transaction, companyAccountId
                    , entity.getId()));
                throw dataException;
            }

            smallFullService.addLink(companyAccountId, SmallFullLinkType.CURRENT_ASSETS_INVESTMENTS_NOTE,
                getSelfLinkFromFixedAssetsInvestmentsEntity(entity), request);

            return new ResponseObject<>(ResponseStatus.CREATED, rest);
        }

        @Override
        public ResponseObject<CurrentAssetsInvestments> update(CurrentAssetsInvestments rest,
                                                               Transaction transaction,
                                                               String companyAccountId,
                                                               HttpServletRequest request) throws DataException {

            setMetadataOnRestObject(rest, transaction, companyAccountId);

            CurrentAssetsInvestmentsEntity entity = transformer.transform(rest);
            entity.setId(generateID(companyAccountId));

            try {
                repository.save(entity);
            } catch (MongoException me) {
                DataException dataException = new DataException("Failed to update " +
                    ResourceName.CURRENT_ASSETS_INVESTMENTS.getName(), me);
                LOGGER.errorRequest(request, dataException, getDebugMap(transaction,
                    companyAccountId, entity.getId()));

                throw dataException;
            }

            return new ResponseObject<>(ResponseStatus.UPDATED, rest);
        }

    @Override
    public ResponseObject<CurrentAssetsInvestments> findById(String id, HttpServletRequest request) throws DataException {

        CurrentAssetsInvestmentsEntity entity;

        try {
            entity = repository.findById(id).orElse(null);
        } catch (MongoException e) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("id", id);
            DataException dataException = new DataException("Failed to find " +
                ResourceName.CURRENT_ASSETS_INVESTMENTS.getName(), e);
            LOGGER.errorRequest(request, dataException, debugMap);

            throw dataException;
        }

        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, transformer.transform(entity));
    }

    @Override
    public ResponseObject<CurrentAssetsInvestments> delete(String companyAccountsId, HttpServletRequest request) throws DataException {

        String currentAssetsInvestmentsId = generateID(companyAccountsId);

        try {
            if (repository.existsById(currentAssetsInvestmentsId)) {
                repository.deleteById(currentAssetsInvestmentsId);

                smallFullService.removeLink(companyAccountsId,
                    SmallFullLinkType.CURRENT_ASSETS_INVESTMENTS_NOTE, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {
                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException me) {
            final Map<String, Object> debugMap = new HashMap<>();

            debugMap.put("id", currentAssetsInvestmentsId);
            DataException dataException = new DataException("Failed to delete " +
                ResourceName.CURRENT_ASSETS_INVESTMENTS.getName(), me);

            LOGGER.errorRequest(request, dataException, debugMap);

            throw dataException;
        }
    }

    @Override
        public String generateID(String companyAccountId) {
            return keyIdGenerator.generate(companyAccountId + "-"
                + ResourceName.CURRENT_ASSETS_INVESTMENTS.getName());
        }

        private void setMetadataOnRestObject(CurrentAssetsInvestments rest, Transaction transaction,
                                             String companyAccountsId) {

            rest.setLinks(createSelfLink(transaction, companyAccountsId));
            rest.setEtag(GenerateEtagUtil.generateEtag());
            rest.setKind(Kind.CURRENT_ASSETS_INVESTMENT_NOTE.getValue());
        }

        private Map<String, String> createSelfLink(Transaction transaction, String companyAccountsId) {

            Map<String, String> map = new HashMap<>();
            map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId));
            return map;
        }

        private String generateSelfLink(Transaction transaction, String companyAccountId) {

            return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/"
                + companyAccountId + "/" + ResourceName.SMALL_FULL.getName() + "/notes/"
                + ResourceName.CURRENT_ASSETS_INVESTMENTS.getName();
        }

        public String getSelfLinkFromFixedAssetsInvestmentsEntity(CurrentAssetsInvestmentsEntity entity) {
            return entity.getData().getLinks().get(BasicLinkType.SELF.getLink());
        }
}
