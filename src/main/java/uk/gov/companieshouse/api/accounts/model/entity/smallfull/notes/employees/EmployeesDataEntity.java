package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.employees;

import java.util.Objects;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

public class EmployeesDataEntity extends BaseDataEntity {

    @Field("current_period")
    private CurrentPeriodEntity currentPeriodEntity;

    @Field("previous_period")
    private PreviousPeriodEntity previousPeriodEntity;

    public CurrentPeriodEntity getCurrentPeriodEntity() {
        return currentPeriodEntity;
    }

    public void setCurrentPeriodEntity(CurrentPeriodEntity currentPeriodEntity) {
        this.currentPeriodEntity = currentPeriodEntity;
    }

    public PreviousPeriodEntity getPreviousPeriodEntity() {
        return previousPeriodEntity;
    }

    public void setPreviousPeriodEntity(PreviousPeriodEntity previousPeriodEntity) {
        this.previousPeriodEntity = previousPeriodEntity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPeriodEntity, previousPeriodEntity);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EmployeesDataEntity)) {
            return false;
        }
        EmployeesDataEntity other = (EmployeesDataEntity) obj;
        return Objects.equals(currentPeriodEntity, other.currentPeriodEntity)
                && Objects.equals(previousPeriodEntity, other.previousPeriodEntity);
    }

    @Override
    public String toString() {
        return "EmployeesDataEntity [currentPeriodEntity=" + currentPeriodEntity
                + ", previousPeriodEntity=" + previousPeriodEntity + "]";
    }
    
}
