package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.DirectorsReportLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.StatementsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Statements;
import uk.gov.companieshouse.api.accounts.repository.StatementsRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.StatementsTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class StatementsService implements ResourceService<Statements> {

    private StatementsTransformer transformer;
    private StatementsRepository statementsRepository;
    private DirectorsReportServiceImpl directorsReportService;
    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public StatementsService(StatementsTransformer transformer, StatementsRepository statementsRepository, DirectorsReportServiceImpl directorsReportService, KeyIdGenerator keyIdGenerator) {

        this.transformer = transformer;
        this.statementsRepository = statementsRepository;
        this.directorsReportService = directorsReportService;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<Statements> create(Statements rest, Transaction transaction,
                                             String companyAccountId, HttpServletRequest request) throws DataException {

        String statementsId = generateID(companyAccountId);

        setMetadataOnRestObject(rest, transaction, companyAccountId);

        StatementsEntity entity = transformer.transform(rest);

        entity.setId(statementsId);

        try {

            statementsRepository.insert(entity);
        } catch (DuplicateKeyException e) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);

        } catch (MongoException e) {

            throw new DataException(e);
        }

        directorsReportService.addLink(companyAccountId, DirectorsReportLinkType.STATEMENTS, getSelfLink(rest), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<Statements> update(Statements rest, Transaction transaction,
                                             String companyAccountId, HttpServletRequest request) throws DataException {

        String statementsId = generateID(companyAccountId);

        setMetadataOnRestObject(rest, transaction, companyAccountId);

        StatementsEntity entity = transformer.transform(rest);
        entity.setId(statementsId);

        try {

            statementsRepository.save(entity);
        } catch (MongoException e) {

            throw new DataException(e);
        }
        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<Statements> find(String companyAccountsId, HttpServletRequest request)
            throws DataException {

        StatementsEntity entity;

        try {

            entity = statementsRepository.findById(generateID(companyAccountsId)).orElse(null);

        } catch (MongoException e) {

            throw new DataException(e);
        }

        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, transformer.transform(entity));
    }

    @Override
    public ResponseObject<Statements> delete(String companyAccountsId, HttpServletRequest request)
            throws DataException {

        String statementsId = generateID(companyAccountsId);

        try {
            if (statementsRepository.existsById(statementsId)) {

                statementsRepository.deleteById(statementsId);

                directorsReportService
                        .removeLink(companyAccountsId, DirectorsReportLinkType.STATEMENTS, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {

                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    private void setMetadataOnRestObject(Statements rest, Transaction transaction, String companyAccountsId) {

        rest.setLinks(createLinks(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.DIRECTORS_REPORT_STATEMENTS.getValue());
    }

    private String getSelfLink(Statements statements) {

        return statements.getLinks().get(BasicLinkType.SELF.getLink());
    }

    private String generateSelfLink(Transaction transaction, String companyAccountId) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountId + "/"
                + ResourceName.SMALL_FULL.getName() + "/"
                + ResourceName.DIRECTORS_REPORT.getName() + "/"
                + ResourceName.STATEMENTS.getName();
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId));
        return map;
    }

    private String generateID(String companyAccountId) {

        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.STATEMENTS.getName());
    }
}

