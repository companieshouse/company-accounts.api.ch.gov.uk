package uk.gov.companieshouse.api.accounts.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        AccountEntity accountEntity = new AccountEntity().build(account);

        AccountEntity createdAccount = accountRepository.insert(accountEntity);

        account = new Account();
        BeanUtils.copyProperties(createdAccount.getData(), account);

        return account;
    }
}