package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.accountingpolicies;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;

public class AccountingPoliciesEntity extends NoteEntity {

    @Field("data")
    private AccountingPoliciesDataEntity data;

    public AccountingPoliciesDataEntity getData() {
        return data;
    }

    public void setData(AccountingPoliciesDataEntity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "AccountingPoliciesEntity{" +
                "data=" + data +
                '}';
    }
}
