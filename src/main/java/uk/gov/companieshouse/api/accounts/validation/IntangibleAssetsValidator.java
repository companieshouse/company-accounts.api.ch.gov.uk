package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.IntangibleAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.IntangibleAssetsResource;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.Cost;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    private static final String COST_AT_PERIOD_END = ".cost.at_period_end";
    private static final String NET_BOOK_VALUE_CURRENT_PERIOD = ".net_book_value_at_end_of_current_period";
    private static final String NET_BOOK_VALUE_PREVIOUS_PERIOD = ".net_book_value_at_end_of_previous_period";

    public Errors validateIntangibleAssets(IntangibleAssets intangibleAssets, Transaction transaction, String companyAccountsId, HttpServletRequest request)
    throws DataException {
        Errors errors = new Errors();

        try {
            boolean isMultipleYearFiler = companyService.isMultipleYearFiler(transaction);

            List<IntangibleSubResource> invalidSubResources = new ArrayList<>();
            verifySubResourcesAreValid(intangibleAssets, errors, isMultipleYearFiler, invalidSubResources);
            verifyNoteNotEmpty(intangibleAssets, errors, isMultipleYearFiler);
            validateSubResourceTotals(intangibleAssets, errors, isMultipleYearFiler, invalidSubResources);

        } catch(ServiceException se) {
            throw new DataException(se.getMessage(), se);
        }

        return errors;
    }

    private void verifyNoteNotEmpty(IntangibleAssets intangibleAssets, Errors errors, boolean isMultipleYearFiler) {

        if(intangibleAssets.getGoodwill() == null &&
                intangibleAssets.getOtherIntangibleAssets() == null &&
                intangibleAssets.getTotal() == null) {

            addError(errors, valueRequired, getJsonPath(IntangibleSubResource.TOTAL, NET_BOOK_VALUE_CURRENT_PERIOD));
        }
        if (isMultipleYearFiler) {
            addError(errors, valueRequired, getJsonPath(IntangibleSubResource.TOTAL, NET_BOOK_VALUE_PREVIOUS_PERIOD));
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

                /* TODO - SFA-1772 */
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

    }

    private Long getCostAtPeriodEnd(IntangibleAssetsResource intangibleAssetsResource) {

        return Optional.ofNullable(intangibleAssetsResource)
                .map(IntangibleAssetsResource::getCost)
                .map(Cost::getAtPeriodEnd)
                .orElse(0L);
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

        if(subResourceInvalid) {
            invalidSubResource.add(intangibleSubResource);
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
