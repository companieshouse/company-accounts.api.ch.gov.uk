package uk.gov.companieshouse.api.accounts.model.entity.notes.employees;

import java.util.Objects;
import org.springframework.data.mongodb.core.mapping.Field;

public class CurrentPeriodEntity {

    @Field("details")
    private String details;
    
    @Field("averageNumberOfEmployees")
    private Long averageNumberOfEmployees;
    
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Long getAverageNumberOfEmployees() {
        return averageNumberOfEmployees;
    }

    public void setAverageNumberOfEmployees(Long averageNumberOfEmployees) {
        this.averageNumberOfEmployees = averageNumberOfEmployees;
    }

    @Override
    public int hashCode() {
        return Objects.hash(averageNumberOfEmployees, details);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CurrentPeriodEntity)) {
            return false;
        }
        CurrentPeriodEntity other = (CurrentPeriodEntity) obj;
        return Objects.equals(averageNumberOfEmployees, other.averageNumberOfEmployees)
                && Objects.equals(details, other.details);
    }
}
