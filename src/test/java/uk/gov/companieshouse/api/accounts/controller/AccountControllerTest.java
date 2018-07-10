package uk.gov.companieshouse.api.accounts.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.Equals;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Account;
import uk.gov.companieshouse.api.accounts.service.AccountService;


@RunWith(MockitoJUnitRunner.class)
public class AccountControllerTest {

    @Mock
    private Account account;

    @Mock
    private Account createdAccount;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @Before
    public void setUp(){
        when(accountService.createAccount(account)).thenReturn(createdAccount);
    }

    @Test
    public void canCreateAccount() {

        ResponseEntity response = accountController.createAccount(account);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(new Equals(createdAccount).matches(response.getBody()));

    }

}