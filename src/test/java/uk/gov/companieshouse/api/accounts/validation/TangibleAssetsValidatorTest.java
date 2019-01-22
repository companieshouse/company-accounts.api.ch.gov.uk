package uk.gov.companieshouse.api.accounts.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.Cost;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.Depreciation;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.TangibleAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.TangibleAssetsResource;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TangibleAssetsValidatorTest {

    @Mock
    private Transaction transaction;

    @Mock
    private HttpServletRequest request;

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private TangibleAssetsValidator validator;

    private static final String INCONSISTENT_DATA_KEY = "inconsistentData";
    private static final String INCONSISTENT_DATA = "inconsistent_data";

    private static final String INVALID_NOTE_KEY = "invalidNote";
    private static final String INVALID_NOTE = "invalid_note";

    private static final String VALUE_REQUIRED_KEY = "valueRequired";
    private static final String VALUE_REQUIRED = "value_required";

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

        ReflectionTestUtils.setField(validator, INCONSISTENT_DATA_KEY, INCONSISTENT_DATA);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, "", request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCONSISTENT_DATA, "$.tangible_assets.fixtures_and_fittings.cost.at_period_start")));
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

        ReflectionTestUtils.setField(validator, INCONSISTENT_DATA_KEY, INCONSISTENT_DATA);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, "", request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCONSISTENT_DATA, "$.tangible_assets.fixtures_and_fittings.depreciation.at_period_start")));
    }

    @Test
    @DisplayName("First year filer - provides net book value at previous period end in sub resource")
    void firstYearFilerProvidesNetBookValueAtPreviousPeriodEndInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();

        fixturesAndFittings.setNetBookValueAtEndOfPreviousPeriod(1L);

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setFixturesAndFittings(fixturesAndFittings);

        ReflectionTestUtils.setField(validator, INCONSISTENT_DATA_KEY, INCONSISTENT_DATA);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, "", request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCONSISTENT_DATA, "$.tangible_assets.fixtures_and_fittings.net_book_value_at_end_of_previous_period")));
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

        ReflectionTestUtils.setField(validator, INVALID_NOTE_KEY, INVALID_NOTE);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, "", request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INVALID_NOTE, "$.tangible_assets.fixtures_and_fittings")));
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

        ReflectionTestUtils.setField(validator, INVALID_NOTE_KEY, INVALID_NOTE);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, "", request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INVALID_NOTE, "$.tangible_assets.fixtures_and_fittings")));
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

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, "", request);

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

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, "", request);

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

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, "", request);

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

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, "", request);

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

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, "", request);

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

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, "", request);

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

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, "", request);

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

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, "", request);

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

        ReflectionTestUtils.setField(validator, INVALID_NOTE_KEY, INVALID_NOTE);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, "", request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INVALID_NOTE, "$.tangible_assets.fixtures_and_fittings")));
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

        ReflectionTestUtils.setField(validator, INVALID_NOTE_KEY, INVALID_NOTE);

        Errors errors = validator.validateTangibleAssets(tangibleAssets, transaction, "", request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INVALID_NOTE, "$.tangible_assets.fixtures_and_fittings")));
    }

    private Error createError(String error, String path) {
        return new Error(error, path, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType());
    }
}
