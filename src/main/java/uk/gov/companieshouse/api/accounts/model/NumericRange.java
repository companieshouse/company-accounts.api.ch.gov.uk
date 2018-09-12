package uk.gov.companieshouse.api.accounts.model;

/**
 * Used to express a numeric range
 */
public class NumericRange {

    private Integer start;

    private Integer end;

    /**
     * Constructor
     *
     * @param start
     * @param end
     * @throws IllegalArgumentException
     */
    public NumericRange(Integer start, Integer end) {
        if (start == null) {
            throw new IllegalArgumentException("Numeric range start cannot be null");
        }
        if (end == null) {
            throw new IllegalArgumentException("Numeric range end cannot be null");
        }
        if (start.compareTo(end) > -1) {
            throw new IllegalArgumentException("Start must be lower than end");
        }
        this.start = start;
        this.end = end;
    }

    /**
     * Determine whether the given value is in the inclusive range
     *
     * @param value
     * @return True or false
     * @throws IllegalArgumentException
     */
    public boolean inRangeInclusive(Integer value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        return  (value.compareTo(start) > -1) &&
            (value.compareTo(end) < 1);
    }

    public Integer getStart() {
        return start;
    }

    public Integer getEnd() {
        return end;
    }

}
