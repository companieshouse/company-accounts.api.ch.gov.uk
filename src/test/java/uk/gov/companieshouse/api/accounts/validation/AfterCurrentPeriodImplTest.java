package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidatorContext;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.accounts.enumeration.AccountType;
import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.model.rest.LastAccounts;
import uk.gov.companieshouse.api.accounts.parent.ParentResource;
import uk.gov.companieshouse.api.accounts.parent.ParentResourceFactory;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AfterCurrentPeriodImplTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AfterCurrentPeriodImpl afterCurrentPeriodImpl;

    @Mock
    private LastAccounts accountingPeriod;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ParentResource<LinkType> parentResource;
    
    @Mock
    private ParentResourceFactory<LinkType> parentResourceFactory;
    
    @Mock
    private AfterCurrentPeriod afterCurrentPeriod; 

    private static final LocalDate PERIOD_END_ON = LocalDate.of(2019, 1, 1);

    private void setUpPeriodEnd() {
    	when(parentResourceFactory.getParentResource(AccountType.SMALL_FULL)).thenReturn(parentResource);
        when(parentResource.getPeriodEndOn(request)).thenReturn(PERIOD_END_ON);
        when(afterCurrentPeriod.accountType()).thenReturn(AccountType.SMALL_FULL);
        afterCurrentPeriodImpl.initialize(afterCurrentPeriod);
    }

    @Test
    @DisplayName("AfterCurrentPeriod - date after period")
    void afterCurrentPeriodDateAfterPeriod() {
        
    	setUpPeriodEnd();
    	
        assertTrue(
                afterCurrentPeriodImpl
                        .isValid(LocalDate.of(2019, 1, 2),
                                context));
    }

    @Test
    @DisplayName("AfterCurrentPeriod - date before period")
    void afterCurrentPeriodDateBeforePeriod() {

    	setUpPeriodEnd();

        assertFalse(
                afterCurrentPeriodImpl
                        .isValid(LocalDate.of(2017, 12, 31),
                                context));
    }

    @Test
    @DisplayName("AfterCurrentPeriod - date equals period end")
    void afterCurrentPeriodDateEqualsPeriodStart() {

    	setUpPeriodEnd();

        assertFalse(
                afterCurrentPeriodImpl
                        .isValid(PERIOD_END_ON, context));
    }

    @Test
    @DisplayName("AfterCurrentPeriod - null date")
    void afterCurrentPeriodNullDate() {

        assertTrue(afterCurrentPeriodImpl.isValid(null, context));

        verify(parentResourceFactory, never()).getParentResource(any(AccountType.class));
    }
}