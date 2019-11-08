package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.stocks;

import org.springframework.data.mongodb.core.mapping.Document;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

@Document(collection = "notes")
public class StocksEntity extends BaseEntity {

    private StocksDataEntity data;

    public StocksDataEntity getData() {
        return data;
    }

    public void setData(StocksDataEntity data) {
        this.data = data;
    }
}
