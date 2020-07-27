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
class ValidRegulatoryStandardsImplTest {

    @Mock
    private ConstraintValidatorContext context;

    private static final String FRS101 = "These financial statements have been prepared in accordance with the provisions of Financial Reporting Standard 101";
    private static final String FRS102 = "These financial statements have been prepared in accordance with the provisions of Section 1A (Small Entities) of Financial Reporting Standard 102";
    private static final String OTHER_STANDARD = "otherStandard";

    private ValidRegulatoryStandardsImpl regulatoryStandards;
    @BeforeEach
    private void setup() {

         regulatoryStandards = new ValidRegulatoryStandardsImpl();
    }

    @Test
    @DisplayName("Test when regulatory standard is FRS101")
    void testRegulatoryStandardFRS101() {

        assertTrue(regulatoryStandards.isValid(FRS101, context));
    }

    @Test
    @DisplayName("Test when regulatory standard is FRS102")
    void testRegulatoryStandardFRS102() {

        assertTrue(regulatoryStandards.isValid(FRS102, context));
    }

    @Test
    @DisplayName("Test when regulatory standard is null")
    void testRegulatoryStandardNull() {

        assertTrue(regulatoryStandards.isValid(null, context));
    }

    @Test
    @DisplayName("Test when regulatory standard is other than FRS101 or FRS102")
    void testRegulatoryStandardOther() {

        assertFalse(regulatoryStandards.isValid(OTHER_STANDARD, context));
    }
}