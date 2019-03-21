package uk.gov.companieshouse.api.accounts.model.entity.notes.fixedassetsinvestments;

import org.springframework.data.mongodb.core.mapping.Document;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

@Document(collection = "notes")
public class FixedAssetsInvestmentsEntity extends BaseEntity {

    private FixedAssetsInvestmentsDataEntity data;

    public FixedAssetsInvestmentsDataEntity getData() {
        return data;
    }

    public void setData(FixedAssetsInvestmentsDataEntity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "FixedAssetsInvestmentsEntity [data=" + data + "]";
    }
}
