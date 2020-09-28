package uk.gov.companieshouse.api.accounts.transformer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.financialcommitments.FinancialCommitmentsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.financialcommitments.FinancialCommitmentsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.financialcommitments.FinancialCommitments;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FinancialCommitmentsTransformerTest {

    private static final String DETAILS = "details";
    private static final String ETAG = "etag";
    private static final String KIND = "kind";

    private FinancialCommitmentsTransformer financialCommitmentsTransformer = new FinancialCommitmentsTransformer();
    @Test
    @DisplayName("Tests transformer with empty rest object returns null values ")
     void testTransformerWithEmptyRestObject() {

        FinancialCommitmentsEntity financialCommitmentsEntity = financialCommitmentsTransformer
                .transform(new FinancialCommitments());

        assertNotNull(financialCommitmentsEntity);
        assertNull(financialCommitmentsEntity.getData().getEtag());
        assertEquals(new HashMap<>(), financialCommitmentsEntity.getData().getLinks());
        assertEquals(AccountingNoteType.SMALL_FULL_FINANCIAL_COMMITMENTS, financialCommitmentsTransformer.getAccountingNoteType());
    }

    @Test
    @DisplayName("Tests transformer with fully populated Rest object and validates values returned")
     void testRestToEntityTransformerWithFullyPopulatedObject() {

        FinancialCommitments financialCommitments = new FinancialCommitments();

        financialCommitments.setDetails(DETAILS);
        financialCommitments.setEtag(ETAG);
        financialCommitments.setKind(KIND);
        financialCommitments.setLinks(new HashMap<>());

        FinancialCommitmentsEntity financialCommitmentsEntity = financialCommitmentsTransformer
                .transform(financialCommitments);

        assertNotNull(financialCommitmentsEntity);
        assertEquals(DETAILS, financialCommitmentsEntity.getData().getDetails());
        assertEquals(new HashMap<>(), financialCommitmentsEntity.getData().getLinks());
        assertEquals(ETAG, financialCommitmentsEntity.getData().getEtag());
        assertEquals(KIND, financialCommitmentsEntity.getData().getKind());
    }

    @Test
    @DisplayName("Tests transformer with empty entity object returns null values ")
     void testTransformerWithEmptyEntityObject() {

        FinancialCommitments financialCommitments = financialCommitmentsTransformer
                .transform(new FinancialCommitmentsEntity());

        assertNotNull(financialCommitments);
        assertNull(financialCommitments.getEtag());
        assertEquals(new HashMap<>(), financialCommitments.getLinks());
    }

    @Test
    @DisplayName("Tests transformer with fully populated Entity object and validates values returned")
     void testEntityToRestTransformerWithFullyPopulatedEntityObject() {

        FinancialCommitmentsEntity financialCommitmentsEntity = new FinancialCommitmentsEntity();
        FinancialCommitmentsDataEntity financialCommitmentsDataEntity = new FinancialCommitmentsDataEntity();

        financialCommitmentsDataEntity.setDetails(DETAILS);
        financialCommitmentsDataEntity.setEtag(ETAG);
        financialCommitmentsDataEntity.setKind(KIND);
        financialCommitmentsDataEntity.setLinks(new HashMap<>());

        financialCommitmentsEntity.setData(financialCommitmentsDataEntity);

        FinancialCommitments financialCommitments = financialCommitmentsTransformer
                .transform(financialCommitmentsEntity);

        assertNotNull(financialCommitments);
        assertEquals(DETAILS, financialCommitments.getDetails());
        assertEquals(new HashMap<>(), financialCommitments.getLinks());
        assertEquals(ETAG, financialCommitments.getEtag());
        assertEquals(KIND, financialCommitments.getKind());
    }
}