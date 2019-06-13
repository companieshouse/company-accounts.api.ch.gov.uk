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
import uk.gov.companieshouse.api.accounts.links.CicReportLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportApprovalEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CicApproval;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.CicReportApprovalRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.CicApprovalTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.CicApprovalValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class CicApprovalService implements ResourceService<CicApproval> {

    private CicReportApprovalRepository cicReportApprovalRepository;

    private CicApprovalTransformer cicApprovalTransformer;

    private CicApprovalValidator cicApprovalValidator;

    private CicReportService cicReportService;

    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public CicApprovalService(
        CicReportApprovalRepository cicReportApprovalRepository,
        CicApprovalTransformer cicApprovalTransformer,
        CicApprovalValidator cicApprovalValidator,
        CicReportService cicReportService,
        KeyIdGenerator keyIdGenerator) {
        this.cicReportApprovalRepository = cicReportApprovalRepository;
        this.cicApprovalTransformer = cicApprovalTransformer;
        this.cicApprovalValidator = cicApprovalValidator;
        this.cicReportService = cicReportService;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<CicApproval> create(CicApproval rest, Transaction transaction,
                                              String companyAccountId, HttpServletRequest request) throws DataException {

        Errors errors = cicApprovalValidator.validateCicReportApproval(rest, request);

        if (errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        String selfLink = createSelfLink(transaction, companyAccountId);
        initLinks(rest, selfLink);
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.CIC_APPROVAL.getValue());
        CicReportApprovalEntity cicReportApprovalEntity = cicApprovalTransformer
            .transform(rest);

        String id = generateID(companyAccountId);
        cicReportApprovalEntity.setId(id);

        try {

            cicReportApprovalRepository.insert(cicReportApprovalEntity);
        } catch (DuplicateKeyException dke) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        cicReportService
            .addLink(companyAccountId, CicReportLinkType.APPROVAL, selfLink, request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<CicApproval> update(CicApproval rest, Transaction transaction,
                                              String companyAccountId, HttpServletRequest request) throws DataException {

        Errors errors = cicApprovalValidator.validateCicReportApproval(rest, request);

        if (errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        String selfLink = createSelfLink(transaction, companyAccountId);
        initLinks(rest, selfLink);
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.CIC_APPROVAL.getValue());

        CicReportApprovalEntity cicReportApprovalEntity = cicApprovalTransformer
            .transform(rest);
        cicReportApprovalEntity.setId(generateID(companyAccountId));

        try {
            cicReportApprovalRepository.save(cicReportApprovalEntity);

        } catch (MongoException ex) {

            throw new DataException(ex);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<CicApproval> find(String companyAccountsId,
                                            HttpServletRequest request) throws DataException {

        CicReportApprovalEntity cicReportApprovalEntity;

        try {

            cicReportApprovalEntity = cicReportApprovalRepository
                .findById(generateID(companyAccountsId)).orElse(null);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        if (cicReportApprovalEntity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        CicApproval cicApproval = cicApprovalTransformer
            .transform(cicReportApprovalEntity);
        return new ResponseObject<>(ResponseStatus.FOUND, cicApproval);
    }

    @Override
    public ResponseObject<CicApproval> delete(String companyAccountsId,
                                              HttpServletRequest request) throws DataException {
        String id = generateID(companyAccountsId);

        try {
            if (cicReportApprovalRepository.existsById(id)) {
                cicReportApprovalRepository.deleteById(id);
                cicReportService
                    .removeLink(companyAccountsId, CicReportLinkType.APPROVAL, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {
                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {
            throw new DataException(e);
        }
    }

    private String generateID(String companyAccountId) {
        return keyIdGenerator
            .generate(companyAccountId + "-" + ResourceName.CIC_APPROVAL.getName());
    }

    public String createSelfLink(Transaction transaction, String companyAccountId) {
        return transaction.getLinks().getSelf() + "/"
            + ResourceName.COMPANY_ACCOUNT.getName() + "/"
            + companyAccountId + "/" + ResourceName.CIC_REPORT.getName() + "/"
            + ResourceName.CIC_APPROVAL.getName();
    }

    private void initLinks(CicApproval cicApproval, String link) {
        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), link);
        cicApproval.setLinks(map);
    }
}