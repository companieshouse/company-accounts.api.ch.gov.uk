package uk.gov.companieshouse.api.accounts.transformer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.notes.currentassetsinvestments.CurrentAssetsInvestmentsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.currentassetsinvestments.CurrentAssetsInvestmentsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.CurrentAssetsInvestments;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CurrentAssetsInvestmentTransformerTest {

    private static final String DETAILS = "details";
    private static final String ETAG = "etag";
    private static final String KIND = "kind";

    private CurrentAssetsInvestmentsTransformer currentAssetsInvestmentsTransformer = new CurrentAssetsInvestmentsTransformer();

    @Test
    @DisplayName("Tests transformer with empty rest object returns null values ")
    public void testTransformerWithEmptyRestObject() {

        CurrentAssetsInvestmentsEntity currentAssetsInvestmentsEntity = currentAssetsInvestmentsTransformer
            .transform(new CurrentAssetsInvestments());

        assertNotNull(currentAssetsInvestmentsEntity);
        assertNull(currentAssetsInvestmentsEntity.getData().getEtag());
        assertEquals(new HashMap<>(), currentAssetsInvestmentsEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests transformer with fully populated Rest object and validates values returned")
    public void testRestToEntityTransformerWithFullyPopulatedObject() {

        CurrentAssetsInvestments currentAssetsInvestments = new CurrentAssetsInvestments();

        currentAssetsInvestments.setDetails(DETAILS);
        currentAssetsInvestments.setEtag(ETAG);
        currentAssetsInvestments.setKind(KIND);
        currentAssetsInvestments.setLinks(new HashMap<>());

        CurrentAssetsInvestmentsEntity fixedAssetsInvestmentsEntity = currentAssetsInvestmentsTransformer
            .transform(currentAssetsInvestments);

        assertNotNull(fixedAssetsInvestmentsEntity);
        assertEquals(DETAILS, fixedAssetsInvestmentsEntity.getData().getDetails());
        assertEquals(new HashMap<>(), fixedAssetsInvestmentsEntity.getData().getLinks());
        assertEquals(ETAG, fixedAssetsInvestmentsEntity.getData().getEtag());
        assertEquals(KIND, fixedAssetsInvestmentsEntity.getData().getKind());
    }

    @Test
    @DisplayName("Tests transformer with empty entity object returns null values ")
    public void testTransformerWithEmptyEntityObject() {

        CurrentAssetsInvestments fixedAssetsInvestments = currentAssetsInvestmentsTransformer
            .transform(new CurrentAssetsInvestmentsEntity());

        assertNotNull(fixedAssetsInvestments);
        assertNull(fixedAssetsInvestments.getEtag());
        assertEquals(new HashMap<>(), fixedAssetsInvestments.getLinks());
    }

    @Test
    @DisplayName("Tests transformer with fully populated Entity object and validates values returned")
    public void testEntityToRestTransformerWithFullyPopulatedEntityObject() {

        CurrentAssetsInvestmentsEntity currentAssetsInvestmentsEntity = new CurrentAssetsInvestmentsEntity();
        CurrentAssetsInvestmentsDataEntity currentAssetsInvestmentsDataEntity = new CurrentAssetsInvestmentsDataEntity();

        currentAssetsInvestmentsDataEntity.setDetails(DETAILS);
        currentAssetsInvestmentsDataEntity.setEtag(ETAG);
        currentAssetsInvestmentsDataEntity.setKind(KIND);
        currentAssetsInvestmentsDataEntity.setLinks(new HashMap<>());

        currentAssetsInvestmentsEntity.setData(currentAssetsInvestmentsDataEntity);

        CurrentAssetsInvestments currentAssetsInvestments = currentAssetsInvestmentsTransformer
            .transform(currentAssetsInvestmentsEntity);

        assertNotNull(currentAssetsInvestments);
        assertEquals(DETAILS, currentAssetsInvestments.getDetails());
        assertEquals(new HashMap<>(), currentAssetsInvestments.getLinks());
        assertEquals(ETAG, currentAssetsInvestments.getEtag());
        assertEquals(KIND, currentAssetsInvestments.getKind());
    }
}
