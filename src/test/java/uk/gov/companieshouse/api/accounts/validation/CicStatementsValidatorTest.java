package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import uk.gov.companieshouse.api.accounts.model.rest.CicStatements;
import uk.gov.companieshouse.api.accounts.model.rest.ReportStatements;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CicStatementsValidatorTest {

    @Mock
    private CicStatements cicStatements;

    @Mock
    private ReportStatements reportStatements;

    @Mock
    private CompanyService mockCompanyService;

    private CicStatementsValidator validator = new CicStatementsValidator(mockCompanyService);

    private static final String CONSULTATION_WITH_STAKEHOLDERS = "consultationWithStakeholders";
    private static final String DIRECTORS_REMUNERATION = "directorsRemuneration";
    private static final String TRANSFER_OF_ASSETS = "transferOfAssets";

    private static final String MANDATORY_ELEMENT_MISSING_NAME = "mandatoryElementMissing";
    private static final String MANDATORY_ELEMENT_MISSING_VALUE = "mandatory.element.missing";

    private static final String CIC_STATEMENTS_BASE_PATH = "$.cic_statements.report_statements.";

    private static final String CONSULTATION_WITH_STAKEHOLDERS_PATH
            = CIC_STATEMENTS_BASE_PATH + "consultation_with_stakeholders";
    private static final String DIRECTORS_REMUNERATION_PATH
            = CIC_STATEMENTS_BASE_PATH + "directors_remuneration";
    private static final String TRANSFER_OF_ASSETS_PATH
            = CIC_STATEMENTS_BASE_PATH + "transfer_of_assets";

    @Test
    @DisplayName("Validate update of CIC statements - all fields provided")
    void validateUpdateOfCicStatementsWithAllFieldsProvided() {

        mockCicStatementsFieldsPopulated(true, true, true);

        Errors errors = validator.validateCicStatementsUpdate(cicStatements);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Validate update of CIC statements - consultation with stakeholders not provided")
    void validateUpdateOfCicStatementsConsultationWithStakeholdersNotProvided() {

        mockCicStatementsFieldsPopulated(false, true, true);
        ReflectionTestUtils.setField(validator, MANDATORY_ELEMENT_MISSING_NAME, MANDATORY_ELEMENT_MISSING_VALUE);

        Errors errors = validator.validateCicStatementsUpdate(cicStatements);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE, CONSULTATION_WITH_STAKEHOLDERS_PATH)));
    }

    @Test
    @DisplayName("Validate update of CIC statements - directors' remuneration not provided")
    void validateUpdateOfCicStatementsDirectorsRemunerationNotProvided() {

        mockCicStatementsFieldsPopulated(true, false, true);
        ReflectionTestUtils.setField(validator, MANDATORY_ELEMENT_MISSING_NAME, MANDATORY_ELEMENT_MISSING_VALUE);

        Errors errors = validator.validateCicStatementsUpdate(cicStatements);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE, DIRECTORS_REMUNERATION_PATH)));
    }

    @Test
    @DisplayName("Validate update of CIC statements - transfer of assets not provided")
    void validateUpdateOfCicStatementsTransferOfAssetsNotProvided() {

        mockCicStatementsFieldsPopulated(true, true, false);
        ReflectionTestUtils.setField(validator, MANDATORY_ELEMENT_MISSING_NAME, MANDATORY_ELEMENT_MISSING_VALUE);

        Errors errors = validator.validateCicStatementsUpdate(cicStatements);

        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE, TRANSFER_OF_ASSETS_PATH)));
    }

    private void mockCicStatementsFieldsPopulated(boolean hasConsultationWithStakeholders,
                                                  boolean hasDirectorsRemuneration,
                                                  boolean hasTransferOfAssets) {

        when(cicStatements.getReportStatements()).thenReturn(reportStatements);

        when(reportStatements.getConsultationWithStakeholders()).thenReturn(
                hasConsultationWithStakeholders ? CONSULTATION_WITH_STAKEHOLDERS : null);

        when(reportStatements.getDirectorsRemuneration()).thenReturn(
                hasDirectorsRemuneration ? DIRECTORS_REMUNERATION : null);

        when(reportStatements.getTransferOfAssets()).thenReturn(
                hasTransferOfAssets? TRANSFER_OF_ASSETS : null);
    }

    private Error createError(String error, String path) {

        return new Error(error, path, LocationType.JSON_PATH.getValue(), ErrorType.VALIDATION.getType());
    }
}
