package uk.gov.companieshouse.api.accounts.model.entity.notes.currentassetsinvestments;

import com.google.gson.Gson;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

import java.util.Objects;

public class CurrentAssetsInvestmentsDataEntity extends BaseDataEntity {

    @Field("details")
    private String details;

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrentAssetsInvestmentsDataEntity)) return false;
        CurrentAssetsInvestmentsDataEntity that = (CurrentAssetsInvestmentsDataEntity) o;
        return Objects.equals(getDetails(), that.getDetails());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDetails());
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
