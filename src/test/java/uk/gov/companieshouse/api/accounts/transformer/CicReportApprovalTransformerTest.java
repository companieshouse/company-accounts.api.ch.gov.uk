package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportApprovalDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportApprovalEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CicReportApproval;


public class CicReportApprovalTransformerTest {

    public static final String ETAG = "etag";
    public static final String KIND = "kind";
    public static final String NAME = "name";

    private CicReportApprovalTransformer cicReportApprovalTransformer = new CicReportApprovalTransformer();

    @Test
    @DisplayName("Tests CicReportApprovalTransformer with empty object which should result in null values")
    public void testTransformerWithEmptyObject() {
        CicReportApprovalEntity cicReportApprovalEntity = cicReportApprovalTransformer
            .transform(new CicReportApproval());

        assertNotNull(cicReportApprovalEntity);
        assertNull(cicReportApprovalEntity.getData().getEtag());
        assertEquals(new HashMap<>(), cicReportApprovalEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests CicReportApprovalTransformer with populated Rest object and validates values returned")
    public void testRestToEntityTransformerWithPopulatedObject() {

        CicReportApproval cicReportApproval = new CicReportApproval();
        cicReportApproval.setEtag(ETAG);
        cicReportApproval.setKind(KIND);
        cicReportApproval.setName(NAME);
        cicReportApproval.setLinks(new HashMap<>());

        CicReportApprovalEntity cicReportApprovalEntity = cicReportApprovalTransformer
            .transform(cicReportApproval);

        CicReportApprovalDataEntity cicReportApprovalEntityData = cicReportApprovalEntity.getData();

        assertNotNull(cicReportApprovalEntity);
        assertEquals(ETAG, cicReportApprovalEntityData.getEtag());
        assertEquals(KIND, cicReportApprovalEntityData.getKind());
        assertEquals(NAME, cicReportApprovalEntityData.getName());
        assertEquals(new HashMap<>(), cicReportApprovalEntityData.getLinks());
    }

    @Test
    @DisplayName("Tests CicReportApprovalTransformer with populated Entity object and validates values returned")
    public void testEntityToRestTransformerWithPopulatedObject() {

        CicReportApprovalEntity cicReportApprovalEntity = new CicReportApprovalEntity();
        CicReportApprovalDataEntity cicReportApprovalDataEntity = new CicReportApprovalDataEntity();
        cicReportApprovalDataEntity.setEtag(ETAG);
        cicReportApprovalDataEntity.setKind(KIND);
        cicReportApprovalDataEntity.setName(NAME);
        cicReportApprovalDataEntity.setLinks(new HashMap<>());
        cicReportApprovalEntity.setData(cicReportApprovalDataEntity);

        CicReportApproval cicReportApproval = cicReportApprovalTransformer
            .transform(cicReportApprovalEntity);

        assertNotNull(cicReportApproval);
        assertEquals(ETAG, cicReportApproval.getEtag());
        assertEquals(KIND, cicReportApproval.getKind());
        assertEquals(NAME, cicReportApproval.getName());
        assertEquals(new HashMap<>(), cicReportApproval.getLinks());
    }


}
