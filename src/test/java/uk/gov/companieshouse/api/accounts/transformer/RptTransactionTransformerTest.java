package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.RptTransactionBreakdownEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.RptTransactionDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.RptTransactionEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RptTransaction;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RptTransactionBreakdown;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RptTransactionTransformerTest {

    private RptTransactionBreakdown rptTransactionBreakdownResource = new RptTransactionBreakdown();

    private static final String RELATED_PARTY_NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String RELATIONSHIP = "relationship";
    private static final String TRANSACTION_TYPE = "transactionType";

    private RptTransactionTransformer transformer = new RptTransactionTransformer();

    @Test
    @DisplayName("Transform rest object to entity")
    void restToEntity() {

        RptTransaction rptTransaction = new RptTransaction();
        rptTransaction.setNameOfRelatedParty(RELATED_PARTY_NAME);
        rptTransaction.setRelationship(RELATIONSHIP);
        rptTransaction.setTransactionType(TRANSACTION_TYPE);
        rptTransaction.setDescriptionOfTransaction(DESCRIPTION);
        rptTransaction.setBreakdown(rptTransactionBreakdownResource);

        RptTransactionEntity rptTransactionEntity = transformer.transform(rptTransaction);

        assertNotNull(rptTransactionEntity);
        assertNotNull(rptTransactionEntity.getData());
        assertNotNull(rptTransactionEntity.getData().getNameOfRelatedParty());
        assertNotNull(rptTransactionEntity.getData().getRelationship());
        assertNotNull(rptTransactionEntity.getData().getTransactionType());
        assertNotNull(rptTransactionEntity.getData().getDescriptionOfTransaction());
        assertNotNull(rptTransactionEntity.getData().getBreakdown());
    }

    @Test
    @DisplayName("Transform rest object to entity")
    void restToEntityNoBreakdown() {

        RptTransaction rptTransaction = new RptTransaction();
        rptTransaction.setNameOfRelatedParty(RELATED_PARTY_NAME);
        rptTransaction.setRelationship(RELATIONSHIP);
        rptTransaction.setTransactionType(TRANSACTION_TYPE);
        rptTransaction.setDescriptionOfTransaction(DESCRIPTION);

        RptTransactionEntity rptTransactionEntity = transformer.transform(rptTransaction);

        assertNotNull(rptTransactionEntity);
        assertNotNull(rptTransactionEntity.getData());
        assertNotNull(rptTransactionEntity.getData().getNameOfRelatedParty());
        assertNotNull(rptTransactionEntity.getData().getRelationship());
        assertNotNull(rptTransactionEntity.getData().getTransactionType());
        assertNotNull(rptTransactionEntity.getData().getDescriptionOfTransaction());
    }

    @Test
    @DisplayName("Transform entity to rest object")
    void entityToRest() {

        RptTransaction rptTransaction = transformer.transform(getRptTransactionEntity(true));

        assertNotNull(rptTransaction);
        assertRestFieldsSet(rptTransaction, true);
    }

    @Test
    @DisplayName("Transform entity to rest object")
    void entityToRestNoBreakdown() {

        RptTransaction rptTransaction = transformer.transform(getRptTransactionEntity(false));

        assertNotNull(rptTransaction);
        assertRestFieldsSet(rptTransaction, false);
    }

    @Test
    @DisplayName("Transform entity array to rest object array")
    void entityArrayToRestArray() {

        RptTransactionEntity[] entities = new RptTransactionEntity[]{getRptTransactionEntity(true), getRptTransactionEntity(true)};

        RptTransaction[] rptTransactions = transformer.transform(entities);

        assertNotNull(rptTransactions);
        assertEquals(2, rptTransactions.length);
        assertRestFieldsSet(rptTransactions[0], true);
        assertRestFieldsSet(rptTransactions[1], true);
    }

    private RptTransactionEntity getRptTransactionEntity(boolean withBreakdown) {

        RptTransactionBreakdownEntity rptTransactionBreakdownEntity = new RptTransactionBreakdownEntity();
        rptTransactionBreakdownEntity.setBalanceAtPeriodEnd(1L);
        rptTransactionBreakdownEntity.setBalanceAtPeriodStart(1L);

        RptTransactionDataEntity rptTransactionDataEntity = new RptTransactionDataEntity();
        rptTransactionDataEntity.setNameOfRelatedParty(RELATED_PARTY_NAME);
        rptTransactionDataEntity.setDescriptionOfTransaction(DESCRIPTION);
        rptTransactionDataEntity.setRelationship(RELATIONSHIP);
        rptTransactionDataEntity.setTransactionType(TRANSACTION_TYPE);
        if(withBreakdown) {
            rptTransactionDataEntity.setBreakdown(rptTransactionBreakdownEntity);
        }

        RptTransactionEntity rptTransactionEntity = new RptTransactionEntity();
        rptTransactionEntity.setData(rptTransactionDataEntity);

        return rptTransactionEntity;
    }

    private void assertRestFieldsSet(RptTransaction rptTransaction, boolean withBreakdown) {
        rptTransactionBreakdownResource.setBalanceAtPeriodStart(1L);
        rptTransactionBreakdownResource.setBalanceAtPeriodEnd(1L);

        assertEquals(RELATED_PARTY_NAME, rptTransaction.getNameOfRelatedParty());
        assertEquals(DESCRIPTION, rptTransaction.getDescriptionOfTransaction());
        assertEquals(RELATIONSHIP, rptTransaction.getRelationship());
        assertEquals(TRANSACTION_TYPE, rptTransaction.getTransactionType());

        if(withBreakdown) {
            assertEquals(rptTransactionBreakdownResource.getBalanceAtPeriodEnd(), rptTransaction.getBreakdown().getBalanceAtPeriodEnd());
            assertEquals(rptTransactionBreakdownResource.getBalanceAtPeriodStart(), rptTransaction.getBreakdown().getBalanceAtPeriodStart());
        }
    }
}