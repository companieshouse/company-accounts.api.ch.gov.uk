package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.fixedassetsinvestments.FixedAssetsInvestments;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class FixedInvestmentsValidatorTest {

    private static final String UNEXPECTED_DATA_NAME = "unexpectedData";
    private static final String UNEXPECTED_DATA_VALUE = "unexpected.data";
    private static final String FIXED_ASSETS_DETAILS_PATH = "$.fixed_assets_investments.details";
    private static final String MANDATORY_ELEMENT_MISSING_NAME = "mandatoryElementMissing";
    private static final String MANDATORY_ELEMENT_MISSING_VALUE =
            "mandatory_element_missing";

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private CurrentPeriodService mockCurrentPeriodService;

    @Mock
    private PreviousPeriodService mockPreviousPeriodService;


    private FixedAssetsInvestments fixedAssetsInvestments;
    private Errors errors;
    private FixedAssetsInvestmentsValidator validator;


    @BeforeEach
    void setup() {
        fixedAssetsInvestments = new FixedAssetsInvestments();
        errors = new Errors();
        validator = new FixedAssetsInvestmentsValidator(mockCurrentPeriodService,
                mockPreviousPeriodService);
    }

    @Test
    @DisplayName("Valid note submitted")
    void testValidNote() throws DataException {

        mockValidBalanceSheetCurrentPeriod();

        fixedAssetsInvestments.setDetails("test");

        errors = validator.validateFixedAssetsInvestments(mockRequest, fixedAssetsInvestments, "");

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Note submitted with no balance sheet values")
    void testValidationWhenNoteSubmittedNoBalanceSheetValues() throws DataException,
            ServiceException {

        fixedAssetsInvestments.setDetails("test");

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_NAME,
                UNEXPECTED_DATA_VALUE);

        errors = validator.validateFixedAssetsInvestments(mockRequest, fixedAssetsInvestments, "");

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA_VALUE,
                FIXED_ASSETS_DETAILS_PATH)));

    }

    @Test
    @DisplayName("Empty note when balance sheet values")
    void testValidationEmptyNoteWhenBalanceSheetValues() throws DataException, ServiceException {

        mockValidBalanceSheetCurrentPeriod();

        ReflectionTestUtils.setField(validator, MANDATORY_ELEMENT_MISSING_NAME,
                MANDATORY_ELEMENT_MISSING_VALUE);

        errors = validator.validateFixedAssetsInvestments(mockRequest, fixedAssetsInvestments, "");

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
                FIXED_ASSETS_DETAILS_PATH)));

    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }

    private ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> generateValidCurrentPeriodResponseObject() {
        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> currentPeriodResponseObject =
                new ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod>(
                        ResponseStatus.FOUND);

        uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod currentPeriodTest =
                new uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod();

        BalanceSheet balanceSheet = new BalanceSheet();

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setInvestments(5L);
        fixedAssets.setTotal(5L);

        balanceSheet.setFixedAssets(fixedAssets);

        currentPeriodTest.setBalanceSheet(balanceSheet);

        currentPeriodResponseObject.setData(currentPeriodTest);
        return currentPeriodResponseObject;
    }

    private void mockValidBalanceSheetCurrentPeriod() throws DataException {
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).find(
                "", mockRequest);
    }



    // empty note when balance sheet not

    // note when balance sheet null
}
