package uk.gov.companieshouse.api.accounts.service.impl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.transformer.GenericTransformer;

@Service
public class CompanyAccountServiceImpl extends AbstractServiceImpl<CompanyAccount, CompanyAccountEntity, String> implements CompanyAccountService  {

    @Autowired
    public CompanyAccountServiceImpl(
            @Qualifier("companyAccountRepository") MongoRepository<CompanyAccountEntity, String> mongoRepository,
            @Qualifier("companyAccountTransformer") GenericTransformer<CompanyAccountEntity, CompanyAccount> companyAccountTransformer) {
        super(mongoRepository, companyAccountTransformer);
    }

    @Override
    public void addLinks(CompanyAccount rest) {
        Map<String, String> links = new HashMap<>();
        links.put(LinkType.SELF.getLink(), "self link");
        rest.setLinks(links);
    }

    @Override
    public void addKind(CompanyAccount rest) {
        rest.setKind("company-accounts");
    }
}