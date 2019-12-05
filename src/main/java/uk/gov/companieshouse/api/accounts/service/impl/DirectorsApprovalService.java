package uk.gov.companieshouse.api.accounts.service.impl;

import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.ApprovalEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Approval;
import uk.gov.companieshouse.api.accounts.repository.ApprovalRepository;
import uk.gov.companieshouse.api.accounts.service.DirectorsReportService;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transformer.ApprovalTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class DirectorsApprovalService implements ResourceService<Approval> {

    private ApprovalTransformer transformer;
    private ApprovalRepository<ApprovalEntity> repository;
    private DirectorsReportService directorsReportService;
    private KeyIdGenerator keyIdGenerator;

    public DirectorsApprovalService(ApprovalTransformer transformer, ApprovalRepository<ApprovalEntity> repository, DirectorsReportService directorsReportService, KeyIdGenerator keyIdGenerator) {
        this.transformer = transformer;
        this.repository = repository;
        this.directorsReportService = directorsReportService;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<Approval> create(Approval rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {
        String approvalId = generateID(companyAccountId);

        setMetadataOnRestObject(rest, transaction, companyAccountId, approvalId);

        return null;
    }

    @Override
    public ResponseObject<Approval> update(Approval rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public ResponseObject<Approval> find(String companyAccountsId, HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public ResponseObject<Approval> delete(String companyAccountsId, HttpServletRequest request) throws DataException {
        return null;
    }

    private String generateID(String companyAccountId) {

        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.APPROVAL.getName());
    }

    private void setMetadataOnRestObject(Approval rest, Transaction transaction, String companyAccountsId, String secretaryId) {

        rest.setLinks(createLinks(transaction, companyAccountsId, secretaryId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.DIRECTORS_REPORT_SECRETARY.getValue());
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId, String secretaryId) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId));
        return map;
    }

    private String generateSelfLink(Transaction transaction, String companyAccountId) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountId + "/"
                + ResourceName.SMALL_FULL.getName() + "/"
                + ResourceName.DIRECTORS_REPORT.getName() + "/"
                + ResourceName.SECRETARY.getName();
    }
}
