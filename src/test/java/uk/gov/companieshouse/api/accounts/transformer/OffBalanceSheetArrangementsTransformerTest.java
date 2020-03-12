package uk.gov.companieshouse.api.accounts.transformer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.offbalancesheetarrangements.OffBalanceSheetArrangementsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.offbalancesheetarrangements.OffBalanceSheetArrangementsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.offbalancesheetarrangements.OffBalanceSheetArrangements;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OffBalanceSheetArrangementsTransformerTest {

    private static final String DETAILS = "details";
    private static final String ETAG = "etag";
    private static final String KIND = "kind";

    private OffBalanceSheetArrangementsTransformer offBalanceSheetArrangementsTransformer = new OffBalanceSheetArrangementsTransformer();
    @Test
    @DisplayName("Tests transformer with empty rest object returns null values ")
     void testTransformerWithEmptyRestObject() {

        OffBalanceSheetArrangementsEntity offBalanceSheetArrangementsEntity = offBalanceSheetArrangementsTransformer
                .transform(new OffBalanceSheetArrangements());

        assertNotNull(offBalanceSheetArrangementsEntity);
        assertNull(offBalanceSheetArrangementsEntity.getData().getEtag());
        assertEquals(new HashMap<>(), offBalanceSheetArrangementsEntity.getData().getLinks());
        assertEquals(AccountingNoteType.SMALL_FULL_OFF_BALANCE_SHEET_ARRANGEMENTS, offBalanceSheetArrangementsTransformer.getAccountingNoteType());
    }

    @Test
    @DisplayName("Tests transformer with fully populated Rest object and validates values returned")
     void testRestToEntityTransformerWithFullyPopulatedObject() {

        OffBalanceSheetArrangements offBalanceSheetArrangements = new OffBalanceSheetArrangements();

        offBalanceSheetArrangements.setDetails(DETAILS);
        offBalanceSheetArrangements.setEtag(ETAG);
        offBalanceSheetArrangements.setKind(KIND);
        offBalanceSheetArrangements.setLinks(new HashMap<>());

        OffBalanceSheetArrangementsEntity offBalanceSheetArrangementsEntity = offBalanceSheetArrangementsTransformer
                .transform(offBalanceSheetArrangements);

        assertNotNull(offBalanceSheetArrangementsEntity);
        assertEquals(DETAILS, offBalanceSheetArrangementsEntity.getData().getDetails());
        assertEquals(new HashMap<>(), offBalanceSheetArrangementsEntity.getData().getLinks());
        assertEquals(ETAG, offBalanceSheetArrangementsEntity.getData().getEtag());
        assertEquals(KIND, offBalanceSheetArrangementsEntity.getData().getKind());
    }

    @Test
    @DisplayName("Tests transformer with empty entity object returns null values ")
     void testTransformerWithEmptyEntityObject() {

        OffBalanceSheetArrangements offBalanceSheetArrangements = offBalanceSheetArrangementsTransformer
                .transform(new OffBalanceSheetArrangementsEntity());

        assertNotNull(offBalanceSheetArrangements);
        assertNull(offBalanceSheetArrangements.getEtag());
        assertEquals(new HashMap<>(), offBalanceSheetArrangements.getLinks());
    }

    @Test
    @DisplayName("Tests transformer with fully populated Entity object and validates values returned")
     void testEntityToRestTransformerWithFullyPopulatedEntityObject() {

        OffBalanceSheetArrangementsEntity offBalanceSheetArrangementsEntity = new OffBalanceSheetArrangementsEntity();
        OffBalanceSheetArrangementsDataEntity offBalanceSheetArrangementsDataEntity = new OffBalanceSheetArrangementsDataEntity();

        offBalanceSheetArrangementsDataEntity.setDetails(DETAILS);
        offBalanceSheetArrangementsDataEntity.setEtag(ETAG);
        offBalanceSheetArrangementsDataEntity.setKind(KIND);
        offBalanceSheetArrangementsDataEntity.setLinks(new HashMap<>());

        offBalanceSheetArrangementsEntity.setData(offBalanceSheetArrangementsDataEntity);

        OffBalanceSheetArrangements offBalanceSheetArrangements = offBalanceSheetArrangementsTransformer
                .transform(offBalanceSheetArrangementsEntity);

        assertNotNull(offBalanceSheetArrangements);
        assertEquals(DETAILS, offBalanceSheetArrangements.getDetails());
        assertEquals(new HashMap<>(), offBalanceSheetArrangements.getLinks());
        assertEquals(ETAG, offBalanceSheetArrangements.getEtag());
        assertEquals(KIND, offBalanceSheetArrangements.getKind());
    }
}