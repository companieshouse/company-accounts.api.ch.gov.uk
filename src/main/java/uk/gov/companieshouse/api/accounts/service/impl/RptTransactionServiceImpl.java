package uk.gov.companieshouse.api.accounts.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import com.mongodb.MongoException;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.RptTransactionEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RptTransaction;
import uk.gov.companieshouse.api.accounts.repository.smallfull.RptTransactionRepository;
import uk.gov.companieshouse.api.accounts.service.MultipleResourceService;
import uk.gov.companieshouse.api.accounts.service.RelatedPartyTransactionsService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.RptTransactionTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class RptTransactionServiceImpl implements MultipleResourceService<RptTransaction> {

    private static final Pattern RPT_TRANSACTION_ID_REGEX = Pattern.compile("^/transactions/.+?/company-accounts/.+?/small-full/notes/related-part-transactions/transactions/(.*)$");

    private static final String RPT_TRANSACTION_LINK = "transactions";

    @Autowired
    private RptTransactionTransformer transformer;

    @Autowired
    private RptTransactionRepository repository;

    @Autowired
    private KeyIdGenerator keyIdGenerator;

    @Autowired
    private RelatedPartyTransactionsService relatedPartyTransactionsService;

    @Override
    public ResponseObject<RptTransaction> findAll(Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {

        RptTransactionEntity[] entity;

        try {
            entity = repository.findAllTransactions(generateRptTransactionLink(transaction, companyAccountId));

        } catch (MongoException e) {

            throw new DataException(e);
        }

        if (entity.length == 0) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, transformer.transform(entity));
    }

    @Override
    public ResponseObject<RptTransaction> create(RptTransaction rest, Transaction transaction,
            String companyAccountId, HttpServletRequest request) throws DataException {

        String rptTransactionId = keyIdGenerator.generateRandom();

        setMetadataOnRestObject(rest, transaction, companyAccountId, rptTransactionId);

        RptTransactionEntity entity = transformer.transform(rest);

        entity.setId(rptTransactionId);
        try {

            repository.insert(entity);
        } catch (DuplicateKeyException e) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        relatedPartyTransactionsService.addRptTransaction(companyAccountId, rptTransactionId,
                getSelfLink(rest), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<RptTransaction> update(RptTransaction rest, Transaction transaction,
            String companyAccountId, HttpServletRequest request) throws DataException {

        String rptTransactionId = getRptTransactionId(request);

        setMetadataOnRestObject(rest, transaction, companyAccountId, rptTransactionId);

        RptTransactionEntity entity = transformer.transform(rest);
        entity.setId(rptTransactionId);

        try {

            repository.save(entity);
        } catch (MongoException e) {

            throw new DataException(e);
        }
        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<RptTransaction> find(String companyAccountsId, HttpServletRequest request)
            throws DataException {

        RptTransactionEntity entity;

        try {

            entity = repository.findById(getRptTransactionId(request)).orElse(null);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, transformer.transform(entity));
    }

    @Override
    public ResponseObject<RptTransaction> delete(String companyAccountsId, HttpServletRequest request) throws DataException {

        String rptTransactionId = getRptTransactionId(request);

        try {
            if (repository.existsById(rptTransactionId)) {

                repository.deleteById(rptTransactionId);

                relatedPartyTransactionsService.removeRptTransaction(companyAccountsId, rptTransactionId, request);

                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {

                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    @Override
    public ResponseObject<RptTransaction> deleteAll(Transaction transaction,
            String companyAccountId, HttpServletRequest request) throws DataException {

        try {
            repository.deleteAllTransactions(generateRptTransactionLink(transaction, companyAccountId));

            relatedPartyTransactionsService.removeAllRptTransactions(companyAccountId);

        } catch (MongoException e) {

            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED);
    }

    private String getRptTransactionId(HttpServletRequest request) {

        String rptTransactionId = "";

        Matcher matcher = RPT_TRANSACTION_ID_REGEX.matcher(request.getRequestURI());
        if (matcher.find()) {
            rptTransactionId = matcher.group(1);
        }

        return rptTransactionId;
    }

    private void setMetadataOnRestObject(RptTransaction rest, Transaction transaction, String companyAccountsId, String rptTransactionId) {

        rest.setLinks(createLinks(transaction, companyAccountsId, rptTransactionId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.RPT_TRANSACTIONS.getValue());
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId, String rptTransactionId) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId, rptTransactionId));
        map.put(RPT_TRANSACTION_LINK, generateRptTransactionLink(transaction, companyAccountsId));
        return map;
    }

    private String generateRptTransactionLink(Transaction transaction, String companyAccountsId) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountsId + "/"
                + ResourceName.SMALL_FULL.getName() + "/notes/"
                + ResourceName.RELATED_PARTY_TRANSACTIONS.getName() + "/"
                + ResourceName.RPT_TRANSACTIONS.getName();
    }

    private String getSelfLink(RptTransaction rptTransaction) {

        return rptTransaction.getLinks().get(BasicLinkType.SELF.getLink());
    }

    private String generateSelfLink(Transaction transaction, String companyAccountId, String rptTransactionId) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountId + "/"
                + ResourceName.SMALL_FULL.getName() + "/notes/"
                + ResourceName.RELATED_PARTY_TRANSACTIONS.getName() + "/"
                + ResourceName.RPT_TRANSACTIONS.getName() + "/"
                + rptTransactionId;
    }
}
