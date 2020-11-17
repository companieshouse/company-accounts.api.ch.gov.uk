package uk.gov.companieshouse.api.accounts.service.impl;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import com.mongodb.MongoException;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.RelatedPartyTransactionsLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.RelatedPartyTransactionsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RelatedPartyTransactions;
import uk.gov.companieshouse.api.accounts.repository.smallfull.RelatedPartyTransactionsRepository;
import uk.gov.companieshouse.api.accounts.service.ParentService;
import uk.gov.companieshouse.api.accounts.service.RelatedPartyTransactionsService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.RelatedPartyTransactionsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class RelatedPartyTransactionsServiceImpl implements ParentService<RelatedPartyTransactions, RelatedPartyTransactionsLinkType>,
        RelatedPartyTransactionsService {

    @Autowired
    private RelatedPartyTransactionsTransformer transformer;

    @Autowired
    private RelatedPartyTransactionsRepository repository;

    @Autowired
    private KeyIdGenerator keyIdGenerator;

    @Autowired
    private SmallFullService smallFullService;

    @Override
    public ResponseObject<RelatedPartyTransactions> create(RelatedPartyTransactions rest, Transaction transaction,
                                                           String companyAccountsId, HttpServletRequest request) throws DataException {

        setMetadataOnRestObject(rest, transaction, companyAccountsId);

        RelatedPartyTransactionsEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountsId));

        try {
            repository.insert(entity);
        } catch (DuplicateKeyException e) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        smallFullService.addLink(companyAccountsId, SmallFullLinkType.RELATED_PARTY_TRANSACTIONS,
                getSelfLinkFromRestEntity(rest), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<RelatedPartyTransactions> find(String companyAccountsId,
            HttpServletRequest request) throws DataException {

        RelatedPartyTransactionsEntity entity;

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
    public ResponseObject<RelatedPartyTransactions> delete(String companyAccountsId,
            HttpServletRequest request) throws DataException {

        String id = generateID(companyAccountsId);

        try {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                smallFullService.removeLink(companyAccountsId, SmallFullLinkType.RELATED_PARTY_TRANSACTIONS, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {
                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    @Override
    public void addLink(String id, RelatedPartyTransactionsLinkType linkType, String link,
            HttpServletRequest request) throws DataException {

        String resourceId = generateID(id);
        RelatedPartyTransactionsEntity entity = repository.findById(resourceId)
                .orElseThrow(() -> new DataException(
                        "Failed to find related party transactions entity to which to add link"));
        entity.getData().getLinks().put(linkType.getLink(), link);

        try {
            repository.save(entity);

        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    @Override
    public void removeLink(String id, RelatedPartyTransactionsLinkType linkType, HttpServletRequest request)
            throws DataException {

        String resourceId = generateID(id);
        RelatedPartyTransactionsEntity entity = repository.findById(resourceId)
                .orElseThrow(() -> new DataException(
                        "Failed to find related party transactions entity from which to remove link"));
        entity.getData().getLinks().remove(linkType.getLink());

        try {
            repository.save(entity);

        } catch (MongoException e) {

            throw new DataException(e);
        }
    }


    @Override
    public void addRptTransaction(String companyAccountsId, String rptTransactionId, String link,
            HttpServletRequest request) throws DataException {

        String resourceId = generateID(companyAccountsId);
        RelatedPartyTransactionsEntity entity = repository.findById(resourceId)
                .orElseThrow(() -> new DataException(
                        "Failed to find related party transactions entity to which to add transaction"));
        if (entity.getData().getTransactions() == null) {
            entity.getData().setTransactions(new HashMap<>());
        }
        entity.getData().getTransactions().put(rptTransactionId, link);

        try {

            repository.save(entity);
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    @Override
    public void removeRptTransaction(String companyAccountsId, String rptTransactionId, HttpServletRequest request)
            throws DataException {

        String resourceId = generateID(companyAccountsId);
        RelatedPartyTransactionsEntity entity = repository.findById(resourceId)
                .orElseThrow(() -> new DataException(
                        "Failed to find related party transactions entity from which to remove transaction"));

        entity.getData().getTransactions().remove(rptTransactionId);

        try {

            repository.save(entity);
        } catch (MongoException e) {

            throw new DataException(e);
        }

    }

    public void removeAllRptTransactions(String companyAccountsId)
            throws DataException {

        String resourceId = generateID(companyAccountsId);
        RelatedPartyTransactionsEntity entity = repository.findById(resourceId)
                .orElseThrow(() -> new DataException(
                        "Failed to find related party transactions entity from which to remove transactions"));

        entity.getData().setTransactions(null);

        try {
            repository.save(entity);
        } catch (MongoException e) {

            throw new DataException(e);
        }

    }
    
    private String generateSelfLink(Transaction transaction, String companyAccountId) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountId + "/"
                + ResourceName.SMALL_FULL.getName() + "/notes/"
                + ResourceName.RELATED_PARTY_TRANSACTIONS.getName();
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId) {

        Map<String, String> map = new HashMap<>();
        map.put(RelatedPartyTransactionsLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId));
        return map;
    }

    private void setMetadataOnRestObject(RelatedPartyTransactions rest, Transaction transaction, String companyAccountsId) {

        rest.setLinks(createLinks(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.RELATED_PARTY_TRANSACTIONS.getValue());
    }

    private String generateID(String companyAccountId) {

        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName());
    }

    private String getSelfLinkFromRestEntity(RelatedPartyTransactions rest) {

        return rest.getLinks().get(RelatedPartyTransactionsLinkType.SELF.getLink());
    }
}
