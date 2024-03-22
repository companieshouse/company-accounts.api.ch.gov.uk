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
import uk.gov.companieshouse.api.accounts.links.DirectorsReportLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.SecretaryEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Secretary;
import uk.gov.companieshouse.api.accounts.repository.SecretaryRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.SecretaryTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class SecretaryService implements ResourceService<Secretary> {

    private SecretaryTransformer transformer;
    private SecretaryRepository secretaryRepository;
    private DirectorsReportServiceImpl directorsReportService;
    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public SecretaryService(SecretaryTransformer transformer,
                            SecretaryRepository secretaryRepository,
                            DirectorsReportServiceImpl directorsReportService,
                            KeyIdGenerator keyIdGenerator) {
        this.transformer = transformer;
        this.secretaryRepository = secretaryRepository;
        this.directorsReportService = directorsReportService;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<Secretary> create(Secretary rest,
                                            Transaction transaction,
                                            String companyAccountId,
                                            HttpServletRequest request) throws DataException {
        String secretaryId = generateID(companyAccountId);

        setMetadataOnRestObject(rest, transaction, companyAccountId);

        SecretaryEntity entity = transformer.transform(rest);

        entity.setId(secretaryId);

        try {
            secretaryRepository.insert(entity);
        } catch (DuplicateKeyException e) {
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        directorsReportService.addLink(companyAccountId, DirectorsReportLinkType.SECRETARY, getSelfLink(rest), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<Secretary> update(Secretary rest,
                                            Transaction transaction,
                                            String companyAccountId,
                                            HttpServletRequest request) throws DataException {
        String secretaryId = generateID(companyAccountId);

        setMetadataOnRestObject(rest, transaction, companyAccountId);

        SecretaryEntity entity = transformer.transform(rest);
        entity.setId(secretaryId);

        try {
            secretaryRepository.save(entity);
        } catch (MongoException e) {
            throw new DataException(e);
        }
        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<Secretary> find(String companyAccountsId, HttpServletRequest request) throws DataException {
        SecretaryEntity entity;

        try {
            entity = secretaryRepository.findById(generateID(companyAccountsId)).orElse(null);

        } catch (MongoException e) {
            throw new DataException(e);
        }

        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, transformer.transform(entity));
    }

    @Override
    public ResponseObject<Secretary> delete(String companyAccountsId, HttpServletRequest request) throws DataException {
        String secretaryId = generateID(companyAccountsId);

        try {
            if (secretaryRepository.existsById(secretaryId)) {

                secretaryRepository.deleteById(secretaryId);

                directorsReportService
                        .removeLink(companyAccountsId, DirectorsReportLinkType.SECRETARY, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {
                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {
            throw new DataException(e);
        }
    }

    private void setMetadataOnRestObject(Secretary rest, Transaction transaction, String companyAccountsId) {
        rest.setLinks(createLinks(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.DIRECTORS_REPORT_SECRETARY.getValue());
    }

    private String getSelfLink(Secretary secretary) {
        return secretary.getLinks().get(BasicLinkType.SELF.getLink());
    }

    private String generateSelfLink(Transaction transaction, String companyAccountId) {
        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountId + "/"
                + ResourceName.SMALL_FULL.getName() + "/"
                + ResourceName.DIRECTORS_REPORT.getName() + "/"
                + ResourceName.SECRETARY.getName();
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId) {
        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId));
        return map;
    }

    private String generateID(String companyAccountId) {
        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.SECRETARY.getName());
    }
}