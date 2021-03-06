package uk.gov.companieshouse.api.accounts.model.validation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Used to encapsulate validation errors
 */
public class Errors {

    @JsonProperty("errors")
    @Field("errors")
    private Set<Error> errorsSet = new HashSet<>();

    /**
     * Add the given {@link Error}
     *
     * @return True if added, false otherwise
     * @throws IllegalArgumentException on null errors
     */
    public boolean addError(final Error error) {
        if (error == null) {
            throw new IllegalArgumentException("Error cannot be null");
        }

        return errorsSet.add(error);
    }

    /**
     * Determine whether there are any {@link Error}s
     *
     * @return True or false
     */
    public boolean hasErrors() {
        return !errorsSet.isEmpty();
    }

    /**
     * Get errors set
     *
     * @return Set of errors
     */
    public Set<Error> getErrors() {
        return this.errorsSet;
    }

    /**
     * Determine whether the given {@link Error} is contained
     *
     * @return True or false
     */
    public boolean containsError(Error error) {
        if (error == null) {
            throw new IllegalArgumentException("Error cannot be null");
        }

        return errorsSet.contains(error);
    }

    /**
     * Get the number of {@link Error}s contained
     *
     * @return An int
     */
    @JsonIgnore
    public int getErrorCount() {
        return errorsSet.size();
    }

}
