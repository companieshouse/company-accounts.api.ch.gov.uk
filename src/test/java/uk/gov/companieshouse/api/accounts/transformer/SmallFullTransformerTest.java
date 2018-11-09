package uk.gov.companieshouse.api.accounts.transformer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;

import java.util.HashMap;

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
    public void testRestToEntityTransformerWithPopulatedObject() {
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

    @Test
    @DisplayName("Tests smallfull transformer with populated object and validates values returned")
    public void testEntityToRestTransformerWithPopulatedObject() {
        SmallFullEntity smallFullEntity = new SmallFullEntity();
        SmallFullDataEntity smallFullDataEntity = new SmallFullDataEntity();
        smallFullDataEntity.setEtag("etag");
        smallFullDataEntity.setKind("kind");
        smallFullDataEntity.setLinks(new HashMap<>());
        smallFullEntity.setData(smallFullDataEntity);

        SmallFull smallFull = smallFullTransformer.transform(smallFullEntity);

        Assertions.assertNotNull(smallFull);
        Assertions.assertEquals("etag", smallFull.getEtag());
        Assertions.assertEquals("kind", smallFull.getKind());
        Assertions.assertEquals(new HashMap<>(), smallFull.getLinks());
    }
}

