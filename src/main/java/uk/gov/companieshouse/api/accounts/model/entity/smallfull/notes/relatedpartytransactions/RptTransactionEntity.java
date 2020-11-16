package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

@Document(collection = "transactions")
public class RptTransactionEntity extends BaseEntity {

    @Field
    private RptTransactionDataEntity data;

    public RptTransactionDataEntity getData() {
        return data;
    }

    public void setData(RptTransactionDataEntity data) {
        this.data = data;
    }
}