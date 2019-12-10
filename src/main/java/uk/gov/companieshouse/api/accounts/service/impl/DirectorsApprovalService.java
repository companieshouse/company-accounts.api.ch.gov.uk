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
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorsApprovalEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsApproval;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.DirectorsApprovalRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.DirectorsApprovalTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.DirectorsApprovalValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class DirectorsApprovalService implements ResourceService<DirectorsApproval> {

    private DirectorsApprovalTransformer transformer;
    private DirectorsApprovalRepository directorsApprovalRepository;
    private DirectorsReportServiceImpl directorsReportService;
    private KeyIdGenerator keyIdGenerator;
    private DirectorsApprovalValidator directorsApprovalValidator;


    @Autowired
    public DirectorsApprovalService(DirectorsApprovalTransformer transformer, DirectorsApprovalRepository directorsApprovalRepository, DirectorsReportServiceImpl directorsReportService, KeyIdGenerator keyIdGenerator, DirectorsApprovalValidator directorsApprovalValidator) {
        this.transformer = transformer;
        this.directorsApprovalRepository = directorsApprovalRepository;
        this.directorsReportService = directorsReportService;
        this.keyIdGenerator = keyIdGenerator;
        this.directorsApprovalValidator = directorsApprovalValidator;
    }

    @Override
    public ResponseObject<DirectorsApproval> create(DirectorsApproval rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {

        Errors errors = directorsApprovalValidator.validateApproval(rest, transaction, companyAccountId, request);

        if(errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        String directorApprovalID = generateID(companyAccountId);

        setMetadataOnRestObject(rest, transaction, companyAccountId);

        DirectorsApprovalEntity entity = transformer.transform(rest);

        entity.setId(directorApprovalID);

        try {

            directorsApprovalRepository.insert(entity);
        } catch (DuplicateKeyException dke) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException ex) {

            throw new DataException(ex);
        }

        directorsReportService.addLink(companyAccountId, DirectorsReportLinkType.APPROVAL, getSelfLink(rest), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<DirectorsApproval> update(DirectorsApproval rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {

        Errors errors = directorsApprovalValidator.validateApproval(rest, transaction, companyAccountId, request);

        if(errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }
        String directorApprovalID = generateID(companyAccountId);

        setMetadataOnRestObject(rest, transaction, companyAccountId);

        DirectorsApprovalEntity entity = transformer.transform(rest);
        entity.setId(directorApprovalID);

        try {

            directorsApprovalRepository.save(entity);
        } catch (MongoException e) {

            throw new DataException(e);
        }
        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<DirectorsApproval> find(String companyAccountsId, HttpServletRequest request) throws DataException {

        DirectorsApprovalEntity directorsApprovalEntity;

        try {

            directorsApprovalEntity =  directorsApprovalRepository.findById(generateID(companyAccountsId)).orElse(null);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        if (directorsApprovalEntity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, transformer.transform(directorsApprovalEntity));
    }

    @Override
    public ResponseObject<DirectorsApproval> delete(String companyAccountsId, HttpServletRequest request) throws DataException {

        String directorsApprovalID = generateID(companyAccountsId);

        try {
            if (directorsApprovalRepository.existsById(directorsApprovalID)) {

                directorsApprovalRepository.deleteById(directorsApprovalID);

                directorsReportService
                        .removeLink(companyAccountsId, DirectorsReportLinkType.APPROVAL, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {

                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    private String getSelfLink(DirectorsApproval directorsApproval) {

        return directorsApproval.getLinks().get(BasicLinkType.SELF.getLink());
    }

    private String generateID(String companyAccountId) {

        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.APPROVAL.getName());
    }

    private void setMetadataOnRestObject(DirectorsApproval rest, Transaction transaction, String companyAccountsId) {

        rest.setLinks(createLinks(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.DIRECTORS_REPORT_APPROVAL.getValue());
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId));
        return map;
    }

    private String generateSelfLink(Transaction transaction, String companyAccountId) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountId + "/"
                + ResourceName.SMALL_FULL.getName() + "/"
                + ResourceName.DIRECTORS_REPORT.getName() + "/"
                + ResourceName.APPROVAL.getName();
    }
}
