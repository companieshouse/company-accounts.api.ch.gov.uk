package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions;

import java.util.Map;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

public class RelatedPartyTransactionsDataEntity extends BaseDataEntity {

    @Field("transactions")
    private Map<String, String> transactions;

    public Map<String, String> getTransactions() {
        return transactions;
    }

    public void setTransactions(Map<String, String> transactions) {
        this.transactions = transactions;
    }
}