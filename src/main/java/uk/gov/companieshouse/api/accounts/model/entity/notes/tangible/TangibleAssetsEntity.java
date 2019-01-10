package uk.gov.companieshouse.api.accounts.model.entity.notes.tangible;

import org.springframework.data.mongodb.core.mapping.Document;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

@Document(collection = "notes")
public class TangibleAssetsEntity extends BaseEntity {

    private TangibleAssetsDataEntity data;

    public TangibleAssetsDataEntity getData() {
        return data;
    }

    public void setData(TangibleAssetsDataEntity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "TangibleAssetsEntity{" +
                "data=" + data +
                '}';
    }
}
