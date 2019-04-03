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
import uk.gov.companieshouse.api.accounts.model.entity.CIC34ReportEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CIC34Report;
import uk.gov.companieshouse.api.accounts.repository.CIC34ReportRepository;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.CIC34ReportTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class CIC34ReportService implements ResourceService<CIC34Report> {

    private CIC34ReportRepository repository;

    private CIC34ReportTransformer transformer;

    private CompanyAccountService companyAccountService;

    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public CIC34ReportService(CIC34ReportRepository repository,
                              CIC34ReportTransformer transformer,
                              CompanyAccountService companyAccountService,
                              KeyIdGenerator keyIdGenerator) {

        this.repository = repository;
        this.transformer = transformer;
        this.companyAccountService = companyAccountService;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<CIC34Report> create(CIC34Report rest, Transaction transaction,
            String companyAccountId, HttpServletRequest request) throws DataException {

        populateMetadataOnRestObject(rest, transaction, companyAccountId);

        CIC34ReportEntity entity = transformer.transform(rest);
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
    public ResponseObject<CIC34Report> update(CIC34Report rest, Transaction transaction,
            String companyAccountId, HttpServletRequest request) throws DataException {

        populateMetadataOnRestObject(rest, transaction, companyAccountId);

        CIC34ReportEntity entity = transformer.transform(rest);
        entity.setId(generateID(companyAccountId));

        try {
            repository.save(entity);

        } catch (MongoException e) {

            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<CIC34Report> find(String companyAccountsId, HttpServletRequest request)
            throws DataException {

        CIC34ReportEntity entity;

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
    public ResponseObject<CIC34Report> delete(String companyAccountsId, HttpServletRequest request)
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

    private void populateMetadataOnRestObject(CIC34Report cic34Report, Transaction transaction, String companyAccountsId) {

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

    private void initLinks(CIC34Report cic34Report, Transaction transaction, String companyAccountsId) {
        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), createSelfLink(transaction, companyAccountsId));
        cic34Report.setLinks(map);
    }

    private String getSelfLink(CIC34Report cic34Report) {

        return cic34Report.getLinks().get(BasicLinkType.SELF.getLink());
    }
}
