package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.Cost;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.Depreciation;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.TangibleAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.TangibleAssetsResource;
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
public class TangibleAssetsValidatorTest {

    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;

    @Mock
    private CompanyService companyService;

    @Mock
    private CurrentPeriodService currentPeriodService;

    @Mock
    private PreviousPeriodService previousPeriodService;

    @InjectMocks
    private TangibleAssetsValidator validator;

    private static final String UNEXPECTED_DATA_KEY = "unexpectedData";
    private static final String UNEXPECTED_DATA = "unexpected.data";

    private static final String VALUE_REQUIRED_KEY = "valueRequired";
    private static final String VALUE_REQUIRED = "value_required";

    private static final String INCORRECT_TOTAL_KEY = "incorrectTotal";
    private static final String INCORRECT_TOTAL = "incorrect_total";

    private static final String CURRENT_BALANCE_SHEET_NOT_EQUAL_KEY = "currentBalanceSheetNotEqual";
    private static final String CURRENT_BALANCE_SHEET_NOT_EQUAL = "value_not_equal_to_current_period_on_balance_sheet";

    private static final String PREVIOUS_BALANCE_SHEET_NOT_EQUAL_KEY = "previousBalanceSheetNotEqual";
    private static final String PREVIOUS_BALANCE_SHEET_NOT_EQUAL = "value_not_equal_to_previous_period_on_balance_sheet";

    private static final String COMPANY_ACCOUNTS_ID = "companyAccountsId";

