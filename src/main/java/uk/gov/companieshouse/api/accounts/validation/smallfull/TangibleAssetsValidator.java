package uk.gov.companieshouse.api.accounts.validation.smallfull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountsResource;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.tangible.Cost;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.tangible.Depreciation;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.tangible.TangibleAssets;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.tangible.TangibleAssetsResource;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.validation.AccountsResourceValidator;
import uk.gov.companieshouse.api.accounts.validation.BaseValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class TangibleAssetsValidator extends BaseValidator implements AccountsResourceValidator<TangibleAssets> {

    private CompanyService companyService;

    private CurrentPeriodService currentPeriodService;

    private PreviousPeriodService previousPeriodService;

    @Autowired
    public TangibleAssetsValidator(CompanyService companyService, CurrentPeriodService currentPeriodService, PreviousPeriodService previousPeriodService) {
        this.companyService = companyService;
        this.currentPeriodService = currentPeriodService;
        this.previousPeriodService = previousPeriodService;
    }

    @Value("${incorrect.total}")
    private String incorrectTotal;

    @Value("${value.required}")
    private String valueRequired;

    private static final String TANGIBLE_NOTE = "$.tangible_assets";
    private static final String COST_AT_PERIOD_START = ".cost.at_period_start";
    private static final String ADDITIONS = ".cost.additions";
    private static final String DISPOSALS = ".cost.disposals";
    private static final String REVALUATIONS = ".cost.revaluations";
    private static final String TRANSFERS = ".cost.transfers";
    private static final String COST_AT_PERIOD_END = ".cost.at_period_end";
    private static final String DEPRECIATION_AT_PERIOD_START = ".depreciation.at_period_start";
    private static final String CHARGE_FOR_YEAR = ".depreciation.charge_for_year";
    private static final String ON_DISPOSALS = ".depreciation.on_disposals";
    private static final String OTHER_ADJUSTMENTS = ".depreciation.other_adjustments";
    private static final String DEPRECIATION_AT_PERIOD_END = ".depreciation.at_period_end";
    private static final String NET_BOOK_VALUE_CURRENT_PERIOD = ".net_book_value_at_end_of_current_period";
    private static final String NET_BOOK_VALUE_PREVIOUS_PERIOD = ".net_book_value_at_end_of_previous_period";

    @Override
    public Errors validateSubmission(TangibleAssets tangibleAssets, Transaction transaction, String companyAccountsId, HttpServletRequest request)
            throws DataException {

        Errors errors = new Errors();

        try {
            boolean isMultipleYearFiler = companyService.isMultipleYearFiler(transaction);

            List<TangibleSubResource> invalidSubResources = new ArrayList<>();

            verifySubResourcesAreValid(tangibleAssets, errors, isMultipleYearFiler, invalidSubResources);
            verifyNoteNotEmpty(tangibleAssets, errors, isMultipleYearFiler);
            validateSubResourceTotals(tangibleAssets, errors, isMultipleYearFiler, invalidSubResources);
            if (errors.hasErrors()) {
                return errors;
            }

            validateTotalFieldsMatch(errors, tangibleAssets, isMultipleYearFiler);
            crossValidate(tangibleAssets, request, companyAccountsId, errors);

        } catch (ServiceException e) {

            throw new DataException(e.getMessage(), e);
        }

        return errors;
    }

    private void verifyNoteNotEmpty(TangibleAssets tangibleAssets, Errors errors, boolean isMultipleYearFiler) {

        if (tangibleAssets.getFixturesAndFittings() == null &&
                tangibleAssets.getLandAndBuildings() == null &&
                tangibleAssets.getMotorVehicles() == null &&
                tangibleAssets.getOfficeEquipment() == null &&
                tangibleAssets.getPlantAndMachinery() == null &&
                tangibleAssets.getTotal() == null) {

            addError(errors, valueRequired, getJsonPath(TangibleSubResource.TOTAL, NET_BOOK_VALUE_CURRENT_PERIOD));
            if (isMultipleYearFiler) {
                addError(errors, valueRequired, getJsonPath(TangibleSubResource.TOTAL, NET_BOOK_VALUE_PREVIOUS_PERIOD));
            }
        }
    }

    private void verifySubResourcesAreValid(TangibleAssets tangibleAssets, Errors errors, boolean isMultipleYearFiler, List<TangibleSubResource> invalidSubResources) {

        validateSubResource(errors, tangibleAssets.getFixturesAndFittings(), isMultipleYearFiler, TangibleSubResource.FIXTURES_AND_FITTINGS, invalidSubResources);
        validateSubResource(errors, tangibleAssets.getLandAndBuildings(), isMultipleYearFiler, TangibleSubResource.LAND_AND_BUILDINGS, invalidSubResources);
        validateSubResource(errors, tangibleAssets.getMotorVehicles(), isMultipleYearFiler, TangibleSubResource.MOTOR_VEHICLES, invalidSubResources);
        validateSubResource(errors, tangibleAssets.getOfficeEquipment(), isMultipleYearFiler, TangibleSubResource.OFFICE_EQUIPMENT, invalidSubResources);
        validateSubResource(errors, tangibleAssets.getPlantAndMachinery(), isMultipleYearFiler, TangibleSubResource.PLANT_AND_MACHINERY, invalidSubResources);
        validateSubResource(errors, tangibleAssets.getTotal(), isMultipleYearFiler, TangibleSubResource.TOTAL, invalidSubResources);
    }

    private void validateSubResource(Errors errors, TangibleAssetsResource resource, boolean isMultipleYearFiler, TangibleSubResource subResource, List<TangibleSubResource> invalidSubResources) {

        if (resource != null) {

            if (isMultipleYearFiler) {
                validatePresenceOfMultipleYearFilerFields(errors, resource, subResource, invalidSubResources);
            } else {
                validatePresenceOfSingleYearFilerFields(errors, resource, subResource, invalidSubResources);
            }
        }
    }

    private void validateSubResourceTotals(TangibleAssets tangibleAssets, Errors errors, boolean isMultipleYearFiler, List<TangibleSubResource> invalidSubResources) {

        if (tangibleAssets.getFixturesAndFittings() != null && !invalidSubResources.contains(TangibleSubResource.FIXTURES_AND_FITTINGS)) {

            validateSubResourceTotal(tangibleAssets.getFixturesAndFittings(), errors, isMultipleYearFiler, TangibleSubResource.FIXTURES_AND_FITTINGS);
        }

        if (tangibleAssets.getLandAndBuildings() != null && !invalidSubResources.contains(TangibleSubResource.LAND_AND_BUILDINGS)) {

            validateSubResourceTotal(tangibleAssets.getLandAndBuildings(), errors, isMultipleYearFiler, TangibleSubResource.LAND_AND_BUILDINGS);
        }

        if (tangibleAssets.getMotorVehicles() != null && !invalidSubResources.contains(TangibleSubResource.MOTOR_VEHICLES)) {

            validateSubResourceTotal(tangibleAssets.getMotorVehicles(), errors, isMultipleYearFiler, TangibleSubResource.MOTOR_VEHICLES);
        }

        if (tangibleAssets.getOfficeEquipment() != null && !invalidSubResources.contains(TangibleSubResource.OFFICE_EQUIPMENT)) {

            validateSubResourceTotal(tangibleAssets.getOfficeEquipment(), errors, isMultipleYearFiler, TangibleSubResource.OFFICE_EQUIPMENT);
        }

        if (tangibleAssets.getPlantAndMachinery() != null && !invalidSubResources.contains(TangibleSubResource.PLANT_AND_MACHINERY)) {

            validateSubResourceTotal(tangibleAssets.getPlantAndMachinery(), errors, isMultipleYearFiler, TangibleSubResource.PLANT_AND_MACHINERY);
        }
    }

    private void validateSubResourceTotal(TangibleAssetsResource tangibleAssetsResource, Errors errors, boolean isMultipleYearFiler, TangibleSubResource subResource) {

        validateCosts(errors, tangibleAssetsResource, subResource);
        validateDepreciation(errors, tangibleAssetsResource, subResource);
        validateCurrentNetBookValue(errors, tangibleAssetsResource, subResource);
        if (isMultipleYearFiler) {
            validatePreviousNetBookValue(errors, tangibleAssetsResource, subResource);
        }
    }

    private void validateTotalFieldsMatch(Errors errors, TangibleAssets tangibleAssets, boolean isMultipleYearFiler) {

        if (isMultipleYearFiler) {
            validateCostAtPeriodStartTotal(errors, tangibleAssets);
            validateDepreciationAtPeriodStartTotal(errors, tangibleAssets);
            validatePreviousNetBookValuesTotal(errors, tangibleAssets);
        }
        validateAdditionsTotal(errors, tangibleAssets);
        validateDisposalsTotal(errors, tangibleAssets);
        validateRevaluationsTotal(errors, tangibleAssets);
        validateTransfersTotal(errors, tangibleAssets);
        validateCostAtPeriodEndTotal(errors, tangibleAssets);
        validateChargeForYearTotal(errors, tangibleAssets);
        validateOnDisposalsTotal(errors, tangibleAssets);
        validateOtherAdjustmentsTotal(errors, tangibleAssets);
        validateDepreciationAtPeriodEndTotal(errors, tangibleAssets);
        validateCurrentNetBookValuesTotal(errors, tangibleAssets);
    }

    private void validateCostAtPeriodStartTotal(Errors errors, TangibleAssets tangibleAssets) {

        Long fixturesCostAtPeriodStart =
                getCostAtPeriodStart(tangibleAssets.getFixturesAndFittings());

        Long landAndBuildingsCostAtPeriodStart =
                getCostAtPeriodStart(tangibleAssets.getLandAndBuildings());

        Long motorVehiclesCostAtPeriodStart =
                getCostAtPeriodStart(tangibleAssets.getMotorVehicles());

        Long officeEquipmentCostAtPeriodStart =
                getCostAtPeriodStart(tangibleAssets.getOfficeEquipment());

        Long plantAndMachineryCostAtPeriodStart =
                getCostAtPeriodStart(tangibleAssets.getPlantAndMachinery());

        Long resourceCostAtPeriodStartTotal = fixturesCostAtPeriodStart + landAndBuildingsCostAtPeriodStart +
                                                motorVehiclesCostAtPeriodStart + officeEquipmentCostAtPeriodStart +
                                                plantAndMachineryCostAtPeriodStart;

        Long costAtPeriodStart = getCostAtPeriodStart(tangibleAssets.getTotal());

        if (!costAtPeriodStart.equals(resourceCostAtPeriodStartTotal)) {

            addError(errors, incorrectTotal, getJsonPath(TangibleSubResource.TOTAL, COST_AT_PERIOD_START));
        }
    }

    private void validateAdditionsTotal(Errors errors, TangibleAssets tangibleAssets) {

        Long fixturesAdditions =
                getAdditions(tangibleAssets.getFixturesAndFittings());

        Long landAndBuildingsAdditions =
                getAdditions(tangibleAssets.getLandAndBuildings());

        Long motorVehiclesAdditions =
                getAdditions(tangibleAssets.getMotorVehicles());

        Long officeEquipmentAdditions =
                getAdditions(tangibleAssets.getOfficeEquipment());

        Long plantAndMachineryAdditions =
                getAdditions(tangibleAssets.getPlantAndMachinery());

        Long resourceAdditionsTotal = fixturesAdditions + landAndBuildingsAdditions +
                                        motorVehiclesAdditions + officeEquipmentAdditions +
                                        plantAndMachineryAdditions;

        Long additions = getAdditions(tangibleAssets.getTotal());

        if (!additions.equals(resourceAdditionsTotal)) {

            addError(errors, incorrectTotal, getJsonPath(TangibleSubResource.TOTAL, ADDITIONS));
        }
    }

    private void validateDisposalsTotal(Errors errors, TangibleAssets tangibleAssets) {

        Long fixturesDisposals =
                getDisposals(tangibleAssets.getFixturesAndFittings());

        Long landAndBuildingsDisposals =
                getDisposals(tangibleAssets.getLandAndBuildings());

        Long motorVehiclesDisposals =
                getDisposals(tangibleAssets.getMotorVehicles());

        Long officeEquipmentDisposals =
                getDisposals(tangibleAssets.getOfficeEquipment());

        Long plantAndMachineryDisposals =
                getDisposals(tangibleAssets.getPlantAndMachinery());

        Long resourceDisposalsTotal = fixturesDisposals + landAndBuildingsDisposals +
                                        motorVehiclesDisposals + officeEquipmentDisposals +
                                        plantAndMachineryDisposals;

        Long disposals = getDisposals(tangibleAssets.getTotal());

        if (!disposals.equals(resourceDisposalsTotal)) {

            addError(errors, incorrectTotal, getJsonPath(TangibleSubResource.TOTAL, DISPOSALS));
        }
    }

    private void validateRevaluationsTotal(Errors errors, TangibleAssets tangibleAssets) {

        Long fixturesRevaluations =
                getRevaluations(tangibleAssets.getFixturesAndFittings());

        Long landAndBuildingsRevaluations =
                getRevaluations(tangibleAssets.getLandAndBuildings());

        Long motorVehiclesRevaluations =
                getRevaluations(tangibleAssets.getMotorVehicles());

        Long officeEquipmentRevaluations =
                getRevaluations(tangibleAssets.getOfficeEquipment());

        Long plantAndMachineryRevaluations =
                getRevaluations(tangibleAssets.getPlantAndMachinery());

        Long resourceRevaluationsTotal = fixturesRevaluations + landAndBuildingsRevaluations +
                                            motorVehiclesRevaluations + officeEquipmentRevaluations +
                                            plantAndMachineryRevaluations;

        Long revaluations = getRevaluations(tangibleAssets.getTotal());

        if (!revaluations.equals(resourceRevaluationsTotal)) {

            addError(errors, incorrectTotal, getJsonPath(TangibleSubResource.TOTAL, REVALUATIONS));
        }
    }

    private void validateTransfersTotal(Errors errors, TangibleAssets tangibleAssets) {

        Long fixturesTransfers =
                getTransfers(tangibleAssets.getFixturesAndFittings());

        Long landAndBuildingsTransfers =
                getTransfers(tangibleAssets.getLandAndBuildings());

        Long motorVehiclesTransfers =
                getTransfers(tangibleAssets.getMotorVehicles());

        Long officeEquipmentTransfers =
                getTransfers(tangibleAssets.getOfficeEquipment());

        Long plantAndMachineryTransfers =
                getTransfers(tangibleAssets.getPlantAndMachinery());

        Long resourceTransfersTotal = fixturesTransfers + landAndBuildingsTransfers +
                                        motorVehiclesTransfers + officeEquipmentTransfers +
                                        plantAndMachineryTransfers;

        Long transfers = getTransfers(tangibleAssets.getTotal());

        if (!transfers.equals(resourceTransfersTotal)) {

            addError(errors, incorrectTotal, getJsonPath(TangibleSubResource.TOTAL, TRANSFERS));
        }
    }

    private void validateCostAtPeriodEndTotal(Errors errors, TangibleAssets tangibleAssets) {

        Long fixturesCostAtPeriodEnd =
                getCostAtPeriodEnd(tangibleAssets.getFixturesAndFittings());

        Long landAndBuildingsCostAtPeriodEnd =
                getCostAtPeriodEnd(tangibleAssets.getLandAndBuildings());

        Long motorVehiclesCostAtPeriodEnd =
                getCostAtPeriodEnd(tangibleAssets.getMotorVehicles());

        Long officeEquipmentCostAtPeriodEnd =
                getCostAtPeriodEnd(tangibleAssets.getOfficeEquipment());

        Long plantAndMachineryCostAtPeriodEnd =
                getCostAtPeriodEnd(tangibleAssets.getPlantAndMachinery());

        Long resourceCostAtPeriodEndTotal = fixturesCostAtPeriodEnd + landAndBuildingsCostAtPeriodEnd +
                                            motorVehiclesCostAtPeriodEnd + officeEquipmentCostAtPeriodEnd +
                                            plantAndMachineryCostAtPeriodEnd;

        Long costAtPeriodEnd = getCostAtPeriodEnd(tangibleAssets.getTotal());

        if (!costAtPeriodEnd.equals(resourceCostAtPeriodEndTotal)) {

            addError(errors, incorrectTotal, getJsonPath(TangibleSubResource.TOTAL, COST_AT_PERIOD_END));
        }
    }

    private void validateDepreciationAtPeriodStartTotal(Errors errors, TangibleAssets tangibleAssets) {

        Long fixturesDepreciationAtPeriodStart =
                getDepreciationAtPeriodStart(tangibleAssets.getFixturesAndFittings());

        Long landAndBuildingsDepreciationAtPeriodStart =
                getDepreciationAtPeriodStart(tangibleAssets.getLandAndBuildings());

        Long motorVehiclesDepreciationAtPeriodStart =
                getDepreciationAtPeriodStart(tangibleAssets.getMotorVehicles());

        Long officeEquipmentDepreciationAtPeriodStart =
                getDepreciationAtPeriodStart(tangibleAssets.getOfficeEquipment());

        Long plantAndMachineryDepreciationAtPeriodStart =
                getDepreciationAtPeriodStart(tangibleAssets.getPlantAndMachinery());

        Long resourceDepreciationAtPeriodStartTotal = fixturesDepreciationAtPeriodStart + landAndBuildingsDepreciationAtPeriodStart +
                                                        motorVehiclesDepreciationAtPeriodStart + officeEquipmentDepreciationAtPeriodStart +
                                                        plantAndMachineryDepreciationAtPeriodStart;

        Long depreciationAtPeriodStart = getDepreciationAtPeriodStart(tangibleAssets.getTotal());

        if (!depreciationAtPeriodStart.equals(resourceDepreciationAtPeriodStartTotal)) {

            addError(errors, incorrectTotal, getJsonPath(TangibleSubResource.TOTAL, DEPRECIATION_AT_PERIOD_START));
        }
    }

    private void validateChargeForYearTotal(Errors errors, TangibleAssets tangibleAssets) {

        Long fixturesChargeForYear =
                getChargeForYear(tangibleAssets.getFixturesAndFittings());

        Long landAndBuildingsChargeForYear =
                getChargeForYear(tangibleAssets.getLandAndBuildings());

        Long motorVehiclesChargeForYear =
                getChargeForYear(tangibleAssets.getMotorVehicles());

        Long officeEquipmentChargeForYear =
                getChargeForYear(tangibleAssets.getOfficeEquipment());

        Long plantAndMachineryChargeForYear =
                getChargeForYear(tangibleAssets.getPlantAndMachinery());

        Long resourceChargeForYearTotal = fixturesChargeForYear + landAndBuildingsChargeForYear +
                                            motorVehiclesChargeForYear + officeEquipmentChargeForYear +
                                            plantAndMachineryChargeForYear;

        Long chargeForYear = getChargeForYear(tangibleAssets.getTotal());

        if (!chargeForYear.equals(resourceChargeForYearTotal)) {

            addError(errors, incorrectTotal, getJsonPath(TangibleSubResource.TOTAL, CHARGE_FOR_YEAR));
        }
    }

    private void validateOnDisposalsTotal(Errors errors, TangibleAssets tangibleAssets) {

        Long fixturesOnDisposals =
                getOnDisposals(tangibleAssets.getFixturesAndFittings());

        Long landAndBuildingsOnDisposals =
                getOnDisposals(tangibleAssets.getLandAndBuildings());

        Long motorVehiclesOnDisposals =
                getOnDisposals(tangibleAssets.getMotorVehicles());

        Long officeEquipmentOnDisposals =
                getOnDisposals(tangibleAssets.getOfficeEquipment());

        Long plantAndMachineryOnDisposals =
                getOnDisposals(tangibleAssets.getPlantAndMachinery());

        Long resourceOnDisposalsTotal = fixturesOnDisposals + landAndBuildingsOnDisposals +
                                        motorVehiclesOnDisposals + officeEquipmentOnDisposals +
                                        plantAndMachineryOnDisposals;

        Long onDisposals = getOnDisposals(tangibleAssets.getTotal());

        if (!onDisposals.equals(resourceOnDisposalsTotal)) {

            addError(errors, incorrectTotal, getJsonPath(TangibleSubResource.TOTAL, ON_DISPOSALS));
        }
    }

    private void validateOtherAdjustmentsTotal(Errors errors, TangibleAssets tangibleAssets) {

        Long fixturesOtherAdjustments =
                getOtherAdjustments(tangibleAssets.getFixturesAndFittings());

        Long landAndBuildingsOtherAdjustments =
                getOtherAdjustments(tangibleAssets.getLandAndBuildings());

        Long motorVehiclesOtherAdjustments =
                getOtherAdjustments(tangibleAssets.getMotorVehicles());

        Long officeEquipmentOtherAdjustments =
                getOtherAdjustments(tangibleAssets.getOfficeEquipment());

        Long plantAndMachineryOtherAdjustments =
                getOtherAdjustments(tangibleAssets.getPlantAndMachinery());

        Long resourceOtherAdjustmentsTotal = fixturesOtherAdjustments + landAndBuildingsOtherAdjustments +
                                                motorVehiclesOtherAdjustments + officeEquipmentOtherAdjustments +
                                                plantAndMachineryOtherAdjustments;

        Long otherAdjustments = getOtherAdjustments(tangibleAssets.getTotal());

        if (!otherAdjustments.equals(resourceOtherAdjustmentsTotal)) {

            addError(errors, incorrectTotal, getJsonPath(TangibleSubResource.TOTAL, OTHER_ADJUSTMENTS));
        }
    }

    private void validateDepreciationAtPeriodEndTotal(Errors errors, TangibleAssets tangibleAssets) {

        Long fixturesDepreciationAtPeriodEnd =
                getDepreciationAtPeriodEnd(tangibleAssets.getFixturesAndFittings());

        Long landAndBuildingsDepreciationAtPeriodEnd =
                getDepreciationAtPeriodEnd(tangibleAssets.getLandAndBuildings());

        Long motorVehiclesDepreciationAtPeriodEnd =
                getDepreciationAtPeriodEnd(tangibleAssets.getMotorVehicles());

        Long officeEquipmentDepreciationAtPeriodEnd =
                getDepreciationAtPeriodEnd(tangibleAssets.getOfficeEquipment());

        Long plantAndMachineryDepreciationAtPeriodEnd =
                getDepreciationAtPeriodEnd(tangibleAssets.getPlantAndMachinery());

        Long resourceDepreciationAtPeriodEndTotal = fixturesDepreciationAtPeriodEnd + landAndBuildingsDepreciationAtPeriodEnd +
                                                    motorVehiclesDepreciationAtPeriodEnd + officeEquipmentDepreciationAtPeriodEnd +
                                                    plantAndMachineryDepreciationAtPeriodEnd;

        Long depreciationAtPeriodEnd = getDepreciationAtPeriodEnd(tangibleAssets.getTotal());

        if (!depreciationAtPeriodEnd.equals(resourceDepreciationAtPeriodEndTotal)) {

            addError(errors, incorrectTotal, getJsonPath(TangibleSubResource.TOTAL, DEPRECIATION_AT_PERIOD_END));
        }
    }

    private void validateCurrentNetBookValuesTotal(Errors errors, TangibleAssets tangibleAssets) {

        Long fixturesNetBookValue =
                getNetBookValueAtEndOfCurrentPeriod(tangibleAssets.getFixturesAndFittings());

        Long landAndBuildingsNetBookValue =
                getNetBookValueAtEndOfCurrentPeriod(tangibleAssets.getLandAndBuildings());

        Long motorVehiclesNetBookValue =
                getNetBookValueAtEndOfCurrentPeriod(tangibleAssets.getMotorVehicles());

        Long officeEquipmentNetBookValue =
                getNetBookValueAtEndOfCurrentPeriod(tangibleAssets.getOfficeEquipment());

        Long plantAndMachineryNetBookValue =
                getNetBookValueAtEndOfCurrentPeriod(tangibleAssets.getPlantAndMachinery());

        Long resourceNetBookValueTotal = fixturesNetBookValue + landAndBuildingsNetBookValue +
                                            motorVehiclesNetBookValue + officeEquipmentNetBookValue +
                                            plantAndMachineryNetBookValue;

        Long netBookValueAtEndOfCurrentPeriod = getNetBookValueAtEndOfCurrentPeriod(tangibleAssets.getTotal());

        if (!netBookValueAtEndOfCurrentPeriod.equals(resourceNetBookValueTotal)) {

            addError(errors, incorrectTotal, getJsonPath(TangibleSubResource.TOTAL, NET_BOOK_VALUE_CURRENT_PERIOD));
        }
    }

    private void validatePreviousNetBookValuesTotal(Errors errors, TangibleAssets tangibleAssets) {

        Long fixturesNetBookValue =
                getNetBookValueAtEndOfPreviousPeriod(tangibleAssets.getFixturesAndFittings());

        Long landAndBuildingsNetBookValue =
                getNetBookValueAtEndOfPreviousPeriod(tangibleAssets.getLandAndBuildings());

        Long motorVehiclesNetBookValue =
                getNetBookValueAtEndOfPreviousPeriod(tangibleAssets.getMotorVehicles());

        Long officeEquipmentNetBookValue =
                getNetBookValueAtEndOfPreviousPeriod(tangibleAssets.getOfficeEquipment());

        Long plantAndMachineryNetBookValue =
                getNetBookValueAtEndOfPreviousPeriod(tangibleAssets.getPlantAndMachinery());

        Long resourceNetBookValueTotal = fixturesNetBookValue + landAndBuildingsNetBookValue +
                                            motorVehiclesNetBookValue + officeEquipmentNetBookValue +
                                            plantAndMachineryNetBookValue;

        Long netBookValueAtEndOfPreviousPeriod = getNetBookValueAtEndOfPreviousPeriod(tangibleAssets.getTotal());

        if (!netBookValueAtEndOfPreviousPeriod.equals(resourceNetBookValueTotal)) {

            addError(errors, incorrectTotal, getJsonPath(TangibleSubResource.TOTAL, NET_BOOK_VALUE_PREVIOUS_PERIOD));
        }
    }

    private void validateCosts(Errors errors, TangibleAssetsResource tangibleAssetsResource, TangibleSubResource subResource) {

        Long atPeriodStart =
                getCostAtPeriodStart(tangibleAssetsResource);

        Long additions =
                getAdditions(tangibleAssetsResource);

        Long disposals =
                getDisposals(tangibleAssetsResource);

        Long revaluations =
                getRevaluations(tangibleAssetsResource);

        Long transfers =
                getTransfers(tangibleAssetsResource);

        Long calculatedAtPeriodEnd = atPeriodStart + additions - disposals + revaluations + transfers;

        if (!tangibleAssetsResource.getCost().getAtPeriodEnd().equals(calculatedAtPeriodEnd)) {

            addError(errors, incorrectTotal, getJsonPath(subResource, COST_AT_PERIOD_END));
        }
    }

    private void validateDepreciation(Errors errors, TangibleAssetsResource tangibleAssetsResource, TangibleSubResource subResource) {

        Long atPeriodStart =
                getDepreciationAtPeriodStart(tangibleAssetsResource);

        Long chargeForYear =
                getChargeForYear(tangibleAssetsResource);

        Long onDisposals =
                getOnDisposals(tangibleAssetsResource);

        Long otherAdjustments =
                getOtherAdjustments(tangibleAssetsResource);

        Long calculatedAtPeriodEnd = atPeriodStart + chargeForYear - onDisposals + otherAdjustments;

        Long atPeriodEnd = getDepreciationAtPeriodEnd(tangibleAssetsResource);

        if (!atPeriodEnd.equals(calculatedAtPeriodEnd)) {

            addError(errors, incorrectTotal, getJsonPath(subResource, DEPRECIATION_AT_PERIOD_END));
        }
    }

    private void validateCurrentNetBookValue(Errors errors, TangibleAssetsResource tangibleAssetsResource, TangibleSubResource subResource) {

        Long costAtPeriodEnd =
                getCostAtPeriodEnd(tangibleAssetsResource);

        Long depreciationAtPeriodEnd =
                getDepreciationAtPeriodEnd(tangibleAssetsResource);

        Long calculatedCurrentNetBookValue = costAtPeriodEnd - depreciationAtPeriodEnd;

        if (!tangibleAssetsResource.getNetBookValueAtEndOfCurrentPeriod().equals(calculatedCurrentNetBookValue)) {

            addError(errors, incorrectTotal, getJsonPath(subResource, NET_BOOK_VALUE_CURRENT_PERIOD));
        }
    }

    private void validatePreviousNetBookValue(Errors errors, TangibleAssetsResource tangibleAssetsResource, TangibleSubResource subResource) {

        Long costAtPeriodStart =
                getCostAtPeriodStart(tangibleAssetsResource);

        Long depreciationAtPeriodStart =
                getDepreciationAtPeriodStart(tangibleAssetsResource);

        Long calculatedPreviousNetBookValue = costAtPeriodStart - depreciationAtPeriodStart;

        if (!tangibleAssetsResource.getNetBookValueAtEndOfPreviousPeriod().equals(calculatedPreviousNetBookValue)) {

            addError(errors, incorrectTotal, getJsonPath(subResource, NET_BOOK_VALUE_PREVIOUS_PERIOD));
        }
    }

    private void validatePresenceOfMultipleYearFilerFields(Errors errors, TangibleAssetsResource tangibleAssetsResource, TangibleSubResource subResource, List<TangibleSubResource> invalidSubResources) {

        boolean subResourceInvalid = false;

        if (tangibleAssetsResource.getNetBookValueAtEndOfCurrentPeriod() != null  || tangibleAssetsResource.getNetBookValueAtEndOfPreviousPeriod() != null) {

            if (tangibleAssetsResource.getNetBookValueAtEndOfPreviousPeriod() == null) {

                addError(errors, valueRequired, getJsonPath(subResource, NET_BOOK_VALUE_PREVIOUS_PERIOD));
                subResourceInvalid = true;
            }

            if (tangibleAssetsResource.getNetBookValueAtEndOfCurrentPeriod() == null) {

                addError(errors, valueRequired, getJsonPath(subResource, NET_BOOK_VALUE_CURRENT_PERIOD));
                subResourceInvalid = true;
            }

            subResourceInvalid = isCostMultipleYearSubResourceInvalid(errors, tangibleAssetsResource.getCost(),
                subResource, subResourceInvalid);

            subResourceInvalid = isDepreciationMultipleYearSubResourceInvalid(errors, tangibleAssetsResource.getDepreciation(),
                subResource, subResourceInvalid);

        } else {

            if (hasMultipleYearFilerNonNetBookValueFieldsSet(tangibleAssetsResource)) {

                addError(errors, valueRequired, getJsonPath(subResource, NET_BOOK_VALUE_CURRENT_PERIOD));
                addError(errors, valueRequired, getJsonPath(subResource, NET_BOOK_VALUE_PREVIOUS_PERIOD));
                subResourceInvalid = true;
            }
        }

        if (subResourceInvalid) {

            invalidSubResources.add(subResource);
        }
    }

    private void validatePresenceOfSingleYearFilerFields(Errors errors, TangibleAssetsResource tangibleAssetsResource, TangibleSubResource subResource, List<TangibleSubResource> invalidSubResources) {

        boolean subResourceInvalid = false;

        if (tangibleAssetsResource.getCost() != null && tangibleAssetsResource.getCost().getAtPeriodStart() != null) {

            addError(errors, unexpectedData, getJsonPath(subResource, COST_AT_PERIOD_START));
            subResourceInvalid = true;
        }

        if (tangibleAssetsResource.getDepreciation() != null && tangibleAssetsResource.getDepreciation().getAtPeriodStart() != null) {

            addError(errors, unexpectedData, getJsonPath(subResource, DEPRECIATION_AT_PERIOD_START));
            subResourceInvalid = true;
        }

        if (tangibleAssetsResource.getNetBookValueAtEndOfPreviousPeriod() != null) {

            addError(errors, unexpectedData, getJsonPath(subResource, NET_BOOK_VALUE_PREVIOUS_PERIOD));
            subResourceInvalid = true;
        }

        if (tangibleAssetsResource.getNetBookValueAtEndOfCurrentPeriod() != null) {

            subResourceInvalid = isCostSingleYearSubResourceInvalid(errors, tangibleAssetsResource.getCost(),
                subResource, subResourceInvalid);

            subResourceInvalid = isDepreciationSingleYearSubResourceInvalid(errors,
                tangibleAssetsResource.getDepreciation(), subResource,
                subResourceInvalid);
        } else {

            if (hasSingleYearFilerNonNetBookValueFieldsSet(tangibleAssetsResource)) {

                addError(errors, valueRequired, getJsonPath(subResource, NET_BOOK_VALUE_CURRENT_PERIOD));
                subResourceInvalid = true;
            }
        }

        if (subResourceInvalid) {

            invalidSubResources.add(subResource);
        }
    }

    private boolean isCostSingleYearSubResourceInvalid(Errors errors,
        Cost cost, TangibleSubResource subResource,
        boolean subResourceInvalid) {

        if (cost == null || cost.getAtPeriodEnd() == null) {

            addError(errors, valueRequired, getJsonPath(subResource, COST_AT_PERIOD_END));
            subResourceInvalid = true;
        }
        return subResourceInvalid;
    }

    private boolean isCostMultipleYearSubResourceInvalid(Errors errors,
        Cost cost, TangibleSubResource subResource,
        boolean subResourceInvalid) {
        if (cost == null || cost.getAtPeriodStart() == null) {

            addError(errors, valueRequired, getJsonPath(subResource, COST_AT_PERIOD_START));
            subResourceInvalid = true;
        }

        subResourceInvalid = isCostSingleYearSubResourceInvalid(errors, cost, subResource, subResourceInvalid);

        return subResourceInvalid;
    }

    private boolean isDepreciationSingleYearSubResourceInvalid(Errors errors,
        Depreciation depreciation, TangibleSubResource subResource,
        boolean subResourceInvalid) {
        if (depreciation != null && hasDepreciationFieldsSet(depreciation)
            && depreciation.getAtPeriodEnd() == null) {

            addError(errors, valueRequired, getJsonPath(subResource, DEPRECIATION_AT_PERIOD_END));
            subResourceInvalid = true;
        }
        return subResourceInvalid;
    }

    private boolean isDepreciationMultipleYearSubResourceInvalid(Errors errors,
        Depreciation depreciation, TangibleSubResource subResource,
        boolean subResourceInvalid) {
        if (depreciation != null && hasDepreciationFieldsSet(depreciation)) {

            if (depreciation.getAtPeriodStart() == null) {

                addError(errors, valueRequired, getJsonPath(subResource, DEPRECIATION_AT_PERIOD_START));
                subResourceInvalid = true;
            }

            subResourceInvalid = isDepreciationSingleYearSubResourceInvalid(errors, depreciation, subResource, subResourceInvalid);

        }
        return subResourceInvalid;
    }

    private boolean hasDepreciationFieldsSet(Depreciation depreciation) {

        return Stream.of(depreciation.getChargeForYear(),
            depreciation.getOnDisposals(),
            depreciation.getOtherAdjustments())
            .anyMatch(Objects::nonNull);
    }

    private boolean hasSingleYearFilerNonNetBookValueFieldsSet(TangibleAssetsResource tangibleAssetsResource) {

        if (tangibleAssetsResource.getCost() != null &&
                Stream.of(tangibleAssetsResource.getCost().getAdditions(),
                            tangibleAssetsResource.getCost().getDisposals(),
                            tangibleAssetsResource.getCost().getRevaluations(),
                            tangibleAssetsResource.getCost().getTransfers(),
                            tangibleAssetsResource.getCost().getAtPeriodEnd())
                    .anyMatch(Objects::nonNull)) {

            return true;
        }

        return tangibleAssetsResource.getDepreciation() != null &&
                Stream.of(tangibleAssetsResource.getDepreciation().getChargeForYear(),
                            tangibleAssetsResource.getDepreciation().getOnDisposals(),
                            tangibleAssetsResource.getDepreciation().getOtherAdjustments(),
                            tangibleAssetsResource.getDepreciation().getAtPeriodEnd())
                    .anyMatch(Objects::nonNull);
    }

    private boolean hasMultipleYearFilerNonNetBookValueFieldsSet(TangibleAssetsResource tangibleAssetsResource) {

        if (tangibleAssetsResource.getCost() != null &&
                Stream.of(tangibleAssetsResource.getCost().getAtPeriodStart(),
                    tangibleAssetsResource.getCost().getAdditions(),
                    tangibleAssetsResource.getCost().getDisposals(),
                    tangibleAssetsResource.getCost().getRevaluations(),
                    tangibleAssetsResource.getCost().getTransfers(),
                    tangibleAssetsResource.getCost().getAtPeriodEnd())
                    .anyMatch(Objects::nonNull)) {

            return true;
        }

        return tangibleAssetsResource.getDepreciation() != null &&
                Stream.of(tangibleAssetsResource.getDepreciation().getAtPeriodStart(),
                    tangibleAssetsResource.getDepreciation().getChargeForYear(),
                    tangibleAssetsResource.getDepreciation().getOnDisposals(),
                    tangibleAssetsResource.getDepreciation().getOtherAdjustments(),
                    tangibleAssetsResource.getDepreciation().getAtPeriodEnd())
                    .anyMatch(Objects::nonNull);
    }

    private Long getCostAtPeriodStart(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(TangibleAssetsResource::getCost)
                .map(Cost::getAtPeriodStart)
                .orElse(0L);
    }

    private Long getAdditions(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(TangibleAssetsResource::getCost)
                .map(Cost::getAdditions)
                .orElse(0L);
    }

    private Long getDisposals(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(TangibleAssetsResource::getCost)
                .map(Cost::getDisposals)
                .orElse(0L);
    }

    private Long getRevaluations(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(TangibleAssetsResource::getCost)
                .map(Cost::getRevaluations)
                .orElse(0L);
    }

    private Long getTransfers(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(TangibleAssetsResource::getCost)
                .map(Cost::getTransfers)
                .orElse(0L);
    }

    private Long getCostAtPeriodEnd(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(TangibleAssetsResource::getCost)
                .map(Cost::getAtPeriodEnd)
                .orElse(0L);
    }

    private Long getDepreciationAtPeriodStart(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(TangibleAssetsResource::getDepreciation)
                .map(Depreciation::getAtPeriodStart)
                .orElse(0L);
    }

    private Long getChargeForYear(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(TangibleAssetsResource::getDepreciation)
                .map(Depreciation::getChargeForYear)
                .orElse(0L);
    }

    private Long getOnDisposals(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(TangibleAssetsResource::getDepreciation)
                .map(Depreciation::getOnDisposals)
                .orElse(0L);
    }

    private Long getOtherAdjustments(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(TangibleAssetsResource::getDepreciation)
                .map(Depreciation::getOtherAdjustments)
                .orElse(0L);
    }

    private Long getDepreciationAtPeriodEnd(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(TangibleAssetsResource::getDepreciation)
                .map(Depreciation::getAtPeriodEnd)
                .orElse(0L);
    }

    private Long getNetBookValueAtEndOfCurrentPeriod(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(TangibleAssetsResource::getNetBookValueAtEndOfCurrentPeriod)
                .orElse(0L);
    }

    private Long getNetBookValueAtEndOfPreviousPeriod(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(TangibleAssetsResource::getNetBookValueAtEndOfPreviousPeriod)
                .orElse(0L);
    }

    private String getJsonPath(TangibleSubResource subResource, String pathSuffix) {

        return TANGIBLE_NOTE + "." + subResource.getJsonPath() + pathSuffix;
    }

    private void crossValidate(TangibleAssets tangibleAssets,
                                HttpServletRequest request,
                                String companyAccountsId,
                                Errors errors) throws DataException {

        BalanceSheet currentPeriodBalanceSheet = getCurrentPeriodBalanceSheet(request,
                companyAccountsId);
        BalanceSheet previousPeriodBalanceSheet = getPreviousPeriodBalanceSheet(request,
                companyAccountsId);

        if (currentPeriodBalanceSheet != null) {
            crossValidateCurrentPeriod(errors, request, companyAccountsId, tangibleAssets);
        }
        
        if (previousPeriodBalanceSheet != null) {
            crossValidatePreviousPeriod(errors, request, companyAccountsId, tangibleAssets);
        }
    }

    private void crossValidateCurrentPeriod(Errors errors, HttpServletRequest request, String companyAccountsId,
                                            TangibleAssets tangibleAssets) throws DataException {

        ResponseObject<CurrentPeriod> currentPeriodResponseObject =
                currentPeriodService.find(companyAccountsId, request);
        CurrentPeriod currentPeriod = currentPeriodResponseObject.getData();

        Long currentPeriodTangible =
                Optional.ofNullable(currentPeriod)
                        .map(CurrentPeriod::getBalanceSheet)
                        .map(BalanceSheet::getFixedAssets)
                        .map(FixedAssets::getTangible)
                        .orElse(null);

        Long currentNetBookValueTotal =
                Optional.ofNullable(tangibleAssets)
                        .map(TangibleAssets::getTotal)
                        .map(TangibleAssetsResource::getNetBookValueAtEndOfCurrentPeriod)
                        .orElse(null);

        if ((currentPeriodTangible != null || currentNetBookValueTotal != null) && (
            (currentPeriodTangible != null && currentNetBookValueTotal == null)
                || currentPeriodTangible == null ||
                (!currentPeriodTangible.equals(currentNetBookValueTotal)))) {
            addError(errors, currentBalanceSheetNotEqual,
                getJsonPath(TangibleSubResource.TOTAL, NET_BOOK_VALUE_CURRENT_PERIOD));
        }
    }

    private void crossValidatePreviousPeriod(Errors errors, HttpServletRequest request, String companyAccountsId,
            TangibleAssets tangibleAssets) throws DataException {

        ResponseObject<PreviousPeriod> previousPeriodResponseObject =
                previousPeriodService.find(companyAccountsId, request);
        PreviousPeriod previousPeriod = previousPeriodResponseObject.getData();

        Long previousPeriodTangible =
                Optional.ofNullable(previousPeriod)
                    .map(PreviousPeriod::getBalanceSheet)
                    .map(BalanceSheet::getFixedAssets)
                    .map(FixedAssets::getTangible)
                    .orElse(null);

        Long previousNetBookValueTotal =
                Optional.ofNullable(tangibleAssets)
                    .map(TangibleAssets::getTotal)
                    .map(TangibleAssetsResource::getNetBookValueAtEndOfPreviousPeriod)
                    .orElse(null);

        if ((previousPeriodTangible != null || previousNetBookValueTotal != null) && (
            (previousPeriodTangible != null && previousNetBookValueTotal == null)
                || previousPeriodTangible == null ||
                (!previousPeriodTangible.equals(previousNetBookValueTotal)))) {
            addError(errors, previousBalanceSheetNotEqual,
                getJsonPath(TangibleSubResource.TOTAL, NET_BOOK_VALUE_PREVIOUS_PERIOD));
        }
    }

    private BalanceSheet getCurrentPeriodBalanceSheet(HttpServletRequest request,
                                                      String companyAccountsId) throws DataException {

        ResponseObject<CurrentPeriod> currentPeriodResponseObject;
        currentPeriodResponseObject = currentPeriodService.find(companyAccountsId, request);

        return Optional.of(currentPeriodResponseObject)
                .map(ResponseObject::getData)
                .map(CurrentPeriod::getBalanceSheet)
                .orElse(null);
    }

    private BalanceSheet getPreviousPeriodBalanceSheet(HttpServletRequest request,
                                                       String companyAccountsId) throws DataException {

        ResponseObject<PreviousPeriod> previousPeriodResponseObject;
        previousPeriodResponseObject = previousPeriodService.find(companyAccountsId, request);

        return Optional.of(previousPeriodResponseObject)
                .map(ResponseObject::getData)
                .map(PreviousPeriod::getBalanceSheet)
                .orElse(null);
    }

    private enum TangibleSubResource {

        FIXTURES_AND_FITTINGS("fixtures_and_fittings"),
        LAND_AND_BUILDINGS("land_and_buildings"),
        MOTOR_VEHICLES("motor_vehicles"),
        OFFICE_EQUIPMENT("office_equipment"),
        PLANT_AND_MACHINERY("plant_and_machinery"),
        TOTAL("total");

        private String jsonPath;

        TangibleSubResource(String jsonPath) {
            this.jsonPath = jsonPath;
        }

        public String getJsonPath() {
            return jsonPath;
        }
    }

    @Override
    public AccountsResource getAccountsResource() { return AccountsResource.SMALL_FULL_TANGIBLE_ASSETS; }
}
