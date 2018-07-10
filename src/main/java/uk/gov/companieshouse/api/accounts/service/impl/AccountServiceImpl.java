package uk.gov.companieshouse.api.accounts.service.impl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.model.entity.AccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.AccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Account;
import uk.gov.companieshouse.api.accounts.repository.AccountRepository;
import uk.gov.companieshouse.api.accounts.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    /**
     * {@inheritDoc}
     */

    public Account createAccount(Account account) {
        AccountDataEntity accountDataEntity = new AccountDataEntity();
        AccountEntity accountEntity = new AccountEntity();
        BeanUtils.copyProperties(account, accountDataEntity);

        //set properties etag, link
        generateEtagLinksKind(accountDataEntity);
        accountEntity.setData(accountDataEntity);

        AccountEntity createdAccount = accountRepository.insert(accountEntity);

        BeanUtils.copyProperties(createdAccount.getData(), account);

        return account;
    }

    private void generateEtagLinksKind(AccountDataEntity accountDataEntity) {
        accountDataEntity.setEtag(GenerateEtagUtil.generateEtag());
        accountDataEntity.setKind(Kind.ACCOUNT.getValue());

        Map<String, String> links = new HashMap<>();

        links.put(LinkType.SELF.getLink(), "");
        accountDataEntity.setLinks(links);
    }

}