package uk.gov.companieshouse.api.accounts.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.model.entity.AccountsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Accounts;
import uk.gov.companieshouse.api.accounts.repository.AccountsRepository;
import uk.gov.companieshouse.api.accounts.service.AccountsService;

@Service
public class AccountsServiceImpl implements AccountsService {

    @Autowired
    private AccountsRepository accountsRepository;

    /**
     * {@inheritDoc}
     */
    public ResponseEntity createAccount(Accounts accounts) {
        try {
            AccountsEntity accountsEntity = new AccountsEntity().build(accounts);

            AccountsEntity createdAccount = accountsRepository.insert(accountsEntity);
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