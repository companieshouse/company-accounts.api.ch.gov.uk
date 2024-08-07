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
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.ApprovalEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Approval;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.ApprovalRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.ApprovalTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.ApprovalValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class ApprovalService implements ResourceService<Approval> {

    private ApprovalRepository approvalRepository;

    private ApprovalTransformer approvalTransformer;

    private ApprovalValidator approvalValidator;

    private SmallFullService smallFullService;

    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public ApprovalService(ApprovalRepository approvalRepository,
                           ApprovalTransformer approvalTransformer,
                           ApprovalValidator approvalValidator,
                           SmallFullService smallFullService,
                           KeyIdGenerator keyIdGenerator) {
        this.approvalRepository = approvalRepository;
        this.approvalTransformer = approvalTransformer;
        this.approvalValidator = approvalValidator;
        this.smallFullService = smallFullService;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<Approval> create(Approval rest,
                                           Transaction transaction,
                                           String companyAccountId,
                                           HttpServletRequest request) throws DataException {
        Errors errors = approvalValidator.validateApproval(rest, transaction, companyAccountId, request);
        if (errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        String selfLink = createSelfLink(transaction, companyAccountId);
        initLinks(rest, selfLink);
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.APPROVAL.getValue());
        ApprovalEntity approvalEntity = approvalTransformer.transform(rest);

        String id = generateID(companyAccountId);
        approvalEntity.setId(id);

        try {
            approvalRepository.insert(approvalEntity);
        } catch (DuplicateKeyException dke) {
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        smallFullService.addLink(companyAccountId, SmallFullLinkType.APPROVAL, selfLink, request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<Approval> update(Approval rest,
                                           Transaction transaction,
                                           String companyAccountId,
                                           HttpServletRequest request) throws DataException {
        Errors errors = approvalValidator.validateApproval(rest, transaction, companyAccountId, request);
        if (errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        String selfLink = createSelfLink(transaction, companyAccountId);
        initLinks(rest, selfLink);
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.APPROVAL.getValue());
        ApprovalEntity approvalEntity = approvalTransformer.transform(rest);

        String id = generateID(companyAccountId);
        approvalEntity.setId(id);

        try {
            approvalRepository.save(approvalEntity);
        } catch (MongoException e) {
            throw new DataException(e);
        }
        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
    }

    @Override
    public ResponseObject<Approval> find(String companyAccountsId, HttpServletRequest request) throws DataException {
        ApprovalEntity approvalEntity;

        try {
            approvalEntity =  approvalRepository.findById(generateID(companyAccountsId)).orElse(null);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        if (approvalEntity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        Approval approval = approvalTransformer.transform(approvalEntity);
        return new ResponseObject<>(ResponseStatus.FOUND, approval);
    }

    @Override
    public ResponseObject<Approval> delete(String companyAccountsId, HttpServletRequest request) throws DataException {
        String approvalId = generateID(companyAccountsId);

        try {
            if (approvalRepository.existsById(approvalId)) {
                approvalRepository.deleteById(approvalId);

                smallFullService.removeLink(companyAccountsId, SmallFullLinkType.APPROVAL, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {
                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {
            throw new DataException(e);
        }
    }

    private String generateID(String companyAccountId) {
        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.APPROVAL.getName());
    }

    public String createSelfLink(Transaction transaction, String companyAccountId) {
        return transaction.getLinks().getSelf() + "/"
            + ResourceName.COMPANY_ACCOUNT.getName() + "/"
            + companyAccountId + "/" + ResourceName.SMALL_FULL.getName() + "/"
            + ResourceName.APPROVAL.getName();
    }

    private void initLinks(Approval approval, String link) {
        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), link);
        approval.setLinks(map);
    }
}
