package uk.gov.companieshouse.api.accounts.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BindingResult;
import uk.gov.companieshouse.api.accounts.model.rest.Debtors;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DebtorsControllerTest {

    @Mock
    BindingResult bindingResult;

    @Mock
    Debtors debtors;

    @Mock
    Transaction transaction;

    @InjectMocks
    DebtorsController controller;



    // test valid debtors resource is created
    @Test
    @DisplayName("SUCCESS- Debtors resource created")
    void createDebtorsResource(){

        when(bindingResult.hasErrors()).thenReturn(false);


    }

    // errors returned with binding result errors

    // data exception thrown
}
