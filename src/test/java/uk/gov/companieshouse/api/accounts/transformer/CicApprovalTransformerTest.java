package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportApprovalDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportApprovalEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CicApproval;

 class CicApprovalTransformerTest {

    public static final String ETAG = "etag";
    public static final String KIND = "kind";
    public static final String NAME = "name";

    private CicApprovalTransformer cicApprovalTransformer = new CicApprovalTransformer();

    @Test
    @DisplayName("Tests CicApprovalTransformer with empty object which should result in null values")
    void testTransformerWithEmptyObject() {
        CicReportApprovalEntity cicReportApprovalEntity = cicApprovalTransformer
            .transform(new CicApproval());

        assertNotNull(cicReportApprovalEntity);
        assertNull(cicReportApprovalEntity.getData().getEtag());
        assertEquals(new HashMap<>(), cicReportApprovalEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests CicApprovalTransformer with populated Rest object and validates values returned")
    void testRestToEntityTransformerWithPopulatedObject() {

        CicApproval cicApproval = new CicApproval();
        cicApproval.setEtag(ETAG);
        cicApproval.setKind(KIND);
        cicApproval.setName(NAME);
        cicApproval.setLinks(new HashMap<>());

        CicReportApprovalEntity cicReportApprovalEntity = cicApprovalTransformer
            .transform(cicApproval);

        CicReportApprovalDataEntity cicReportApprovalEntityData = cicReportApprovalEntity.getData();

        assertNotNull(cicReportApprovalEntity);
        assertEquals(ETAG, cicReportApprovalEntityData.getEtag());
        assertEquals(KIND, cicReportApprovalEntityData.getKind());
        assertEquals(NAME, cicReportApprovalEntityData.getName());
        assertEquals(new HashMap<>(), cicReportApprovalEntityData.getLinks());
    }

    @Test
    @DisplayName("Tests CicApprovalTransformer with populated Entity object and validates values returned")
    void testEntityToRestTransformerWithPopulatedObject() {

        CicReportApprovalEntity cicReportApprovalEntity = new CicReportApprovalEntity();
        CicReportApprovalDataEntity cicReportApprovalDataEntity = new CicReportApprovalDataEntity();
        cicReportApprovalDataEntity.setEtag(ETAG);
        cicReportApprovalDataEntity.setKind(KIND);
        cicReportApprovalDataEntity.setName(NAME);
        cicReportApprovalDataEntity.setLinks(new HashMap<>());
        cicReportApprovalEntity.setData(cicReportApprovalDataEntity);

        CicApproval cicApproval = cicApprovalTransformer
            .transform(cicReportApprovalEntity);

        assertNotNull(cicApproval);
        assertEquals(ETAG, cicApproval.getEtag());
        assertEquals(KIND, cicApproval.getKind());
        assertEquals(NAME, cicApproval.getName());
        assertEquals(new HashMap<>(), cicApproval.getLinks());
    }
}
