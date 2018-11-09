package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.accounts.model.entity.ApprovalDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.ApprovalEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Approval;

public class ApprovalTransformerTest {

    public static final String ETAG = "etag";
    public static final String KIND = "kind";
    public static final String NAME = "name";

    private ApprovalTransformer approvalTransformer = new ApprovalTransformer();

    @Test
    @DisplayName("Tests approval  transformer with empty object which should result in null values")
    public void testTransformerWithEmptyObject() {
        ApprovalEntity approvalEntity = approvalTransformer
            .transform(new Approval());

        assertNotNull(approvalEntity);
        assertNull(approvalEntity.getData().getEtag());
        assertEquals(new HashMap<>(), approvalEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests approval transformer with populated Rest object and validates values returned")
    public void testRestToEntityTransformerWithPopulatedObject() {

        Approval approval = new Approval();
        approval.setEtag(ETAG);
        approval.setKind(KIND);
        approval.setName(NAME);
        approval.setLinks(new HashMap<>());

        ApprovalEntity approvalEntity = approvalTransformer
            .transform(approval);

        ApprovalDataEntity approvalDataEntity = approvalEntity.getData();

        assertNotNull(approvalEntity);
        assertEquals(ETAG, approvalDataEntity.getEtag());
        assertEquals(KIND, approvalDataEntity.getKind());
        assertEquals(NAME, approvalDataEntity.getName());
        assertEquals(new HashMap<>(), approvalDataEntity.getLinks());
    }

    @Test
    @DisplayName("Tests approval transformer with populated Entity object and validates values returned")
    public void testEntityToRestTransformerWithPopulatedObject() {

        ApprovalEntity approvalEntity = new ApprovalEntity();
        ApprovalDataEntity approvalDataEntity = new ApprovalDataEntity();
        approvalDataEntity.setEtag(ETAG);
        approvalDataEntity.setKind(KIND);
        approvalDataEntity.setName(NAME);
        approvalDataEntity.setLinks(new HashMap<>());
        approvalEntity.setData(approvalDataEntity);


        Approval approval = approvalTransformer.transform(approvalEntity);


        assertNotNull(approval);
        assertEquals(ETAG, approval.getEtag());
        assertEquals(KIND, approval.getKind());
        assertEquals(NAME, approval.getName());
        assertEquals(new HashMap<>(), approval.getLinks());
    }


}
