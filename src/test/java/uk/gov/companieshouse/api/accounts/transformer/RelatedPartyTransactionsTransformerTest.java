package uk.gov.companieshouse.api.accounts.transformer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.RelatedPartyTransactionsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.RelatedPartyTransactionsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RelatedPartyTransactions;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RelatedPartyTransactionsTransformerTest {

    private final RelatedPartyTransactionsTransformer transformer = new RelatedPartyTransactionsTransformer();

    @Test
    @DisplayName("Transform rest object to entity")
    void restToEntity() {

        RelatedPartyTransactions relatedPartyTransactions = new RelatedPartyTransactions();

        RelatedPartyTransactionsEntity relatedPartyTransactionsEntity = transformer.transform(relatedPartyTransactions);

        assertNotNull(relatedPartyTransactionsEntity);
        assertNotNull(relatedPartyTransactionsEntity.getData());
    }

    @Test
    @DisplayName("Transform entity to rest object")
    void entityToRest() {
        RelatedPartyTransactionsDataEntity relatedPartyTransactionsDataEntity = new RelatedPartyTransactionsDataEntity();

        RelatedPartyTransactionsEntity relatedPartyTransactionsEntity = new RelatedPartyTransactionsEntity();
        relatedPartyTransactionsEntity.setData(relatedPartyTransactionsDataEntity);

        RelatedPartyTransactions relatedPartyTransactions = transformer.transform(relatedPartyTransactionsEntity);

        assertNotNull(relatedPartyTransactions);
    }
}
