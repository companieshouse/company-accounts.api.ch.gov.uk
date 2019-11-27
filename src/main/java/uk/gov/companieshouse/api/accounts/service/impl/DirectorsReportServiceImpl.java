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
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorsReportEntity;
import uk.gov.companieshouse.api.accounts.model.rest.DirectorsReport;
import uk.gov.companieshouse.api.accounts.repository.DirectorsReportRepository;
import uk.gov.companieshouse.api.accounts.service.DirectorsReportService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.DirectorsReportTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class DirectorsReportServiceImpl implements DirectorsReportService {

    private static final Pattern DIRECTORS_REPORT_PATTERN =
            Pattern.compile("^/transactions/.+?/company-accounts/.+?/small-full/directors-report");

    private DirectorsReportRepository directorsReportRepository;
    private DirectorsReportTransformer directorsReportTransformer;
    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public DirectorsReportServiceImpl(
            DirectorsReportRepository directorsReportRepository, DirectorsReportTransformer directorsReportTransformer, KeyIdGenerator keyIdGenerator) {
        this.directorsReportRepository = directorsReportRepository;
        this.directorsReportTransformer = directorsReportTransformer;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<DirectorsReport> create(DirectorsReport rest, Transaction transaction, String companyAccountsId, HttpServletRequest request)
            throws DataException {

        setMetadataOnRestObject(rest, transaction, companyAccountsId);

        DirectorsReportEntity entity = directorsReportTransformer.transform(rest);
        entity.setId(generateID(companyAccountsId));

        try {
            directorsReportRepository.insert(entity);
        } catch (DuplicateKeyException e) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.CREATED, rest);

    }

    @Override
    public ResponseObject<DirectorsReport> update(DirectorsReport rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public ResponseObject<DirectorsReport> find(String companyAccountsId, HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public ResponseObject<DirectorsReport> delete(String companyAccountsId, HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public void addDirector(String companyAccountsID, String directorID, String link, HttpServletRequest request) throws DataException {

    }

    @Override
    public void removeDirector(String companyAccountsID, String directorID, HttpServletRequest request) throws DataException {

    }

    @Override
    public void addSecretary(String companyAccountsID, String directorID, String link, HttpServletRequest request) throws DataException {

    }

    @Override
    public void removeSecretary(String companyAccountsID, String directorID, HttpServletRequest request) throws DataException {

    }

    private String generateSelfLink(Transaction transaction, String companyAccountId) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountId + "/"
                + ResourceName.SMALL_FULL.getName() + "/"
                + ResourceName.DIRECTORS_REPORT.getName();
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId));
        return map;
    }

    private void setMetadataOnRestObject(DirectorsReport rest, Transaction transaction, String companyAccountsId) {

        rest.setLinks(createLinks(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.DIRECTORS_REPORT.getValue());
    }

    private String generateID(String companyAccountId) {

        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.DIRECTORS_REPORT.getName());
    }
}
