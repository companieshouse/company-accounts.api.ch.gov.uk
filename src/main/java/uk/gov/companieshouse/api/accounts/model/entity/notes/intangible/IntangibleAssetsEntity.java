package uk.gov.companieshouse.api.accounts.model.entity.notes.intangible;

import org.springframework.data.mongodb.core.mapping.Document;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

@Document(collection = "notes")
public class IntangibleAssetsEntity extends BaseEntity {

    private IntangibleAssetsDataEntity data;

    public IntangibleAssetsDataEntity getData() {
        return data;
    }

    public void setData(IntangibleAssetsDataEntity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "IntangibleAssetEntity{" +
                "data=" + data +
                '}';
    }
}
