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
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.debtors.DebtorsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.debtors.Debtors;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.DebtorsRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.DebtorsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.DebtorsValidator;

@Service
public class DebtorsService implements ResourceService<Debtors> {

    private DebtorsRepository repository;
    private DebtorsTransformer transformer;
    private SmallFullService smallFullService;
    private KeyIdGenerator keyIdGenerator;
    private DebtorsValidator debtorsValidator;

    @Autowired
    public DebtorsService (DebtorsRepository repository, DebtorsTransformer transformer,
            SmallFullService smallFullService, KeyIdGenerator keyIdGenerator,
            DebtorsValidator debtorsValidator) {

        this.repository = repository;
        this.transformer = transformer;
        this.smallFullService = smallFullService;
        this.keyIdGenerator = keyIdGenerator;
        this.debtorsValidator = debtorsValidator;
    }

    @Override
    public ResponseObject<Debtors> create (Debtors rest, Transaction transaction,
            String companyAccountsId, HttpServletRequest request) throws DataException {

        Errors errors = debtorsValidator.validateDebtors(rest, transaction, companyAccountsId,
                request);

        if (errors.hasErrors()) {

            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        setMetadataOnRestObject(rest, transaction, companyAccountsId);

        DebtorsEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountsId));

        try {

            repository.insert(entity);
        } catch (DuplicateKeyException e) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        smallFullService.addLink(companyAccountsId, SmallFullLinkType.DEBTORS_NOTE,
                getSelfLinkFromDebtorsEntity(entity), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<Debtors> update (Debtors rest, Transaction transaction,
            String companyAccountsId, HttpServletRequest request) throws DataException {
        setMetadataOnRestObject(rest, transaction, companyAccountsId);

        Errors errors = debtorsValidator.validateDebtors(rest, transaction, companyAccountsId,
                request);

        if (errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        DebtorsEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountsId));

        try {
            repository.save(entity);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<Debtors> find(String companyAccountsId, HttpServletRequest request) throws DataException {
        DebtorsEntity entity;

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
    public ResponseObject<Debtors> delete(String companyAccountsId, HttpServletRequest request) throws DataException {

        String debtorsId = generateID(companyAccountsId);

        try {
            if (repository.existsById(debtorsId)) {
                repository.deleteById(debtorsId);
                smallFullService
                        .removeLink(companyAccountsId, SmallFullLinkType.DEBTORS_NOTE, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {
                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    private String generateID(String companyAccountId) {
        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.DEBTORS.getName());
    }

    private String generateSelfLink (Transaction transaction, String companyAccountId) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountId + "/"
                + ResourceName.SMALL_FULL.getName() + "/notes/" + ResourceName.DEBTORS.getName();
    }

    public String getSelfLinkFromDebtorsEntity (DebtorsEntity entity) {

        return entity.getData().getLinks().get(BasicLinkType.SELF.getLink());
    }

    private Map<String, String> createSelfLink (Transaction transaction, String companyAccountsId) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId));
        return map;
    }

    private void setMetadataOnRestObject (Debtors rest, Transaction transaction,
            String companyAccountsId) {

        rest.setLinks(createSelfLink(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.DEBTORS_NOTE.getValue());
    }
}
