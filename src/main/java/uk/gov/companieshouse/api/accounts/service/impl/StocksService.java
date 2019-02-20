package uk.gov.companieshouse.api.accounts.service.impl;

import org.springframework.dao.DuplicateKeyException;
import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.stocks.StocksEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.stocks.Stocks;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.StocksRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.StocksTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.StocksValidator;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

@Service
public class StocksService implements ResourceService<Stocks> {

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    private StocksRepository repository;
    private StocksTransformer transformer;
    private StocksValidator validator;
    private KeyIdGenerator keyIdGenerator;
    private SmallFullService smallFullService;

    @Autowired
    public StocksService (StocksRepository repository,
                          StocksTransformer transformer,
                          KeyIdGenerator keyIdGenerator,
                          SmallFullService smallFullService,
                          StocksValidator validator) {

        this.repository = repository;
        this.transformer = transformer;
        this.validator = validator;
        this.keyIdGenerator = keyIdGenerator;
        this.smallFullService = smallFullService;
    }

    @Override
    public ResponseObject<Stocks> create(Stocks rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {

        Errors errors = validator.validateStocks(rest, transaction, companyAccountId, request);

        if (errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        setMetadataOnRestObject(rest, transaction, companyAccountId);

        StocksEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {
            repository.insert(entity);
        } catch (DuplicateKeyException e) {
            LOGGER.errorRequest(request, e, getDebugMap(transaction, companyAccountId, entity.getId()));
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {
            DataException dataException = new DataException("Failed to insert "
                    + ResourceName.STOCKS.getName(), e);
            LOGGER.errorRequest(request, dataException, getDebugMap(transaction, companyAccountId, entity.getId()));
            throw dataException;
        }

        smallFullService.addLink(companyAccountId, SmallFullLinkType.STOCKS_NOTE,
                getSelfLinkFromStocksEntity(entity), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<Stocks> update(Stocks rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {

        Errors errors = validator.validateStocks(rest, transaction, companyAccountId, request);

        if (errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        setMetadataOnRestObject(rest, transaction, companyAccountId);

        StocksEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {
            repository.save(entity);
        } catch (MongoException me) {
            DataException dataException =
                    new DataException("Failed to update" + ResourceName.STOCKS.getName(), me);
            LOGGER.errorRequest(request, dataException, getDebugMap(transaction,
                    companyAccountId, entity.getId()));

            throw dataException;
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<Stocks> findById(String id, HttpServletRequest request) throws DataException {

        StocksEntity entity;

        try {
            entity = repository.findById(id).orElse(null);
        } catch (MongoException e) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("id", id);
            DataException dataException = new DataException("Failed to find Stocks", e);
            LOGGER.errorRequest(request, dataException, debugMap);

            throw dataException;
        }

        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, transformer.transform(entity));
    }

    @Override
    public ResponseObject<Stocks> delete(String companyAccountsId, HttpServletRequest request) throws DataException {

        String stocksId = generateID(companyAccountsId);

        try {
            if (repository.existsById(stocksId)) {
                repository.deleteById(stocksId);
                smallFullService
                        .removeLink(companyAccountsId, SmallFullLinkType.STOCKS_NOTE, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {
                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException me) {
            final Map<String, Object> debugMap = new HashMap<>();
            debugMap.put("id", companyAccountsId);
            DataException dataException = new DataException("Failed to delete Stocks", me);
            LOGGER.errorRequest(request, dataException, debugMap);

            throw dataException;
        }
    }

    @Override
    public String generateID(String companyAccountId) {
        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.STOCKS.getName());
    }

    private void setMetadataOnRestObject(Stocks rest, Transaction transaction,
                                         String companyAccountsId) {

        rest.setLinks(createSelfLink(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.STOCKS_NOTE.getValue());
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
                + ResourceName.STOCKS.getName();
    }

    public String getSelfLinkFromStocksEntity(StocksEntity entity) {
        return entity.getData().getLinks().get(BasicLinkType.SELF.getLink());
    }
}
