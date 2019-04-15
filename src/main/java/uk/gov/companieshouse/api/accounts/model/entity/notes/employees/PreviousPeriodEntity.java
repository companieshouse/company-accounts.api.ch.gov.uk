package uk.gov.companieshouse.api.accounts.model.entity.notes.employees;

import java.util.Objects;
import org.springframework.data.mongodb.core.mapping.Field;

public class PreviousPeriodEntity {

    @Field("average_number_of_employees")
    private Long averageNumberOfEmployees;

    public Long getAverageNumberOfEmployees() {
        return averageNumberOfEmployees;
    }

    public void setAverageNumberOfEmployees(Long averageNumberOfEmployees) {
        this.averageNumberOfEmployees = averageNumberOfEmployees;
    }

    @Override
    public int hashCode() {
        return Objects.hash(averageNumberOfEmployees);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof PreviousPeriodEntity)) {
            return false;
        }
        PreviousPeriodEntity other = (PreviousPeriodEntity) obj;
        return Objects.equals(averageNumberOfEmployees, other.averageNumberOfEmployees);
    }
}
