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
import uk.gov.companieshouse.api.accounts.model.rest.CicReportApproval;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.CicReportApprovalRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.CicReportApprovalTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.CicReportApprovalValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class CicReportApprovalService implements ResourceService<CicReportApproval> {

    private CicReportApprovalRepository cicReportApprovalRepository;

    private CicReportApprovalTransformer cicReportApprovalTransformer;

    private CicReportApprovalValidator cicReportApprovalValidator;

    private CicReportService cicReportService;

    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public CicReportApprovalService(
        CicReportApprovalRepository cicReportApprovalRepository,
        CicReportApprovalTransformer cicReportApprovalTransformer,
        CicReportApprovalValidator cicReportApprovalValidator,
        CicReportService cicReportService,
        KeyIdGenerator keyIdGenerator) {
        this.cicReportApprovalRepository = cicReportApprovalRepository;
        this.cicReportApprovalTransformer = cicReportApprovalTransformer;
        this.cicReportApprovalValidator = cicReportApprovalValidator;
        this.cicReportService = cicReportService;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<CicReportApproval> create(CicReportApproval rest, Transaction transaction,
        String companyAccountId, HttpServletRequest request) throws DataException {

        Errors errors = cicReportApprovalValidator.validateCicReportApproval(rest, request);

        if (errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        String selfLink = createSelfLink(transaction, companyAccountId);
        initLinks(rest, selfLink);
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.APPROVAL.getValue());
        CicReportApprovalEntity cicReportApprovalEntity = cicReportApprovalTransformer
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
    public ResponseObject<CicReportApproval> update(CicReportApproval rest, Transaction transaction,
        String companyAccountId, HttpServletRequest request) throws DataException {
        //TODO implement method
        return null;
    }

    @Override
    public ResponseObject<CicReportApproval> find(String companyAccountsId,
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

        CicReportApproval cicReportApproval = cicReportApprovalTransformer
            .transform(cicReportApprovalEntity);
        return new ResponseObject<>(ResponseStatus.FOUND, cicReportApproval);
    }

    @Override
    public ResponseObject<CicReportApproval> delete(String companyAccountsId,
        HttpServletRequest request) throws DataException {
        return null;
    }

    private String generateID(String companyAccountId) {
        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.CIC_APPROVAL.getName());
    }

    public String createSelfLink(Transaction transaction, String companyAccountId) {
        return transaction.getLinks().getSelf() + "/"
            + ResourceName.COMPANY_ACCOUNT.getName() + "/"
            + companyAccountId + "/" + ResourceName.CIC_REPORT.getName() + "/"
            + ResourceName.CIC_APPROVAL.getName();
    }

    private void initLinks(CicReportApproval cicReportApproval, String link) {
        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), link);
        cicReportApproval.setLinks(map);
    }
}
