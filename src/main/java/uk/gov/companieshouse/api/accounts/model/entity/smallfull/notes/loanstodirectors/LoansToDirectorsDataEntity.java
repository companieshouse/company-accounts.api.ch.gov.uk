package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors;

import java.util.Map;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

public class LoansToDirectorsDataEntity extends BaseDataEntity {

    @Field("loans")
    private Map<String, String> loans;

    public Map<String, String> getLoans() {
        return loans;
    }

    public void setLoans(Map<String, String> loans) {
        this.loans = loans;
    }
}