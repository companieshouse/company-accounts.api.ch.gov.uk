package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

@Document(collection = "notes")
public class LoansToDirectorsEntity extends BaseEntity {

    @Field("data")
    private LoansToDirectorsDataEntity data;

    public LoansToDirectorsDataEntity getData() {
        return data;
    }

    public void setData(LoansToDirectorsDataEntity data) {
        this.data = data;
    }

}