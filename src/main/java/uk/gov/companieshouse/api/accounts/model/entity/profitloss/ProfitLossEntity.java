package uk.gov.companieshouse.api.accounts.model.entity.profitloss;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

@Document(collection = "profitLoss")
public class ProfitLossEntity extends BaseEntity {

    @Field("data")
    private ProfitLossDataEntity data;

    public ProfitLossDataEntity getData() {
        return data;
    }

    public void setData(ProfitLossDataEntity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ProfitLossEntity{" +
                "data=" + data +
                '}';
    }
}
