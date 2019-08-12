package uk.gov.companieshouse.api.accounts.transformer;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.notes.intangible.AmortisationEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.intangible.CostEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.intangible.IntangibleAssetsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.intangible.IntangibleAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.intangible.IntangibleAssetsResourceEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.Amortisation;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.IntangibleAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.IntangibleAssetsResource;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.Cost;


import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class IntangibleAssetsTransformerTest {

    private static final Long NET_BOOK_VALUE_AT_END_OF_CURRENT_PERIOD = 1L;
    private static final Long NET_BOOK_VALUE_AT_END_OF_PREVIOUS_PERIOD = 2L;
    private static final Long COST_ADDITIONS = 3L;
    private static final Long COST_AT_PERIOD_END = 4L;
    private static final Long COST_AT_PERIOD_START = 5L;
    private static final Long COST_DISPOSALS = 6L;
    private static final Long COST_REVALUATIONS = 7L;
    private static final Long COST_TRANSFERS = 8L;
    private static final Long AMORTISATION_AT_PERIOD_END = 9L;
    private static final Long AMORTISATION_AT_PERIOD_START = 10L;
    private static final Long AMORTISATION_CHARGE_FOR_YEAR = 11L;
    private static final Long AMORTISATION_ON_DISPOSALS = 12L;
    private static final Long AMORTISATION_OTHER_ADJUSTMENTS = 13L;
    private static final String ADDITIONAL_INFORMATION = "additionalInformation";

    private static final String ETAG = "etag";
    private static final String KIND = "kind";
    private static final Map<String, String> LINKS = new HashMap<>();

    private IntangibleAssetsTransformer transformer = new IntangibleAssetsTransformer();

    @Test
    @DisplayName("Test goodwill field map from the REST object when the nested cost object is not null")
    void TestGoodwillMapFromRestObjectCostNotNull() {

        IntangibleAssets intangibleassets = createIntangibleAssets();
        intangibleassets.setGoodwill(createIntangibleAssetsResource(false, true));

        IntangibleAssetsEntity intangibleAssetsEntity = transformer.transform(intangibleassets);

        assertNotNull(intangibleAssetsEntity);
        assertNotNull(intangibleAssetsEntity.getData());
        assertNotNull(intangibleAssetsEntity.getData().getGoodwill());
        assertNotNull(intangibleAssetsEntity.getData().getGoodwill().getCost());

        assertNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets());
        assertNull(intangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(intangibleAssetsEntity);
        assertAdditionalInformationMapped(intangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(intangibleAssetsEntity.getData().getGoodwill());
        assertCostFieldsMapped(intangibleAssetsEntity.getData().getGoodwill().getCost());

        assertNull(intangibleAssetsEntity.getData().getGoodwill().getAmortisation());

    }

    @Test
    @DisplayName("Test goodwill field map from the REST object when the nested amortisation object is not null")
    void TestGoodwillMapFromRestObjectAmortisationNotNull() {

        IntangibleAssets intangibleassets = createIntangibleAssets();
        intangibleassets.setGoodwill(createIntangibleAssetsResource(true, false));

        IntangibleAssetsEntity intangibleAssetsEntity = transformer.transform(intangibleassets);

        assertNotNull(intangibleAssetsEntity);
        assertNotNull(intangibleAssetsEntity.getData());
        assertNotNull(intangibleAssetsEntity.getData().getGoodwill());
        assertNotNull(intangibleAssetsEntity.getData().getGoodwill().getAmortisation());

        assertNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets());
        assertNull(intangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(intangibleAssetsEntity);
        assertAdditionalInformationMapped(intangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(intangibleAssetsEntity.getData().getGoodwill());
        assertAmortisationFieldsMapped(intangibleAssetsEntity.getData().getGoodwill().getAmortisation());

        assertNull(intangibleAssetsEntity.getData().getGoodwill().getCost());

    }

    @Test
    @DisplayName("Test goodwill field map from the REST object when the nested amortisation and cost objects are not null")
    void TestGoodwillMapFromRestObjectCostAndAmortisationNotNull() {

        IntangibleAssets intangibleassets = createIntangibleAssets();
        intangibleassets.setGoodwill(createIntangibleAssetsResource(false, false));

        IntangibleAssetsEntity intangibleAssetsEntity = transformer.transform(intangibleassets);

        assertNotNull(intangibleAssetsEntity);
        assertNotNull(intangibleAssetsEntity.getData());
        assertNotNull(intangibleAssetsEntity.getData().getGoodwill());

        assertNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets());
        assertNull(intangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(intangibleAssetsEntity);
        assertAdditionalInformationMapped(intangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(intangibleAssetsEntity.getData().getGoodwill());
        assertAmortisationFieldsMapped(intangibleAssetsEntity.getData().getGoodwill().getAmortisation());
        assertCostFieldsMapped(intangibleAssetsEntity.getData().getGoodwill().getCost());

        assertNotNull(intangibleAssetsEntity.getData().getGoodwill().getCost());
        assertNotNull(intangibleAssetsEntity.getData().getGoodwill().getAmortisation());
    }

    @Test
    @DisplayName("Test goodwill field map from the REST object when the nested amortisation and cost objects are null")
    void TestGoodwillMapFromRestObjectCostAndAmortisationNull() {

        IntangibleAssets intangibleassets = createIntangibleAssets();
        intangibleassets.setGoodwill(createIntangibleAssetsResource(true, true));

        IntangibleAssetsEntity intangibleAssetsEntity = transformer.transform(intangibleassets);

        assertNotNull(intangibleAssetsEntity);
        assertNotNull(intangibleAssetsEntity.getData());
        assertNotNull(intangibleAssetsEntity.getData().getGoodwill());

        assertNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets());
        assertNull(intangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(intangibleAssetsEntity);
        assertAdditionalInformationMapped(intangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(intangibleAssetsEntity.getData().getGoodwill());

        assertNull(intangibleAssetsEntity.getData().getGoodwill().getCost());
        assertNull(intangibleAssetsEntity.getData().getGoodwill().getAmortisation());

    }

    @Test
    @DisplayName("Test other intangible assets field map from the REST object when the nested cost objects is not null")
    void TestOtherIntangibleAssetsMapFromRestObjectCostNotNull() {

        IntangibleAssets intangibleassets = createIntangibleAssets();
        intangibleassets.setOtherIntangibleAssets(createIntangibleAssetsResource(false, true));

        IntangibleAssetsEntity intangibleAssetsEntity = transformer.transform(intangibleassets);

        assertNotNull(intangibleAssetsEntity);
        assertNotNull(intangibleAssetsEntity.getData());
        assertNotNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets());
        assertNotNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets().getCost());

        assertNull(intangibleAssetsEntity.getData().getGoodwill());
        assertNull(intangibleAssetsEntity.getData().getTotal());
        assertNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets().getAmortisation());

        assertRestObjectFieldsMapped(intangibleAssetsEntity);
        assertAdditionalInformationMapped(intangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(intangibleAssetsEntity.getData().getOtherIntangibleAssets());

    }

    @Test
    @DisplayName("Test other intangible assets field map from the REST object when the nested amortisation object is not null")
    void TestOtherIntangibleAssetsMapFromRestObjectAmortisationNotNull() {

        IntangibleAssets intangibleassets = createIntangibleAssets();
        intangibleassets.setOtherIntangibleAssets(createIntangibleAssetsResource(true, false));

        IntangibleAssetsEntity intangibleAssetsEntity = transformer.transform(intangibleassets);

        assertNotNull(intangibleAssetsEntity);
        assertNotNull(intangibleAssetsEntity.getData());
        assertNotNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets());
        assertNotNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets().getAmortisation());

        assertNull(intangibleAssetsEntity.getData().getGoodwill());
        assertNull(intangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(intangibleAssetsEntity);
        assertAdditionalInformationMapped(intangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(intangibleAssetsEntity.getData().getOtherIntangibleAssets());
        assertAmortisationFieldsMapped(intangibleAssetsEntity.getData().getOtherIntangibleAssets().getAmortisation());

        assertNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets().getCost());

    }

    @Test
    @DisplayName("Test other intangible assets field map from the REST object when the nested amortisation and cost objects are not null")
    void TestOtherIntangibleAssetsMapFromRestObjectCostAndAmortisationNotNull() {

        IntangibleAssets intangibleassets = createIntangibleAssets();
        intangibleassets.setOtherIntangibleAssets(createIntangibleAssetsResource(false, false));

        IntangibleAssetsEntity intangibleAssetsEntity = transformer.transform(intangibleassets);

        assertNotNull(intangibleAssetsEntity);
        assertNotNull(intangibleAssetsEntity.getData());
        assertNotNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets());

        assertNull(intangibleAssetsEntity.getData().getGoodwill());
        assertNull(intangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(intangibleAssetsEntity);
        assertAdditionalInformationMapped(intangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(intangibleAssetsEntity.getData().getOtherIntangibleAssets());
        assertAmortisationFieldsMapped(intangibleAssetsEntity.getData().getOtherIntangibleAssets().getAmortisation());
        assertCostFieldsMapped(intangibleAssetsEntity.getData().getOtherIntangibleAssets().getCost());

        assertNotNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets().getCost());
        assertNotNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets().getAmortisation());
    }

    @Test
    @DisplayName("Test other intangible assets field map from the REST object when the nested amortisation and cost objects are null")
    void TestOtherIntangibleAssetsMapFromRestObjectCostAndAmortisationNull() {

        IntangibleAssets intangibleassets = createIntangibleAssets();
        intangibleassets.setOtherIntangibleAssets(createIntangibleAssetsResource(true, true));

        IntangibleAssetsEntity intangibleAssetsEntity = transformer.transform(intangibleassets);

        assertNotNull(intangibleAssetsEntity);
        assertNotNull(intangibleAssetsEntity.getData());
        assertNotNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets());

        assertNull(intangibleAssetsEntity.getData().getGoodwill());
        assertNull(intangibleAssetsEntity.getData().getTotal());

        assertRestObjectFieldsMapped(intangibleAssetsEntity);
        assertAdditionalInformationMapped(intangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(intangibleAssetsEntity.getData().getOtherIntangibleAssets());

        assertNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets().getCost());
        assertNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets().getAmortisation());

    }

    @Test
    @DisplayName("Test total field map from the REST object when the nested cost objects is not null")
    void TestTotalMapFromRestObjectCostNotNull() {

        IntangibleAssets intangibleassets = createIntangibleAssets();
        intangibleassets.setTotal(createIntangibleAssetsResource(false, true));

        IntangibleAssetsEntity intangibleAssetsEntity = transformer.transform(intangibleassets);

        assertNotNull(intangibleAssetsEntity);
        assertNotNull(intangibleAssetsEntity.getData());
        assertNotNull(intangibleAssetsEntity.getData().getTotal());
        assertNotNull(intangibleAssetsEntity.getData().getTotal().getCost());

        assertNull(intangibleAssetsEntity.getData().getGoodwill());
        assertNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets());
        assertNull(intangibleAssetsEntity.getData().getTotal().getAmortisation());

        assertRestObjectFieldsMapped(intangibleAssetsEntity);
        assertAdditionalInformationMapped(intangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(intangibleAssetsEntity.getData().getTotal());

    }

    @Test
    @DisplayName("Test total field map from the REST object when the nested amortisation object is not null")
    void TestTotalMapFromRestObjectAmortisationNotNull() {

        IntangibleAssets intangibleassets = createIntangibleAssets();
        intangibleassets.setTotal(createIntangibleAssetsResource(true, false));

        IntangibleAssetsEntity intangibleAssetsEntity = transformer.transform(intangibleassets);

        assertNotNull(intangibleAssetsEntity);
        assertNotNull(intangibleAssetsEntity.getData());
        assertNotNull(intangibleAssetsEntity.getData().getTotal());
        assertNotNull(intangibleAssetsEntity.getData().getTotal().getAmortisation());

        assertNull(intangibleAssetsEntity.getData().getGoodwill());
        assertNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets());

        assertRestObjectFieldsMapped(intangibleAssetsEntity);
        assertAdditionalInformationMapped(intangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(intangibleAssetsEntity.getData().getTotal());
        assertAmortisationFieldsMapped(intangibleAssetsEntity.getData().getTotal().getAmortisation());

        assertNull(intangibleAssetsEntity.getData().getTotal().getCost());

    }

    @Test
    @DisplayName("Test other intangible assets field map from the REST object when the nested amortisation and cost objects are not null")
    void TestTotalMapFromRestObjectCostAndAmortisationNotNull() {

        IntangibleAssets intangibleassets = createIntangibleAssets();
        intangibleassets.setTotal(createIntangibleAssetsResource(false, false));

        IntangibleAssetsEntity intangibleAssetsEntity = transformer.transform(intangibleassets);

        assertNotNull(intangibleAssetsEntity);
        assertNotNull(intangibleAssetsEntity.getData());
        assertNotNull(intangibleAssetsEntity.getData().getTotal());

        assertNull(intangibleAssetsEntity.getData().getGoodwill());
        assertNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets());

        assertRestObjectFieldsMapped(intangibleAssetsEntity);
        assertAdditionalInformationMapped(intangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(intangibleAssetsEntity.getData().getTotal());
        assertAmortisationFieldsMapped(intangibleAssetsEntity.getData().getTotal().getAmortisation());
        assertCostFieldsMapped(intangibleAssetsEntity.getData().getTotal().getCost());

        assertNotNull(intangibleAssetsEntity.getData().getTotal().getCost());
        assertNotNull(intangibleAssetsEntity.getData().getTotal().getAmortisation());
    }

    @Test
    @DisplayName("Test other intangible assets field map from the REST object when the nested amortisation and cost objects are null")
    void TestTotalMapFromRestObjectCostAndAmortisationNull() {

        IntangibleAssets intangibleassets = createIntangibleAssets();
        intangibleassets.setTotal(createIntangibleAssetsResource(true, true));

        IntangibleAssetsEntity intangibleAssetsEntity = transformer.transform(intangibleassets);

        assertNotNull(intangibleAssetsEntity);
        assertNotNull(intangibleAssetsEntity.getData());
        assertNotNull(intangibleAssetsEntity.getData().getTotal());

        assertNull(intangibleAssetsEntity.getData().getGoodwill());
        assertNull(intangibleAssetsEntity.getData().getOtherIntangibleAssets());

        assertRestObjectFieldsMapped(intangibleAssetsEntity);
        assertAdditionalInformationMapped(intangibleAssetsEntity.getData());
        assertNetBookValueFieldsMapped(intangibleAssetsEntity.getData().getTotal());

        assertNull(intangibleAssetsEntity.getData().getTotal().getCost());
        assertNull(intangibleAssetsEntity.getData().getTotal().getAmortisation());

    }

    @Test
    @DisplayName("Test goodwill field map from the entity object when the nested cost object is not null")
    void TestGoodwillMapFromEntityObjectCostNotNull() {

        IntangibleAssetsDataEntity intangibleAssetsDataEntity = createIntangibleAssetsDataEntity();
        intangibleAssetsDataEntity.setGoodwill(createIntangibleAssetsResourceEntity(false, true));

        IntangibleAssetsEntity intangibleAssetsEntity = new IntangibleAssetsEntity();
        intangibleAssetsEntity.setData(intangibleAssetsDataEntity);

        IntangibleAssets intangibleAssets = transformer.transform(intangibleAssetsEntity);

        assertNotNull(intangibleAssets);
        assertNotNull(intangibleAssets.getGoodwill());
        assertNotNull(intangibleAssets.getGoodwill().getCost());


        assertNull(intangibleAssets.getOtherIntangibleAssets());
        assertNull(intangibleAssets.getTotal());

        assertRestObjectFieldsMapped(intangibleAssets);
        assertAdditionalInformationMapped(intangibleAssets);
        assertNetBookValueFieldsMapped(intangibleAssets.getGoodwill());
        assertCostFieldsMapped(intangibleAssets.getGoodwill().getCost());

        assertNull(intangibleAssets.getGoodwill().getAmortisation());

    }

    @Test
    @DisplayName("Test goodwill field map from the entity object when the nested amortisation object is not null")
    void TestGoodwillMapFromEntityObjectAmortisationNotNull() {

        IntangibleAssetsDataEntity intangibleAssetsDataEntity = createIntangibleAssetsDataEntity();
        intangibleAssetsDataEntity.setGoodwill(createIntangibleAssetsResourceEntity(true, false));

        IntangibleAssetsEntity intangibleAssetsEntity = new IntangibleAssetsEntity();
        intangibleAssetsEntity.setData(intangibleAssetsDataEntity);

        IntangibleAssets intangibleAssets = transformer.transform(intangibleAssetsEntity);

        assertNotNull(intangibleAssets);
        assertNotNull(intangibleAssets.getGoodwill());
        assertNotNull(intangibleAssets.getGoodwill().getAmortisation());


        assertNull(intangibleAssets.getOtherIntangibleAssets());
        assertNull(intangibleAssets.getTotal());

        assertRestObjectFieldsMapped(intangibleAssets);
        assertAdditionalInformationMapped(intangibleAssets);
        assertNetBookValueFieldsMapped(intangibleAssets.getGoodwill());
        assertAmortisationFieldsMapped(intangibleAssets.getGoodwill().getAmortisation());

        assertNull(intangibleAssets.getGoodwill().getCost());

    }

    @Test
    @DisplayName("Test goodwill field map from the entity object when the nested amortisation and cost objects are not null")
    void TestGoodwillMapFromEntityObjectAmortisationAndCostNotNull() {

        IntangibleAssetsDataEntity intangibleAssetsDataEntity = createIntangibleAssetsDataEntity();
        intangibleAssetsDataEntity.setGoodwill(createIntangibleAssetsResourceEntity(false, false));

        IntangibleAssetsEntity intangibleAssetsEntity = new IntangibleAssetsEntity();
        intangibleAssetsEntity.setData(intangibleAssetsDataEntity);

        IntangibleAssets intangibleAssets = transformer.transform(intangibleAssetsEntity);

        assertNotNull(intangibleAssets);
        assertNotNull(intangibleAssets.getGoodwill());
        assertNotNull(intangibleAssets.getGoodwill().getAmortisation());
        assertNotNull(intangibleAssets.getGoodwill().getCost());



        assertNull(intangibleAssets.getOtherIntangibleAssets());
        assertNull(intangibleAssets.getTotal());

        assertRestObjectFieldsMapped(intangibleAssets);
        assertAdditionalInformationMapped(intangibleAssets);
        assertNetBookValueFieldsMapped(intangibleAssets.getGoodwill());
        assertAmortisationFieldsMapped(intangibleAssets.getGoodwill().getAmortisation());
        assertCostFieldsMapped(intangibleAssets.getGoodwill().getCost());


    }

    @Test
    @DisplayName("Test goodwill field map from the entity object when the nested amortisation and cost objects are null")
    void TestGoodwillMapFromEntityObjectAmortisationAndCostNull() {

        IntangibleAssetsDataEntity intangibleAssetsDataEntity = createIntangibleAssetsDataEntity();
        intangibleAssetsDataEntity.setGoodwill(createIntangibleAssetsResourceEntity(true, true));

        IntangibleAssetsEntity intangibleAssetsEntity = new IntangibleAssetsEntity();
        intangibleAssetsEntity.setData(intangibleAssetsDataEntity);

        IntangibleAssets intangibleAssets = transformer.transform(intangibleAssetsEntity);

        assertNotNull(intangibleAssets);
        assertNotNull(intangibleAssets.getGoodwill());

        assertNull(intangibleAssets.getOtherIntangibleAssets());
        assertNull(intangibleAssets.getTotal());
        assertNull(intangibleAssets.getGoodwill().getAmortisation());
        assertNull(intangibleAssets.getGoodwill().getCost());


        assertRestObjectFieldsMapped(intangibleAssets);
        assertAdditionalInformationMapped(intangibleAssets);
        assertNetBookValueFieldsMapped(intangibleAssets.getGoodwill());

    }

    @Test
    @DisplayName("Test other intangible assets field map from the entity object when the nested cost object is not null")
    void TestOtherIntangibleAssetsMapFromEntityObjectCostNotNull() {

        IntangibleAssetsDataEntity intangibleAssetsDataEntity = createIntangibleAssetsDataEntity();
        intangibleAssetsDataEntity.setOtherIntangibleAssets(createIntangibleAssetsResourceEntity(false, true));

        IntangibleAssetsEntity intangibleAssetsEntity = new IntangibleAssetsEntity();
        intangibleAssetsEntity.setData(intangibleAssetsDataEntity);

        IntangibleAssets intangibleAssets = transformer.transform(intangibleAssetsEntity);

        assertNotNull(intangibleAssets);
        assertNotNull(intangibleAssets.getOtherIntangibleAssets());
        assertNotNull(intangibleAssets.getOtherIntangibleAssets().getCost());


        assertNull(intangibleAssets.getGoodwill());
        assertNull(intangibleAssets.getTotal());

        assertRestObjectFieldsMapped(intangibleAssets);
        assertAdditionalInformationMapped(intangibleAssets);
        assertNetBookValueFieldsMapped(intangibleAssets.getOtherIntangibleAssets());
        assertCostFieldsMapped(intangibleAssets.getOtherIntangibleAssets().getCost());

        assertNull(intangibleAssets.getOtherIntangibleAssets().getAmortisation());

    }

    @Test
    @DisplayName("Test other intangible assets field map from the entity object when the nested amortisation object is not null")
    void TestOtherIntangibleAssetsMapFromEntityObjectAmortisationNotNull() {

        IntangibleAssetsDataEntity intangibleAssetsDataEntity = createIntangibleAssetsDataEntity();
        intangibleAssetsDataEntity.setOtherIntangibleAssets(createIntangibleAssetsResourceEntity(true, false));

        IntangibleAssetsEntity intangibleAssetsEntity = new IntangibleAssetsEntity();
        intangibleAssetsEntity.setData(intangibleAssetsDataEntity);

        IntangibleAssets intangibleAssets = transformer.transform(intangibleAssetsEntity);

        assertNotNull(intangibleAssets);
        assertNotNull(intangibleAssets.getOtherIntangibleAssets());
        assertNotNull(intangibleAssets.getOtherIntangibleAssets().getAmortisation());


        assertNull(intangibleAssets.getGoodwill());
        assertNull(intangibleAssets.getTotal());

        assertRestObjectFieldsMapped(intangibleAssets);
        assertAdditionalInformationMapped(intangibleAssets);
        assertNetBookValueFieldsMapped(intangibleAssets.getOtherIntangibleAssets());
        assertAmortisationFieldsMapped(intangibleAssets.getOtherIntangibleAssets().getAmortisation());

        assertNull(intangibleAssets.getOtherIntangibleAssets().getCost());

    }

    @Test
    @DisplayName("Test other intangible assets field map from the entity object when the nested amortisation and cost objects are not null")
    void TestOtherIntangibleAssetsMapFromEntityObjectAmortisationAndCostNotNull() {

        IntangibleAssetsDataEntity intangibleAssetsDataEntity = createIntangibleAssetsDataEntity();
        intangibleAssetsDataEntity.setOtherIntangibleAssets(createIntangibleAssetsResourceEntity(false, false));

        IntangibleAssetsEntity intangibleAssetsEntity = new IntangibleAssetsEntity();
        intangibleAssetsEntity.setData(intangibleAssetsDataEntity);

        IntangibleAssets intangibleAssets = transformer.transform(intangibleAssetsEntity);

        assertNotNull(intangibleAssets);
        assertNotNull(intangibleAssets.getOtherIntangibleAssets());
        assertNotNull(intangibleAssets.getOtherIntangibleAssets().getAmortisation());
        assertNotNull(intangibleAssets.getOtherIntangibleAssets().getCost());



        assertNull(intangibleAssets.getGoodwill());
        assertNull(intangibleAssets.getTotal());

        assertRestObjectFieldsMapped(intangibleAssets);
        assertAdditionalInformationMapped(intangibleAssets);
        assertNetBookValueFieldsMapped(intangibleAssets.getOtherIntangibleAssets());
        assertAmortisationFieldsMapped(intangibleAssets.getOtherIntangibleAssets().getAmortisation());
        assertCostFieldsMapped(intangibleAssets.getOtherIntangibleAssets().getCost());


    }

    @Test
    @DisplayName("Test other intangible assets field map from the entity object when the nested amortisation and cost objects are null")
    void TestOtherIntangibleAssetsMapFromEntityObjectAmortisationAndCostNull() {

        IntangibleAssetsDataEntity intangibleAssetsDataEntity = createIntangibleAssetsDataEntity();
        intangibleAssetsDataEntity.setOtherIntangibleAssets(createIntangibleAssetsResourceEntity(true, true));

        IntangibleAssetsEntity intangibleAssetsEntity = new IntangibleAssetsEntity();
        intangibleAssetsEntity.setData(intangibleAssetsDataEntity);

        IntangibleAssets intangibleAssets = transformer.transform(intangibleAssetsEntity);

        assertNotNull(intangibleAssets);
        assertNotNull(intangibleAssets.getOtherIntangibleAssets());

        assertNull(intangibleAssets.getGoodwill());
        assertNull(intangibleAssets.getTotal());
        assertNull(intangibleAssets.getOtherIntangibleAssets().getAmortisation());
        assertNull(intangibleAssets.getOtherIntangibleAssets().getCost());


        assertRestObjectFieldsMapped(intangibleAssets);
        assertAdditionalInformationMapped(intangibleAssets);
        assertNetBookValueFieldsMapped(intangibleAssets.getOtherIntangibleAssets());

    }

    @Test
    @DisplayName("Test total field map from the ENTITY object when the nested cost objects is not null")
    void TestTotalMapFromEntityObjectCostNotNull() {
        IntangibleAssetsDataEntity intangibleAssetsDataEntity = createIntangibleAssetsDataEntity();
        intangibleAssetsDataEntity.setTotal(createIntangibleAssetsResourceEntity(false, true));

        IntangibleAssetsEntity intangibleAssetsEntity = new IntangibleAssetsEntity();
        intangibleAssetsEntity.setData(intangibleAssetsDataEntity);

        IntangibleAssets intangibleAssets = transformer.transform(intangibleAssetsEntity);

        assertNotNull(intangibleAssets);
        assertNotNull(intangibleAssets.getTotal());
        assertNotNull(intangibleAssets.getTotal().getCost());

        assertNull(intangibleAssets.getOtherIntangibleAssets());
        assertNull(intangibleAssets.getGoodwill());

        assertRestObjectFieldsMapped(intangibleAssets);
        assertAdditionalInformationMapped(intangibleAssets);
        assertNetBookValueFieldsMapped(intangibleAssets.getTotal());
        assertCostFieldsMapped(intangibleAssets.getTotal().getCost());

        assertNull(intangibleAssets.getTotal().getAmortisation());
    }
    @Test
    @DisplayName("Test total field map from the ENTITY object when the nested amortisation object is not null")
    void TestTotalMapFromEntityObjectAmortisationNotNull() {
        IntangibleAssetsDataEntity intangibleAssetsDataEntity = createIntangibleAssetsDataEntity();
        intangibleAssetsDataEntity.setTotal(createIntangibleAssetsResourceEntity(true, false));

        IntangibleAssetsEntity intangibleAssetsEntity = new IntangibleAssetsEntity();

        intangibleAssetsEntity.setData(intangibleAssetsDataEntity);
        IntangibleAssets intangibleAssets = transformer.transform(intangibleAssetsEntity);

        assertNotNull(intangibleAssets);
        assertNotNull(intangibleAssets.getTotal());
        assertNotNull(intangibleAssets.getTotal().getAmortisation());

        assertNull(intangibleAssets.getOtherIntangibleAssets());
        assertNull(intangibleAssets.getGoodwill());

        assertRestObjectFieldsMapped(intangibleAssets);
        assertAdditionalInformationMapped(intangibleAssets);
        assertNetBookValueFieldsMapped(intangibleAssets.getTotal());
        assertAmortisationFieldsMapped(intangibleAssets.getTotal().getAmortisation());
        assertNull(intangibleAssets.getTotal().getCost());
    }
    @Test
    @DisplayName("Test other intangible assets field map from the ENTITY object when the nested amortisation and cost objects are not null")
    void TestTotalMapFromEntityObjectCostAndAmortisationNotNull() {
        IntangibleAssetsDataEntity intangibleAssetsDataEntity = createIntangibleAssetsDataEntity();
        intangibleAssetsDataEntity.setTotal(createIntangibleAssetsResourceEntity(false, false));

        IntangibleAssetsEntity intangibleAssetsEntity = new IntangibleAssetsEntity();
        intangibleAssetsEntity.setData(intangibleAssetsDataEntity);
        IntangibleAssets intangibleAssets = transformer.transform(intangibleAssetsEntity);

        assertNotNull(intangibleAssets);
        assertNotNull(intangibleAssets.getTotal());
        assertNotNull(intangibleAssets.getTotal().getAmortisation());
        assertNotNull(intangibleAssets.getTotal().getCost());

        assertNull(intangibleAssets.getOtherIntangibleAssets());
        assertNull(intangibleAssets.getGoodwill());

        assertRestObjectFieldsMapped(intangibleAssets);
        assertAdditionalInformationMapped(intangibleAssets);
        assertNetBookValueFieldsMapped(intangibleAssets.getTotal());
        assertAmortisationFieldsMapped(intangibleAssets.getTotal().getAmortisation());
    }
    @Test
    @DisplayName("Test other intangible assets field map from the ENTITY object when the nested amortisation and cost objects are null")
    void TestTotalMapFromEntityObjectCostAndAmortisationNull() {
        IntangibleAssetsDataEntity intangibleAssetsDataEntity = createIntangibleAssetsDataEntity();
        intangibleAssetsDataEntity.setTotal(createIntangibleAssetsResourceEntity(true, true));

        IntangibleAssetsEntity intangibleAssetsEntity = new IntangibleAssetsEntity();
        intangibleAssetsEntity.setData(intangibleAssetsDataEntity);
        IntangibleAssets intangibleAssets = transformer.transform(intangibleAssetsEntity);

        assertNotNull(intangibleAssets);
        assertNotNull(intangibleAssets.getTotal());

        assertNull(intangibleAssets.getTotal().getAmortisation());
        assertNull(intangibleAssets.getTotal().getCost());
        assertNull(intangibleAssets.getOtherIntangibleAssets());
        assertNull(intangibleAssets.getGoodwill());

        assertRestObjectFieldsMapped(intangibleAssets);
        assertAdditionalInformationMapped(intangibleAssets);
        assertNetBookValueFieldsMapped(intangibleAssets.getTotal());
    }



    private IntangibleAssets createIntangibleAssets() {

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        intangibleAssets.setAdditionalInformation(ADDITIONAL_INFORMATION);
        intangibleAssets.setEtag(ETAG);
        intangibleAssets.setKind(KIND);
        intangibleAssets.setLinks(LINKS);
        return intangibleAssets;
    }

    private IntangibleAssetsResource createIntangibleAssetsResource(boolean costNull,
                                                                    boolean amortisationNull) {

        IntangibleAssetsResource intangibleAssetsResource = new IntangibleAssetsResource();
        intangibleAssetsResource
                .setNetBookValueAtEndOfCurrentPeriod(NET_BOOK_VALUE_AT_END_OF_CURRENT_PERIOD);
        intangibleAssetsResource
                .setNetBookValueAtEndOfPreviousPeriod(NET_BOOK_VALUE_AT_END_OF_PREVIOUS_PERIOD);

        if (!costNull) {
            intangibleAssetsResource.setCost(createCost());
        }

        if (!amortisationNull) {
            intangibleAssetsResource.setAmortisation(createAmortisation());
        }

        return intangibleAssetsResource;
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

    private Amortisation createAmortisation() {

        Amortisation amortisation = new Amortisation();
        amortisation.setAtPeriodEnd(AMORTISATION_AT_PERIOD_END);
        amortisation.setAtPeriodStart(AMORTISATION_AT_PERIOD_START);
        amortisation.setChargeForYear(AMORTISATION_CHARGE_FOR_YEAR);
        amortisation.setOnDisposals(AMORTISATION_ON_DISPOSALS);
        amortisation.setOtherAdjustments(AMORTISATION_OTHER_ADJUSTMENTS);
        return amortisation;
    }

    private IntangibleAssetsDataEntity createIntangibleAssetsDataEntity() {

        IntangibleAssetsDataEntity dataEntity = new IntangibleAssetsDataEntity();
        dataEntity.setAdditionalInformation(ADDITIONAL_INFORMATION);
        dataEntity.setEtag(ETAG);
        dataEntity.setKind(KIND);
        dataEntity.setLinks(LINKS);
        return dataEntity;
    }

    private IntangibleAssetsResourceEntity createIntangibleAssetsResourceEntity(boolean costNull,
                                                                                boolean amortisationNull) {

        IntangibleAssetsResourceEntity intangibleAssetsResourceEntity = new IntangibleAssetsResourceEntity();
        intangibleAssetsResourceEntity
                .setNetBookValueAtEndOfCurrentPeriod(NET_BOOK_VALUE_AT_END_OF_CURRENT_PERIOD);
        intangibleAssetsResourceEntity
                .setNetBookValueAtEndOfPreviousPeriod(NET_BOOK_VALUE_AT_END_OF_PREVIOUS_PERIOD);

        if (!costNull) {
            intangibleAssetsResourceEntity.setCost(createCostEntity());
        }

        if (!amortisationNull) {
            intangibleAssetsResourceEntity.setAmortisation(createAmortisationEntity());
        }

        return intangibleAssetsResourceEntity;
    }

    private CostEntity createCostEntity() {

        CostEntity cost = new CostEntity();
        cost.setAdditions(COST_ADDITIONS);
        cost.setAtPeriodEnd(COST_AT_PERIOD_END);
        cost.setAtPeriodStart(COST_AT_PERIOD_START);
        cost.setDisposals(COST_DISPOSALS);
        cost.setRevaluations(COST_REVALUATIONS);
        cost.setTransfers(COST_TRANSFERS);
        return cost;
    }

    private AmortisationEntity createAmortisationEntity() {

        AmortisationEntity amortisation = new AmortisationEntity();
        amortisation.setAtPeriodEnd(AMORTISATION_AT_PERIOD_END);
        amortisation.setAtPeriodStart(AMORTISATION_AT_PERIOD_START);
        amortisation.setChargeForYear(AMORTISATION_CHARGE_FOR_YEAR);
        amortisation.setOnDisposals(AMORTISATION_ON_DISPOSALS);
        amortisation.setOtherAdjustments(AMORTISATION_OTHER_ADJUSTMENTS);
        return amortisation;
    }

    private void assertRestObjectFieldsMapped(IntangibleAssetsEntity intangibleAssets) {
        assertEquals(ETAG, intangibleAssets.getData().getEtag());
        assertEquals(KIND, intangibleAssets.getData().getKind());
        assertEquals(LINKS, intangibleAssets.getData().getLinks());
    }

    private void assertAdditionalInformationMapped(IntangibleAssetsDataEntity intangibleAssets) {
        assertEquals(ADDITIONAL_INFORMATION, intangibleAssets.getAdditionalInformation());
    }

    private void assertNetBookValueFieldsMapped(IntangibleAssetsResourceEntity intangibleAssetsResource) {
        assertEquals(NET_BOOK_VALUE_AT_END_OF_CURRENT_PERIOD,
                intangibleAssetsResource.getNetBookValueAtEndOfCurrentPeriod());
        assertEquals(NET_BOOK_VALUE_AT_END_OF_CURRENT_PERIOD,
                intangibleAssetsResource.getNetBookValueAtEndOfCurrentPeriod());
    }

    private void assertCostFieldsMapped(CostEntity cost) {
        assertEquals(COST_ADDITIONS, cost.getAdditions());
        assertEquals(COST_AT_PERIOD_END, cost.getAtPeriodEnd());
        assertEquals(COST_AT_PERIOD_START, cost.getAtPeriodStart());
        assertEquals(COST_DISPOSALS, cost.getDisposals());
        assertEquals(COST_REVALUATIONS, cost.getRevaluations());
        assertEquals(COST_TRANSFERS, cost.getTransfers());
    }

    private void assertAmortisationFieldsMapped(AmortisationEntity amortisation) {
        assertEquals(AMORTISATION_AT_PERIOD_END, amortisation.getAtPeriodEnd());
        assertEquals(AMORTISATION_AT_PERIOD_START, amortisation.getAtPeriodStart());
        assertEquals(AMORTISATION_CHARGE_FOR_YEAR, amortisation.getChargeForYear());
        assertEquals(AMORTISATION_ON_DISPOSALS, amortisation.getOnDisposals());
        assertEquals(AMORTISATION_OTHER_ADJUSTMENTS, amortisation.getOtherAdjustments());
    }

    private void assertRestObjectFieldsMapped(IntangibleAssets intangibleAssets) {
        assertEquals(ETAG, intangibleAssets.getEtag());
        assertEquals(KIND, intangibleAssets.getKind());
        assertEquals(LINKS, intangibleAssets.getLinks());
    }

    private void assertAdditionalInformationMapped(IntangibleAssets intangibleAssets) {
        assertEquals(ADDITIONAL_INFORMATION, intangibleAssets.getAdditionalInformation());
    }

    private void assertNetBookValueFieldsMapped(IntangibleAssetsResource intangibleAssetsResource) {
        assertEquals(NET_BOOK_VALUE_AT_END_OF_CURRENT_PERIOD,
                intangibleAssetsResource.getNetBookValueAtEndOfCurrentPeriod());
        assertEquals(NET_BOOK_VALUE_AT_END_OF_CURRENT_PERIOD,
                intangibleAssetsResource.getNetBookValueAtEndOfCurrentPeriod());
    }

    private void assertCostFieldsMapped(Cost cost) {
        assertEquals(COST_ADDITIONS, cost.getAdditions());
        assertEquals(COST_AT_PERIOD_END, cost.getAtPeriodEnd());
        assertEquals(COST_AT_PERIOD_START, cost.getAtPeriodStart());
        assertEquals(COST_DISPOSALS, cost.getDisposals());
        assertEquals(COST_REVALUATIONS, cost.getRevaluations());
        assertEquals(COST_TRANSFERS, cost.getTransfers());
    }

    private void assertAmortisationFieldsMapped(Amortisation amortisation) {
        assertEquals(AMORTISATION_AT_PERIOD_END, amortisation.getAtPeriodEnd());
        assertEquals(AMORTISATION_AT_PERIOD_START, amortisation.getAtPeriodStart());
        assertEquals(AMORTISATION_CHARGE_FOR_YEAR, amortisation.getChargeForYear());
        assertEquals(AMORTISATION_ON_DISPOSALS, amortisation.getOnDisposals());
        assertEquals(AMORTISATION_OTHER_ADJUSTMENTS, amortisation.getOtherAdjustments());
    }



}
