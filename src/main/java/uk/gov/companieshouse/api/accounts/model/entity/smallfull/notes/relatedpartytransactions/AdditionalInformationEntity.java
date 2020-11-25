package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;

@Document(collection = "notes")
public class AdditionalInformationEntity extends BaseEntity {

    @Field
    private AdditionalInformationDataEntity data;

    public AdditionalInformationDataEntity getData() {
        return data;
    }

    public void setData(
            AdditionalInformationDataEntity data) {
        this.data = data;
    }
}
