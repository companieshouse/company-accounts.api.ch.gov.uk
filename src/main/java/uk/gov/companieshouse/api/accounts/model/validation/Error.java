package uk.gov.companieshouse.api.accounts.validation;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a validation error
 */
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
     * @param error
     * @param location
     * @param locationType
     * @param type
     * @throws IllegalArgumentException on null or empty arguments
     */
    public Error(String error, String location, String locationType, String type) {
        if (error == null || error.isEmpty()) {
            throw new IllegalArgumentException("Error cannot be null or empty");
        }
        if (location == null || location.isEmpty()) {
            throw new IllegalArgumentException("Location cannot be null or empty");
        }
        if (locationType == null || locationType.isEmpty()) {
            throw new IllegalArgumentException("Location type cannot be null or empty");
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Error other = (Error) o;

        if (!error.equals(other.error)) return false;
        if (errorValues != null ? !errorValues.equals(other.errorValues) : other.errorValues != null) return false;
        if (!location.equals(other.location)) return false;
        if (!locationType.equals(other.locationType)) return false;
        return type.equals(other.type);
    }

    @Override
    public int hashCode() {
        int result = error.hashCode();
        result = 31 * result + (errorValues != null ? errorValues.hashCode() : 0);
        result = 31 * result + location.hashCode();
        result = 31 * result + locationType.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    /**
     * Add an error value
     *
     * @param argument
     * @param value
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
        if(errorValues != null) {
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
