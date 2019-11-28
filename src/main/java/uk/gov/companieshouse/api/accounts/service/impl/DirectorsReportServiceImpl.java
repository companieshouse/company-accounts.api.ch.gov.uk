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
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorsReportDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorsReportEntity;
import uk.gov.companieshouse.api.accounts.model.rest.DirectorsReport;
import uk.gov.companieshouse.api.accounts.repository.DirectorsReportRepository;
import uk.gov.companieshouse.api.accounts.service.DirectorsReportService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.DirectorsReportTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class DirectorsReportServiceImpl implements DirectorsReportService {

    private DirectorsReportRepository directorsReportRepository;
    private DirectorsReportTransformer directorsReportTransformer;
    private KeyIdGenerator keyIdGenerator;
    private SmallFullService smallFullService;

    @Autowired
    public DirectorsReportServiceImpl(
            DirectorsReportRepository directorsReportRepository, DirectorsReportTransformer directorsReportTransformer, KeyIdGenerator keyIdGenerator, SmallFullService smallFullService) {
        this.directorsReportRepository = directorsReportRepository;
        this.directorsReportTransformer = directorsReportTransformer;
        this.keyIdGenerator = keyIdGenerator;
        this.smallFullService = smallFullService;
    }

    @Override
    public ResponseObject<DirectorsReport> create(DirectorsReport rest, Transaction transaction, String companyAccountsId, HttpServletRequest request)
            throws DataException {

        setMetadataOnRestObject(rest, transaction, companyAccountsId);

        rest.setDirectors(null);
        rest.setSecretaries(null);

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
    public ResponseObject<DirectorsReport> update(DirectorsReport rest, Transaction transaction, String companyAccountsId, HttpServletRequest request)
            throws DataException {

        setMetadataOnRestObject(rest, transaction, companyAccountsId);

        try {
            DirectorsReportEntity original =
                    directorsReportRepository.findById(generateID(companyAccountsId))
                            .orElseThrow(() -> new DataException(""));

            rest.setDirectors(original.getData().getDirectorsEntity());
            rest.setSecretaries(original.getData().getSecretariesEntity());

            DirectorsReportEntity entity = directorsReportTransformer.transform(rest);
            entity.setId(generateID(companyAccountsId));

            directorsReportRepository.save(entity);

        } catch (MongoException e) {
            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);
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
    public void addDirector(String companyAccountsID, String directorID, String link, HttpServletRequest request)
            throws DataException {

        String reportId = generateID(companyAccountsID);
        DirectorsReportEntity entity = directorsReportRepository.findById(reportId)
                .orElseThrow(() -> new DataException(
                        "Failed to get Directors report entity to which to add director"));
        if (entity.getData().getDirectorsEntity() == null) {

            entity.getData().setDirectorsEntity(new HashMap<>());
        }
        entity.getData().getDirectorsEntity().put(directorID, link);

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
                        "Failed to get directors report entity to which to remove director"));

        entity.getData().getDirectorsEntity().remove(directorID);

        try {

            directorsReportRepository.save(entity);
        } catch (MongoException e) {

            throw new DataException(e);
        }

    }

    @Override
    public void addSecretary(String companyAccountsID, String secretaryID, String link, HttpServletRequest request) throws DataException {
        String reportId = generateID(companyAccountsID);
        DirectorsReportEntity entity = directorsReportRepository.findById(reportId)
                .orElseThrow(() -> new DataException(
                        "Failed to get Directors report entity to which to add secretary"));
        if (entity.getData().getSecretariesEntity() == null) {

            entity.getData().setSecretariesEntity(new HashMap<>());
        }
        entity.getData().getSecretariesEntity().put(secretaryID, link);

        try {

            directorsReportRepository.save(entity);
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    @Override
    public void removeSecretary(String companyAccountsID, String secretaryID, HttpServletRequest request) throws DataException {
        String reportId = generateID(companyAccountsID);

        DirectorsReportEntity entity = directorsReportRepository.findById(reportId)
                .orElseThrow(() -> new DataException(
                        "Failed to get directors report entity to which to remove secretary"));

        entity.getData().getSecretariesEntity().remove(secretaryID);

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
        map.put(BasicLinkType.SELF.getLink(), generateSelfLink(transaction, companyAccountsId));
        return map;
    }

    private void setMetadataOnRestObject(DirectorsReport rest, Transaction transaction, String companyAccountsId) {

        rest.setLinks(createLinks(transaction, companyAccountsId));
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.DIRECTORS_REPORT.getValue());
    }

    private String generateID(String companyAccountId) {

        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.DIRECTORS_REPORT.getName());
    }

    public String getSelfLinkFromRestEntity(DirectorsReport rest) {

        return rest.getLinks().get(BasicLinkType.SELF.getLink());
    }
}
