package uk.gov.companieshouse.api.accounts.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintValidatorContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ValidRptTransactionTypeImplTest {

    @Mock
    private ConstraintValidatorContext context;

    private static final String MONEY_GIVEN_TO_RELATED_PARTY = "Money given to a related party by the company";
    private static final String MONEY_GIVEN_BY_RELATED_PARTY = "Money given to the company by a related party";
    private static final String OTHER = "Money given by someone else";

    private ValidRptTransactionTypeImpl validRptTransactionType;

    @BeforeEach
    private void setup() {

        validRptTransactionType = new ValidRptTransactionTypeImpl();
    }

    @Test
    @DisplayName("Test when money is given to related party by the company")
    void testMoneyGivenToRelatedParty() {

        assertTrue(validRptTransactionType.isValid(MONEY_GIVEN_TO_RELATED_PARTY, context));
    }

    @Test
    @DisplayName("Test when money is given by related party to the company")
    void testMoneyGivenByRelatedParty() {

        assertTrue(validRptTransactionType.isValid(MONEY_GIVEN_BY_RELATED_PARTY, context));
    }

    @Test
    @DisplayName("Test when RPT transaction type is null")
    void testRptTransactionTypeIsNull() {

        assertTrue(validRptTransactionType.isValid(null, context));
    }

    @Test
    @DisplayName("Test when RPT transaction type is other than money give to the related party or money given by related party")
    void testRegulatoryStandardOther() {

        assertFalse(validRptTransactionType.isValid(OTHER, context));
    }
}