package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.financialcommitments;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

public class FinancialCommitmentsDataEntity extends BaseDataEntity {

    @Field("details")
    private String details;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "FinancialCommitmentsDataEntity{" +
                "details='" + details + '\'' +
                '}';
    }
}
