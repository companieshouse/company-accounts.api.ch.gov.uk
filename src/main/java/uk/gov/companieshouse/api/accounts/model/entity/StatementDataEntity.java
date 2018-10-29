package uk.gov.companieshouse.api.accounts.model.entity;

import java.util.HashMap;
import java.util.Map;
import org.springframework.data.mongodb.core.mapping.Field;

public class StatementDataEntity extends BaseDataEntity {

    @Field("legal_statements")
    private Map<String, String> legalStatements = new HashMap<>();

    @Field("has_agreed_to_legal_statements")
    private Boolean hasAgreedToLegalStatements;

    public Map<String, String> getLegalStatements() {
        return legalStatements;
    }

    public void setLegalStatements(Map<String, String> legalStatements) {
        this.legalStatements = legalStatements;
    }

    public Boolean getHasAgreedToLegalStatements() {
        return hasAgreedToLegalStatements;
    }

    public void setHasAgreedToLegalStatements(Boolean hasAgreedToLegalStatements) {
        this.hasAgreedToLegalStatements = hasAgreedToLegalStatements;
    }
}
