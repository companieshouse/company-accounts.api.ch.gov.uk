package uk.gov.companieshouse.api.accounts.model.rest.notes.employees;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.charset.CharSet;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrentPeriod {

    private static final int MAX_FIELD_LENGTH = 20000;
    private static final int MIN_FIELD_LENGTH = 1;
    private static final int MAX_RANGE = 99999;
    private static final int MIN_RANGE = 0;

    @Size(min = MIN_FIELD_LENGTH, max = MAX_FIELD_LENGTH, message = "invalid.input.length")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("details")
    private String details;

    @Range(min=MIN_RANGE,max=MAX_RANGE, message = "value.outside.range")
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
