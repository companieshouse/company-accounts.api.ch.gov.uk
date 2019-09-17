package uk.gov.companieshouse.api.accounts.validation;

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
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.Amortisation;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.Cost;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.IntangibleAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.IntangibleAssetsResource;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
        cost.setAtPeriodEnd(1L);

        goodwill.setCost(cost);
        goodwill.setNetBookValueAtEndOfCurrentPeriod(1L);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_KEY, UNEXPECTED_DATA);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA, "$.intangible_assets.goodwill.cost.at_period_start")));
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
    @DisplayName("Single year filer - amortisation fields don't total in sub resource")
    void singleYearFilerAmortisationFieldsDoNotTotalInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAdditions(1L);
        cost.setAtPeriodEnd(1L);
        goodwill.setCost(cost);

        Amortisation amortisation = new Amortisation();
        amortisation.setChargeForYear(2L);
        amortisation.setAtPeriodEnd(1L);
        goodwill.setAmortisation(amortisation);

        goodwill.setNetBookValueAtEndOfCurrentPeriod(0L);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.goodwill.amortisation.at_period_end")));
    }

    @Test
    @DisplayName("First year filer - Does not provide cost at period end in sub resource")
    void firstYearFilerDoesNotProvideCostAtPeriodEndInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();

        goodwill.setCost(new Cost());

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.intangible_assets.goodwill.cost.at_period_end")));
    }

    @Test
    @DisplayName("Multiple year filer - provides only additional info in note")
    void multipleYearFilerProvidesOnlyAdditionalInfo() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setAdditionalInformation("additionalInfo");

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(2, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.intangible_assets.total.net_book_value_at_end_of_current_period")));
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.intangible_assets.total.net_book_value_at_end_of_previous_period")));

    }

    @Test
    @DisplayName("Multiple year filer - doesn't provide cost in sub resource")
    void multipleYearFilerDoesNotProvideCostInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();

        Amortisation amortisation = new Amortisation();

        amortisation.setAtPeriodEnd(1L);
        amortisation.setAtPeriodStart(1L);
        goodwill.setAmortisation(amortisation);

        goodwill.setNetBookValueAtEndOfCurrentPeriod(1L);
        goodwill.setNetBookValueAtEndOfPreviousPeriod(1L);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(2, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.intangible_assets.goodwill.cost.at_period_start")));
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.intangible_assets.goodwill.cost.at_period_end")));
    }

    @Test
    @DisplayName("Multi year filer - doesn't provide cost at period start in sub resource")
    void multiYearFilerDoesNotProvideCostAtPeriodStartInSubResource() throws ServiceException, DataException{

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();
        goodwill.setNetBookValueAtEndOfPreviousPeriod(1L);
        goodwill.setNetBookValueAtEndOfCurrentPeriod(1L);

        Cost cost = new Cost();
        cost.setAtPeriodEnd(1L);

        Amortisation amortisation = new Amortisation();
        amortisation.setAtPeriodEnd(1L);
        amortisation.setAtPeriodStart(1L);

        goodwill.setCost(cost);
        goodwill.setAmortisation(amortisation);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.intangible_assets.goodwill.cost.at_period_start")));
    }

    @Test
    @DisplayName("Multi year filer - doesn't provide cost at period end in sub resource")
    void multiYearFilerDoesNotProvideCostAtPeriodEndInSubResource() throws ServiceException, DataException{

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();
        goodwill.setNetBookValueAtEndOfCurrentPeriod(1L);
        goodwill.setNetBookValueAtEndOfPreviousPeriod(1L);

        Cost cost = new Cost();
        cost.setAtPeriodStart(1L);
        goodwill.setCost(cost);

        Amortisation amortisation = new Amortisation();
        amortisation.setAtPeriodEnd(1L);
        amortisation.setAtPeriodStart(1L);
        goodwill.setAmortisation(amortisation);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.intangible_assets.goodwill.cost.at_period_end")));

    }

    @Test
    @DisplayName("Multiple year filer - cost fields do not total in sub resource")
    void multipleYearFilerCostFieldDoNotTotalInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAtPeriodStart(1L);
        cost.setAdditions(1L);
        cost.setAtPeriodEnd(3L);
        goodwill.setCost(cost);

        Amortisation amortisation = new Amortisation();
        amortisation.setAtPeriodEnd(1L);
        amortisation.setAtPeriodStart(1L);
        goodwill.setAmortisation(amortisation);

        goodwill.setNetBookValueAtEndOfCurrentPeriod(2L);
        goodwill.setNetBookValueAtEndOfPreviousPeriod(1L);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.goodwill.cost.at_period_end")));

    }

    @Test
    @DisplayName("Multiple year filer - amortisation fields don't total in sub resource")
    void multipleYearFilerAmortisationFieldsDoNotTotalInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAtPeriodStart(1L);
        cost.setAdditions(1L);
        cost.setAtPeriodEnd(2L);
        goodwill.setCost(cost);

        Amortisation amortisation = new Amortisation();
        amortisation.setAtPeriodStart(1L);
        amortisation.setChargeForYear(2L);
        amortisation.setAtPeriodEnd(1L);
        goodwill.setAmortisation(amortisation);

        goodwill.setNetBookValueAtEndOfCurrentPeriod(1L);
        goodwill.setNetBookValueAtEndOfPreviousPeriod(0L);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);
        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.goodwill.amortisation.at_period_end")));
    }

    @Test
    @DisplayName("Single year filer - no total fields match")
    void singleYearFilerNoTotalFieldsMatch() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();

        Cost goodwillCost = new Cost();
        goodwillCost.setAdditions(1L);
        goodwillCost.setDisposals(1L);
        goodwillCost.setRevaluations(1L);
        goodwillCost.setTransfers(1L);
        goodwillCost.setAtPeriodEnd(2L);
        goodwill.setCost(goodwillCost);

        Amortisation goodwillAmortisation = new Amortisation();
        goodwillAmortisation.setChargeForYear(1L);
        goodwillAmortisation.setOnDisposals(1L);
        goodwillAmortisation.setOtherAdjustments(1L);
        goodwillAmortisation.setAtPeriodEnd(1L);
        goodwill.setAmortisation(goodwillAmortisation);

        goodwill.setNetBookValueAtEndOfCurrentPeriod(1L);

        IntangibleAssetsResource total = new IntangibleAssetsResource();

        Cost totalCost = new Cost();
        totalCost.setAdditions(2L);
        totalCost.setDisposals(2L);
        totalCost.setRevaluations(2L);
        totalCost.setTransfers(2L);
        totalCost.setAtPeriodEnd(4L);
        total.setCost(totalCost);

        Amortisation totalAmortisation = new Amortisation();
        totalAmortisation.setChargeForYear(2L);
        totalAmortisation.setOnDisposals(2L);
        totalAmortisation.setOtherAdjustments(2L);
        totalAmortisation.setAtPeriodEnd(2L);
        total.setAmortisation(totalAmortisation);

        total.setNetBookValueAtEndOfCurrentPeriod(1L);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);
        intangibleAssets.setTotal(total);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(9, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.cost.additions")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.cost.disposals")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.cost.revaluations")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.cost.transfers")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.cost.at_period_end")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.amortisation.charge_for_year")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.amortisation.on_disposals")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.amortisation.other_adjustments")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.amortisation.at_period_end")));
    }

    @Test
    @DisplayName("Multiple year filer - no total fields match")
    void multipleYearFilerNoTotalFieldsMatch() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();

        Amortisation amortisation = new Amortisation();
        amortisation.setAtPeriodEnd(1L);
        amortisation.setAtPeriodStart(1L);


        Cost goodwillCost = new Cost();
        goodwillCost.setAtPeriodStart(1L);
        goodwillCost.setAdditions(1L);
        goodwillCost.setDisposals(1L);
        goodwillCost.setRevaluations(1L);
        goodwillCost.setTransfers(1L);
        goodwillCost.setAtPeriodEnd(3L);
        goodwill.setCost(goodwillCost);
        goodwill.setAmortisation(amortisation);

        Amortisation goodwillAmortisation = new Amortisation();
        goodwillAmortisation.setAtPeriodStart(1L);
        goodwillAmortisation.setChargeForYear(1L);
        goodwillAmortisation.setOnDisposals(1L);
        goodwillAmortisation.setOtherAdjustments(1L);
        goodwillAmortisation.setAtPeriodEnd(2L);
        goodwill.setAmortisation(goodwillAmortisation);

        goodwill.setNetBookValueAtEndOfCurrentPeriod(1L);

        IntangibleAssetsResource total = new IntangibleAssetsResource();

        Cost totalCost = new Cost();
        totalCost.setAtPeriodStart(3L);
        totalCost.setAdditions(2L);
        totalCost.setDisposals(2L);
        totalCost.setRevaluations(2L);
        totalCost.setTransfers(2L);
        totalCost.setAtPeriodEnd(7L);
        total.setCost(totalCost);
        total.setAmortisation(amortisation);

        Amortisation totalAmortisation = new Amortisation();
        totalAmortisation.setAtPeriodStart(3L);
        totalAmortisation.setChargeForYear(2L);
        totalAmortisation.setOnDisposals(2L);
        totalAmortisation.setOtherAdjustments(2L);
        totalAmortisation.setAtPeriodEnd(4L);
        total.setAmortisation(totalAmortisation);

        total.setNetBookValueAtEndOfCurrentPeriod(1L);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);
        intangibleAssets.setTotal(total);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(11, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.cost.at_period_start")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.cost.additions")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.cost.disposals")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.cost.revaluations")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.cost.transfers")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.cost.at_period_end")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.amortisation.at_period_start")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.amortisation.charge_for_year")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.amortisation.on_disposals")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.amortisation.other_adjustments")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.total.amortisation.at_period_end")));
    }

    @Test
    @DisplayName("First year filer - provides only amortisation fields in sub resource")
    void firstYearFilerProvidesOnlyAmortisationFieldInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();

        Amortisation amortisation = new Amortisation();
        amortisation.setChargeForYear(1L);

        goodwill.setAmortisation(amortisation);
        goodwill.setNetBookValueAtEndOfCurrentPeriod(1L);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);
        assertEquals(2, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.intangible_assets.goodwill.cost.at_period_end")));
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.intangible_assets.goodwill.amortisation.at_period_end")));
    }

    @Test
    @DisplayName("First year filer - incorrect total for amortisation at period end")
    void firstYearFilerIncorrectTotalAmortisationAtPeriodEnd() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAdditions(9L);
        cost.setAtPeriodEnd(9L);

        Amortisation amortisation = new Amortisation();
        amortisation.setChargeForYear(1L);
        amortisation.setOnDisposals(1L);
        amortisation.setOtherAdjustments(1L);

        amortisation.setAtPeriodEnd(7L);

        goodwill.setCost(cost);
        goodwill.setAmortisation(amortisation);
        goodwill.setNetBookValueAtEndOfCurrentPeriod(2L);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.goodwill.amortisation.at_period_end")));
    }

    @Test
    @DisplayName("First year filer - provides amortisation at period start in sub resource")
    void firstYearFilerProvidesAmortisationAtPeriodStartInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAdditions(9L);
        cost.setAtPeriodEnd(9L);

        Amortisation amortisation = new Amortisation();
        amortisation.setAtPeriodStart(1L);
        amortisation.setAtPeriodEnd(1L);

        goodwill.setCost(cost);
        goodwill.setAmortisation(amortisation);

        goodwill.setNetBookValueAtEndOfCurrentPeriod(7L);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_KEY, UNEXPECTED_DATA);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA, "$.intangible_assets.goodwill.amortisation.at_period_start")));
    }
    private Error createError(String error,  String path) {

        return new Error(error, path, LocationType.JSON_PATH.getValue(),
        ErrorType.VALIDATION.getType());
    }

    @Test
    @DisplayName("Multiple year filer - Does not provide amortisation at period start in sub resource")
    void multipleYearFilerDoesNotProvideAmortisationAtPeriodStartInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAtPeriodStart(1L);
        cost.setAtPeriodEnd(1L);

       Amortisation amortisation = new Amortisation();
       amortisation.setAtPeriodEnd(1L);

        goodwill.setCost(cost);
        goodwill.setAmortisation(amortisation);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.intangible_assets.goodwill.amortisation.at_period_start")));
    }

    @Test
    @DisplayName("Multiple year filer - Does not provide amortisation at period end in sub resource")
    void multipleYearFilerDoesNotProvideAmortisationAtPeriodEndInSubResource() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAtPeriodStart(1L);
        cost.setAtPeriodEnd(1L);

        goodwill.setCost(cost);

        Amortisation amortisation = new Amortisation();
        amortisation.setAtPeriodStart(1L);

        goodwill.setAmortisation(amortisation);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, VALUE_REQUIRED_KEY, VALUE_REQUIRED);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(VALUE_REQUIRED, "$.intangible_assets.goodwill.amortisation.at_period_end")));
    }

    @Test
    @DisplayName("Multiple year filer - Total does not match for sub resources")
    void multipleYearFilerTotalDoesNotMatchForSubResources() throws ServiceException, DataException {

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(true);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();

        Cost cost = new Cost();
        cost.setAtPeriodStart(1L);
        cost.setAdditions(1L);
        cost.setAtPeriodEnd(2L);
        goodwill.setCost(cost);

        Amortisation amortisation = new Amortisation();
        amortisation.setOnDisposals(1L);
        amortisation.setOtherAdjustments(1L);
        amortisation.setChargeForYear(1L);
        amortisation.setAtPeriodEnd(5L);
        amortisation.setAtPeriodStart(1L);
        goodwill.setAmortisation(amortisation);

        goodwill.setNetBookValueAtEndOfCurrentPeriod(5L);
        goodwill.setNetBookValueAtEndOfPreviousPeriod(1L);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, INCORRECT_TOTAL_KEY, INCORRECT_TOTAL);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(2, errors.getErrorCount());
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.goodwill.amortisation.at_period_end")));
        assertTrue(errors.containsError(createError(INCORRECT_TOTAL, "$.intangible_assets.goodwill.net_book_value_at_end_of_current_period")));
    }

    @Test
    @DisplayName("First year filer - provides net book value at previous period end in sub resource")
    void firstYearFilerProvidesNetBookValueAtPreviousEndInSubResource() throws ServiceException, DataException{

        when(companyService.isMultipleYearFiler(any(Transaction.class))).thenReturn(false);

        IntangibleAssetsResource goodwill = new IntangibleAssetsResource();

        goodwill.setNetBookValueAtEndOfPreviousPeriod(1L);

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setGoodwill(goodwill);

        ReflectionTestUtils.setField(validator, UNEXPECTED_DATA_KEY, UNEXPECTED_DATA);

        Errors errors = validator.validateIntangibleAssets(intangibleAssets, transaction, COMPANY_ACCOUNTS_ID, request);

        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(createError(UNEXPECTED_DATA, "$.intangible_assets.goodwill.net_book_value_at_end_of_previous_period")));
    }

    @Test
    @DisplayName("Single year filer - net book value at end of current period doesn't total in sub resource")
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
}