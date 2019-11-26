package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Director;
import uk.gov.companieshouse.api.accounts.repository.DirectorRepository;
import uk.gov.companieshouse.api.accounts.service.DirectorsReportService;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.DirectorTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class DirectorService implements ResourceService<Director> {

    private DirectorTransformer transformer;

    private DirectorRepository repository;

    private DirectorsReportService directorsReportService;

    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public DirectorService(DirectorTransformer transformer, DirectorRepository repository,
                           DirectorsReportService directorsReportService, KeyIdGenerator keyIdGenerator) {

        this.transformer = transformer;
        this.repository = repository;
        this.directorsReportService = directorsReportService;
        this.keyIdGenerator = keyIdGenerator;
    }

    private static final Pattern DIRECTOR_ID_REGEX =
            Pattern.compile("^/transactions/[^/]company-accounts/[^/]small-full/directors-report/directors/(.*)$");

    @Override
    public ResponseObject<Director> create(Director rest, Transaction transaction,
            String companyAccountId, HttpServletRequest request) throws DataException {

        String directorId = keyIdGenerator.generateRandom();

        setMetadataOnRestObject(rest, transaction, companyAccountId, directorId);

        DirectorEntity entity = transformer.transform(rest);

        entity.setId(directorId);

        try {

            repository.insert(entity);
        } catch (DuplicateKeyException e) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        directorsReportService.addDirector(companyAccountId, directorId, getSelfLink(rest), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<Director> update(Director rest, Transaction transaction,
            String companyAccountId, HttpServletRequest request) throws DataException {

        String directorId = getDirectorId(request);

        setMetadataOnRestObject(rest, transaction, companyAccountId, directorId);

        DirectorEntity entity = transformer.transform(rest);
        entity.setId(directorId);

        try {

            repository.save(entity);
        } catch (MongoException e) {

            throw new DataException(e);
        }
        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<Director> find(String companyAccountsId, HttpServletRequest request)
            throws DataException {

        DirectorEntity entity;

        try {

            entity = repository.findById(getDirectorId(request)).orElse(null);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, transformer.transform(entity));
    }

    @Override
    public ResponseObject<Director> delete(String companyAccountsId, HttpServletRequest request)
            throws DataException {

        String directorId = getDirectorId(request);

        try {
            if (repository.existsById(directorId)) {

                repository.deleteById(directorId);

                directorsReportService
                        .removeDirector(companyAccountsId, directorId, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {

                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    private String generateSelfLink(Transaction transaction, String companyAccountId, String directorId) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountId + "/"
                + ResourceName.SMALL_FULL.getName() + "/"
                + ResourceName.DIRECTORS_REPORT.getName() + "/"
                + ResourceName.DIRECTORS.getName() + "/"
                + directorId;
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId, String directorId) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId, directorId));
        return map;
    }

    private void setMetadataOnRestObject(Director rest, Transaction transaction, String companyAccountsId, String directorId) {

        rest.setLinks(createLinks(transaction, companyAccountsId, directorId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.DIRECTORS_REPORT_DIRECTOR.getValue());
    }

    private String getSelfLink(Director director) {

        return director.getLinks().get(BasicLinkType.SELF.getLink());
    }

    private String getDirectorId(HttpServletRequest request) {

        String directorId = null;

        Matcher matcher = DIRECTOR_ID_REGEX.matcher(request.getRequestURI());
        if (matcher.find()) {
            directorId = matcher.group(1);
        }

        return directorId;
    }
}
