package uk.gov.companieshouse.api.accounts.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.repository.CompanyAccountRepository;
import uk.gov.companieshouse.api.accounts.transformer.CompanyAccountTransformer;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class CompanyAccountServiceImplTest {

    @Mock
    private CompanyAccount companyAccount;

    @Mock
    private CompanyAccountEntity createdCompanyAccountEntity;

    @Mock
    private CompanyAccountRepository companyAccountRepository;

    @Mock
    private CompanyAccountTransformer companyAccountTransformer;

    @InjectMocks
    private CompanyAccountServiceImpl companyAccountService;

    @BeforeEach
    public void setUp() {
        when(companyAccountTransformer.transform(companyAccount)).thenReturn(createdCompanyAccountEntity);
    }

    @Test
    @DisplayName("Tests the successful creation of an companyAccount resource")
    public void canCreateAccount() {
        CompanyAccount result = companyAccountService.save(companyAccount);
        assertNotNull(result);
        assertEquals(companyAccount, result);

    }
}