package uk.gov.companieshouse.api.accounts.validation;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.TangibleAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.TangibleAssetsResource;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

@Component
public class TangibleAssetsValidator extends BaseValidator {

    private CompanyService companyService;

    @Autowired
    public TangibleAssetsValidator(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Value("${incorrect.total}")
    private String incorrectTotal;

    @Value("${value.required}")
    private String valueRequired;

    @Value("${inconsistent.data}")
    private String inconsistentData;

    private static final String TANGIBLE_NOTE = "$.tangible_assets.";
    private static final String COST_AT_PERIOD_END = ".cost.at_period_end";
    private static final String COST_AT_PERIOD_START = ".cost.at_period_start";
    private static final String DEPRECIATION_AT_PERIOD_END = ".depreciation.at_period_end";
    private static final String DEPRECIATION_AT_PERIOD_START = ".depreciation.at_period_start";
    private static final String NET_BOOK_VALUE_CURRENT_PERIOD = ".net_book_value_at_end_of_current_period";
    private static final String NET_BOOK_VALUE_PREVIOUS_PERIOD = ".net_book_value_at_end_of_previous_period";
    private static final String TOTAL_COST_AT_PERIOD_START = "$.tangible_assets.total.cost.at_period_start";
    private static final String TOTAL_ADDITIONS = "$.tangible_assets.total.cost.additions";
    private static final String TOTAL_DISPOSALS = "$.tangible_assets.total.cost.disposals";
    private static final String TOTAL_REVALUATIONS = "$.tangible_assets.total.cost.revaluations";
    private static final String TOTAL_TRANSFERS = "$.tangible_assets.total.cost.transfers";
    private static final String TOTAL_COST_AT_PERIOD_END = "$.tangible_assets.total.cost.at_period_end";
    private static final String TOTAL_DEPRECIATION_AT_PERIOD_START = "$.tangible_assets.depreciation.cost.at_period_start";
    private static final String TOTAL_CHARGE_FOR_YEAR = "$.tangible_assets.total.depreciation.charge_for_year";
    private static final String TOTAL_ON_DISPOSALS = "$.tangible_assets.total.depreciation.on_disposals";
    private static final String TOTAL_OTHER_ADJUSTMENTS = "$.tangible_assets.total.depreciation.other_adjustments";
    private static final String TOTAL_DEPRECIATION_AT_PERIOD_END = "$.tangible_assets.total.depreciation.at_period_end";
    private static final String TOTAL_NET_BOOK_VALUE_CURRENT = "$.tangible_assets.total.net_book_value_at_end_of_current_period";
    private static final String TOTAL_NET_BOOK_VALUE_PREVIOUS = "$.tangible_assets.total.net_book_value_at_end_of_previous_period";

    public Errors validateTangibleAssets(TangibleAssets tangibleAssets, Transaction transaction, String companyAccountsId, HttpServletRequest request)
            throws DataException {

        Errors errors = new Errors();

        try {
            boolean isMultipleYearFiler = companyService.isMultipleYearFiler(transaction);

            validateSubResourceTotals(tangibleAssets, errors, isMultipleYearFiler);
            if (errors.hasErrors()) {
                return errors;
            }

            validateTotalFieldsMatch(errors, tangibleAssets);

        } catch (ServiceException e) {

            throw new DataException(e.getMessage(), e);
        }

        return errors;
    }

    private void validateSubResourceShape(TangibleAssets tangibleAssets, Errors errors, boolean isMultipleYearFiler) {

        if (tangibleAssets.getFixturesAndFittings() != null) {

            validatePresenceOfConditionalFields(errors, tangibleAssets.getFixturesAndFittings(), TangibleSubResource.FIXTURES_AND_FITTINGS, isMultipleYearFiler);
        }
    }

    private void validateSubResourceTotals(TangibleAssets tangibleAssets, Errors errors, boolean isMultipleYearFiler) {

        if (tangibleAssets.getFixturesAndFittings() != null) {

            validateCosts(errors, tangibleAssets.getFixturesAndFittings(), TangibleSubResource.FIXTURES_AND_FITTINGS);
            validateDepreciation(errors, tangibleAssets.getFixturesAndFittings(), TangibleSubResource.FIXTURES_AND_FITTINGS);
            validateCurrentNetBookValue(errors, tangibleAssets.getFixturesAndFittings(), TangibleSubResource.FIXTURES_AND_FITTINGS);
            if (isMultipleYearFiler) {
                validatePreviousNetBookValue(errors, tangibleAssets.getFixturesAndFittings(),
                        TangibleSubResource.FIXTURES_AND_FITTINGS);
            }
        }

        if (tangibleAssets.getLandAndBuildings() != null) {

            validateCosts(errors, tangibleAssets.getLandAndBuildings(), TangibleSubResource.LAND_AND_BUILDINGS);
            validateDepreciation(errors, tangibleAssets.getLandAndBuildings(), TangibleSubResource.LAND_AND_BUILDINGS);
            validateCurrentNetBookValue(errors, tangibleAssets.getLandAndBuildings(), TangibleSubResource.LAND_AND_BUILDINGS);
            if (isMultipleYearFiler) {
                validatePreviousNetBookValue(errors, tangibleAssets.getLandAndBuildings(),
                        TangibleSubResource.LAND_AND_BUILDINGS);
            }
        }

        if (tangibleAssets.getMotorVehicles() != null) {

            validateCosts(errors, tangibleAssets.getMotorVehicles(), TangibleSubResource.MOTOR_VEHICLES);
            validateDepreciation(errors, tangibleAssets.getMotorVehicles(), TangibleSubResource.MOTOR_VEHICLES);
            validateCurrentNetBookValue(errors, tangibleAssets.getMotorVehicles(), TangibleSubResource.MOTOR_VEHICLES);
            if (isMultipleYearFiler) {
                validatePreviousNetBookValue(errors, tangibleAssets.getMotorVehicles(),
                        TangibleSubResource.MOTOR_VEHICLES);
            }
        }

        if (tangibleAssets.getOfficeEquipment() != null) {

            validateCosts(errors, tangibleAssets.getOfficeEquipment(), TangibleSubResource.OFFICE_EQUIPMENT);
            validateDepreciation(errors, tangibleAssets.getOfficeEquipment(), TangibleSubResource.OFFICE_EQUIPMENT);
            validateCurrentNetBookValue(errors, tangibleAssets.getOfficeEquipment(), TangibleSubResource.OFFICE_EQUIPMENT);
            if (isMultipleYearFiler) {
                validatePreviousNetBookValue(errors, tangibleAssets.getOfficeEquipment(),
                        TangibleSubResource.OFFICE_EQUIPMENT);
            }
        }

        if (tangibleAssets.getPlantAndMachinery() != null) {

            validateCosts(errors, tangibleAssets.getPlantAndMachinery(), TangibleSubResource.PLANT_AND_MACHINERY);
            validateDepreciation(errors, tangibleAssets.getPlantAndMachinery(), TangibleSubResource.PLANT_AND_MACHINERY);
            validateCurrentNetBookValue(errors, tangibleAssets.getPlantAndMachinery(), TangibleSubResource.PLANT_AND_MACHINERY);
            if (isMultipleYearFiler) {
                validatePreviousNetBookValue(errors, tangibleAssets.getPlantAndMachinery(),
                        TangibleSubResource.PLANT_AND_MACHINERY);
            }
        }
    }

    private void validateTotalFieldsMatch(Errors errors, TangibleAssets tangibleAssets) {

        validateCostAtPeriodStartTotal(errors, tangibleAssets);
        validateAdditionsTotal(errors, tangibleAssets);
        validateDisposalsTotal(errors, tangibleAssets);
        validateRevaluationsTotal(errors, tangibleAssets);
        validateTransfersTotal(errors, tangibleAssets);
        validateCostAtPeriodEndTotal(errors, tangibleAssets);
        validateDepreciationAtPeriodStartTotal(errors, tangibleAssets);
        validateChargeForYearTotal(errors, tangibleAssets);
        validateOnDisposalsTotal(errors, tangibleAssets);
        validateOtherAdjustmentsTotal(errors, tangibleAssets);
        validateDepreciationAtPeriodEndTotal(errors, tangibleAssets);
        validateCurrentNetBookValuesTotal(errors, tangibleAssets);
        validatePreviousNetBookValuesTotal(errors, tangibleAssets);
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

        if (!tangibleAssets.getTotal().getCost().getAtPeriodStart().equals(resourceCostAtPeriodStartTotal)) {

            addError(errors, incorrectTotal, TOTAL_COST_AT_PERIOD_START);
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

        if (!tangibleAssets.getTotal().getCost().getAdditions().equals(resourceAdditionsTotal)) {

            addError(errors, incorrectTotal, TOTAL_ADDITIONS);
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

        if (!tangibleAssets.getTotal().getCost().getDisposals().equals(resourceDisposalsTotal)) {

            addError(errors, incorrectTotal, TOTAL_DISPOSALS);
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

        if (!tangibleAssets.getTotal().getCost().getRevaluations().equals(resourceRevaluationsTotal)) {

            addError(errors, incorrectTotal, TOTAL_REVALUATIONS);
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

        if (!tangibleAssets.getTotal().getCost().getTransfers().equals(resourceTransfersTotal)) {

            addError(errors, incorrectTotal, TOTAL_TRANSFERS);
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

        if (!tangibleAssets.getTotal().getCost().getAtPeriodEnd().equals(resourceCostAtPeriodEndTotal)) {

            addError(errors, incorrectTotal, TOTAL_COST_AT_PERIOD_END);
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

        if (!tangibleAssets.getTotal().getDepreciation().getAtPeriodStart().equals(resourceDepreciationAtPeriodStartTotal)) {

            addError(errors, incorrectTotal, TOTAL_DEPRECIATION_AT_PERIOD_START);
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

        if (!tangibleAssets.getTotal().getDepreciation().getChargeForYear().equals(resourceChargeForYearTotal)) {

            addError(errors, incorrectTotal, TOTAL_CHARGE_FOR_YEAR);
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

        if (!tangibleAssets.getTotal().getDepreciation().getOnDisposals().equals(resourceOnDisposalsTotal)) {

            addError(errors, incorrectTotal, TOTAL_ON_DISPOSALS);
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

        if (!tangibleAssets.getTotal().getDepreciation().getOtherAdjustments().equals(resourceOtherAdjustmentsTotal)) {

            addError(errors, incorrectTotal, TOTAL_OTHER_ADJUSTMENTS);
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

        if (!tangibleAssets.getTotal().getDepreciation().getAtPeriodEnd().equals(resourceDepreciationAtPeriodEndTotal)) {

            addError(errors, incorrectTotal, TOTAL_DEPRECIATION_AT_PERIOD_END);
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

        if (!tangibleAssets.getTotal().getNetBookValueAtEndOfCurrentPeriod().equals(resourceNetBookValueTotal)) {

            addError(errors, incorrectTotal, TOTAL_NET_BOOK_VALUE_CURRENT);
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

        if (!tangibleAssets.getTotal().getNetBookValueAtEndOfPreviousPeriod().equals(resourceNetBookValueTotal)) {

            addError(errors, incorrectTotal, TOTAL_NET_BOOK_VALUE_PREVIOUS);
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

            addError(errors, incorrectTotal, TANGIBLE_NOTE + subResource.getJsonPath() + COST_AT_PERIOD_END);
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

        if (!tangibleAssetsResource.getDepreciation().getAtPeriodEnd().equals(calculatedAtPeriodEnd)) {

            addError(errors, incorrectTotal, TANGIBLE_NOTE + subResource.getJsonPath() + DEPRECIATION_AT_PERIOD_END);
        }
    }

    private void validateCurrentNetBookValue(Errors errors, TangibleAssetsResource tangibleAssetsResource, TangibleSubResource subResource) {

        Long costAtPeriodEnd =
                getCostAtPeriodEnd(tangibleAssetsResource);

        Long depreciationAtPeriodEnd =
                getDepreciationAtPeriodEnd(tangibleAssetsResource);

        Long calculatedCurrentNetBookValue = costAtPeriodEnd - depreciationAtPeriodEnd;

        if (!tangibleAssetsResource.getNetBookValueAtEndOfCurrentPeriod().equals(calculatedCurrentNetBookValue)) {

            addError(errors, incorrectTotal, TANGIBLE_NOTE + subResource.getJsonPath() + NET_BOOK_VALUE_CURRENT_PERIOD);
        }
    }

    private void validatePreviousNetBookValue(Errors errors, TangibleAssetsResource tangibleAssetsResource, TangibleSubResource subResource) {

        Long costAtPeriodStart =
                getCostAtPeriodStart(tangibleAssetsResource);

        Long depreciationAtPeriodStart =
                getDepreciationAtPeriodStart(tangibleAssetsResource);

        Long calculatedPreviousNetBookValue = costAtPeriodStart - depreciationAtPeriodStart;

        if (!tangibleAssetsResource.getNetBookValueAtEndOfPreviousPeriod().equals(calculatedPreviousNetBookValue)) {

            addError(errors, incorrectTotal, TANGIBLE_NOTE + subResource.getJsonPath() + NET_BOOK_VALUE_PREVIOUS_PERIOD);
        }
    }

    private void validatePresenceOfConditionalFields(Errors errors, TangibleAssetsResource tangibleAssetsResource,
                                                    TangibleSubResource subResource, boolean isMultipleYearFiler) {

        if (isMultipleYearFiler) {
            validatePresenceOfMultipleYearFilerFields(errors, tangibleAssetsResource, subResource);
        } else {
            validatePresenceOfSingleYearFilerFields(errors, tangibleAssetsResource, subResource);
        }
    }

    private void validatePresenceOfMultipleYearFilerFields(Errors errors, TangibleAssetsResource tangibleAssetsResource, TangibleSubResource subResource) {

        // Validate multi year filer fields here
    }

    private void validatePresenceOfSingleYearFilerFields(Errors errors, TangibleAssetsResource tangibleAssetsResource, TangibleSubResource subResource) {

        if (tangibleAssetsResource.getCost() != null && tangibleAssetsResource.getCost().getAtPeriodStart() != null) {

            addError(errors, inconsistentData, TANGIBLE_NOTE + subResource + COST_AT_PERIOD_START);
        }

        if (tangibleAssetsResource.getDepreciation() != null && tangibleAssetsResource.getDepreciation().getAtPeriodStart() != null) {

            addError(errors, inconsistentData, TANGIBLE_NOTE + subResource + DEPRECIATION_AT_PERIOD_START);
        }

        if (tangibleAssetsResource.getNetBookValueAtEndOfPreviousPeriod() != null) {

            addError(errors, inconsistentData, TANGIBLE_NOTE + subResource.getJsonPath() + NET_BOOK_VALUE_PREVIOUS_PERIOD);
        }

        if (tangibleAssetsResource.getNetBookValueAtEndOfCurrentPeriod() != null) {

            if (tangibleAssetsResource.getCost() == null || tangibleAssetsResource.getCost().getAtPeriodStart() == null) {

                addError(errors, valueRequired, TANGIBLE_NOTE + subResource.getJsonPath() + COST_AT_PERIOD_START);
            }

            if (tangibleAssetsResource.getDepreciation() != null) {

                if (hasDepreciationFieldsSet(tangibleAssetsResource)
                        && tangibleAssetsResource.getDepreciation().getAtPeriodEnd() == null) {

                    addError(errors, valueRequired, TANGIBLE_NOTE + subResource.getJsonPath() + DEPRECIATION_AT_PERIOD_END);
                }
            }
        } else {

            // verify whole sub resource is empty here...
        }
    }

    private boolean hasDepreciationFieldsSet(TangibleAssetsResource tangibleAssetsResource) {

        return Stream.of(tangibleAssetsResource.getDepreciation().getChargeForYear(),
                            tangibleAssetsResource.getDepreciation().getOnDisposals(),
                            tangibleAssetsResource.getDepreciation().getOtherAdjustments())
                .anyMatch(Objects::nonNull);
    }

    private Long getCostAtPeriodStart(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(resource -> resource.getCost())
                .map(cost -> cost.getAtPeriodStart())
                .orElse(0L);
    }

    private Long getAdditions(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(resource -> resource.getCost())
                .map(cost -> cost.getAdditions())
                .orElse(0L);
    }

    private Long getDisposals(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(resource -> resource.getCost())
                .map(cost -> cost.getDisposals())
                .orElse(0L);
    }

    private Long getRevaluations(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(resource -> resource.getCost())
                .map(cost -> cost.getRevaluations())
                .orElse(0L);
    }

    private Long getTransfers(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(resource -> resource.getCost())
                .map(cost -> cost.getTransfers())
                .orElse(0L);
    }

    private Long getCostAtPeriodEnd(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(resource -> resource.getCost())
                .map(cost -> cost.getAtPeriodEnd())
                .orElse(0L);
    }

    private Long getDepreciationAtPeriodStart(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(resource -> resource.getDepreciation())
                .map(depreciation -> depreciation.getAtPeriodStart())
                .orElse(0L);
    }

    private Long getChargeForYear(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(resource -> resource.getDepreciation())
                .map(depreciation -> depreciation.getChargeForYear())
                .orElse(0L);
    }

    private Long getOnDisposals(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(resource -> resource.getDepreciation())
                .map(depreciation -> depreciation.getOnDisposals())
                .orElse(0L);
    }

    private Long getOtherAdjustments(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(resource -> resource.getDepreciation())
                .map(depreciation -> depreciation.getOtherAdjustments())
                .orElse(0L);
    }

    private Long getDepreciationAtPeriodEnd(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(resource -> resource.getDepreciation())
                .map(depreciation -> depreciation.getAtPeriodEnd())
                .orElse(0L);
    }

    private Long getNetBookValueAtEndOfCurrentPeriod(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(resource -> resource.getNetBookValueAtEndOfCurrentPeriod())
                .orElse(0L);
    }

    private Long getNetBookValueAtEndOfPreviousPeriod(TangibleAssetsResource tangibleAssetsResource) {

        return Optional.ofNullable(tangibleAssetsResource)
                .map(resource -> resource.getNetBookValueAtEndOfPreviousPeriod())
                .orElse(0L);
    }

    private enum TangibleSubResource {

        FIXTURES_AND_FITTINGS("fixtures_and_fittings"),
        LAND_AND_BUILDINGS("land_and_buildings"),
        MOTOR_VEHICLES("motor_vehicles"),
        OFFICE_EQUIPMENT("office_equipment"),
        PLANT_AND_MACHINERY("plant_and_machinery");

        private String jsonPath;

        TangibleSubResource(String jsonPath) {
            this.jsonPath = jsonPath;
        }

        public String getJsonPath() {
            return jsonPath;
        }
    }
}
