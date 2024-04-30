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
import uk.gov.companieshouse.api.accounts.links.LoansToDirectorsLinkType;
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
                                                        Transaction transaction,
                                                        String companyAccountId,
                                                        HttpServletRequest request) throws DataException {
        setMetadataOnRest(rest, transaction, companyAccountId);

        AdditionalInformationEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {
            repository.insert(entity);
        } catch (DuplicateKeyException e) {
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        loansToDirectorsService.addLink(companyAccountId, LoansToDirectorsLinkType.ADDITIONAL_INFO,
                getSelfLinkFromRestEntity(rest), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<AdditionalInformation> update(AdditionalInformation rest,
                                                        Transaction transaction,
                                                        String companyAccountId,
                                                        HttpServletRequest request) throws DataException {
        setMetadataOnRest(rest, transaction, companyAccountId);

        AdditionalInformationEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

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
    public ResponseObject<AdditionalInformation> delete(String companyAccountsId,
                                                        HttpServletRequest request) throws DataException {
        String id = generateID(companyAccountsId);

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

    private String generateSelfLink(Transaction transaction, String companyAccountId) {
        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountId + "/"
                + ResourceName.SMALL_FULL.getName() + "/notes/"
                + ResourceName.LOANS_TO_DIRECTORS.getName() + "/"
                + ResourceName.ADDITIONAL_INFO.getName();
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId) {
        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId));
        return map;
    }

    private void setMetadataOnRest(AdditionalInformation rest, Transaction transaction, String companyAccountsId) {
        rest.setLinks(createLinks(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.LOANS_TO_DIRECTORS_ADDITIONAL_INFO.getValue());
    }

    private String generateID(String companyAccountId) {
        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.LOANS_TO_DIRECTORS.getName() +
                "-" + ResourceName.ADDITIONAL_INFO.getName());
    }

    private String getSelfLinkFromRestEntity(AdditionalInformation rest) {
        return rest.getLinks().get(BasicLinkType.SELF.getLink());
    }
}
