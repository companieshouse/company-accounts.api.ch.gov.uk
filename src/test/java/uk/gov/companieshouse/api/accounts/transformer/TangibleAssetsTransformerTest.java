package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.CostEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.DepreciationEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.TangibleAssetsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.TangibleAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.TangibleAssetsResourceEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.Cost;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.Depreciation;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.TangibleAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.TangibleAssetsResource;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class TangibleAssetsTransformerTest {

    private static final Long NET_BOOK_VALUE_AT_END_OF_CURRENT_PERIOD = 1L;
    private static final Long NET_BOOK_VALUE_AT_END_OF_PREVIOUS_PERIOD = 2L;
    private static final Long COST_ADDITIONS = 3L;
    private static final Long COST_AT_PERIOD_END = 4L;
    private static final Long COST_AT_PERIOD_START = 5L;
    private static final Long COST_DISPOSALS = 6L;
    private static final Long COST_REVALUATIONS = 7L;
    private static final Long COST_TRANSFERS = 8L;
    private static final Long DEPRECIATION_AT_PERIOD_END = 9L;
    private static final Long DEPRECIATION_AT_PERIOD_START = 10L;
    private static final Long DEPRECIATION_CHARGE_FOR_YEAR = 11L;
    private static final Long DEPRECIATION_ON_DISPOSALS = 12L;
    private static final Long DEPRECIATION_OTHER_ADJUSTMENTS = 13L;
    private static final String ADDITIONAL_INFORMATION = "additionalInformation";

    private static final String ETAG = "etag";
    private static final String KIND = "kind";
    private static final Map<String, String> LINKS = new HashMap<>();

    private TangibleAssetsTransformer transformer = new TangibleAssetsTransformer();

    @Test
    @DisplayName("Test fixtures and fittings fields map from the REST object when the nested cost object is not null")
    void TestFixturesAndFittingsMapFromRestObjectCostNotNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setFixturesAndFittings(createTangibleAssetsResource(false, true));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNotNull(tangibleAssetsEntity.getData().getFixturesAndFittings().getCost());

        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertCostFieldsMapped(tangibleAssetsEntity.getData().getFixturesAndFittings().getCost());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings().getDepreciation());
    }

    @Test
    @DisplayName("Test fixtures and fittings fields map from the REST object when the nested depreciation object is not null")
    void TestFixturesAndFittingsMapFromRestObjectDepreciationNotNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setFixturesAndFittings(createTangibleAssetsResource(true, false));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNotNull(tangibleAssetsEntity.getData().getFixturesAndFittings().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertDepreciationFieldsMapped(tangibleAssetsEntity.getData().getFixturesAndFittings().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings().getCost());
    }

    @Test
    @DisplayName("Test fixtures and fittings fields map from the REST object when the nested cost and depreciation objects are not null")
    void TestFixturesAndFittingsMapFromRestObjectCostAndDepreciationNotNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setFixturesAndFittings(createTangibleAssetsResource(false, false));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNotNull(tangibleAssetsEntity.getData().getFixturesAndFittings().getCost());
        assertNotNull(tangibleAssetsEntity.getData().getFixturesAndFittings().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertCostFieldsMapped(tangibleAssetsEntity.getData().getFixturesAndFittings().getCost());
        assertDepreciationFieldsMapped(tangibleAssetsEntity.getData().getFixturesAndFittings().getDepreciation());
    }

    @Test
    @DisplayName("Test fixtures and fittings fields map from the REST object when the nested cost and depreciation objects are null")
    void TestFixturesAndFittingsMapFromRestObjectCostAndDepreciationNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setFixturesAndFittings(createTangibleAssetsResource(true, true));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings().getCost());
        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getFixturesAndFittings());
    }

    @Test
    @DisplayName("Test land and buildings fields map from the REST object when the nested cost object is not null")
    void TestLandAndBuildingsMapFromRestObjectCostNotNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setLandAndBuildings(createTangibleAssetsResource(false, true));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNotNull(tangibleAssetsEntity.getData().getLandAndBuildings().getCost());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertCostFieldsMapped(tangibleAssetsEntity.getData().getLandAndBuildings().getCost());

        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings().getDepreciation());
    }

    @Test
    @DisplayName("Test land and buildings fields map from the REST object when the nested depreciation object is not null")
    void TestLandAndBuildingsMapFromRestObjectDepreciationNotNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setLandAndBuildings(createTangibleAssetsResource(true, false));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNotNull(tangibleAssetsEntity.getData().getLandAndBuildings().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertDepreciationFieldsMapped(tangibleAssetsEntity.getData().getLandAndBuildings().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings().getCost());
    }

    @Test
    @DisplayName("Test land and buildings fields map from the REST object when the nested cost and depreciation objects are not null")
    void TestLandAndBuildingsMapFromRestObjectCostAndDepreciationNotNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setLandAndBuildings(createTangibleAssetsResource(false, false));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNotNull(tangibleAssetsEntity.getData().getLandAndBuildings().getCost());
        assertNotNull(tangibleAssetsEntity.getData().getLandAndBuildings().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertCostFieldsMapped(tangibleAssetsEntity.getData().getLandAndBuildings().getCost());
        assertDepreciationFieldsMapped(tangibleAssetsEntity.getData().getLandAndBuildings().getDepreciation());
    }

    @Test
    @DisplayName("Test land and buildings fields map from the REST object when the nested cost and depreciation objects are null")
    void TestLandAndBuildingsMapFromRestObjectCostAndDepreciationNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setLandAndBuildings(createTangibleAssetsResource(true, true));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings().getCost());
        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getLandAndBuildings());
    }

    @Test
    @DisplayName("Test motor vehicles fields map from the REST object when the nested cost object is not null")
    void TestMotorVehiclesMapFromRestObjectCostNotNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setMotorVehicles(createTangibleAssetsResource(false, true));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNotNull(tangibleAssetsEntity.getData().getMotorVehicles().getCost());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getMotorVehicles());
        assertCostFieldsMapped(tangibleAssetsEntity.getData().getMotorVehicles().getCost());

        assertNull(tangibleAssetsEntity.getData().getMotorVehicles().getDepreciation());
    }

    @Test
    @DisplayName("Test motor vehicles fields map from the REST object when the nested depreciation object is not null")
    void TestMotorVehiclesMapFromRestObjectDepreciationNotNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setMotorVehicles(createTangibleAssetsResource(true, false));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNotNull(tangibleAssetsEntity.getData().getMotorVehicles().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getMotorVehicles());
        assertDepreciationFieldsMapped(tangibleAssetsEntity.getData().getMotorVehicles().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getMotorVehicles().getCost());
    }

    @Test
    @DisplayName("Test motor vehicles fields map from the REST object when the nested cost and depreciation objects are not null")
    void TestMotorVehiclesMapFromRestObjectCostAndDepreciationNotNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setMotorVehicles(createTangibleAssetsResource(false, false));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNotNull(tangibleAssetsEntity.getData().getMotorVehicles().getCost());
        assertNotNull(tangibleAssetsEntity.getData().getMotorVehicles().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getMotorVehicles());
        assertCostFieldsMapped(tangibleAssetsEntity.getData().getMotorVehicles().getCost());
        assertDepreciationFieldsMapped(tangibleAssetsEntity.getData().getMotorVehicles().getDepreciation());
    }

    @Test
    @DisplayName("Test motor vehicles fields map from the REST object when the nested cost and depreciation objects are null")
    void TestMotorVehiclesMapFromRestObjectCostAndDepreciationNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setMotorVehicles(createTangibleAssetsResource(true, true));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles().getCost());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getMotorVehicles());
    }

    @Test
    @DisplayName("Test office equipment fields map from the REST object when the nested cost object is not null")
    void TestOfficeEquipmentMapFromRestObjectCostNotNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setOfficeEquipment(createTangibleAssetsResource(false, true));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNotNull(tangibleAssetsEntity.getData().getOfficeEquipment().getCost());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertCostFieldsMapped(tangibleAssetsEntity.getData().getOfficeEquipment().getCost());

        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment().getDepreciation());
    }

    @Test
    @DisplayName("Test office equipment fields map from the REST object when the nested depreciation object is not null")
    void TestOfficeEquipmentMapFromRestObjectDepreciationNotNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setOfficeEquipment(createTangibleAssetsResource(true, false));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNotNull(tangibleAssetsEntity.getData().getOfficeEquipment().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertDepreciationFieldsMapped(tangibleAssetsEntity.getData().getOfficeEquipment().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment().getCost());
    }

    @Test
    @DisplayName("Test office equipment fields map from the REST object when the nested cost and depreciation objects are not null")
    void TestOfficeEquipmentMapFromRestObjectCostAndDepreciationNotNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setOfficeEquipment(createTangibleAssetsResource(false, false));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNotNull(tangibleAssetsEntity.getData().getOfficeEquipment().getCost());
        assertNotNull(tangibleAssetsEntity.getData().getOfficeEquipment().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertCostFieldsMapped(tangibleAssetsEntity.getData().getOfficeEquipment().getCost());
        assertDepreciationFieldsMapped(tangibleAssetsEntity.getData().getOfficeEquipment().getDepreciation());
    }

    @Test
    @DisplayName("Test office equipment fields map from the REST object when the nested cost and depreciation objects are null")
    void TestOfficeEquipmentMapFromRestObjectCostAndDepreciationNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setOfficeEquipment(createTangibleAssetsResource(true, true));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment().getCost());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getOfficeEquipment());
    }

    @Test
    @DisplayName("Test plant and machinery fields map from the REST object when the nested cost object is not null")
    void TestPlantAndMachineryMapFromRestObjectCostNotNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setPlantAndMachinery(createTangibleAssetsResource(false, true));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNotNull(tangibleAssetsEntity.getData().getPlantAndMachinery().getCost());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertCostFieldsMapped(tangibleAssetsEntity.getData().getPlantAndMachinery().getCost());

        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery().getDepreciation());
    }

    @Test
    @DisplayName("Test plant and machinery fields map from the REST object when the nested depreciation object is not null")
    void TestPlantAndMachineryMapFromRestObjectDepreciationNotNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setPlantAndMachinery(createTangibleAssetsResource(true, false));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNotNull(tangibleAssetsEntity.getData().getPlantAndMachinery().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertDepreciationFieldsMapped(tangibleAssetsEntity.getData().getPlantAndMachinery().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery().getCost());
    }

    @Test
    @DisplayName("Test plant and machinery fields map from the REST object when the nested cost and depreciation objects are not null")
    void TestPlantAndMachineryMapFromRestObjectCostAndDepreciationNotNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setPlantAndMachinery(createTangibleAssetsResource(false, false));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNotNull(tangibleAssetsEntity.getData().getPlantAndMachinery().getCost());
        assertNotNull(tangibleAssetsEntity.getData().getPlantAndMachinery().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertCostFieldsMapped(tangibleAssetsEntity.getData().getPlantAndMachinery().getCost());
        assertDepreciationFieldsMapped(tangibleAssetsEntity.getData().getPlantAndMachinery().getDepreciation());
    }

    @Test
    @DisplayName("Test plant and machinery fields map from the REST object when the nested cost and depreciation objects are null")
    void TestPlantAndMachineryMapFromRestObjectCostAndDepreciationNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setPlantAndMachinery(createTangibleAssetsResource(true, true));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getPlantAndMachinery());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery().getCost());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getPlantAndMachinery());
    }

    @Test
    @DisplayName("Test total fields map from the REST object when the nested cost object is not null")
    void TestTotalMapFromRestObjectCostNotNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setTotal(createTangibleAssetsResource(false, true));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getTotal());
        assertNotNull(tangibleAssetsEntity.getData().getTotal().getCost());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getTotal());
        assertCostFieldsMapped(tangibleAssetsEntity.getData().getTotal().getCost());

        assertNull(tangibleAssetsEntity.getData().getTotal().getDepreciation());
    }

    @Test
    @DisplayName("Test total fields map from the REST object when the nested depreciation object is not null")
    void TestTotalMapFromRestObjectDepreciationNotNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setTotal(createTangibleAssetsResource(true, false));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getTotal());
        assertNotNull(tangibleAssetsEntity.getData().getTotal().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getTotal());
        assertDepreciationFieldsMapped(tangibleAssetsEntity.getData().getTotal().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getTotal().getCost());
    }

    @Test
    @DisplayName("Test total fields map from the REST object when the nested cost and depreciation objects are not null")
    void TestTotalMapFromRestObjectCostAndDepreciationNotNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setTotal(createTangibleAssetsResource(false, false));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getTotal());
        assertNotNull(tangibleAssetsEntity.getData().getTotal().getCost());
        assertNotNull(tangibleAssetsEntity.getData().getTotal().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getTotal());
        assertCostFieldsMapped(tangibleAssetsEntity.getData().getTotal().getCost());
        assertDepreciationFieldsMapped(tangibleAssetsEntity.getData().getTotal().getDepreciation());
    }

    @Test
    @DisplayName("Test total fields map from the REST object when the nested cost and depreciation objects are null")
    void TestTotalMapFromRestObjectCostAndDepreciationNull() {

        TangibleAssets tangibleAssets = createTangibleAssets();
        tangibleAssets.setTotal(createTangibleAssetsResource(true, true));

        TangibleAssetsEntity tangibleAssetsEntity = transformer.transform(tangibleAssets);

        assertNotNull(tangibleAssetsEntity);
        assertNotNull(tangibleAssetsEntity.getData());
        assertNotNull(tangibleAssetsEntity.getData().getTotal());
        assertNull(tangibleAssetsEntity.getData().getTotal().getCost());
        assertNull(tangibleAssetsEntity.getData().getTotal().getDepreciation());

        assertNull(tangibleAssetsEntity.getData().getFixturesAndFittings());
        assertNull(tangibleAssetsEntity.getData().getLandAndBuildings());
        assertNull(tangibleAssetsEntity.getData().getMotorVehicles());
        assertNull(tangibleAssetsEntity.getData().getOfficeEquipment());
        assertNull(tangibleAssetsEntity.getData().getPlantAndMachinery());

        assertRestObjectFieldsMapped(tangibleAssetsEntity);
        assertAdditionalInformationMapped(tangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(tangibleAssetsEntity.getData().getTotal());
    }

    private TangibleAssets createTangibleAssets() {

        TangibleAssets tangibleAssets = new TangibleAssets();
        tangibleAssets.setAdditionalInformation(ADDITIONAL_INFORMATION);
        tangibleAssets.setEtag(ETAG);
        tangibleAssets.setKind(KIND);
        tangibleAssets.setLinks(LINKS);
        return tangibleAssets;
    }

    private TangibleAssetsResource createTangibleAssetsResource(boolean costNull,
                                                                boolean depreciationNull) {

        TangibleAssetsResource tangibleAssetsResource = new TangibleAssetsResource();
        tangibleAssetsResource
                .setNetBookValueAtEndOfCurrentPeriod(NET_BOOK_VALUE_AT_END_OF_CURRENT_PERIOD);
        tangibleAssetsResource
                .setNetBookValueAtEndOfPreviousPeriod(NET_BOOK_VALUE_AT_END_OF_PREVIOUS_PERIOD);

        if (!costNull) {
            tangibleAssetsResource.setCost(createCost());
        }

        if (!depreciationNull) {
            tangibleAssetsResource.setDepreciation(createDepreciation());
        }

        return tangibleAssetsResource;
    }

    private Cost createCost() {

        Cost cost = new Cost();
        cost.setAdditions(COST_ADDITIONS);
        cost.setAtPeriodEnd(COST_AT_PERIOD_END);
        cost.setAtPeriodStart(COST_AT_PERIOD_START);
        cost.setDisposals(COST_DISPOSALS);
        cost.setRevaluations(COST_REVALUATIONS);
        cost.setTransfers(COST_TRANSFERS);
        return cost;
    }

    private Depreciation createDepreciation() {

        Depreciation depreciation = new Depreciation();
        depreciation.setAtPeriodEnd(DEPRECIATION_AT_PERIOD_END);
        depreciation.setAtPeriodStart(DEPRECIATION_AT_PERIOD_START);
        depreciation.setChargeForYear(DEPRECIATION_CHARGE_FOR_YEAR);
        depreciation.setOnDisposals(DEPRECIATION_ON_DISPOSALS);
        depreciation.setOtherAdjustments(DEPRECIATION_OTHER_ADJUSTMENTS);
        return depreciation;
    }

    private void assertRestObjectFieldsMapped(TangibleAssetsEntity tangibleAssets) {
        assertEquals(ETAG, tangibleAssets.getData().getEtag());
        assertEquals(KIND, tangibleAssets.getData().getKind());
        assertEquals(LINKS, tangibleAssets.getData().getLinks());
    }

    private void assertAdditionalInformationMapped(TangibleAssetsDataEntity tangibleAssets) {
        assertEquals(ADDITIONAL_INFORMATION, tangibleAssets.getAdditionalInformation());
    }

    private void assertNetBookValueFieldsMapped(TangibleAssetsResourceEntity tangibleAssetsResource) {
        assertEquals(NET_BOOK_VALUE_AT_END_OF_CURRENT_PERIOD,
                tangibleAssetsResource.getNetBookValueAtEndOfCurrentPeriod());
        assertEquals(NET_BOOK_VALUE_AT_END_OF_CURRENT_PERIOD,
                tangibleAssetsResource.getNetBookValueAtEndOfCurrentPeriod());
    }

    private void assertCostFieldsMapped(CostEntity cost) {
        assertEquals(COST_ADDITIONS, cost.getAdditions());
        assertEquals(COST_AT_PERIOD_END, cost.getAtPeriodEnd());
        assertEquals(COST_AT_PERIOD_START, cost.getAtPeriodStart());
        assertEquals(COST_DISPOSALS, cost.getDisposals());
        assertEquals(COST_REVALUATIONS, cost.getRevaluations());
        assertEquals(COST_TRANSFERS, cost.getTransfers());
    }

    private void assertDepreciationFieldsMapped(DepreciationEntity depreciation) {
        assertEquals(DEPRECIATION_AT_PERIOD_END, depreciation.getAtPeriodEnd());
        assertEquals(DEPRECIATION_AT_PERIOD_START, depreciation.getAtPeriodStart());
        assertEquals(DEPRECIATION_CHARGE_FOR_YEAR, depreciation.getChargeForYear());
        assertEquals(DEPRECIATION_ON_DISPOSALS, depreciation.getOnDisposals());
        assertEquals(DEPRECIATION_OTHER_ADJUSTMENTS, depreciation.getOtherAdjustments());
    }
}
