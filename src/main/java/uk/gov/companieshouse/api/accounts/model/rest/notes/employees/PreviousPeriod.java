package uk.gov.companieshouse.api.accounts.model.rest.notes.employees;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreviousPeriod {

    @JsonProperty("average_number_of_employees")
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
        if (!(obj instanceof PreviousPeriod)) {
            return false;
        }
        PreviousPeriod other = (PreviousPeriod) obj;
        return Objects.equals(averageNumberOfEmployees, other.averageNumberOfEmployees);
    }

    @Override
    public String toString() {
        return "PreviousPeriod [averageNumberOfEmployees=" + averageNumberOfEmployees + "]";
    }
    
}
