package uk.gov.companieshouse.api.accounts.transformer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorsApprovalDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorsApprovalEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsApproval;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DirectorsApprovalTransformerTest {

    public static final String ETAG = "etag";
    public static final String KIND = "kind";
    public static final String NAME = "name";

    private DirectorsApprovalTransformer directorsApprovalTransformer = new DirectorsApprovalTransformer();

    @Test
    @DisplayName("Tests directors approval  transformer with empty object which should result in null values")
    void testTransformerWithEmptyObject() {
        DirectorsApprovalEntity directorsApprovalEntity = directorsApprovalTransformer
                .transform(new DirectorsApproval());

        assertNotNull(directorsApprovalEntity);
        assertNull(directorsApprovalEntity.getData().getEtag());
        assertEquals(new HashMap<>(), directorsApprovalEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests directors approval transformer with populated Rest object and validates values returned")
    void testRestToEntityTransformerWithPopulatedObject() {

        DirectorsApproval directorsApproval = new DirectorsApproval();
        directorsApproval.setEtag(ETAG);
        directorsApproval.setKind(KIND);
        directorsApproval.setName(NAME);
        directorsApproval.setLinks(new HashMap<>());

        DirectorsApprovalEntity directorsApprovalEntity = directorsApprovalTransformer
                .transform(directorsApproval);

        DirectorsApprovalDataEntity directorsApprovalDataEntity = directorsApprovalEntity.getData();

        assertNotNull(directorsApprovalEntity);
        assertEquals(ETAG, directorsApprovalDataEntity.getEtag());
        assertEquals(KIND, directorsApprovalDataEntity.getKind());
        assertEquals(NAME, directorsApprovalDataEntity.getName());
        assertEquals(new HashMap<>(), directorsApprovalDataEntity.getLinks());
    }

    @Test
    @DisplayName("Tests directors approval transformer with populated Entity object and validates values returned")
    void testEntityToRestTransformerWithPopulatedObject() {

        DirectorsApprovalEntity directorsApprovalEntity = new DirectorsApprovalEntity();
        DirectorsApprovalDataEntity directorsApprovalDataEntity = new DirectorsApprovalDataEntity();
        directorsApprovalDataEntity.setEtag(ETAG);
        directorsApprovalDataEntity.setKind(KIND);
        directorsApprovalDataEntity.setName(NAME);
        directorsApprovalDataEntity.setLinks(new HashMap<>());
        directorsApprovalEntity.setData(directorsApprovalDataEntity);

        DirectorsApproval directorsApproval = directorsApprovalTransformer.transform(directorsApprovalEntity);

        assertNotNull(directorsApproval);
        assertEquals(ETAG, directorsApproval.getEtag());
        assertEquals(KIND, directorsApproval.getKind());
        assertEquals(NAME, directorsApproval.getName());
        assertEquals(new HashMap<>(), directorsApproval.getLinks());
    }
}