package uk.gov.companieshouse.api.accounts.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity createAccount(Account account) {
        try {
            AccountEntity accountEntity = new AccountEntity().build(account);

            AccountEntity createdAccount = accountRepository.insert(accountEntity);
            if (createdAccount != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount.getData());
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (DataAccessException dataAccessException) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}