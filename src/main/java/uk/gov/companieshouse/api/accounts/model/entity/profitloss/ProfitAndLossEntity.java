package uk.gov.companieshouse.api.accounts.model.entity.profitloss;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

@Document(collection = "profit_and_loss")
public class ProfitAndLossEntity extends BaseEntity {

    @Field("data")
    private ProfitAndLossDataEntity data;

    public ProfitAndLossDataEntity getData() {
        return data;
    }

    public void setData(ProfitAndLossDataEntity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ProfitLossEntity{" +
                "data=" + data +
                "}";
    }
}
