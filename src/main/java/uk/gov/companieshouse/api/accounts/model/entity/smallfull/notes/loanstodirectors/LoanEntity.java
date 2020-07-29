package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

@Document(collection = "notes")
public class LoanEntity extends BaseEntity {

    @Field
    private LoanDataEntity data;

    public LoanDataEntity getData() {
        return data;
    }

    public void setData(LoanDataEntity data) {
        this.data = data;
    }
}
