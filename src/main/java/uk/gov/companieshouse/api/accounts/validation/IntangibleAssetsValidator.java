package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.Amortisation;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.IntangibleAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.IntangibleAssetsResource;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.Cost;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Component
public class IntangibleAssetsValidator  extends BaseValidator  {

    private CompanyService companyService;

    @Autowired
    public IntangibleAssetsValidator(CompanyService companyService) {

        this.companyService = companyService;

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

        } catch(ServiceException se) {
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
            }
            else {
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
    }

    private void validateAmortisation(IntangibleAssetsResource intangibleAssetsResource, Errors errors, IntangibleSubResource subResource) {

        Long chargeForYear = getChargeForYear(intangibleAssetsResource);
        Long onDisposal = getOnDisposals(intangibleAssetsResource);
        Long otherAdjustments = getOtherAdjustments(intangibleAssetsResource);
        Long atPeriodStart = getAmortisationAtPeriodStart(intangibleAssetsResource);

        Long calculatedAtPeriodEnd = atPeriodStart + chargeForYear - onDisposal + otherAdjustments;

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

        if(subResourceInvalid) {
            invalidSubResource.add(intangibleSubResource);
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
