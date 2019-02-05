package uk.gov.companieshouse.api.accounts.model.rest.notes.employees;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrentPeriod {

    @JsonProperty("details")
    private String details;
    
    @JsonProperty("average_number_of_employees")
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
        if (!(obj instanceof CurrentPeriod)) {
            return false;
        }
        CurrentPeriod other = (CurrentPeriod) obj;
        return Objects.equals(averageNumberOfEmployees, other.averageNumberOfEmployees)
                && Objects.equals(details, other.details);
    }

    @Override
    public String toString() {
        return "CurrentPeriod [details=" + details + ", averageNumberOfEmployees="
                + averageNumberOfEmployees + "]";
    }
    
}
