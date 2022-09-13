package uk.gov.companieshouse.api.accounts.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import com.mongodb.MongoException;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.LoansToDirectorsLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Approval;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Director;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsApproval;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoansToDirectors;
import uk.gov.companieshouse.api.accounts.repository.DirectorRepository;
import uk.gov.companieshouse.api.accounts.service.DirectorsReportService;
import uk.gov.companieshouse.api.accounts.service.MultipleResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.DirectorTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class DirectorService implements MultipleResourceService<Director> {

    private static final Pattern DIRECTOR_ID_REGEX =
                   // Pattern.compile("^/transactions/[0-9-]+/company-accounts/[A-Za-z0-9-_]/small-full/directors-report/directors/(.*)$");
                    //Pattern.compile("^/transactions/[0-9]{6}-[0-9]{6}-[0-9]{6}/company-accounts/[A-Za-z0-9-_]{27}=/small-full/notes/related-party-transactions/transactions/([A-Za-z0-9-_]{27}=)$");
                    //Pattern.compile("^/transactions/.+?/company-accounts/.+?/small-full/directors-report/directors/(.*)$");
                    Pattern.compile("^/transactions/[^/]+/company-accounts/[^/]+/small-full/directors-report/directors/(.*)$");

    private static final String DIRECTORS_LINK = "directors";

    @Autowired
    private DirectorTransformer transformer;

    @Autowired
    private DirectorRepository repository;

    @Autowired
    private DirectorsReportService directorsReportService;

    @Autowired
    private KeyIdGenerator keyIdGenerator;

    @Autowired
    private LoanServiceImpl loanService;

    @Autowired
    private LoansToDirectorsServiceImpl loansToDirectorsService;

    @Autowired
    private DirectorsApprovalService directorsApprovalService;

    @Autowired
    private ApprovalService approvalService;

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

        removeAssociatedApprovals(companyAccountId, request);
        removeAssociatedLoans(transaction, companyAccountId, request);

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

        removeAssociatedApprovals(companyAccountId, request);
        removeAssociatedLoans(transaction, companyAccountId, request);

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
    public ResponseObject<Director> findAll(Transaction transaction, String companyAccountId, HttpServletRequest request)
            throws DataException {

        DirectorEntity[] entity;

        try {
            entity = repository.findAllDirectors(generateDirectorsLink(transaction, companyAccountId));

        } catch (MongoException e) {

            throw new DataException(e);
        }

        if (entity.length == 0) {
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

                Transaction transaction = (Transaction) request
                                .getAttribute(AttributeName.TRANSACTION.getValue());

                removeAssociatedApprovals(companyAccountsId, request);
                removeAssociatedLoans(transaction, companyAccountsId, request);

                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {

                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {

            throw new DataException(e);
        }

    }

    @Override
    public ResponseObject<Director> deleteAll(Transaction transaction, String companyAccountId, HttpServletRequest request)
            throws DataException {

        try {
            repository.deleteAllDirectors(generateDirectorsLink(transaction, companyAccountId));

        } catch (MongoException e) {

            throw new DataException(e);
        }

        removeAssociatedApprovals(companyAccountId, request);
        removeAssociatedLoans(transaction, companyAccountId, request);

        return new ResponseObject<>(ResponseStatus.UPDATED);
    }

    private String generateSelfLink(Transaction transaction, String companyAccountId, String directorId) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountId + "/"
                + ResourceName.SMALL_FULL.getName() + "/"
                + ResourceName.DIRECTORS_REPORT.getName() + "/"
                + ResourceName.DIRECTORS.getName() + "/"
                + directorId;
    }

    private String generateDirectorsLink(Transaction transaction, String companyAccountId) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountId + "/"
                + ResourceName.SMALL_FULL.getName() + "/"
                + ResourceName.DIRECTORS_REPORT.getName() + "/"
                + ResourceName.DIRECTORS.getName();
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId, String directorId) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId, directorId));
        map.put(DIRECTORS_LINK, generateDirectorsLink(transaction, companyAccountsId));
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

        String directorId = "";
        Matcher matcher = DIRECTOR_ID_REGEX.matcher(request.getRequestURI());
        if (matcher.find()) {
            directorId = matcher.group(1);
        }

        return directorId;
    }

    private void removeAssociatedLoans(Transaction transaction, String companyAccountsId,
                    HttpServletRequest request) throws DataException {

        ResponseObject<LoansToDirectors> loansToDirectorsResponse =
                        loansToDirectorsService.find(companyAccountsId, request);

        if (loansToDirectorsResponse.getStatus() == ResponseStatus.FOUND) {
            LoansToDirectors loansToDirectors = loansToDirectorsResponse.getData();
            if (StringUtils.isNotBlank(loansToDirectors.getLinks().get(
                            LoansToDirectorsLinkType.ADDITIONAL_INFO.getLink()))) {
                // additionalInfo exists, just delete loans
                loanService.deleteAll(transaction, companyAccountsId, request);
            } else {
                loansToDirectorsService.delete(companyAccountsId, request);
            }
        }
    }

    private void removeAssociatedApprovals(String companyAccountsId, HttpServletRequest request)
            throws DataException {
        
        ResponseObject<DirectorsApproval> directorsApproval = directorsApprovalService.find(companyAccountsId, request);

        if(directorsApproval.getStatus() == ResponseStatus.FOUND) {
            directorsApprovalService.delete(companyAccountsId, request);
        }

        ResponseObject<Approval> approvalResponse =
                        approvalService.find(companyAccountsId, request);

        if (approvalResponse.getStatus() == ResponseStatus.FOUND) {
            approvalService.delete(companyAccountsId, request);
        }
    }
}
