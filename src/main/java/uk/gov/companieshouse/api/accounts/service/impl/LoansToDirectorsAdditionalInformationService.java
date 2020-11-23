package uk.gov.companieshouse.api.accounts.service.impl;

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
import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.links.LoansToDirectorsLinkType;
import uk.gov.companieshouse.api.accounts.links.RelatedPartyTransactionsLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.AdditionalInformationEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.AdditionalInformation;
import uk.gov.companieshouse.api.accounts.repository.smallfull.LoansToDirectorsAdditionalInformationRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.LoansToDirectorsAdditionalInformationTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class LoansToDirectorsAdditionalInformationService implements ResourceService<AdditionalInformation> {

    private static final String RELATED_PARTY = "related-party";

    @Autowired
    private LoansToDirectorsAdditionalInformationTransformer transformer;

    @Autowired
    private LoansToDirectorsAdditionalInformationRepository repository;

    @Autowired
    private LoansToDirectorsServiceImpl loansToDirectorsService;

    @Autowired
    private KeyIdGenerator keyIdGenerator;

    @Override
    public ResponseObject<AdditionalInformation> create(AdditionalInformation rest,
            Transaction transaction, String companyAccountId, HttpServletRequest request)
            throws DataException {

        setMetadataOnRest(rest, transaction, companyAccountId, request);

        AdditionalInformationEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId, request));

        try {
            repository.insert(entity);
        } catch (DuplicateKeyException e) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        if(request.getRequestURI().contains(RELATED_PARTY)) {

            loansToDirectorsService.addLink(companyAccountId, RelatedPartyTransactionsLinkType.ADDITIONAL_INFO,
                    getSelfLinkFromRestEntity(rest), request);
        } else {

            loansToDirectorsService.addLink(companyAccountId, LoansToDirectorsLinkType.ADDITIONAL_INFO,
                    getSelfLinkFromRestEntity(rest), request);
        }
        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<AdditionalInformation> update(AdditionalInformation rest,
            Transaction transaction, String companyAccountId, HttpServletRequest request)
            throws DataException {

        setMetadataOnRest(rest, transaction, companyAccountId, request);

        AdditionalInformationEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId, request));

        try {
            repository.save(entity);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<AdditionalInformation> find(String companyAccountsId,
            HttpServletRequest request) throws DataException {

        AdditionalInformationEntity entity;

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
    public ResponseObject<AdditionalInformation> delete(String companyAccountsId,
            HttpServletRequest request) throws DataException {

        String id = generateID(companyAccountsId, request);

        try {
            if (repository.existsById(id)) {
                repository.deleteById(id);

                loansToDirectorsService.removeLink(companyAccountsId,
                        LoansToDirectorsLinkType.ADDITIONAL_INFO, request);

                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {

                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    private String generateSelfLink(Transaction transaction, String companyAccountId, HttpServletRequest request) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountId + "/"
                + ResourceName.SMALL_FULL.getName() + "/notes/"
                + (request.getRequestURI().contains(RELATED_PARTY) ? ResourceName.RELATED_PARTY_TRANSACTIONS.getName() : ResourceName.LOANS_TO_DIRECTORS.getName()) + "/"
                + ResourceName.ADDITIONAL_INFO.getName();
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId, HttpServletRequest request) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId, request));
        return map;
    }

    private void setMetadataOnRest(AdditionalInformation rest, Transaction transaction,
                                   String companyAccountsId, HttpServletRequest request) {

        rest.setLinks(createLinks(transaction, companyAccountsId, request));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        if (request.getRequestURI().contains(RELATED_PARTY)) {
            rest.setKind(Kind.RELATED_PARTY_TRANSACTIONS_ADDITIONAL_INFO.getValue());
        } else {
            rest.setKind(Kind.LOANS_TO_DIRECTORS_ADDITIONAL_INFO.getValue());
        }
    }

    private String generateID(String companyAccountId, HttpServletRequest request) {

        return request.getRequestURI().contains(RELATED_PARTY) ? keyIdGenerator.generate(companyAccountId + "-" + ResourceName.RELATED_PARTY_TRANSACTIONS.getName() +
                "-" + ResourceName.ADDITIONAL_INFO.getName()) : keyIdGenerator.generate(companyAccountId + "-" + ResourceName.LOANS_TO_DIRECTORS.getName() +
                "-" + ResourceName.ADDITIONAL_INFO.getName());
    }

    private String getSelfLinkFromRestEntity(AdditionalInformation rest) {

        return rest.getLinks().get(BasicLinkType.SELF.getLink());
    }
}
