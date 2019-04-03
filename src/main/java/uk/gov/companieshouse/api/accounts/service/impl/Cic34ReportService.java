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
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.Cic34ReportEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Cic34Report;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.Cic34ReportRepository;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.Cic34ReportTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.Cic34ReportValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class Cic34ReportService implements ResourceService<Cic34Report> {

    private Cic34ReportRepository repository;

    private Cic34ReportTransformer transformer;

    private Cic34ReportValidator validator;

    private CompanyAccountService companyAccountService;

    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public Cic34ReportService(Cic34ReportRepository repository,
                              Cic34ReportTransformer transformer,
                              Cic34ReportValidator validator,
                              CompanyAccountService companyAccountService,
                              KeyIdGenerator keyIdGenerator) {

        this.repository = repository;
        this.transformer = transformer;
        this.validator = validator;
        this.companyAccountService = companyAccountService;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<Cic34Report> create(Cic34Report rest, Transaction transaction,
            String companyAccountId, HttpServletRequest request) throws DataException {

        Errors errors = validator.validateCIC34ReportSubmission(transaction);
        if (errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        populateMetadataOnRestObject(rest, transaction, companyAccountId);

        Cic34ReportEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {
            repository.insert(entity);

        } catch (DuplicateKeyException dke) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        companyAccountService.addLink(companyAccountId, CompanyAccountLinkType.CIC34_REPORT, getSelfLink(rest));

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<Cic34Report> update(Cic34Report rest, Transaction transaction,
            String companyAccountId, HttpServletRequest request) throws DataException {

        populateMetadataOnRestObject(rest, transaction, companyAccountId);

        Cic34ReportEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {
            repository.save(entity);

        } catch (MongoException e) {

            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<Cic34Report> find(String companyAccountsId, HttpServletRequest request)
            throws DataException {

        Cic34ReportEntity entity;

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
    public ResponseObject<Cic34Report> delete(String companyAccountsId, HttpServletRequest request)
            throws DataException {

        String cic34ReportId = generateID(companyAccountsId);

        try {
            if (repository.existsById(cic34ReportId)) {
                repository.deleteById(cic34ReportId);

                companyAccountService.removeLink(companyAccountsId, CompanyAccountLinkType.CIC34_REPORT);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {
                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    private void populateMetadataOnRestObject(Cic34Report cic34Report, Transaction transaction, String companyAccountsId) {

        initLinks(cic34Report, transaction, companyAccountsId);
        cic34Report.setEtag(GenerateEtagUtil.generateEtag());
        cic34Report.setKind(Kind.CIC34_REPORT.getValue());
    }

    private String generateID(String companyAccountId) {
        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.CIC34_REPORT.getName());
    }

    private String createSelfLink(Transaction transaction, String companyAccountId) {
        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/"
                + companyAccountId + "/"
                + ResourceName.CIC34_REPORT.getName();
    }

    private void initLinks(Cic34Report cic34Report, Transaction transaction, String companyAccountsId) {
        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), createSelfLink(transaction, companyAccountsId));
        cic34Report.setLinks(map);
    }

    private String getSelfLink(Cic34Report cic34Report) {

        return cic34Report.getLinks().get(BasicLinkType.SELF.getLink());
    }
}
