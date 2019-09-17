package uk.gov.companieshouse.api.accounts.validation;

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
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.CurrentAssetsInvestments;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrentInvestmentsValidatorTest {

    private static final String UNEXPECTED_DATA_NAME = "unexpectedData";
    private static final String UNEXPECTED_DATA_VALUE = "unexpected.data";
    private static final String CURRENT_ASSETS_DETAILS_PATH = "$.current_assets_investments.details";
    private static final String EMPTY_RESOURCE_NAME = "emptyResource";
    private static final String EMPTY_RESOURCE_VALUE = "empty_resource";
    private static final String MANDATORY_ELEMENT_MISSING_NAME = "mandatoryElementMissing";
    private static final String MANDATORY_ELEMENT_MISSING_VALUE =
        "mandatory_element_missing";

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private CurrentPeriodService mockCurrentPeriodService;

    @Mock
    private PreviousPeriodService mockPreviousPeriodService;


    private CurrentAssetsInvestments currentAssetsInvestments;
    private Errors errors;
    private CurrentAssetsInvestmentsValidator validator;


    @BeforeEach
    void setup() {
        currentAssetsInvestments = new CurrentAssetsInvestments();
        errors = new Errors();
        validator = new CurrentAssetsInvestmentsValidator(mockCurrentPeriodService,
            mockPreviousPeriodService);
    }

    @Test
    @DisplayName("Valid note submitted successfully")
    void testValidNote() throws DataException {

        mockValidBalanceSheetCurrentPeriod();

        currentAssetsInvestments.setDetails("test");

        errors = validator.validateCurrentAssetsInvestments(mockRequest, currentAssetsInvestments, "");

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Error thrown when note submitted with no balance sheet values")
    void testValidationWhenNoteSubmittedNoBalanceSheetValues() throws DataException,
        ServiceException {

        currentAssetsInvestments.setDetails("test");

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_NAME,
            UNEXPECTED_DATA_VALUE);

        errors = validator.validateCurrentAssetsInvestments(mockRequest, currentAssetsInvestments, "");

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA_VALUE,
            CURRENT_ASSETS_DETAILS_PATH)));

    }

    @Test
    @DisplayName("Error thrown when empty note submitted when balance sheet values")
    void testValidationEmptyNoteWhenBalanceSheetValues() throws DataException, ServiceException {

        mockValidBalanceSheetCurrentPeriod();

        ReflectionTestUtils.setField(validator, MANDATORY_ELEMENT_MISSING_NAME,
            MANDATORY_ELEMENT_MISSING_VALUE);

        errors = validator.validateCurrentAssetsInvestments(mockRequest, currentAssetsInvestments, "");

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
            CURRENT_ASSETS_DETAILS_PATH)));

    }

    @Test
    @DisplayName("Error thrown when empty resource submitted")
    void testValidationEmptyResource() throws DataException, ServiceException {


        ReflectionTestUtils.setField(validator, EMPTY_RESOURCE_NAME,
            EMPTY_RESOURCE_VALUE);

        errors = validator.validateCurrentAssetsInvestments(mockRequest, currentAssetsInvestments, "");

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(EMPTY_RESOURCE_VALUE,
            CURRENT_ASSETS_DETAILS_PATH)));

    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
            ErrorType.VALIDATION.getType());
    }

    private ResponseObject<CurrentPeriod> generateValidCurrentPeriodResponseObject() {
        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> currentPeriodResponseObject =
            new ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod>(
                ResponseStatus.FOUND);

        uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod currentPeriodTest =
            new uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod();

        BalanceSheet balanceSheet = new BalanceSheet();

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setInvestments(5L);
        currentAssets.setTotal(5L);

        balanceSheet.setCurrentAssets(currentAssets);

        currentPeriodTest.setBalanceSheet(balanceSheet);

        currentPeriodResponseObject.setData(currentPeriodTest);
        return currentPeriodResponseObject;
    }

    private void mockValidBalanceSheetCurrentPeriod() throws DataException {
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).find(
            "", mockRequest);
    }
}