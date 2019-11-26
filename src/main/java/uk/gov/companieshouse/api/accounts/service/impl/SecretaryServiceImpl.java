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
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.SecretaryEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Secretary;
import uk.gov.companieshouse.api.accounts.repository.SecretaryRepository;
import uk.gov.companieshouse.api.accounts.service.DirectorsReportService;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.SecretaryTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SecretaryServiceImpl implements ResourceService<Secretary> {

    private SecretaryTransformer transformer;
    private SecretaryRepository secretaryRepository;
    private DirectorsReportService directorsReportService;
    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public SecretaryServiceImpl(SecretaryTransformer transformer, SecretaryRepository secretaryRepository, DirectorsReportService directorsReportService, KeyIdGenerator keyIdGenerator) {

        this.transformer = transformer;
        this.secretaryRepository = secretaryRepository;
        this.directorsReportService = directorsReportService;
        this.keyIdGenerator = keyIdGenerator;
    }

    private static final Pattern SECRETARY_ID_REGEX = Pattern.compile("^/transactions/[^/]company-accounts/[^/]small-full/directors-report/secretaries/(.*)$");


    @Override
    public ResponseObject<Secretary> create(Secretary rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {

        String secretaryId = keyIdGenerator.generateRandom();

        setMetadataOnRestObject(rest, transaction, companyAccountId, secretaryId);

        SecretaryEntity entity = transformer.transform(rest);

        entity.setId(secretaryId);

        try {

            secretaryRepository.insert(entity);
        } catch (DuplicateKeyException e) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);

        } catch (MongoException e) {

            throw new DataException(e);
        }

        directorsReportService.addSecretary(companyAccountId, secretaryId, getSelfLink(rest), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<Secretary> update(Secretary rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {

        String secretaryId = getSecretaryId(request);

        setMetadataOnRestObject(rest, transaction, companyAccountId, secretaryId);

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

            entity = secretaryRepository.findById(getSecretaryId(request)).orElse(null);

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

        String secretaryId = getSecretaryId(request);

        try {
            if (secretaryRepository.existsById(secretaryId)) {

                secretaryRepository.deleteById(secretaryId);

                directorsReportService
                        .removeSecretary(companyAccountsId, secretaryId, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {

                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    private void setMetadataOnRestObject(Secretary rest, Transaction transaction, String companyAccountsId, String directorId) {

        rest.setLinks(createLinks(transaction, companyAccountsId, directorId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.DIRECTORS_REPORT_SECRETARY.getValue());
    }

    private String getSelfLink(Secretary secretary) {

        return secretary.getLinks().get(BasicLinkType.SELF.getLink());
    }

    private String getSecretaryId(HttpServletRequest request) {

        String secretaryId = null;

        Matcher matcher = SECRETARY_ID_REGEX.matcher(request.getRequestURI());
        if (matcher.find()) {
            secretaryId = matcher.group(1);
        }

        return secretaryId;

}
    private String generateSelfLink(Transaction transaction, String companyAccountId, String directorId) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountId + "/"
                + ResourceName.SMALL_FULL.getName() + "/"
                + ResourceName.DIRECTORS_REPORT.getName() + "/"
                + ResourceName.SECRETARIES.getName() + "/"
                + directorId;
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId, String directorId) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId, directorId));
        return map;
    }
}
