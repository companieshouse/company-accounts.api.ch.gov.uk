package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.links.LoansToDirectorsLinkType;
import uk.gov.companieshouse.api.accounts.links.RelatedPartyTransactionsLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoansToDirectorsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.RelatedPartyTransactionsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoansToDirectors;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RelatedPartyTransactions;
import uk.gov.companieshouse.api.accounts.repository.smallfull.LoansToDirectorsRepository;
import uk.gov.companieshouse.api.accounts.repository.smallfull.RelatedPartyTransactionsRepository;
import uk.gov.companieshouse.api.accounts.service.LoansToDirectorsService;
import uk.gov.companieshouse.api.accounts.service.ParentService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.LoansToDirectorsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class LoansToDirectorsServiceImpl implements ParentService<LoansToDirectors, LinkType>,
        LoansToDirectorsService {

    @Autowired
    private LoansToDirectorsTransformer transformer;

    @Autowired
    private LoansToDirectorsRepository repository;

    @Autowired
    private RelatedPartyTransactionsRepository relatedPartyTransactionsRepository;

    @Autowired
    private KeyIdGenerator keyIdGenerator;

    @Autowired
    private SmallFullService smallFullService;

    @Autowired
    private LoanServiceImpl loanService;

    @Autowired
    private LoansToDirectorsAdditionalInformationService loansToDirectorsAdditionalInformationService;

    @Override
    public ResponseObject<LoansToDirectors> create(LoansToDirectors rest, Transaction transaction,
            String companyAccountsId, HttpServletRequest request) throws DataException {

        setMetadataOnRestObject(rest, transaction, companyAccountsId);

        LoansToDirectorsEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountsId, request));

        try {
            repository.insert(entity);
        } catch (DuplicateKeyException e) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        smallFullService.addLink(companyAccountsId, SmallFullLinkType.LOANS_TO_DIRECTORS,
                getSelfLinkFromRestEntity(rest), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<LoansToDirectors> find(String companyAccountsId,
            HttpServletRequest request) throws DataException {

        LoansToDirectorsEntity entity;

        try {
            entity = repository.findById(generateID(companyAccountsId, request)).orElse(null);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }
        return new ResponseObject<>(ResponseStatus.FOUND, transformer.transform(entity));
    }

    @Override
    public ResponseObject<LoansToDirectors> delete(String companyAccountsId,
            HttpServletRequest request) throws DataException {

        String id = generateID(companyAccountsId, request);

        Transaction transaction = (Transaction) request
                .getAttribute(AttributeName.TRANSACTION.getValue());

        loanService.deleteAll(transaction, companyAccountsId, request);
        loansToDirectorsAdditionalInformationService.delete(companyAccountsId, request);

        try {
            if (repository.existsById(id)) {

                repository.deleteById(id);

                smallFullService.removeLink(companyAccountsId, SmallFullLinkType.LOANS_TO_DIRECTORS, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {

                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    @Override
    public void addLink(String id, LinkType linkType, String link, HttpServletRequest request) throws DataException {

        String resourceId = generateID(id, request);

        try {
        if(request.getRequestURI().contains("related-party")) {
            RelatedPartyTransactionsEntity entity =  relatedPartyTransactionsRepository.findById(resourceId)
                    .orElseThrow(() -> new DataException(
                            "Failed to find loans to directors entity to which to add link"));
            entity.getData().getLinks().put(linkType.getLink(), link);
            relatedPartyTransactionsRepository.save(entity);
        } else {
            LoansToDirectorsEntity entity = repository.findById(resourceId)
                    .orElseThrow(() -> new DataException(
                            "Failed to find loans to directors entity to which to add link"));
            entity.getData().getLinks().put(linkType.getLink(), link);

            repository.save(entity);
        }



        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    @Override
    public void removeLink(String id, LinkType linkType, HttpServletRequest request) throws DataException {

        String resourceId = generateID(id, request);
        LoansToDirectorsEntity entity = repository.findById(resourceId)
                .orElseThrow(() -> new DataException(
                        "Failed to find loans to directors entity from which to remove link"));
        entity.getData().getLinks().remove(linkType.getLink());

        try {
            repository.save(entity);

        } catch (MongoException e) {

            throw new DataException(e);
        }
    }
/*
    @Override
    public void addLink(String id, LoansToDirectorsLinkType linkType, String link,
            HttpServletRequest request) throws DataException {

        String resourceId = generateID(id);
        LoansToDirectorsEntity entity = repository.findById(resourceId)
                .orElseThrow(() -> new DataException(
                        "Failed to find loans to directors entity to which to add link"));
        entity.getData().getLinks().put(linkType.getLink(), link);

        try {
            repository.save(entity);

        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    @Override
    public void removeLink(String id, LoansToDirectorsLinkType linkType, HttpServletRequest request)
            throws DataException {

        String resourceId = generateID(id);
        LoansToDirectorsEntity entity = repository.findById(resourceId)
                .orElseThrow(() -> new DataException(
                        "Failed to find loans to directors entity from which to remove link"));
        entity.getData().getLinks().remove(linkType.getLink());

        try {
            repository.save(entity);

        } catch (MongoException e) {

            throw new DataException(e);
        }
    }
*/
    @Override
    public void addLoan(String companyAccountsId, String loanId, String link,
            HttpServletRequest request) throws DataException {

        String resourceId = generateID(companyAccountsId, request);
        LoansToDirectorsEntity entity = repository.findById(resourceId)
                .orElseThrow(() -> new DataException(
                        "Failed to find loans to directors entity to which to add loan"));
        if (entity.getData().getLoans() == null) {

            entity.getData().setLoans(new HashMap<>());
        }
        entity.getData().getLoans().put(loanId, link);

        try {

            repository.save(entity);
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    @Override
    public void removeLoan(String companyAccountsId, String loanId, HttpServletRequest request)
            throws DataException {

        String resourceId = generateID(companyAccountsId, request);
        LoansToDirectorsEntity entity = repository.findById(resourceId)
                .orElseThrow(() -> new DataException(
                        "Failed to find loans to directors entity from which to remove loan"));

        entity.getData().getLoans().remove(loanId);

        try {

            repository.save(entity);
        } catch (MongoException e) {

            throw new DataException(e);
        }

    }

    public void removeAllLoans(String companyAccountsId, HttpServletRequest request)
            throws DataException {

        String resourceId = generateID(companyAccountsId, request);
        LoansToDirectorsEntity entity = repository.findById(resourceId)
                .orElseThrow(() -> new DataException(
                        "Failed to find loans to directors entity from which to remove loan"));

        entity.getData().setLoans(null);

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
                + ResourceName.LOANS_TO_DIRECTORS.getName();
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId) {

        Map<String, String> map = new HashMap<>();
        map.put(LoansToDirectorsLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId));
        return map;
    }

    private void setMetadataOnRestObject(LoansToDirectors rest, Transaction transaction, String companyAccountsId) {

        rest.setLinks(createLinks(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.LOANS_TO_DIRECTORS.getValue());
        rest.setLoans(null);
    }

    private String generateID(String companyAccountId, HttpServletRequest request) {

        return keyIdGenerator.generate(companyAccountId + "-" +
                (request.getRequestURI().contains("related-party") ?
                ResourceName.RELATED_PARTY_TRANSACTIONS.getName() : ResourceName.LOANS_TO_DIRECTORS.getName()));
    }

    private String getSelfLinkFromRestEntity(LoansToDirectors rest) {

        return rest.getLinks().get(LoansToDirectorsLinkType.SELF.getLink());
    }
}
