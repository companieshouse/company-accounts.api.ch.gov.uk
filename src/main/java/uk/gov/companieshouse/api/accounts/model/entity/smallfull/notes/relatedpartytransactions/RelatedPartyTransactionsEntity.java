package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

@Document(collection = "notes")
public class RelatedPartyTransactionsEntity extends BaseEntity {

    @Field("data")
    private RelatedPartyTransactionsDataEntity data;

    public RelatedPartyTransactionsDataEntity getData() {
        return data;
    }

    public void setData(RelatedPartyTransactionsDataEntity data) {
        this.data = data;
    }

}