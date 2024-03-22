package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

import jakarta.servlet.http.HttpServletRequest;

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
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.fixedassetsinvestments.FixedAssetsInvestments;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FixedAssetsInvestmentsValidatorTest {

    private static final String UNEXPECTED_DATA_NAME = "unexpectedData";
    private static final String UNEXPECTED_DATA_VALUE = "unexpected.data";
    private static final String FIXED_ASSETS_DETAILS_PATH = "$.fixed_assets_investments.details";
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

    @Mock
    private Transaction transaction;
    
    @Mock
    private CompanyService mockCompanyService;

    private FixedAssetsInvestments fixedAssetsInvestments;
    private Errors errors;
    private FixedAssetsInvestmentsValidator validator;


    @BeforeEach
    void setup() {
        fixedAssetsInvestments = new FixedAssetsInvestments();
        errors = new Errors();
        validator = new FixedAssetsInvestmentsValidator(mockCompanyService, mockCurrentPeriodService,
                mockPreviousPeriodService);
    }

    @Test
    @DisplayName("Valid note submitted successfully")
    void testValidNote() throws DataException {

        mockValidBalanceSheetCurrentPeriod();
        mockValidBalanceSheetPreviousPeriod();

        fixedAssetsInvestments.setDetails("test");

        errors = validator.validateSubmission(fixedAssetsInvestments, transaction, "", mockRequest);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Error thrown when note submitted with no fixed assets values in the balance sheet")
    void testValidationWhenNoteSubmittedNoFixedAssetsInBalanceSheetValues() throws DataException,
            ServiceException {
        mockValidBalanceSheetCurrentPeriodWithoutFixedAssetsInvestments();
        mockValidBalanceSheetPreviousPeriodWithoutFixedAssetsInvestments();
        fixedAssetsInvestments.setDetails("test");

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_NAME,
                UNEXPECTED_DATA_VALUE);

        errors = validator.validateSubmission(fixedAssetsInvestments, transaction, "", mockRequest);

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA_VALUE,
                FIXED_ASSETS_DETAILS_PATH)));

    }

    @Test
    @DisplayName("Valid note submitted when no balance sheet")
    void testValidationWhenNoteSubmittedNoBalanceSheetValues() throws DataException,
            ServiceException {

        fixedAssetsInvestments.setDetails("test");
        errors = validator.validateSubmission(fixedAssetsInvestments, transaction, "", mockRequest);
        assertFalse(errors.hasErrors());

    }
    
    @Test
    @DisplayName("Error thrown when empty note submitted when balance sheet values")
    void testValidationEmptyNoteWhenBalanceSheetValues() throws DataException, ServiceException {

        mockValidBalanceSheetCurrentPeriod();
        mockValidBalanceSheetPreviousPeriod();

        ReflectionTestUtils.setField(validator, MANDATORY_ELEMENT_MISSING_NAME,
                MANDATORY_ELEMENT_MISSING_VALUE);

        errors = validator.validateSubmission(fixedAssetsInvestments, transaction, "", mockRequest);

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(MANDATORY_ELEMENT_MISSING_VALUE,
                FIXED_ASSETS_DETAILS_PATH)));

    }

    @Test
    @DisplayName("Error thrown when empty resource submitted")
    void testValidationEmptyResource() throws DataException, ServiceException {

        ReflectionTestUtils.setField(validator, EMPTY_RESOURCE_NAME,
                EMPTY_RESOURCE_VALUE);

        errors = validator.validateSubmission(fixedAssetsInvestments, transaction, "", mockRequest);

        assertTrue(errors.hasErrors());
        assertTrue(errors.containsError(createError(EMPTY_RESOURCE_VALUE,
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

    private ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> generateValidPreviousPeriodResponseObject() {
        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> previousPeriodResponseObject =
               new ResponseObject<PreviousPeriod>(ResponseStatus.FOUND);

        uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod previousPeriodTest =
                new uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod();

        BalanceSheet balanceSheet = new BalanceSheet();

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setInvestments(5L);
        fixedAssets.setTotal(5L);

        balanceSheet.setFixedAssets(fixedAssets);

        previousPeriodTest.setBalanceSheet(balanceSheet);

        previousPeriodResponseObject.setData(previousPeriodTest);
        return previousPeriodResponseObject;
    }

    private ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> generateValidCurrentPeriodResponseObjectWithoutFixedAssets() {
        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod> currentPeriodResponseObject =
                new ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod>(
                        ResponseStatus.FOUND);

        uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod currentPeriodTest =
                new uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod();

        BalanceSheet balanceSheet = new BalanceSheet();

        currentPeriodTest.setBalanceSheet(balanceSheet);

        currentPeriodResponseObject.setData(currentPeriodTest);
        return currentPeriodResponseObject;
    }

    private ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> generateValidPreviousPeriodResponseObjectWithoutFixedAssets() {
        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> previousPeriodResponseObject =
                new ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod>(
                        ResponseStatus.FOUND);

        uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod previousPeriodTest =
                new uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod();

        BalanceSheet balanceSheet = new BalanceSheet();

        previousPeriodTest.setBalanceSheet(balanceSheet);

        previousPeriodResponseObject.setData(previousPeriodTest);
        return previousPeriodResponseObject;
    }

    private void mockValidBalanceSheetCurrentPeriod() throws DataException {
        doReturn(generateValidCurrentPeriodResponseObject()).when(mockCurrentPeriodService).find(
                "", mockRequest);
    }

    private void mockValidBalanceSheetCurrentPeriodWithoutFixedAssetsInvestments() throws DataException {
        doReturn(generateValidCurrentPeriodResponseObjectWithoutFixedAssets()).when(mockCurrentPeriodService).find(
                "", mockRequest);
    }

    private void mockValidBalanceSheetPreviousPeriod() throws DataException {
        doReturn(generateValidPreviousPeriodResponseObject()).when(mockPreviousPeriodService).find(
                "", mockRequest);
    }

    private void mockValidBalanceSheetPreviousPeriodWithoutFixedAssetsInvestments() throws DataException {
        doReturn(generateValidPreviousPeriodResponseObjectWithoutFixedAssets()).when(mockPreviousPeriodService).find(
                "", mockRequest);
    }
}
