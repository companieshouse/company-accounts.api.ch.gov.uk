package uk.gov.companieshouse.api.accounts.model.validation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Represents a uk.gov.companieshouse.api.accounts.validation error
 */
@JsonInclude(Include.NON_NULL)
public class Error {

    @JsonProperty("error")
    @Field("error")
    private String error;

    @JsonProperty("error_values")
    @Field("error_values")
    private Map<String, String> errorValues;

    @JsonProperty("location")
    @Field("location")
    private String location;

    @JsonProperty("location_type")
    @Field("location_type")
    private String locationType;

    @JsonProperty("type")
    @Field("type")
    private String type;

    /**
     * Constructor
     *
     * @throws IllegalArgumentException on null or empty arguments
     */
    public Error(String error, String location, String locationType, String type) {
        if (error == null || error.isEmpty()) {
            throw new IllegalArgumentException("Error cannot be null or empty");
        }
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }

        this.error = error;
        this.location = location;
        this.locationType = locationType;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Error error1 = (Error) o;
        return Objects.equals(getError(), error1.getError()) &&
            Objects.equals(getErrorValues(), error1.getErrorValues()) &&
            Objects.equals(getLocation(), error1.getLocation()) &&
            Objects.equals(getLocationType(), error1.getLocationType()) &&
            Objects.equals(getType(), error1.getType());
    }

    @Override
    public int hashCode() {
        return Objects
            .hash(getError(), getErrorValues(), getLocation(), getLocationType(), getType());
    }

    /**
     * Add an error value
     *
     * @throws IllegalArgumentException on null or empty arguments
     */
    public void addErrorValue(String argument, String value) {
        if (argument == null || argument.isEmpty()) {
            throw new IllegalArgumentException("Argument cannot be null or empty");
        }
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty");
        }
        if (errorValues == null) {
            errorValues = new HashMap<>();
        }
        errorValues.put(argument, value);
    }

    public String getError() {
        return error;
    }

    public Map<String, String> getErrorValues() {
        if (errorValues != null) {
            return Collections.unmodifiableMap(errorValues);
        }

        return null;
    }

    public String getLocation() {
        return location;
    }

    public String getLocationType() {
        return locationType;
    }

    public String getType() {
        return type;
    }


}
