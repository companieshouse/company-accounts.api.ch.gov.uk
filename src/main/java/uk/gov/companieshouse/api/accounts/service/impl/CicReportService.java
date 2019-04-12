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
import uk.gov.companieshouse.api.accounts.links.CicReportLinkType;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CicReport;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.CicReportRepository;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.ParentService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.CicReportTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.CicReportValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class CicReportService implements ParentService<CicReport, CicReportLinkType> {

    private CicReportRepository repository;

    private CicReportTransformer transformer;

    private CicReportValidator validator;

    private CompanyAccountService companyAccountService;

    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public CicReportService(CicReportRepository repository,
                            CicReportTransformer transformer,
                            CicReportValidator validator,
                            CompanyAccountService companyAccountService,
                            KeyIdGenerator keyIdGenerator) {

        this.repository = repository;
        this.transformer = transformer;
        this.validator = validator;
        this.companyAccountService = companyAccountService;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<CicReport> create(CicReport rest, Transaction transaction,
            String companyAccountId, HttpServletRequest request) throws DataException {

        Errors errors = validator.validateCicReportCreation(transaction);
        if (errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        setMetadataOnRestObject(rest, transaction, companyAccountId);

        CicReportEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {
            repository.insert(entity);

        } catch (DuplicateKeyException e) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        companyAccountService
                .addLink(companyAccountId, CompanyAccountLinkType.CIC_REPORT, getSelfLink(rest));

        return new ResponseObject<>(ResponseStatus.CREATED, rest);

    }

    @Override
    public ResponseObject<CicReport> find(String companyAccountsId, HttpServletRequest request)
            throws DataException {

        String cicReportId = generateID(companyAccountsId);

        CicReportEntity entity;

        try {
            entity = repository.findById(cicReportId).orElse(null);

        } catch (MongoException e) {

            throw new DataException(e);
        }

        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, transformer.transform(entity));
    }

    @Override
    public ResponseObject<CicReport> delete(String companyAccountsId, HttpServletRequest request)
            throws DataException {

        String cicReportId = generateID(companyAccountsId);

        try {
            if (repository.existsById(cicReportId)) {

                repository.deleteById(cicReportId);
                companyAccountService
                        .removeLink(companyAccountsId, CompanyAccountLinkType.CIC_REPORT);

                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else  {

                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    @Override
    public void addLink(String id, CicReportLinkType linkType, String link,
            HttpServletRequest request) throws DataException {

        String cicReportId = generateID(id);

        try {
            CicReportEntity entity =
                    repository.findById(cicReportId).orElseThrow(() ->
                            new DataException("Failed to retrieve a cic report entity for company accounts id: " +
                                    id + " to which to add link: " + linkType.getLink()));

            entity.getData().getLinks().put(linkType.getLink(), link);

            repository.save(entity);

        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    @Override
    public void removeLink(String id, CicReportLinkType linkType, HttpServletRequest request)
            throws DataException {

        String cicReportId = generateID(id);

        try {
            CicReportEntity entity =
                    repository.findById(cicReportId).orElseThrow(() ->
                            new DataException("Failed to retrieve a cic report entity for company accounts id: " +
                                    id + " from which to remove link: " + linkType.getLink()));

            entity.getData().getLinks().remove(linkType.getLink());

            repository.save(entity);

        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    private void setMetadataOnRestObject(CicReport rest,
                                         Transaction transaction,
                                         String companyAccountsId) {

        rest.setLinks(createLinks(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.CIC_REPORT.getValue());
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId) {

        Map<String, String> map = new HashMap<>();
        map.put(CicReportLinkType.SELF.getLink(), createSelfLink(transaction, companyAccountsId));
        return map;
    }

    private String createSelfLink(Transaction transaction, String companyAccountsId) {

        return getTransactionSelfLink(transaction) + "/" + ResourceName.COMPANY_ACCOUNT.getName()
                + "/" + companyAccountsId + "/" + ResourceName.CIC_REPORT.getName();
    }

    private String getTransactionSelfLink(Transaction transaction) {

        return transaction.getLinks().getSelf();
    }

    private String getSelfLink(CicReport cicReport) {

        return cicReport.getLinks().get(CicReportLinkType.SELF.getLink());
    }

    private String generateID(String companyAccountsId) {

        return keyIdGenerator.generate(companyAccountsId + "-" + ResourceName.CIC_REPORT.getName());
    }
}
