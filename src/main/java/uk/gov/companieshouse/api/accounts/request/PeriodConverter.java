package uk.gov.companieshouse.api.accounts.request;

import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.Period;

@Component
public class PeriodConverter extends PropertyEditorSupport {

    private static final Map<String, Period> PERIOD_MAP = new HashMap<>();

    PeriodConverter() {

        Arrays.stream(Period.values()).forEach(period ->
            PERIOD_MAP.put(period.getAccountingPeriod(), period)
        );
    }

    @Override
    public void setAsText(final String type) {

        Period period = PERIOD_MAP.get(type);
        if (period == null) {
            throw new RuntimeException();
        }
        setValue(period);
    }
}