    @Test
    @DisplayName("First year filer - provides only additional info in note")
    void firstYearFilerNoteOnlyContainsAdditionalInfo() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setAdditionalInformation("additionalInfo");

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.tangible_assets.total.net_book_value_at_end_of_current_period")));
    }

    @Test
    @DisplayName("Multiple year filer - provides only additional info in note")
    void multipleYearFilerNoteOnlyContainsAdditionalInfo() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setAdditionalInformation("additionalInfo");

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(2, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.tangible_assets.total.net_book_value_at_end_of_current_period")));
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.tangible_assets.total.net_book_value_at_end_of_previous_period")));
    }

    @Test
    @DisplayName("First year filer - provides cost at period start in sub resource")
    void firstYearFilerProvidesCostAtPeriodStartInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAtPeriodStart(1L);

        fixturesAndFittings.setCost(cost);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_KEY, UNEXPECTED_DATA);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA, "$.tangible_assets.fixtures_and_fittings.cost.at_period_start")));
    }

    @Test
    @DisplayName("First year filer - provides depreciation at period start in sub resource")
    void firstYearFilerProvidesDepreciationAtPeriodStartInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        Depreciation depreciation = new Depreciation();
        depreciation.setAtPeriodStart(1L);

        fixturesAndFittings.setDepreciation(depreciation);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_KEY, UNEXPECTED_DATA);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA, "$.tangible_assets.fixtures_and_fittings.depreciation.at_period_start")));
    }

    @Test
    @DisplayName("First year filer - provides net book value at previous period end in sub resource")
    void firstYearFilerProvidesNetBookValueAtPreviousPeriodEndInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        fixturesAndFittings.setNetBookValueAtEndOfPreviousPeriod(1L);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_KEY, UNEXPECTED_DATA);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA, "$.tangible_assets.fixtures_and_fittings.net_book_value_at_end_of_previous_period")));
    }

    @Test
    @DisplayName("First year filer - provides only cost fields in sub resource")
    void firstYearFilerProvidesOnlyCostFieldsInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAdditions(1L);

        fixturesAndFittings.setCost(cost);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.tangible_assets.fixtures_and_fittings.net_book_value_at_end_of_current_period")));
    }

    @Test
    @DisplayName("First year filer - provides only depreciation fields in sub resource")
    void firstYearFilerProvidesOnlyDepreciationFieldsInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        Depreciation depreciation = new Depreciation();
        depreciation.setChargeForYear(1L);

        fixturesAndFittings.setDepreciation(depreciation);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.tangible_assets.fixtures_and_fittings.net_book_value_at_end_of_current_period")));
    }

    @Test
    @DisplayName("First year filer - doesn't provide cost in sub resource")
    void firstYearFilerDoesNotProvideCostInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        fixturesAndFittings.setNetBookValueAtEndOfCurrentPeriod(1L);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.tangible_assets.fixtures_and_fittings.cost.at_period_end")));
    }

    @Test
    @DisplayName("First year filer - doesn't provide cost at period start in sub resource")
    void firstYearFilerDoesNotProvideCostAtPeriodStartInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        fixturesAndFittings.setNetBookValueAtEndOfCurrentPeriod(1L);
        fixturesAndFittings.setCost(new Cost());

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.tangible_assets.fixtures_and_fittings.cost.at_period_end")));
    }

    @Test
    @DisplayName("Multiple year filer - doesn't provide net book value at end of previous period in sub resource")
    void multipleYearFilerDoesNotProvideNetBookValuePreviousPeriodInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAtPeriodStart(1L);
        cost.setAtPeriodEnd(1L);

        fixturesAndFittings.setCost(cost);
        fixturesAndFittings.setNetBookValueAtEndOfCurrentPeriod(1L);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.tangible_assets.fixtures_and_fittings.net_book_value_at_end_of_previous_period")));
    }

    @Test
    @DisplayName("Multiple year filer - doesn't provide net book value at end of current period in sub resource")
    void multipleYearFilerDoesNotProvideNetBookValueCurrentPeriodInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAtPeriodStart(1L);
        cost.setAtPeriodEnd(1L);

        fixturesAndFittings.setCost(cost);
        fixturesAndFittings.setNetBookValueAtEndOfPreviousPeriod(1L);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.tangible_assets.fixtures_and_fittings.net_book_value_at_end_of_current_period")));
    }

    @Test
    @DisplayName("Multiple year filer - doesn't provide cost in sub resource")
    void multipleYearFilerDoesNotProvideCostInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();
        fixturesAndFittings.setNetBookValueAtEndOfPreviousPeriod(1L);
        fixturesAndFittings.setNetBookValueAtEndOfCurrentPeriod(1L);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(2, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.tangible_assets.fixtures_and_fittings.cost.at_period_start")));
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.tangible_assets.fixtures_and_fittings.cost.at_period_end")));
    }

    @Test
    @DisplayName("Multiple year filer - doesn't provide cost at period start in sub resource")
    void multipleYearFilerDoesNotProvideCostAtPeriodStartInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();
        fixturesAndFittings.setNetBookValueAtEndOfPreviousPeriod(1L);
        fixturesAndFittings.setNetBookValueAtEndOfCurrentPeriod(1L);

        Cost cost = new Cost();
        cost.setAtPeriodEnd(1L);
        fixturesAndFittings.setCost(cost);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.tangible_assets.fixtures_and_fittings.cost.at_period_start")));
    }

    @Test
    @DisplayName("Multiple year filer - doesn't provide cost at period end in sub resource")
    void multipleYearFilerDoesNotProvideCostAtPeriodEndInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();
        fixturesAndFittings.setNetBookValueAtEndOfPreviousPeriod(1L);
        fixturesAndFittings.setNetBookValueAtEndOfCurrentPeriod(1L);

        Cost cost = new Cost();
        cost.setAtPeriodStart(1L);
        fixturesAndFittings.setCost(cost);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.tangible_assets.fixtures_and_fittings.cost.at_period_end")));
    }

    @Test
    @DisplayName("Multiple year filer - doesn't provide depreciation at period start or end in sub resource")
    void multipleYearFilerDoesNotProvideDepreciationAtPeriodStartOrEndInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();
        fixturesAndFittings.setNetBookValueAtEndOfPreviousPeriod(1L);
        fixturesAndFittings.setNetBookValueAtEndOfCurrentPeriod(1L);

        Cost cost = new Cost();
        cost.setAtPeriodStart(1L);
        cost.setAtPeriodEnd(1L);
        fixturesAndFittings.setCost(cost);

        Depreciation depreciation = new Depreciation();
        depreciation.setChargeForYear(1L);
        fixturesAndFittings.setDepreciation(depreciation);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(2, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.tangible_assets.fixtures_and_fittings.depreciation.at_period_start")));
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.tangible_assets.fixtures_and_fittings.depreciation.at_period_end")));
    }

    @Test
    @DisplayName("Multiple year filer - provides only cost fields in sub resource")
    void multipleYearFilerProvidesOnlyCostFieldsInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAtPeriodStart(1L);
        fixturesAndFittings.setCost(cost);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(2, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.tangible_assets.fixtures_and_fittings.net_book_value_at_end_of_current_period")));
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.tangible_assets.fixtures_and_fittings.net_book_value_at_end_of_previous_period")));
    }

    @Test
    @DisplayName("Multiple year filer - provides only depreciation fields in sub resource")
    void multipleYearFilerProvidesOnlyDepreciationFieldsInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        Depreciation depreciation = new Depreciation();
        depreciation.setAtPeriodStart(1L);
        fixturesAndFittings.setDepreciation(depreciation);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(2, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.tangible_assets.fixtures_and_fittings.net_book_value_at_end_of_current_period")));
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.tangible_assets.fixtures_and_fittings.net_book_value_at_end_of_previous_period")));
    }

    @Test
    @DisplayName("Single year filer - cost fields don't total in sub resource")
    void singleYearFilerCostFieldsDoNotTotalInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAdditions(1L);
        cost.setAtPeriodEnd(2L);
        fixturesAndFittings.setCost(cost);
        fixturesAndFittings.setNetBookValueAtEndOfCurrentPeriod(2L);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.fixtures_and_fittings.cost.at_period_end")));
    }

    @Test
    @DisplayName("Single year filer - depreciation fields don't total in sub resource")
    void singleYearFilerDepreciationFieldsDoNotTotalInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAdditions(1L);
        cost.setAtPeriodEnd(1L);
        fixturesAndFittings.setCost(cost);

        Depreciation depreciation = new Depreciation();
        depreciation.setChargeForYear(2L);
        depreciation.setAtPeriodEnd(1L);
        fixturesAndFittings.setDepreciation(depreciation);

        fixturesAndFittings.setNetBookValueAtEndOfCurrentPeriod(0L);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.fixtures_and_fittings.depreciation.at_period_end")));
    }

    @Test
    @DisplayName("Single year filer - net book value at end of current period doesn't total in sub resource")
    void singleYearFilerCurrentNetBookValueDoesNotTotalInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAdditions(1L);
        cost.setAtPeriodEnd(1L);
        fixturesAndFittings.setCost(cost);

        fixturesAndFittings.setNetBookValueAtEndOfCurrentPeriod(2L);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.fixtures_and_fittings.net_book_value_at_end_of_current_period")));
    }

    @Test
    @DisplayName("Multiple year filer - cost fields don't total in sub resource")
    void multipleYearFilerCostFieldsDoNotTotalInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAtPeriodStart(1L);
        cost.setAdditions(1L);
        cost.setAtPeriodEnd(3L);
        fixturesAndFittings.setCost(cost);
        fixturesAndFittings.setNetBookValueAtEndOfCurrentPeriod(3L);
        fixturesAndFittings.setNetBookValueAtEndOfPreviousPeriod(1L);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.fixtures_and_fittings.cost.at_period_end")));
    }

    @Test
    @DisplayName("Multiple year filer - depreciation fields don't total in sub resource")
    void multipleYearFilerDepreciationFieldsDoNotTotalInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAtPeriodStart(1L);
        cost.setAdditions(1L);
        cost.setAtPeriodEnd(2L);
        fixturesAndFittings.setCost(cost);

        Depreciation depreciation = new Depreciation();
        depreciation.setAtPeriodStart(1L);
        depreciation.setChargeForYear(2L);
        depreciation.setAtPeriodEnd(1L);
        fixturesAndFittings.setDepreciation(depreciation);

        fixturesAndFittings.setNetBookValueAtEndOfCurrentPeriod(1L);
        fixturesAndFittings.setNetBookValueAtEndOfPreviousPeriod(0L);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.fixtures_and_fittings.depreciation.at_period_end")));
    }

    @Test
    @DisplayName("Multiple year filer - net book value at end of current period doesn't total in sub resource")
    void multipleYearFilerCurrentNetBookValueDoesNotTotalInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAtPeriodStart(1L);
        cost.setAdditions(1L);
        cost.setAtPeriodEnd(2L);
        fixturesAndFittings.setCost(cost);

        fixturesAndFittings.setNetBookValueAtEndOfCurrentPeriod(1L);
        fixturesAndFittings.setNetBookValueAtEndOfPreviousPeriod(1L);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.fixtures_and_fittings.net_book_value_at_end_of_current_period")));
    }

    @Test
    @DisplayName("Multiple year filer - net book value at end of previous period doesn't total in sub resource")
    void multipleYearFilerPreviousNetBookValueDoesNotTotalInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAtPeriodStart(1L);
        cost.setAdditions(1L);
        cost.setAtPeriodEnd(2L);
        fixturesAndFittings.setCost(cost);

        fixturesAndFittings.setNetBookValueAtEndOfCurrentPeriod(2L);
        fixturesAndFittings.setNetBookValueAtEndOfPreviousPeriod(2L);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.fixtures_and_fittings.net_book_value_at_end_of_previous_period")));
    }

    @Test
    @DisplayName("Single year filer - no total fields match")
    void singleYearFilerNoTotalFieldsMatch() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        Cost fixturesCost = new Cost();
        fixturesCost.setAdditions(1L);
        fixturesCost.setDisposals(1L);
        fixturesCost.setRevaluations(1L);
        fixturesCost.setTransfers(1L);
        fixturesCost.setAtPeriodEnd(2L);
        fixturesAndFittings.setCost(fixturesCost);

        Depreciation fixturesDepreciation = new Depreciation();
        fixturesDepreciation.setChargeForYear(1L);
        fixturesDepreciation.setOnDisposals(1L);
        fixturesDepreciation.setOtherAdjustments(1L);
        fixturesDepreciation.setAtPeriodEnd(1L);
        fixturesAndFittings.setDepreciation(fixturesDepreciation);

        fixturesAndFittings.setNetBookValueAtEndOfCurrentPeriod(1L);

        TangibleAssetsResource total = new TangibleAssetsResource();

        Cost totalCost = new Cost();
        totalCost.setAdditions(2L);
        totalCost.setDisposals(2L);
        totalCost.setRevaluations(2L);
        totalCost.setTransfers(2L);
        totalCost.setAtPeriodEnd(4L);
        total.setCost(totalCost);

        Depreciation totalDepreciation = new Depreciation();
        totalDepreciation.setChargeForYear(2L);
        totalDepreciation.setOnDisposals(2L);
        totalDepreciation.setOtherAdjustments(2L);
        totalDepreciation.setAtPeriodEnd(2L);
        total.setDepreciation(totalDepreciation);

        total.setNetBookValueAtEndOfCurrentPeriod(2L);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);
        tangibleAssets.setTotal(total);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        when(currentPeriodService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(createCurrentPeriodResponseObject(2L));

        when(previousPeriodService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(10, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.cost.additions")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.cost.disposals")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.cost.revaluations")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.cost.transfers")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.cost.at_period_end")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.depreciation.charge_for_year")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.depreciation.on_disposals")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.depreciation.other_adjustments")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.depreciation.at_period_end")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.net_book_value_at_end_of_current_period")));
    }

    @Test
    @DisplayName("Multiple year filer - no total fields match")
    void multipleYearFilerNoTotalFieldsMatch() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        Cost fixturesCost = new Cost();
        fixturesCost.setAtPeriodStart(1L);
        fixturesCost.setAdditions(1L);
        fixturesCost.setDisposals(1L);
        fixturesCost.setRevaluations(1L);
        fixturesCost.setTransfers(1L);
        fixturesCost.setAtPeriodEnd(3L);
        fixturesAndFittings.setCost(fixturesCost);

        Depreciation fixturesDepreciation = new Depreciation();
        fixturesDepreciation.setAtPeriodStart(1L);
        fixturesDepreciation.setChargeForYear(1L);
        fixturesDepreciation.setOnDisposals(1L);
        fixturesDepreciation.setOtherAdjustments(1L);
        fixturesDepreciation.setAtPeriodEnd(2L);
        fixturesAndFittings.setDepreciation(fixturesDepreciation);

        fixturesAndFittings.setNetBookValueAtEndOfCurrentPeriod(1L);
        fixturesAndFittings.setNetBookValueAtEndOfPreviousPeriod(0L);

        TangibleAssetsResource total = new TangibleAssetsResource();

        Cost totalCost = new Cost();
        totalCost.setAtPeriodStart(3L);
        totalCost.setAdditions(2L);
        totalCost.setDisposals(2L);
        totalCost.setRevaluations(2L);
        totalCost.setTransfers(2L);
        totalCost.setAtPeriodEnd(7L);
        total.setCost(totalCost);

        Depreciation totalDepreciation = new Depreciation();
        totalDepreciation.setAtPeriodStart(2L);
        totalDepreciation.setChargeForYear(2L);
        totalDepreciation.setOnDisposals(2L);
        totalDepreciation.setOtherAdjustments(2L);
        totalDepreciation.setAtPeriodEnd(4L);
        total.setDepreciation(totalDepreciation);

        total.setNetBookValueAtEndOfCurrentPeriod(3L);
        total.setNetBookValueAtEndOfPreviousPeriod(1L);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);
        tangibleAssets.setTotal(total);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        when(currentPeriodService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(createCurrentPeriodResponseObject(3L));

        when(previousPeriodService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(createPreviousPeriodResponseObject(1L));

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(13, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.cost.at_period_start")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.cost.additions")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.cost.disposals")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.cost.revaluations")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.cost.transfers")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.cost.at_period_end")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.depreciation.at_period_start")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.depreciation.charge_for_year")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.depreciation.on_disposals")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.depreciation.other_adjustments")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.depreciation.at_period_end")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.net_book_value_at_end_of_current_period")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.tangible_assets.total.net_book_value_at_end_of_previous_period")));
    }

    @Test
    @DisplayName("Single year filer - no current period to validate against")
    void singleYearFilerWithoutCurrentPeriod() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(createValidSubResource(false));
        tangibleAssets.setLandAndBuildings(createValidSubResource(false));
        tangibleAssets.setMotorVehicles(createValidSubResource(false));
        tangibleAssets.setOfficeEquipment(createValidSubResource(false));
        tangibleAssets.setPlantAndMachinery(createValidSubResource(false));

        TangibleAssetsResource total = new TangibleAssetsResource();

        Cost totalCost = new Cost();
        totalCost.setAdditions(5L);
        totalCost.setDisposals(5L);
        totalCost.setRevaluations(5L);
        totalCost.setTransfers(5L);
        totalCost.setAtPeriodEnd(10L);
        total.setCost(totalCost);

        Depreciation totalDepreciation = new Depreciation();
        totalDepreciation.setChargeForYear(5L);
        totalDepreciation.setOnDisposals(5L);
        totalDepreciation.setOtherAdjustments(5L);
        totalDepreciation.setAtPeriodEnd(5L);
        total.setDepreciation(totalDepreciation);

        total.setNetBookValueAtEndOfCurrentPeriod(5L);

        tangibleAssets.setTotal(total);

        when(currentPeriodService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));

        when(previousPeriodService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));

        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_KEY, CURRENT_BALANCE_SHEET_NOT_EQUAL);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(CURRENT_BALANCE_SHEET_NOT_EQUAL, "$.tangible_assets.total.net_book_value_at_end_of_current_period")));
    }

    @Test
    @DisplayName("Single year filer - current period does not match note")
    void singleYearFilerCurrentPeriodDoesNotMatchNote() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(createValidSubResource(false));
        tangibleAssets.setLandAndBuildings(createValidSubResource(false));
        tangibleAssets.setMotorVehicles(createValidSubResource(false));
        tangibleAssets.setOfficeEquipment(createValidSubResource(false));
        tangibleAssets.setPlantAndMachinery(createValidSubResource(false));

        TangibleAssetsResource total = new TangibleAssetsResource();

        Cost totalCost = new Cost();
        totalCost.setAdditions(5L);
        totalCost.setDisposals(5L);
        totalCost.setRevaluations(5L);
        totalCost.setTransfers(5L);
        totalCost.setAtPeriodEnd(10L);
        total.setCost(totalCost);

        Depreciation totalDepreciation = new Depreciation();
        totalDepreciation.setChargeForYear(5L);
        totalDepreciation.setOnDisposals(5L);
        totalDepreciation.setOtherAdjustments(5L);
        totalDepreciation.setAtPeriodEnd(5L);
        total.setDepreciation(totalDepreciation);

        total.setNetBookValueAtEndOfCurrentPeriod(5L);

        tangibleAssets.setTotal(total);

        when(currentPeriodService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(createCurrentPeriodResponseObject(100L));

        when(previousPeriodService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));

        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_KEY, CURRENT_BALANCE_SHEET_NOT_EQUAL);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(CURRENT_BALANCE_SHEET_NOT_EQUAL, "$.tangible_assets.total.net_book_value_at_end_of_current_period")));
    }

    @Test
    @DisplayName("Multiple year filer - no current or previous period to validate against")
    void multipleYearFilerWithoutCurrentOrPreviousPeriod() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(createValidSubResource(true));
        tangibleAssets.setLandAndBuildings(createValidSubResource(true));
        tangibleAssets.setMotorVehicles(createValidSubResource(true));
        tangibleAssets.setOfficeEquipment(createValidSubResource(true));
        tangibleAssets.setPlantAndMachinery(createValidSubResource(true));

        TangibleAssetsResource total = new TangibleAssetsResource();

        Cost totalCost = new Cost();
        totalCost.setAtPeriodStart(5L);
        totalCost.setAdditions(5L);
        totalCost.setDisposals(5L);
        totalCost.setRevaluations(5L);
        totalCost.setTransfers(5L);
        totalCost.setAtPeriodEnd(15L);
        total.setCost(totalCost);

        Depreciation totalDepreciation = new Depreciation();
        totalDepreciation.setAtPeriodStart(5L);
        totalDepreciation.setChargeForYear(5L);
        totalDepreciation.setOnDisposals(5L);
        totalDepreciation.setOtherAdjustments(5L);
        totalDepreciation.setAtPeriodEnd(10L);
        total.setDepreciation(totalDepreciation);

        total.setNetBookValueAtEndOfCurrentPeriod(5L);
        total.setNetBookValueAtEndOfPreviousPeriod(0L);

        tangibleAssets.setTotal(total);

        when(currentPeriodService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));

        when(previousPeriodService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));

        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_KEY, CURRENT_BALANCE_SHEET_NOT_EQUAL);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_KEY, PREVIOUS_BALANCE_SHEET_NOT_EQUAL);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(2, errors.getErrorCount());
        assertTrue(errors.containsError(createError(CURRENT_BALANCE_SHEET_NOT_EQUAL, "$.tangible_assets.total.net_book_value_at_end_of_current_period")));
        assertTrue(errors.containsError(createError(PREVIOUS_BALANCE_SHEET_NOT_EQUAL, "$.tangible_assets.total.net_book_value_at_end_of_previous_period")));
    }

    @Test
    @DisplayName("Multiple year filer - neither current nor previous period match note")
    void multipleYearFilerCurrentAndPreviousPeriodDoNotMatchNote() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(createValidSubResource(true));
        tangibleAssets.setLandAndBuildings(createValidSubResource(true));
        tangibleAssets.setMotorVehicles(createValidSubResource(true));
        tangibleAssets.setOfficeEquipment(createValidSubResource(true));
        tangibleAssets.setPlantAndMachinery(createValidSubResource(true));

        TangibleAssetsResource total = new TangibleAssetsResource();

        Cost totalCost = new Cost();
        totalCost.setAtPeriodStart(5L);
        totalCost.setAdditions(5L);
        totalCost.setDisposals(5L);
        totalCost.setRevaluations(5L);
        totalCost.setTransfers(5L);
        totalCost.setAtPeriodEnd(15L);
        total.setCost(totalCost);

        Depreciation totalDepreciation = new Depreciation();
        totalDepreciation.setAtPeriodStart(5L);
        totalDepreciation.setChargeForYear(5L);
        totalDepreciation.setOnDisposals(5L);
        totalDepreciation.setOtherAdjustments(5L);
        totalDepreciation.setAtPeriodEnd(10L);
        total.setDepreciation(totalDepreciation);

        total.setNetBookValueAtEndOfCurrentPeriod(5L);
        total.setNetBookValueAtEndOfPreviousPeriod(0L);

        tangibleAssets.setTotal(total);

        when(currentPeriodService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(createCurrentPeriodResponseObject(100L));

        when(previousPeriodService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(createPreviousPeriodResponseObject(100L));

        ReflectionTestUtils.setField(validator, CURRENT_BALANCE_SHEET_NOT_EQUAL_KEY, CURRENT_BALANCE_SHEET_NOT_EQUAL);
        ReflectionTestUtils.setField(validator, PREVIOUS_BALANCE_SHEET_NOT_EQUAL_KEY, PREVIOUS_BALANCE_SHEET_NOT_EQUAL);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(2, errors.getErrorCount());
        assertTrue(errors.containsError(createError(CURRENT_BALANCE_SHEET_NOT_EQUAL, "$.tangible_assets.total.net_book_value_at_end_of_current_period")));
        assertTrue(errors.containsError(createError(PREVIOUS_BALANCE_SHEET_NOT_EQUAL, "$.tangible_assets.total.net_book_value_at_end_of_previous_period")));
    }

    @Test
    @DisplayName("Single year filer - valid submission")
    void singleYearFilerValidSubmission() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(createValidSubResource(false));
        tangibleAssets.setLandAndBuildings(createValidSubResource(false));
        tangibleAssets.setMotorVehicles(createValidSubResource(false));
        tangibleAssets.setOfficeEquipment(createValidSubResource(false));
        tangibleAssets.setPlantAndMachinery(createValidSubResource(false));

        TangibleAssetsResource total = new TangibleAssetsResource();

        Cost totalCost = new Cost();
        totalCost.setAdditions(5L);
        totalCost.setDisposals(5L);
        totalCost.setRevaluations(5L);
        totalCost.setTransfers(5L);
        totalCost.setAtPeriodEnd(10L);
        total.setCost(totalCost);

        Depreciation totalDepreciation = new Depreciation();
        totalDepreciation.setChargeForYear(5L);
        totalDepreciation.setOnDisposals(5L);
        totalDepreciation.setOtherAdjustments(5L);
        totalDepreciation.setAtPeriodEnd(5L);
        total.setDepreciation(totalDepreciation);

        total.setNetBookValueAtEndOfCurrentPeriod(5L);

        tangibleAssets.setTotal(total);

        when(currentPeriodService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(createCurrentPeriodResponseObject(5L));

        when(previousPeriodService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(new ResponseObject<>(ResponseStatus.NOT_FOUND));

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertFalse(errors.hasErrors());
    }

    @Test
    @DisplayName("Multiple year filer - valid submission")
    void multipleYearFilerValidSubmission() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(createValidSubResource(true));
        tangibleAssets.setLandAndBuildings(createValidSubResource(true));
        tangibleAssets.setMotorVehicles(createValidSubResource(true));
        tangibleAssets.setOfficeEquipment(createValidSubResource(true));
        tangibleAssets.setPlantAndMachinery(createValidSubResource(true));

        TangibleAssetsResource total = new TangibleAssetsResource();

        Cost totalCost = new Cost();
        totalCost.setAtPeriodStart(5L);
        totalCost.setAdditions(5L);
        totalCost.setDisposals(5L);
        totalCost.setRevaluations(5L);
        totalCost.setTransfers(5L);
        totalCost.setAtPeriodEnd(15L);
        total.setCost(totalCost);

        Depreciation totalDepreciation = new Depreciation();
        totalDepreciation.setAtPeriodStart(5L);
        totalDepreciation.setChargeForYear(5L);
        totalDepreciation.setOnDisposals(5L);
        totalDepreciation.setOtherAdjustments(5L);
        totalDepreciation.setAtPeriodEnd(10L);
        total.setDepreciation(totalDepreciation);

        total.setNetBookValueAtEndOfCurrentPeriod(5L);
        total.setNetBookValueAtEndOfPreviousPeriod(0L);

        tangibleAssets.setTotal(total);

        when(currentPeriodService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(createCurrentPeriodResponseObject(5L));

        when(previousPeriodService.find(COMPANY_ACCOUNTS_ID, request))
                .thenReturn(createPreviousPeriodResponseObject(0L));

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertFalse(errors.hasErrors());
    }

    private TangibleAssetsResource createValidSubResource(boolean isMultipleYearFiler) {

        TangibleAssetsResource resource = new TangibleAssetsResource();

        Cost cost = new Cost();
        if (isMultipleYearFiler) {
            cost.setAtPeriodStart(1L);
        }
        cost.setAdditions(1L);
        cost.setDisposals(1L);
        cost.setRevaluations(1L);
        cost.setTransfers(1L);
        cost.setAtPeriodEnd(isMultipleYearFiler ? 3L : 2L);
        resource.setCost(cost);

        Depreciation depreciation = new Depreciation();
        if (isMultipleYearFiler) {
            depreciation.setAtPeriodStart(1L);
        }
        depreciation.setChargeForYear(1L);
        depreciation.setOnDisposals(1L);
        depreciation.setOtherAdjustments(1L);
        depreciation.setAtPeriodEnd(isMultipleYearFiler ? 2L : 1L);
        resource.setDepreciation(depreciation);

        resource.setNetBookValueAtEndOfCurrentPeriod(1L);
        if (isMultipleYearFiler) {
            resource.setNetBookValueAtEndOfPreviousPeriod(0L);
        }

        return resource;
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }

    private ResponseObject<CurrentPeriod> createCurrentPeriodResponseObject(Long currentPeriodTangible) {

        FixedAssets currentFixedAssets = new FixedAssets();
        currentFixedAssets.setTangible(currentPeriodTangible);

        BalanceSheet currentBalanceSheet = new BalanceSheet();
        currentBalanceSheet.setFixedAssets(currentFixedAssets);

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setBalanceSheet(currentBalanceSheet);

        return new ResponseObject<>(ResponseStatus.FOUND, currentPeriod);
    }

    private ResponseObject<PreviousPeriod> createPreviousPeriodResponseObject(Long previousPeriodTangible) {

        FixedAssets previousFixedAssets = new FixedAssets();
        previousFixedAssets.setTangible(previousPeriodTangible);

        BalanceSheet previousBalanceSheet = new BalanceSheet();
        previousBalanceSheet.setFixedAssets(previousFixedAssets);

        PreviousPeriod previousPeriod = new PreviousPeriod();
        previousPeriod.setBalanceSheet(previousBalanceSheet);

        return new ResponseObject<>(ResponseStatus.FOUND, previousPeriod);
    }
}
