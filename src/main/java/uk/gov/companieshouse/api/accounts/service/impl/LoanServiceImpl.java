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
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoanEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.Loan;
import uk.gov.companieshouse.api.accounts.repository.smallfull.LoanRepository;
import uk.gov.companieshouse.api.accounts.service.LoansToDirectorsService;
import uk.gov.companieshouse.api.accounts.service.MultipleResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.LoanTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class LoanServiceImpl implements MultipleResourceService<Loan> {

    private LoanTransformer transformer;

    private LoanRepository repository;

    private LoansToDirectorsService loansToDirectorsService;

    private KeyIdGenerator keyIdGenerator;

    private static final Pattern LOAN_ID_REGEX = Pattern.compile("^/transactions/.+?/company-accounts/.+?/small-full/loans-to-directors/notes/loans/(.*)$");

    private static final String LOANS_LINK = "loans";


    @Autowired
    public LoanServiceImpl(LoanTransformer transformer, LoanRepository repository, LoansToDirectorsService loansToDirectorsService, KeyIdGenerator keyIdGenerator) {

        this.transformer = transformer;
        this.repository = repository;
        this.loansToDirectorsService = loansToDirectorsService;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<Loan> findAll(Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {

        LoanEntity[] entity;

        try {
            entity = repository.findAllLoans(generateLoansLink(transaction, companyAccountId));

        } catch (MongoException e) {

            throw new DataException(e);
        }

        if (entity.length == 0) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, transformer.transform(entity));
    }

    @Override
    public ResponseObject<Loan> deleteAll(Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {

        try {
            repository.deleteAllLoans(generateLoansLink(transaction, companyAccountId));

        } catch (MongoException e) {

            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED);
    }

    @Override
    public ResponseObject<Loan> create(Loan rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {

        String loanId = keyIdGenerator.generateRandom();

        setMetadataOnRestObject(rest, transaction, companyAccountId, loanId);

        LoanEntity entity = transformer.transform(rest);

        entity.setId(loanId);
        try {

            repository.insert(entity);
        } catch (DuplicateKeyException e) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        loansToDirectorsService.addLoan(companyAccountId, loanId, getSelfLink(rest), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<Loan> update(Loan rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {

        String loanId = getLoanId(request);

        setMetadataOnRestObject(rest, transaction, companyAccountId, loanId);

        LoanEntity entity = transformer.transform(rest);
        entity.setId(loanId);

        try {

            repository.save(entity);
        } catch (MongoException e) {

            throw new DataException(e);
        }
        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<Loan> find(String companyAccountsId, HttpServletRequest request) throws DataException {

        LoanEntity entity;

        try {

            entity = repository.findById(getLoanId(request)).orElse(null);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, transformer.transform(entity));
    }

    @Override
    public ResponseObject<Loan> delete(String companyAccountsId, HttpServletRequest request) throws DataException {

        String loanId = getLoanId(request);

        try {
            if (repository.existsById(loanId)) {

                repository.deleteById(loanId);

                loansToDirectorsService
                        .removeLoan(companyAccountsId, loanId, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {

                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    private String getLoanId(HttpServletRequest request) {

        String loanId = null;

        Matcher matcher = LOAN_ID_REGEX.matcher(request.getRequestURI());
        if (matcher.find()) {
            loanId = matcher.group(1);
        }

        return loanId;
    }

    private void setMetadataOnRestObject(Loan rest, Transaction transaction, String companyAccountsId, String loanId) {

        rest.setLinks(createLinks(transaction, companyAccountsId, loanId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.LOANS_TO_DIRECTORS_LOANS.getValue());
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId, String loanId) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId, loanId));
        map.put(LOANS_LINK, generateLoansLink(transaction, companyAccountsId));
        return map;
    }

    private String generateLoansLink(Transaction transaction, String companyAccountsId) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountsId + "/"
                + ResourceName.SMALL_FULL.getName() + "/"
                + ResourceName.LOANS_TO_DIRECTORS.getName() + "/notes/"
                + ResourceName.LOANS.getName();
    }

    private String getSelfLink(Loan loan) {

        return loan.getLinks().get(BasicLinkType.SELF.getLink());
    }

    private String generateSelfLink(Transaction transaction, String companyAccountId, String loanId) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountId + "/"
                + ResourceName.SMALL_FULL.getName() + "/"
                + ResourceName.LOANS_TO_DIRECTORS.getName() + "/notes/"
                + ResourceName.LOANS.getName() + "/"
                + loanId;
    }
}
