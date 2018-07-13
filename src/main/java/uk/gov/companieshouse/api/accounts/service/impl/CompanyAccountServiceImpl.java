package uk.gov.companieshouse.api.accounts.service.impl;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.repository.CompanyAccountRepository;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.transformer.CompanyAccountTransformer;

@Service
public class CompanyAccountServiceImpl implements CompanyAccountService {

    @Autowired
    private CompanyAccountRepository companyAccountRepository;

    @Resource
    private CompanyAccountTransformer companyAccountTransformer;

    /**
     * {@inheritDoc}
     */
    public CompanyAccount createCompanyAccount(CompanyAccount companyAccount) {
        generateEtagLinksKind(companyAccount);

        CompanyAccountEntity companyAccountEntity = companyAccountTransformer.transform(companyAccount);

        companyAccountRepository.insert(companyAccountEntity);

        return companyAccount;
    }

    private void generateEtagLinksKind(CompanyAccount companyAccount) {
        companyAccount.setEtag(GenerateEtagUtil.generateEtag());
        companyAccount.setKind(Kind.ACCOUNT.getValue());

        Map<String, String> links = new HashMap<>();

        links.put(LinkType.SELF.getLink(), "");
        companyAccount.setLinks(links);
    }
}