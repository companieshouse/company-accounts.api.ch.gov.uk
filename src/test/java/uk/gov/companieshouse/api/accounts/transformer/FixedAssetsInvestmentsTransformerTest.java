package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.notes.fixedassetsinvestments.FixedAssetsInvestmentsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.fixedassetsinvestments.FixedAssetsInvestmentsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.fixedassetsinvestments.FixedAssetsInvestments;


@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class FixedAssetsInvestmentsTransformerTest {

    private static final String DETAILS = "details";
    private static final String ETAG = "etag";
    private static final String KIND = "kind";
    
    private FixedAssetsInvestmentsTransformer fixedAssetsInvestmentsTransformer = new FixedAssetsInvestmentsTransformer();

    @Test
    @DisplayName("Tests transformer with empty rest object returns null values ")
    public void testTransformerWithEmptyRestObject() {

        FixedAssetsInvestmentsEntity fixedAssetsInvestmentsEntity = fixedAssetsInvestmentsTransformer
            .transform(new FixedAssetsInvestments());

        assertNotNull(fixedAssetsInvestmentsEntity);
        assertNull(fixedAssetsInvestmentsEntity.getData().getEtag());
        assertEquals(new HashMap<>(), fixedAssetsInvestmentsEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests transformer with fully populated Rest object and validates values returned")
    public void testRestToEntityTransformerWithFullyPopulatedObject() {

        FixedAssetsInvestments fixedAssetsInvestments = new FixedAssetsInvestments();

        fixedAssetsInvestments.setDetails(DETAILS);
        fixedAssetsInvestments.setEtag(ETAG);
        fixedAssetsInvestments.setKind(KIND);
        fixedAssetsInvestments.setLinks(new HashMap<>());

        FixedAssetsInvestmentsEntity fixedAssetsInvestmentsEntity = fixedAssetsInvestmentsTransformer
            .transform(fixedAssetsInvestments);

        assertNotNull(fixedAssetsInvestmentsEntity);
        assertEquals(DETAILS, fixedAssetsInvestmentsEntity.getData().getDetails());
        assertEquals(new HashMap<>(), fixedAssetsInvestmentsEntity.getData().getLinks());
        assertEquals(ETAG, fixedAssetsInvestmentsEntity.getData().getEtag());
        assertEquals(KIND, fixedAssetsInvestmentsEntity.getData().getKind());
    }

    @Test
    @DisplayName("Tests transformer with empty entity object returns null values ")
    public void testTransformerWithEmptyEntityObject() {

        FixedAssetsInvestments fixedAssetsInvestments = fixedAssetsInvestmentsTransformer
            .transform(new FixedAssetsInvestmentsEntity());

        assertNotNull(fixedAssetsInvestments);
        assertNull(fixedAssetsInvestments.getEtag());
        assertEquals(new HashMap<>(), fixedAssetsInvestments.getLinks());
    }

    @Test
    @DisplayName("Tests transformer with fully populated Entity object and validates values returned")
    public void testEntityToRestTransformerWithFullyPopulatedEntityObject() {

        FixedAssetsInvestmentsEntity fixedAssetsInvestmentsEntity = new FixedAssetsInvestmentsEntity();
        FixedAssetsInvestmentsDataEntity fixedAssetsInvestmentsDataEntity = new FixedAssetsInvestmentsDataEntity();

        fixedAssetsInvestmentsDataEntity.setDetails(DETAILS);
        fixedAssetsInvestmentsDataEntity.setEtag(ETAG);
        fixedAssetsInvestmentsDataEntity.setKind(KIND);
        fixedAssetsInvestmentsDataEntity.setLinks(new HashMap<>());

        fixedAssetsInvestmentsEntity.setData(fixedAssetsInvestmentsDataEntity);

        FixedAssetsInvestments fixedAssetsInvestments = fixedAssetsInvestmentsTransformer
            .transform(fixedAssetsInvestmentsEntity);

        assertNotNull(fixedAssetsInvestments);
        assertEquals(DETAILS, fixedAssetsInvestments.getDetails());
        assertEquals(new HashMap<>(), fixedAssetsInvestments.getLinks());
        assertEquals(ETAG, fixedAssetsInvestments.getEtag());
        assertEquals(KIND, fixedAssetsInvestments.getKind());
    }
}
