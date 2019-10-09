package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.Amortisation;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.IntangibleAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.IntangibleAssetsResource;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.Cost;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class IntangibleAssetsValidator  extends BaseValidator implements CrossValidator<IntangibleAssets>   {

    private CompanyService companyService;

    private CurrentPeriodService currentPeriodService;

    private PreviousPeriodService previousPeriodService;

    @Autowired
    public IntangibleAssetsValidator(CompanyService companyService, CurrentPeriodService currentPeriodService, PreviousPeriodService previousPeriodService) {

        this.companyService = companyService;
        this.currentPeriodService = currentPeriodService;
        this.previousPeriodService = previousPeriodService;

    }

    @Value("${incorrect.total}")
    private String incorrectTotal;

    @Value("${value.required}")
    private String valueRequired;

    private static final String INTANGIBLE_NOTE = "$.intangible_assets";
    private static final String COST_AT_PERIOD_START = ".cost.at_period_start";
    private static final String ADDITIONS = ".cost.additions";
    private static final String DISPOSALS = ".cost.disposals";
    private static final String REVALUATIONS = ".cost.revaluations";
    private static final String TRANSFERS = ".cost.transfers";
    private static final String COST_AT_PERIOD_END = ".cost.at_period_end";
    private static final String CHARGE_FOR_YEAR = ".amortisation.charge_for_year";
    private static final String ON_DISPOSALS = ".amortisation.on_disposals";
    private static final String OTHER_ADJUSTMENTS = ".amortisation.other_adjustments";
    private static final String NET_BOOK_VALUE_CURRENT_PERIOD = ".net_book_value_at_end_of_current_period";
    private static final String NET_BOOK_VALUE_PREVIOUS_PERIOD = ".net_book_value_at_end_of_previous_period";
    private static final String AMORTISATION_AT_PERIOD_END = ".amortisation.at_period_end";
    private static final String AMORTISATION_AT_PERIOD_START = ".amortisation.at_period_start";

    public Errors validateIntangibleAssets(IntangibleAssets intangibleAssets, Transaction transaction, String companyAccountsId, HttpServletRequest request)
    throws DataException {
        Errors errors = new Errors();

        try {
            boolean isMultipleYearFiler = companyService.isMultipleYearFiler(transaction);

            List<IntangibleSubResource> invalidSubResources = new ArrayList<>();

            verifySubResourcesAreValid(intangibleAssets, errors, isMultipleYearFiler, invalidSubResources);
            verifyNoteNotEmpty(intangibleAssets, errors, isMultipleYearFiler);
            validateSubResourceTotals(intangibleAssets, errors, isMultipleYearFiler, invalidSubResources);
            if (errors.hasErrors()) {
                return errors;
            }
            validateTotalFieldsMatch(errors, intangibleAssets, isMultipleYearFiler);
            crossValidate(intangibleAssets, request, companyAccountsId, errors);

        } catch (ServiceException se) {

            throw new DataException(se.getMessage(), se);
        }
        return errors;
    }

    private void verifyNoteNotEmpty(IntangibleAssets intangibleAssets, Errors errors, boolean isMultipleYearFiler) {

        if (intangibleAssets.getGoodwill() == null &&
                intangibleAssets.getOtherIntangibleAssets() == null &&
                intangibleAssets.getTotal() == null) {

            addError(errors, valueRequired, getJsonPath(IntangibleSubResource.TOTAL, NET_BOOK_VALUE_CURRENT_PERIOD));

            if (isMultipleYearFiler) {
                addError(errors, valueRequired, getJsonPath(IntangibleSubResource.TOTAL, NET_BOOK_VALUE_PREVIOUS_PERIOD));
            }
        }
    }

    private void verifySubResourcesAreValid(IntangibleAssets intangibleAssets, Errors errors, boolean isMultipleYearFiler, List<IntangibleSubResource> invalidSubResources) {

        validateSubResource(intangibleAssets.getGoodwill(), errors,  isMultipleYearFiler, IntangibleSubResource.GOODWILL, invalidSubResources);
        validateSubResource(intangibleAssets.getOtherIntangibleAssets(), errors, isMultipleYearFiler, IntangibleSubResource.OTHER_INTANGIBLE_ASSETS, invalidSubResources);
        validateSubResource(intangibleAssets.getTotal(), errors, isMultipleYearFiler, IntangibleSubResource.TOTAL, invalidSubResources);
    }

    private void validateSubResource(IntangibleAssetsResource intangibleAssetsResource, Errors errors, boolean isMultipleYearFiler, IntangibleSubResource intangibleSubResource, List<IntangibleSubResource> invalidSubResource ) {

        if(intangibleAssetsResource != null) {

            if(isMultipleYearFiler) {
                validatePresenceOfMultipleYearFilerFields(intangibleAssetsResource, errors, intangibleSubResource, invalidSubResource);
            } else {
                validatePresenceOfSingleYearFilerFields(intangibleAssetsResource, errors, intangibleSubResource, invalidSubResource);
            }
        }
    }

    private void validateSubResourceTotals(IntangibleAssets intangibleAssets, Errors errors, boolean isMultipleYearFiler, List<IntangibleSubResource> invalidSubResources) {

        if(intangibleAssets.getGoodwill() != null && !invalidSubResources.contains(IntangibleSubResource.GOODWILL)) {

            validateSubResourceTotal(intangibleAssets.getGoodwill(), errors, isMultipleYearFiler, IntangibleSubResource.GOODWILL);
        }

        if(intangibleAssets.getOtherIntangibleAssets() != null && !invalidSubResources.contains(IntangibleSubResource.OTHER_INTANGIBLE_ASSETS)) {

            validateSubResourceTotal(intangibleAssets.getOtherIntangibleAssets(), errors, isMultipleYearFiler, IntangibleSubResource.OTHER_INTANGIBLE_ASSETS);
        }

    }

    private void validateSubResourceTotal(IntangibleAssetsResource intangibleAssetsResource, Errors errors, boolean isMultipleYearFiler, IntangibleSubResource subResource) {

        validateCosts(intangibleAssetsResource, errors, subResource);
        validateAmortisation(intangibleAssetsResource, errors, subResource);
        validateCurrentNetBookValue(errors, intangibleAssetsResource, subResource);
        if(isMultipleYearFiler) {
            validatePreviousNetBookValue(errors, intangibleAssetsResource, subResource);
        }
    }

    private void validateAmortisation(IntangibleAssetsResource intangibleAssetsResource, Errors errors, IntangibleSubResource subResource) {

        Long chargeForYear = getChargeForYear(intangibleAssetsResource);
        Long onDisposals = getOnDisposals(intangibleAssetsResource);
        Long otherAdjustments = getOtherAdjustments(intangibleAssetsResource);
        Long atPeriodStart = getAmortisationAtPeriodStart(intangibleAssetsResource);

        Long calculatedAtPeriodEnd = atPeriodStart + chargeForYear - onDisposals + otherAdjustments;

        Long atPeriodEnd = getAmortisationAtPeriodEnd(intangibleAssetsResource);

        if(!atPeriodEnd.equals(calculatedAtPeriodEnd)) {
            addError(errors, incorrectTotal, getJsonPath(subResource, AMORTISATION_AT_PERIOD_END));
        }
    }

    private Long getAmortisationAtPeriodEnd(IntangibleAssetsResource intangibleAssetsResource) {

        return Optional.ofNullable(intangibleAssetsResource)
                .map(IntangibleAssetsResource::getAmortisation)
                .map(Amortisation::getAtPeriodEnd)
                .orElse(0L);

    }

    private Long getAmortisationAtPeriodStart(IntangibleAssetsResource intangibleAssetsResource) {

        return Optional.ofNullable(intangibleAssetsResource)
                .map(IntangibleAssetsResource::getAmortisation)
                .map(Amortisation::getAtPeriodStart)
                .orElse(0L);
    }

    private Long getOtherAdjustments(IntangibleAssetsResource intangibleAssetsResource) {

        return Optional.ofNullable(intangibleAssetsResource)
                .map(IntangibleAssetsResource::getAmortisation)
                .map(Amortisation::getOtherAdjustments)
                .orElse(0L);
    }

    private Long getOnDisposals(IntangibleAssetsResource intangibleAssetsResource) {

        return Optional.ofNullable(intangibleAssetsResource)
                .map(IntangibleAssetsResource::getAmortisation)
                .map(Amortisation::getOnDisposals)
                .orElse(0L);
    }

    private Long getChargeForYear(IntangibleAssetsResource intangibleAssetsResource) {

        return Optional.ofNullable(intangibleAssetsResource)
                .map(IntangibleAssetsResource::getAmortisation)
                .map(Amortisation::getChargeForYear)
                .orElse(0L);

    }

    private void validateAdditionsTotal(Errors errors, IntangibleAssets intangibleAssets) {

        Long goodwillAdditions =
                getAdditions(intangibleAssets.getGoodwill());

        Long otherAdditions =
                getAdditions(intangibleAssets.getOtherIntangibleAssets());


        Long resourceAdditionsTotal = goodwillAdditions + otherAdditions;

        Long additions = getAdditions(intangibleAssets.getTotal());

        if (!additions.equals(resourceAdditionsTotal)) {

            addError(errors, incorrectTotal, getJsonPath(IntangibleSubResource.TOTAL, ADDITIONS));
        }
    }

    private void validateDisposalsTotal(Errors errors, IntangibleAssets intangibleAssets) {

        Long goodwillDisposals =
                getDisposals(intangibleAssets.getGoodwill());

        Long otherDisposals =
                getDisposals(intangibleAssets.getOtherIntangibleAssets());

        Long resourceDisposalsTotal = goodwillDisposals + otherDisposals;

        Long disposals = getDisposals(intangibleAssets.getTotal());

        if (!disposals.equals(resourceDisposalsTotal)) {
            addError(errors, incorrectTotal, getJsonPath(IntangibleSubResource.TOTAL, DISPOSALS));
        }
    }

    private void validateRevaluationsTotal(Errors errors, IntangibleAssets intangibleAssets) {

        Long goodwillRevaluations =
                getRevaluations(intangibleAssets.getGoodwill());

        Long otherRevaluations =
                getRevaluations(intangibleAssets.getOtherIntangibleAssets());

        Long resourceRevaluationsTotal = goodwillRevaluations + otherRevaluations;

        Long revaluations = getRevaluations(intangibleAssets.getTotal());

        if (!revaluations.equals(resourceRevaluationsTotal)) {
            addError(errors, incorrectTotal, getJsonPath(IntangibleSubResource.TOTAL, REVALUATIONS));
        }
    }

    private void validateTransfersTotal(Errors errors, IntangibleAssets intangibleAssets) {

        Long goodwillTransfers = getTransfers(intangibleAssets.getGoodwill());

        Long otherTransfers = getTransfers(intangibleAssets.getOtherIntangibleAssets());

        Long resourceTransfersTotal = goodwillTransfers + otherTransfers;

        Long transfers = getTransfers(intangibleAssets.getTotal());

        if (!transfers.equals(resourceTransfersTotal)) {
            addError(errors, incorrectTotal, getJsonPath(IntangibleSubResource.TOTAL, TRANSFERS));
        }
    }

    private void validateCostAtEndTotal(Errors errors, IntangibleAssets intangibleAssets) {

        Long goodwillCostAtEnd = getCostAtPeriodEnd(intangibleAssets.getGoodwill());

        Long otherCostAtEnd = getCostAtPeriodEnd(intangibleAssets.getOtherIntangibleAssets());

        Long resourceCostAtEndTotal = goodwillCostAtEnd + otherCostAtEnd;

        Long costAtEnd = getCostAtPeriodEnd(intangibleAssets.getTotal());

        if (!costAtEnd.equals(resourceCostAtEndTotal)) {
            addError(errors, incorrectTotal, getJsonPath(IntangibleSubResource.TOTAL, COST_AT_PERIOD_END));
        }
    }

    private void validateCostAtStartTotal(Errors errors, IntangibleAssets intangibleAssets) {

        Long goodwillCostAtStart = getCostAtPeriodStart(intangibleAssets.getGoodwill());

        Long otherCostAtStart = getCostAtPeriodStart(intangibleAssets.getOtherIntangibleAssets());

        Long resourceCostAtStartTotal = goodwillCostAtStart + otherCostAtStart;

        Long costAtStart = getCostAtPeriodStart(intangibleAssets.getTotal());

        if (!costAtStart.equals(resourceCostAtStartTotal)) {
            addError(errors, incorrectTotal, getJsonPath(IntangibleSubResource.TOTAL, COST_AT_PERIOD_START));
        }
    }

    private void validateAmortisationAtPeriodStartTotal(Errors errors, IntangibleAssets intangibleAssets) {

        Long goodwillAmortisationAtPeriodStart =
                getAmortisationAtPeriodStart(intangibleAssets.getGoodwill());

        Long otherAmortisationAtPeriodStart =
                getAmortisationAtPeriodStart(intangibleAssets.getOtherIntangibleAssets());

        Long resourceAmortisationAtPeriodStartTotal = goodwillAmortisationAtPeriodStart + otherAmortisationAtPeriodStart;

        Long amortisationAtPeriodStart = getAmortisationAtPeriodStart(intangibleAssets.getTotal());

        if (!amortisationAtPeriodStart.equals(resourceAmortisationAtPeriodStartTotal)) {
            addError(errors, incorrectTotal, getJsonPath(IntangibleSubResource.TOTAL, AMORTISATION_AT_PERIOD_START));
        }

    }

    private void validateChargeForYearTotal(Errors errors, IntangibleAssets intangibleAssets) {

        Long goodwillChargeForYear = getChargeForYear(intangibleAssets.getGoodwill());

        Long otherChargeForYear = getChargeForYear(intangibleAssets.getOtherIntangibleAssets());

        Long resourceChargeForYearTotal = goodwillChargeForYear + otherChargeForYear;

        Long chargeForYear = getChargeForYear(intangibleAssets.getTotal());

        if (!chargeForYear.equals(resourceChargeForYearTotal)) {
            addError(errors, incorrectTotal, getJsonPath(IntangibleSubResource.TOTAL, CHARGE_FOR_YEAR));
        }
    }

    private void validateOtherAdjustmentsTotal(Errors errors, IntangibleAssets intangibleAssets) {

        Long goodwillOtherAdjustments = getOtherAdjustments(intangibleAssets.getGoodwill());

        Long otherOtherAdjustments = getOtherAdjustments(intangibleAssets.getOtherIntangibleAssets());

        Long resourceOtherAdjustmentsTotal = goodwillOtherAdjustments + otherOtherAdjustments;

        Long otherAdjustments = getOtherAdjustments(intangibleAssets.getTotal());

        if (!otherAdjustments.equals(resourceOtherAdjustmentsTotal)) {
            addError(errors, incorrectTotal, getJsonPath(IntangibleSubResource.TOTAL, OTHER_ADJUSTMENTS));
        }
    }

    private void validateOnDisposalsTotal(Errors errors, IntangibleAssets intangibleAssets) {

        Long goodwillOnDisposals = getOnDisposals(intangibleAssets.getGoodwill());

        Long otherOnDisposals = getOnDisposals(intangibleAssets.getOtherIntangibleAssets());

        Long resourceOnDisposalsTotal = goodwillOnDisposals + otherOnDisposals;

        Long onDisposals = getOnDisposals(intangibleAssets.getTotal());

        if (!onDisposals.equals(resourceOnDisposalsTotal)) {
            addError(errors, incorrectTotal, getJsonPath(IntangibleSubResource.TOTAL, ON_DISPOSALS));
        }
    }

    private void validateAmortisationAtPeriodEndTotal(Errors errors, IntangibleAssets intangibleAssets) {

        Long goodwillAmortisationAtPeriodEnd =
                getAmortisationAtPeriodEnd(intangibleAssets.getGoodwill());

        Long otherAmortisationAtPeriodEnd =
                getAmortisationAtPeriodEnd(intangibleAssets.getOtherIntangibleAssets());

        Long resourceAmortisationAtPeriodEndTotal = goodwillAmortisationAtPeriodEnd + otherAmortisationAtPeriodEnd;

        Long amortisationAtPeriodEnd = getAmortisationAtPeriodEnd(intangibleAssets.getTotal());

        if (!amortisationAtPeriodEnd.equals(resourceAmortisationAtPeriodEndTotal)) {
            addError(errors, incorrectTotal, getJsonPath(IntangibleSubResource.TOTAL, AMORTISATION_AT_PERIOD_END));
        }
    }


    private void validateTotalFieldsMatch(Errors errors, IntangibleAssets intangibleAssets, boolean isMultipleYearFiler) {

        if (isMultipleYearFiler) {
            validateCostAtStartTotal(errors, intangibleAssets);
            validateAmortisationAtPeriodStartTotal(errors, intangibleAssets);
            validatePreviousNetBookValuesTotal(errors, intangibleAssets);
        }
        validateAdditionsTotal(errors, intangibleAssets);
        validateDisposalsTotal(errors, intangibleAssets);
        validateRevaluationsTotal(errors, intangibleAssets);
        validateTransfersTotal(errors, intangibleAssets);
        validateCostAtEndTotal(errors, intangibleAssets);
        validateAmortisationAtPeriodEndTotal(errors, intangibleAssets);
        validateChargeForYearTotal(errors, intangibleAssets);
        validateOtherAdjustmentsTotal(errors, intangibleAssets);
        validateOnDisposalsTotal(errors, intangibleAssets);
        validateCurrentNetBookValuesTotal(errors, intangibleAssets);
    }

    private void validateCosts(IntangibleAssetsResource intangibleAssetsResource, Errors errors, IntangibleSubResource subResource) {

        Long atPeriodStart = getCostAtPeriodStart(intangibleAssetsResource);

        Long additions = getAdditions(intangibleAssetsResource);

        Long disposals = getDisposals(intangibleAssetsResource);

        Long revaluations = getRevaluations(intangibleAssetsResource);

        Long transfers = getTransfers(intangibleAssetsResource);

        Long calculatedAtPeriodEnd = atPeriodStart + additions - disposals + revaluations + transfers;

        if(!getCostAtPeriodEnd(intangibleAssetsResource).equals(calculatedAtPeriodEnd)) {
            addError(errors, incorrectTotal, getJsonPath(subResource, COST_AT_PERIOD_END));
        }
    }

    private Long getCostAtPeriodEnd(IntangibleAssetsResource intangibleAssetsResource) {

        return Optional.ofNullable(intangibleAssetsResource)
                .map(IntangibleAssetsResource::getCost)
                .map(Cost::getAtPeriodEnd)
                .orElse(0L);
    }

    private Long getTransfers(IntangibleAssetsResource intangibleAssetsResource) {

        return Optional.ofNullable(intangibleAssetsResource)
                .map(IntangibleAssetsResource::getCost)
                .map(Cost::getTransfers)
                .orElse(0L);
    }

    private Long getRevaluations(IntangibleAssetsResource intangibleAssetsResource) {

        return Optional.ofNullable(intangibleAssetsResource)
                .map(IntangibleAssetsResource::getCost)
                .map(Cost::getRevaluations)
                .orElse(0L);
    }

    private Long getDisposals(IntangibleAssetsResource intangibleAssetsResource) {

        return Optional.ofNullable(intangibleAssetsResource)
                .map(IntangibleAssetsResource::getCost)
                .map(Cost::getDisposals)
                .orElse(0L);

    }

    private Long getAdditions(IntangibleAssetsResource intangibleAssetsResource) {

        return Optional.ofNullable(intangibleAssetsResource)
                .map(IntangibleAssetsResource::getCost)
                .map(Cost::getAdditions)
                .orElse(0L);
    }

    private Long getCostAtPeriodStart(IntangibleAssetsResource intangibleAssetsResource) {
        return Optional.ofNullable(intangibleAssetsResource)
                .map(IntangibleAssetsResource::getCost)
                .map(Cost::getAtPeriodStart)
                .orElse(0L);
    }

    private void validatePresenceOfSingleYearFilerFields(IntangibleAssetsResource intangibleAssetsResource, Errors errors, IntangibleSubResource intangibleSubResource, List<IntangibleSubResource> invalidSubResource) {

        boolean subResourceInvalid = false;

        if(intangibleAssetsResource.getCost() != null && intangibleAssetsResource.getCost().getAtPeriodStart() != null) {
            addError(errors, unexpectedData, getJsonPath (intangibleSubResource, COST_AT_PERIOD_START));
            subResourceInvalid = true;
        }

        if(intangibleAssetsResource.getCost() != null
                && intangibleAssetsResource.getCost().getAtPeriodEnd() == null) {
            addError(errors, valueRequired, getJsonPath(intangibleSubResource, COST_AT_PERIOD_END));
            subResourceInvalid = true;
        }

        if(intangibleAssetsResource.getAmortisation() != null
                && intangibleAssetsResource.getAmortisation().getAtPeriodStart() != null) {
            addError(errors, unexpectedData, getJsonPath(intangibleSubResource, AMORTISATION_AT_PERIOD_START));
            subResourceInvalid = true;
        }

        if(intangibleAssetsResource.getAmortisation() != null
                && hasAmortisationFieldsSet(intangibleAssetsResource.getAmortisation())
                && intangibleAssetsResource.getAmortisation().getAtPeriodEnd() == null) {
            addError(errors, valueRequired, getJsonPath(intangibleSubResource, AMORTISATION_AT_PERIOD_END));
            subResourceInvalid = true;
        }

        if (intangibleAssetsResource.getNetBookValueAtEndOfPreviousPeriod() != null) {

            addError(errors, unexpectedData,
                getJsonPath(intangibleSubResource, NET_BOOK_VALUE_PREVIOUS_PERIOD));
            subResourceInvalid = true;
        }

        if (intangibleAssetsResource.getNetBookValueAtEndOfCurrentPeriod() != null) {

            subResourceInvalid = isCostSingleYearSubResourceInvalid(errors, intangibleAssetsResource.getCost(),
                intangibleSubResource, subResourceInvalid);

            subResourceInvalid = isAmortisationSingleYearSubResourceInvalid(errors,
                intangibleAssetsResource.getAmortisation(), intangibleSubResource,
                subResourceInvalid);
        } else {

            if (hasSingleYearFilerNonNetBookValueFieldsSet(intangibleAssetsResource)) {

                addError(errors, valueRequired, getJsonPath(intangibleSubResource, NET_BOOK_VALUE_CURRENT_PERIOD));
                subResourceInvalid = true;
            }
        }

        if(subResourceInvalid) {
            invalidSubResource.add(intangibleSubResource);
        }
    }

    private boolean isCostSingleYearSubResourceInvalid(Errors errors, Cost cost, IntangibleSubResource intangibleSubResource,
        boolean subResourceInvalid) {

        if(cost == null || cost.getAtPeriodEnd() == null) {

            addError(errors, valueRequired, getJsonPath(intangibleSubResource, COST_AT_PERIOD_END));
            subResourceInvalid = true;
        }
        return subResourceInvalid;
    }

    private boolean isAmortisationSingleYearSubResourceInvalid(Errors errors, Amortisation amortisation, IntangibleSubResource intangibleSubResource,
        boolean subResourceInvalid) {

        if (amortisation != null && hasAmortisationFieldsSet(amortisation)
            && amortisation.getAtPeriodEnd() == null) {

            addError(errors, valueRequired, getJsonPath(intangibleSubResource, AMORTISATION_AT_PERIOD_END));
            subResourceInvalid = true;
        }
        return subResourceInvalid;
    }

    private void validateCurrentNetBookValue(Errors errors, IntangibleAssetsResource intangibleAssetsResource, IntangibleSubResource intangibleSubResource) {

        Long costAtPeriodEnd = getCostAtPeriodEnd(intangibleAssetsResource);

        Long amortisationAtPeriodEnd = getAmortisationAtPeriodEnd(intangibleAssetsResource);

        Long calculatedCurrentNetBookValue = costAtPeriodEnd - amortisationAtPeriodEnd;

        if(!intangibleAssetsResource.getNetBookValueAtEndOfCurrentPeriod().equals(calculatedCurrentNetBookValue)) {

            addError(errors, incorrectTotal, getJsonPath(intangibleSubResource, NET_BOOK_VALUE_CURRENT_PERIOD));
        }
    }

    private void validatePresenceOfMultipleYearFilerFields(IntangibleAssetsResource intangibleAssetsResource, Errors errors, IntangibleSubResource intangibleSubResource, List<IntangibleSubResource> invalidSubResource) {

        boolean subResourceInvalid = false;

        if(intangibleAssetsResource.getCost() == null || intangibleAssetsResource.getCost().getAtPeriodEnd() == null) {
            addError(errors, valueRequired, getJsonPath(intangibleSubResource, COST_AT_PERIOD_END));
            subResourceInvalid = true;
        }

        if(intangibleAssetsResource.getCost() == null || intangibleAssetsResource.getCost().getAtPeriodStart() == null) {
            addError(errors, valueRequired, getJsonPath(intangibleSubResource, COST_AT_PERIOD_START));
            subResourceInvalid = true;
        }

        if(intangibleAssetsResource.getAmortisation() == null || intangibleAssetsResource.getAmortisation().getAtPeriodStart() == null) {

            addError(errors, valueRequired, getJsonPath(intangibleSubResource, AMORTISATION_AT_PERIOD_START));
            subResourceInvalid = true;
        }

        if(intangibleAssetsResource.getAmortisation() == null || intangibleAssetsResource.getAmortisation().getAtPeriodEnd() == null) {

            addError(errors, valueRequired, getJsonPath(intangibleSubResource, AMORTISATION_AT_PERIOD_END));
            subResourceInvalid = true;
        }

        if (intangibleAssetsResource.getNetBookValueAtEndOfCurrentPeriod() != null  || intangibleAssetsResource.getNetBookValueAtEndOfPreviousPeriod() != null) {

            if (intangibleAssetsResource.getNetBookValueAtEndOfPreviousPeriod() == null) {

                addError(errors, valueRequired, getJsonPath(intangibleSubResource, NET_BOOK_VALUE_PREVIOUS_PERIOD));
                subResourceInvalid = true;
            }

            if (intangibleAssetsResource.getNetBookValueAtEndOfCurrentPeriod() == null) {

                addError(errors, valueRequired, getJsonPath(intangibleSubResource, NET_BOOK_VALUE_CURRENT_PERIOD));
                subResourceInvalid = true;
            }

            subResourceInvalid = isCostMultipleYearSubResourceInvalid(errors, intangibleAssetsResource.getCost(),
                intangibleSubResource, subResourceInvalid);

            subResourceInvalid = isAmortisationMultipleYearSubResourceInvalid(errors, intangibleAssetsResource.getAmortisation(),
                intangibleSubResource, subResourceInvalid);

        } else {

            if (hasMultipleYearFilerNonNetBookValueFieldsSet(intangibleAssetsResource)) {

                addError(errors, valueRequired, getJsonPath(intangibleSubResource, NET_BOOK_VALUE_CURRENT_PERIOD));
                addError(errors, valueRequired, getJsonPath(intangibleSubResource, NET_BOOK_VALUE_PREVIOUS_PERIOD));
                subResourceInvalid = true;
            }
        }

        if(subResourceInvalid) {
            invalidSubResource.add(intangibleSubResource);
        }
    }

    private boolean hasAmortisationFieldsSet(Amortisation amortisation) {
        return Stream.of(amortisation.getChargeForYear(),
                amortisation.getOnDisposals(),
                amortisation.getOtherAdjustments())
                .anyMatch(Objects::nonNull);

    }

    private boolean hasSingleYearFilerNonNetBookValueFieldsSet(IntangibleAssetsResource intangibleAssetsResource) {

        if (intangibleAssetsResource.getCost() != null &&
            Stream.of(intangibleAssetsResource.getCost().getAdditions(),
                intangibleAssetsResource.getCost().getDisposals(),
                intangibleAssetsResource.getCost().getRevaluations(),
                intangibleAssetsResource.getCost().getTransfers(),
                intangibleAssetsResource.getCost().getAtPeriodEnd())
                .anyMatch(Objects::nonNull)) {

            return true;
        }

        return intangibleAssetsResource.getAmortisation() != null &&
            Stream.of(intangibleAssetsResource.getAmortisation().getChargeForYear(),
                intangibleAssetsResource.getAmortisation().getOnDisposals(),
                intangibleAssetsResource.getAmortisation().getOtherAdjustments(),
                intangibleAssetsResource.getAmortisation().getAtPeriodEnd())
                .anyMatch(Objects::nonNull);
    }

    private boolean isCostMultipleYearSubResourceInvalid(Errors errors,
        Cost cost, IntangibleSubResource intangibleSubResource,
        boolean subResourceInvalid) {
        if (cost == null || cost.getAtPeriodStart() == null) {

            addError(errors, valueRequired, getJsonPath(intangibleSubResource, COST_AT_PERIOD_START));
            subResourceInvalid = true;
        }

        subResourceInvalid = isCostSingleYearSubResourceInvalid(errors, cost, intangibleSubResource, subResourceInvalid);

        return subResourceInvalid;
    }

    private boolean isAmortisationMultipleYearSubResourceInvalid(Errors errors,
        Amortisation amortisation, IntangibleSubResource intangibleSubResource,
        boolean subResourceInvalid) {
        if (amortisation != null && hasAmortisationFieldsSet(amortisation)) {

            if (amortisation.getAtPeriodStart() == null) {

                addError(errors, valueRequired, getJsonPath(intangibleSubResource, AMORTISATION_AT_PERIOD_START));
                subResourceInvalid = true;
            }

            subResourceInvalid = isAmortisationSingleYearSubResourceInvalid(errors, amortisation, intangibleSubResource, subResourceInvalid);

        }
        return subResourceInvalid;
    }

    private boolean hasMultipleYearFilerNonNetBookValueFieldsSet(IntangibleAssetsResource intangibleAssetsResource) {

        if (intangibleAssetsResource.getCost() != null &&
            Stream.of(intangibleAssetsResource.getCost().getAtPeriodStart(),
                intangibleAssetsResource.getCost().getAdditions(),
                intangibleAssetsResource.getCost().getDisposals(),
                intangibleAssetsResource.getCost().getRevaluations(),
                intangibleAssetsResource.getCost().getTransfers(),
                intangibleAssetsResource.getCost().getAtPeriodEnd())
                .anyMatch(Objects::nonNull)) {

            return true;
        }

        return intangibleAssetsResource.getAmortisation() != null &&
            Stream.of(intangibleAssetsResource.getAmortisation().getAtPeriodStart(),
                intangibleAssetsResource.getAmortisation().getChargeForYear(),
                intangibleAssetsResource.getAmortisation().getOnDisposals(),
                intangibleAssetsResource.getAmortisation().getOtherAdjustments(),
                intangibleAssetsResource.getAmortisation().getAtPeriodEnd())
                .anyMatch(Objects::nonNull);
    }

    private void validatePreviousNetBookValue(Errors errors, IntangibleAssetsResource intangibleAssetsResource, IntangibleSubResource intangibleSubResource) {

        Long costAtPeriodStart = getCostAtPeriodStart(intangibleAssetsResource);

        Long amortisationAtPeriodStart = getAmortisationAtPeriodStart(intangibleAssetsResource);

        Long calculatedPreviousNetBookValue = costAtPeriodStart - amortisationAtPeriodStart;

        if(!intangibleAssetsResource.getNetBookValueAtEndOfPreviousPeriod().equals(calculatedPreviousNetBookValue)) {

            addError(errors, incorrectTotal, getJsonPath(intangibleSubResource, NET_BOOK_VALUE_PREVIOUS_PERIOD));
        }
    }

    private void validateCurrentNetBookValuesTotal(Errors errors, IntangibleAssets intangibleAssets) {

        Long goodwillNetBookValue = getNetBookValueAtEndOfCurrentPeriod(intangibleAssets.getGoodwill());

        Long otherIntangibleAssetsNetBookValue = getNetBookValueAtEndOfCurrentPeriod(intangibleAssets.getOtherIntangibleAssets());

        Long resourceNetBookValueTotal = goodwillNetBookValue + otherIntangibleAssetsNetBookValue;

        Long netBookValueAtEndOfCurrentPeriod = getNetBookValueAtEndOfCurrentPeriod(intangibleAssets.getTotal());

        if(!netBookValueAtEndOfCurrentPeriod.equals(resourceNetBookValueTotal)) {

            addError(errors, incorrectTotal, getJsonPath(IntangibleSubResource.TOTAL, NET_BOOK_VALUE_CURRENT_PERIOD));
        }
    }

    private void validatePreviousNetBookValuesTotal(Errors errors, IntangibleAssets intangibleAssets) {

        Long goodwillNetBookValue = getNetBookValueAtEndOfPreviousPeriod(intangibleAssets.getGoodwill());

        Long otherIntangibleAssetsNetBookValue = getNetBookValueAtEndOfPreviousPeriod(intangibleAssets.getOtherIntangibleAssets());

        Long resourceNetBookValueTotal = goodwillNetBookValue + otherIntangibleAssetsNetBookValue;

        Long netBookValueAtEndOfPreviousPeriod = getNetBookValueAtEndOfPreviousPeriod(intangibleAssets.getTotal());

        if(!netBookValueAtEndOfPreviousPeriod.equals(resourceNetBookValueTotal)) {

            addError(errors, incorrectTotal, getJsonPath(IntangibleSubResource.TOTAL, NET_BOOK_VALUE_PREVIOUS_PERIOD));
        }
    }

    private Long getNetBookValueAtEndOfCurrentPeriod(IntangibleAssetsResource intangibleAssetsResource) {

        return Optional.ofNullable(intangibleAssetsResource)
            .map(IntangibleAssetsResource::getNetBookValueAtEndOfCurrentPeriod)
            .orElse(0L);
    }

    private Long getNetBookValueAtEndOfPreviousPeriod(IntangibleAssetsResource intangibleAssetsResource) {

        return Optional.ofNullable(intangibleAssetsResource)
            .map(IntangibleAssetsResource::getNetBookValueAtEndOfPreviousPeriod)
            .orElse(0L);
    }

    @Override
    public Errors crossValidate(IntangibleAssets intangibleAssets,
                                HttpServletRequest request,
                                String companyAccountsId,
                                Errors errors) throws DataException {

        BalanceSheet currentPeriodBalanceSheet = getCurrentPeriodBalanceSheet(request,
                companyAccountsId);
        BalanceSheet previousPeriodBalanceSheet = getPreviousPeriodBalanceSheet(request,
                companyAccountsId);

        if (currentPeriodBalanceSheet != null) {
            crossValidateCurrentPeriod(errors, request, companyAccountsId, intangibleAssets);
        }
        if (previousPeriodBalanceSheet != null) {
            crossValidatePreviousPeriod(errors, request, companyAccountsId, intangibleAssets);
        }
        return errors;
    }

    private void crossValidateCurrentPeriod(Errors errors, HttpServletRequest request, String companyAccountsId,
                                            IntangibleAssets intangibleAssets) throws DataException {

        ResponseObject<CurrentPeriod> currentPeriodResponseObject =
                currentPeriodService.find(companyAccountsId, request);
        CurrentPeriod currentPeriod = currentPeriodResponseObject.getData();

        Long currentPeriodIntangible =
                Optional.ofNullable(currentPeriod)
                        .map(CurrentPeriod::getBalanceSheet)
                        .map(BalanceSheet::getFixedAssets)
                        .map(FixedAssets::getIntangible)
                        .orElse(null);

        Long currentNetBookValueTotal =
                Optional.ofNullable(intangibleAssets)
                        .map(IntangibleAssets::getTotal)
                        .map(IntangibleAssetsResource::getNetBookValueAtEndOfCurrentPeriod)
                        .orElse(null);

        if ((currentPeriodIntangible != null || currentNetBookValueTotal != null) && (
                (currentPeriodIntangible != null && currentNetBookValueTotal == null)
                        || currentPeriodIntangible == null ||
                        (!currentPeriodIntangible.equals(currentNetBookValueTotal)))) {
            addError(errors, currentBalanceSheetNotEqual,
                    getJsonPath(IntangibleSubResource.TOTAL, NET_BOOK_VALUE_CURRENT_PERIOD));
        }
    }

    private void crossValidatePreviousPeriod(Errors errors, HttpServletRequest request, String companyAccountsId,
                                             IntangibleAssets intangibleAssets) throws DataException {

        ResponseObject<PreviousPeriod> previousPeriodResponseObject =
                previousPeriodService.find(companyAccountsId, request);
        PreviousPeriod previousPeriod = previousPeriodResponseObject.getData();

        Long previousPeriodIntangible =
                Optional.ofNullable(previousPeriod)
                        .map(PreviousPeriod::getBalanceSheet)
                        .map(BalanceSheet::getFixedAssets)
                        .map(FixedAssets::getIntangible)
                        .orElse(null);

        Long previousNetBookValueTotal =
                Optional.ofNullable(intangibleAssets)
                        .map(IntangibleAssets::getTotal)
                        .map(IntangibleAssetsResource::getNetBookValueAtEndOfPreviousPeriod)
                        .orElse(null);

        if ((previousPeriodIntangible != null || previousNetBookValueTotal != null) && (
                (previousPeriodIntangible != null && previousNetBookValueTotal == null)
                        || previousPeriodIntangible == null ||
                        (!previousPeriodIntangible.equals(previousNetBookValueTotal)))) {
            addError(errors, previousBalanceSheetNotEqual,
                    getJsonPath(IntangibleSubResource.TOTAL, NET_BOOK_VALUE_PREVIOUS_PERIOD));
        }
    }

    private BalanceSheet getCurrentPeriodBalanceSheet(HttpServletRequest request,
                                                      String companyAccountsId) throws DataException {

        ResponseObject<CurrentPeriod> currentPeriodResponseObject;
        currentPeriodResponseObject = currentPeriodService.find(companyAccountsId, request);

        if (currentPeriodResponseObject != null && currentPeriodResponseObject.getData() != null) {
            return currentPeriodResponseObject.getData().getBalanceSheet();
        } else {
            return null;
        }
    }

    private BalanceSheet getPreviousPeriodBalanceSheet(HttpServletRequest request,
                                                      String companyAccountsId) throws DataException {

        ResponseObject<PreviousPeriod> previousPeriodResponseObject;
        previousPeriodResponseObject = previousPeriodService.find(companyAccountsId, request);

        if (previousPeriodResponseObject != null && previousPeriodResponseObject.getData() != null) {
            return previousPeriodResponseObject.getData().getBalanceSheet();
        } else {
            return null;
        }
    }

    private String getJsonPath(IntangibleSubResource intangibleSubResource, String pathSuffix) {

        return INTANGIBLE_NOTE + "." + intangibleSubResource.getJsonPath() + pathSuffix;
    }

    private enum IntangibleSubResource {
        GOODWILL("goodwill"),
        OTHER_INTANGIBLE_ASSETS("other_intangible_assets"),
        TOTAL("total");

        private String jsonPath;

        IntangibleSubResource(String jsonPath) {
            this.jsonPath = jsonPath;
        }

        public String getJsonPath() {
            return jsonPath;
        }

    }
}
