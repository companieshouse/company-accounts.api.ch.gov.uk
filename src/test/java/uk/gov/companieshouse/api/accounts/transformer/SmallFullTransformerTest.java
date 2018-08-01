package uk.gov.companieshouse.api.accounts.transformer;

import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class SmallFullTransformerTest {


    private SmallFullTransformer smallFullTransformer = new SmallFullTransformer();

    @Test
    @DisplayName("Tests smallfull transformer with empty object which should result in null values")
    public void testTransformerWithEmptyObject() {
        SmallFullEntity smallFullEntity = smallFullTransformer.transform(new SmallFull());

        Assertions.assertNotNull(smallFullEntity);
        Assertions.assertNull(smallFullEntity.getData().getEtag());
        Assertions.assertNull(smallFullEntity.getData().getKind());
        Assertions.assertEquals(new HashMap<>(), smallFullEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests smallfull transformer with populated object and validates values returned")
    public void testTransformerWithPopulatedObject() {
        SmallFull smallFull = new SmallFull();
        smallFull.setEtag("etag");
        smallFull.setKind("kind");
        smallFull.setLinks(new HashMap<>());

        SmallFullEntity smallFullEntity = smallFullTransformer.transform(smallFull);

        Assertions.assertNotNull(smallFullEntity);
        Assertions.assertEquals("etag", smallFullEntity.getData().getEtag());
        Assertions.assertEquals("kind", smallFullEntity.getData().getKind());
        Assertions.assertEquals(new HashMap<>(), smallFullEntity.getData().getLinks());
    }
}

