package uk.gov.companieshouse.api.accounts.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;

import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.MissingAccountingPeriodException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.LastAccounts;
import uk.gov.companieshouse.api.accounts.model.rest.NextAccounts;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.rest.Statement;
import uk.gov.companieshouse.api.accounts.repository.SmallFullRepository;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.LinkService;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.SmallFullTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class SmallFullService implements
    ResourceService<SmallFull>, LinkService<SmallFullLinkType> {

    private SmallFullRepository smallFullRepository;

    private SmallFullTransformer smallFullTransformer;

    private CompanyAccountService companyAccountService;

    private KeyIdGenerator keyIdGenerator;

    private CompanyService companyService;

    @Autowired
    private StatementService statementService;

    @Autowired
    public SmallFullService(SmallFullRepository smallFullRepository,
        SmallFullTransformer smallFullTransformer,
        CompanyAccountService companyAccountService,
        KeyIdGenerator keyIdGenerator,
        CompanyService companyService) {
        this.smallFullRepository = smallFullRepository;
        this.smallFullTransformer = smallFullTransformer;
        this.companyAccountService = companyAccountService;
        this.keyIdGenerator = keyIdGenerator;
        this.companyService = companyService;
    }

    @Override
    public ResponseObject<SmallFull> create(SmallFull smallFull, Transaction transaction,
        String companyAccountId, HttpServletRequest request)
        throws DataException {

        String selfLink = createSelfLink(transaction, companyAccountId);
        initLinks(smallFull, selfLink);
        smallFull.setEtag(GenerateEtagUtil.generateEtag());
        smallFull.setKind(Kind.SMALL_FULL_ACCOUNT.getValue());
        try {
	        CompanyProfileApi companyProfile =
	                companyService.getCompanyProfile(transaction.getCompanyNumber());
	        setAccountingPeriodDatesOnRestObject(smallFull, companyProfile);
	        SmallFullEntity baseEntity = smallFullTransformer.transform(smallFull);
	        baseEntity.setId(generateID(companyAccountId));

            smallFullRepository.insert(baseEntity);
        } catch (DuplicateKeyException dke) {
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException|ServiceException e) {
            throw new DataException(e);
        }

        companyAccountService
            .addLink(companyAccountId, CompanyAccountLinkType.SMALL_FULL, selfLink);

        return new ResponseObject<>(ResponseStatus.CREATED, smallFull);
    }

    @Override
    public ResponseObject<SmallFull> find(String companyAccountsId, HttpServletRequest request) throws DataException {

        SmallFullEntity smallFullEntity;
        try {
            smallFullEntity = smallFullRepository.findById(generateID(companyAccountsId)).orElse(null);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        if (smallFullEntity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        SmallFull smallFull = smallFullTransformer.transform(smallFullEntity);
        return new ResponseObject<>(ResponseStatus.FOUND, smallFull);
    }

    @Override
    public ResponseObject<SmallFull> update(SmallFull smallFull, Transaction transaction,
        String companyAccountId, HttpServletRequest request)
        throws DataException {

    	String smallFullId = generateID(companyAccountId);
    	
        try {
        	SmallFullEntity smallFullEntity = smallFullRepository.findById(smallFullId).orElseThrow(() -> new DataException("Failed to find Small Full Account using Company Account ID: " + companyAccountId));
        	
        	smallFull.setLinks(smallFullEntity.getData().getLinks());
            smallFull.setEtag(GenerateEtagUtil.generateEtag());
            smallFull.setKind(Kind.SMALL_FULL_ACCOUNT.getValue());

	        CompanyProfileApi companyProfile =
	                companyService.getCompanyProfile(transaction.getCompanyNumber());
	        setAccountingPeriodDatesOnRestObject(smallFull, companyProfile);
	        SmallFullEntity baseEntity = smallFullTransformer.transform(smallFull);
	        baseEntity.setId(smallFullId);

            smallFullRepository.save(baseEntity);
        } catch (MongoException|ServiceException e) {
            throw new DataException(e);
        }

        invalidateStatementsIfExisting(companyAccountId, request);

        return new ResponseObject<>(ResponseStatus.UPDATED);
    }

    @Override
    public ResponseObject<SmallFull> delete(String companyAccountsId, HttpServletRequest request) throws DataException {
        return null;
    }

    @Override
    public void addLink(String id, SmallFullLinkType linkType, String link, HttpServletRequest request)
        throws DataException {
        String smallFullId = generateID(id);
        SmallFullEntity smallFullEntity = smallFullRepository.findById(smallFullId)
            .orElseThrow(() -> new DataException(
                "Failed to get Small full entity to add link"));
        smallFullEntity.getData().getLinks().put(linkType.getLink(), link);

        try {
            smallFullRepository.save(smallFullEntity);
        } catch (MongoException e) {
            throw new DataException(e);
        }
    }

    @Override
    public void removeLink(String id, SmallFullLinkType linkType, HttpServletRequest request)
            throws DataException {

        String smallFullId = generateID(id);
        SmallFullEntity smallFullEntity = smallFullRepository.findById(smallFullId)
                .orElseThrow(() -> new DataException(
                        "Failed to get Small full entity from which to remove link"));
        smallFullEntity.getData().getLinks().remove(linkType.getLink());

        try {
            smallFullRepository.save(smallFullEntity);
        } catch (MongoException e) {
            throw new DataException(e);
        }
    }

    private void setAccountingPeriodDatesOnRestObject(SmallFull rest, CompanyProfileApi companyProfile) {

        if (companyProfile.getAccounts() != null) {

            if (companyProfile.getAccounts().getNextAccounts() != null) {

                NextAccounts nextAccounts;
            	if(rest.getNextAccounts() != null) {
            		nextAccounts = rest.getNextAccounts();
            	} else {
            		nextAccounts = new NextAccounts();
            		rest.setNextAccounts(nextAccounts);
            	}
                nextAccounts.setPeriodStartOn(companyProfile.getAccounts().getNextAccounts().getPeriodStartOn());
                if(rest.getNextAccounts().getPeriodEndOn() == null) {
                	nextAccounts.setPeriodEndOn(companyProfile.getAccounts().getNextAccounts().getPeriodEndOn());
                }
            } else {

                throw new MissingAccountingPeriodException("No next accounts found for company: "
                        + companyProfile.getCompanyNumber() + " trying to file their accounts");
            }

            if (companyProfile.getAccounts().getLastAccounts() != null) {

                LastAccounts lastAccounts = new LastAccounts();
                lastAccounts.setPeriodStartOn(companyProfile.getAccounts().getLastAccounts().getPeriodStartOn());
                lastAccounts.setPeriodEndOn(companyProfile.getAccounts().getLastAccounts().getPeriodEndOn());
                rest.setLastAccounts(lastAccounts);
            }
        } else {

            throw new MissingAccountingPeriodException("No accounts found for company: "
                    + companyProfile.getCompanyNumber() + " trying to file their accounts");
        }
    }

    private String generateID(String value) {
        return keyIdGenerator.generate(value + "-" + ResourceName.SMALL_FULL.getName());
    }

    public String createSelfLink(Transaction transaction, String companyAccountId) {
        return transaction.getLinks().getSelf() + "/"
            + ResourceName.COMPANY_ACCOUNT.getName() + "/"
            + companyAccountId + "/" + ResourceName.SMALL_FULL.getName();
    }

    private void initLinks(SmallFull smallFull, String link) {
        Map<String, String> map = new HashMap<>();
        map.put(SmallFullLinkType.SELF.getLink(), link);
        smallFull.setLinks(map);
    }

    private void invalidateStatementsIfExisting(String companyAccountId, HttpServletRequest request)
        throws DataException {

        if (statementService.find(companyAccountId, request).getStatus().equals(ResponseStatus.FOUND)) {

            Statement statement = new Statement();
            Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

            statement.setHasAgreedToLegalStatements(false);

            statementService.update(statement, transaction, companyAccountId, request);
        }
    }
}