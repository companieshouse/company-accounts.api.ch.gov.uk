package uk.gov.companieshouse.api.accounts.validation;

import org.apache.http.impl.execchain.ServiceUnavailableRetryExec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.Amortisation;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.Cost;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.IntangibleAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.IntangibleAssetsResource;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.TransactionService;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.serial.SerialException;
import javax.swing.tree.TreeNode;
import javax.xml.ws.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.intThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IntangibleAssetsValidatorTest {

    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;

    @Mock
    private CompanyService companyService;

    @Mock
    private CurrentPeriodService currentPeriodService;

    @InjectMocks
    private IntangibleAssetsValidator validator;

    private static final String UNEXPECTED_DATA_KEY = "unexpectedData";
    private static final String UNEXPECTED_DATA = "unexpected.data";

    private static final String VALUE_REQUIRED_KEY = "valueRequired";
    private static final String VALUE_REQUIRED = "value_required";

    private static final String INCORRECT_TOTAL_KEY = "incorrectTotal";
    private static final String INCORRECT_TOTAL = "incorrect_total";

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";


    @Test
    @DisplayName("First year filer - provides only additional info in note")
    void firstYearFilerNoteOnlyContainsAdditionalInfo() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setAdditionalInformation("additionalInfo");

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.intangible_assets.total.net_book_value_at_end_of_current_period")));



    }

    @Test
    @DisplayName("First year filer - provides cost at period start in sub resource")
    void firstYearFilerProvidesCostAtPeriodStartInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAtPeriodStart(1L);

        goodwill.setCost(cost);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_KEY, UNEXPECTED_DATA);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA, "$.intangible_assets.goodwill.cost.at_period_start")));

    }

    @Test
    @DisplayName("First year filer - provides only cost fields in sub resource")
    void firstYearFilerProvidesOnlyCostFieldsInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAdditions(1L);

        goodwill.setCost(cost);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.intangible_assets.goodwill.net_book_value_at_end_of_current_period")));


    }

    @Test
    @DisplayName("First year filer - doesn't provide cost in sub resource")
    void firstYearFilerDoesNotProvideCostInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();
        goodwill.setNetBookValueAtEndOfCurrentPeriod(1L);

        IntangibleAssets intangibleAssets = new IntangibleAssets();

        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.intangible_assets.goodwill.cost.at_period_end")));

    }

    @Test
    @DisplayName("First year filer - doesn't provide cost at period start in sub resource")
    void firstYearFilerDoesNotProvideCostAtPeriodStartInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();

        goodwill.setNetBookValueAtEndOfCurrentPeriod(1L);
        goodwill.setCost(new Cost());

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.intangible_assets.goodwill.cost.at_period_end")));

    }

    @Test
    @DisplayName("Single year filer - cost fields don't total in sub resource")
    void singleYearFilerCostFieldsDoNotTotalInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAdditions(1L);
        cost.setAtPeriodEnd(2L);
        goodwill.setCost(cost);
        goodwill.setNetBookValueAtEndOfCurrentPeriod(2L);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.goodwill.cost.at_period_end")));

    }

    @Test
    @DisplayName("Single year filer - net book value at end of current period  doesn't total in sub resource")
    void singleYearFilerCurrentNetBookValueDoesNotTotalInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAdditions(1L);
        cost.setAtPeriodEnd(1L);
        goodwill.setCost(cost);

        goodwill.setNetBookValueAtEndOfCurrentPeriod(2L);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.goodwill.net_book_value_at_end_of_current_period")));

    }

    private IntangibleAssetsResource createValidSubResource(boolean isMultipleYearFiler) {

        IntangibleAssetsResource resource = new IntangibleAssetsResource();

        Cost cost = new Cost();
        if(isMultipleYearFiler) {
            cost.setAtPeriodStart(1L);
        }

        cost.setAdditions(1L);
        cost.setDisposals(1L);
        cost.setRevaluations(1L);
        cost.setTransfers(1L);
        cost.setAtPeriodEnd(isMultipleYearFiler ? 3L : 2L);
        resource.setCost(cost);

        Amortisation amortisation = new Amortisation();
        if(isMultipleYearFiler) {
            amortisation.setAtPeriodStart(1L);
        }
        amortisation.setChargeForYear(1L);
        amortisation.setOnDisposals(1L);
        amortisation.setOtherAdjustments(1L);
        amortisation.setAtPeriodEnd(isMultipleYearFiler ? 2L : 1L);
        resource.setAmortisation(amortisation);

        resource.setNetBookValueAtEndOfCurrentPeriod(1L);

        if(isMultipleYearFiler) {
            resource.setNetBookValueAtEndOfPreviousPeriod(0L);
        }

        return resource;
    }

    private Error createError(String error,  String path) {

        return new Error(error, path, LocationType.JSON_PATH.getValue(),
        ErrorType.VALIDATION.getType());
    }


}