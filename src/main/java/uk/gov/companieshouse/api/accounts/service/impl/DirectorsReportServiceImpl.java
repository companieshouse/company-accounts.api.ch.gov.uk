package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.DirectorsReportLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorsReportEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsReport;
import uk.gov.companieshouse.api.accounts.repository.DirectorsReportRepository;
import uk.gov.companieshouse.api.accounts.service.DirectorsReportService;
import uk.gov.companieshouse.api.accounts.service.ParentService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.DirectorsReportTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class DirectorsReportServiceImpl implements ParentService<DirectorsReport, DirectorsReportLinkType>, DirectorsReportService {

    @Autowired
    private DirectorsReportRepository directorsReportRepository;

    @Autowired
    private DirectorsReportTransformer directorsReportTransformer;

    @Autowired
    private KeyIdGenerator keyIdGenerator;

    @Autowired
    private SmallFullService smallFullService;

    @Autowired
    private DirectorService directorService;

    @Autowired
    private SecretaryService secretaryService;

    @Autowired
    private StatementsService statementsService;

    @Autowired
    private DirectorsApprovalService directorsApprovalService;

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

        smallFullService.addLink(companyAccountsId, SmallFullLinkType.DIRECTORS_REPORT,
                getSelfLinkFromRestEntity(rest), request);

        return new ResponseObject<>(ResponseStatus.CREATED, rest);

    }

    @Override
    public ResponseObject<DirectorsReport> find(String companyAccountsId, HttpServletRequest request)
            throws DataException {

        DirectorsReportEntity entity;

        try {
            entity = directorsReportRepository.findById(generateID(companyAccountsId)).orElse(null);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }
        return new ResponseObject<>(ResponseStatus.FOUND, directorsReportTransformer.transform(entity));
    }

    @Override
    public ResponseObject<DirectorsReport> delete(String companyAccountsId, HttpServletRequest request)
            throws DataException {

        String reportId = generateID(companyAccountsId);

        Transaction transaction = (Transaction) request
                .getAttribute(AttributeName.TRANSACTION.getValue());

        directorService.deleteAll(transaction, companyAccountsId, request);
        secretaryService.delete(companyAccountsId, request);
        statementsService.delete(companyAccountsId, request);
        directorsApprovalService.delete(companyAccountsId, request);

        try {
            if (directorsReportRepository.existsById(reportId)) {

                directorsReportRepository.deleteById(reportId);

                smallFullService.removeLink(companyAccountsId, SmallFullLinkType.DIRECTORS_REPORT, request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {

                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    @Override
    public void addLink(String id, DirectorsReportLinkType linkType, String link,
            HttpServletRequest request) throws DataException {

        String directorsReportId = generateID(id);
        DirectorsReportEntity directorsReportEntity = directorsReportRepository.findById(directorsReportId)
                .orElseThrow(() -> new DataException(
                        "Failed to get Directors report entity to which to add link"));
        directorsReportEntity.getData().getLinks().put(linkType.getLink(), link);

        try {
            directorsReportRepository.save(directorsReportEntity);

        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    @Override
    public void removeLink(String id, DirectorsReportLinkType linkType, HttpServletRequest request)
            throws DataException {

        String directorsReportId = generateID(id);
        DirectorsReportEntity directorsReportEntity = directorsReportRepository.findById(directorsReportId)
                .orElseThrow(() -> new DataException(
                        "Failed to get Directors report entity from which to remove link"));
        directorsReportEntity.getData().getLinks().remove(linkType.getLink());

        try {
            directorsReportRepository.save(directorsReportEntity);

        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    @Override
    public void addDirector(String companyAccountsID, String directorID, String link, HttpServletRequest request)
            throws DataException {

        String reportId = generateID(companyAccountsID);
        DirectorsReportEntity entity = directorsReportRepository.findById(reportId)
                .orElseThrow(() -> new DataException(
                        "Failed to get Directors report entity to which to add director"));
        if (entity.getData().getDirectors() == null) {

            entity.getData().setDirectors(new HashMap<>());
        }
        entity.getData().getDirectors().put(directorID, link);

        try {

            directorsReportRepository.save(entity);
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    @Override
    public void removeDirector(String companyAccountsID, String directorID, HttpServletRequest request)
            throws DataException {
        String reportId = generateID(companyAccountsID);

        DirectorsReportEntity entity = directorsReportRepository.findById(reportId)
                .orElseThrow(() -> new DataException(
                        "Failed to get directors report entity from which to remove director"));

        entity.getData().getDirectors().remove(directorID);

        try {

            directorsReportRepository.save(entity);
        } catch (MongoException e) {

            throw new DataException(e);
        }

    }

    private String generateSelfLink(Transaction transaction, String companyAccountId) {

        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/" + companyAccountId + "/"
                + ResourceName.SMALL_FULL.getName() + "/"
                + ResourceName.DIRECTORS_REPORT.getName();
    }

    private Map<String, String> createLinks(Transaction transaction, String companyAccountsId) {

        Map<String, String> map = new HashMap<>();
        map.put(DirectorsReportLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId));
        return map;
    }

    private void setMetadataOnRestObject(DirectorsReport rest, Transaction transaction, String companyAccountsId) {

        rest.setLinks(createLinks(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.DIRECTORS_REPORT.getValue());
        rest.setDirectors(null);
    }

    private String generateID(String companyAccountId) {

        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.DIRECTORS_REPORT.getName());
    }

    public String getSelfLinkFromRestEntity(DirectorsReport rest) {

        return rest.getLinks().get(DirectorsReportLinkType.SELF.getLink());
    }
}
