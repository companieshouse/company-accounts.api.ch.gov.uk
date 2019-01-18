package uk.gov.companieshouse.api.accounts.service.impl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:LegalStatements.properties")
@ConfigurationProperties(prefix = "balance.sheet")
public class StatementsServiceProperties {

    private Map<String, String> statements;

    public Map<String, String> getCloneOfStatements() {
        return new HashMap<>(statements);
    }

    public void setStatements(Map<String, String> statements) {
        this.statements = statements;
    }
}
