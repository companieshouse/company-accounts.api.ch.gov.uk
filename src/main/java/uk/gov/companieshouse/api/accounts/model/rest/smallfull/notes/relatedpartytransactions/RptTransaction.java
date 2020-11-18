package uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RptTransaction extends RestObject {

    @JsonProperty("name_of_related_party")
    private String nameOfRelatedParty;

    @NotNull
    @JsonProperty("relationship")
    private String relationship;

    @NotNull
    @JsonProperty("description_of_transaction")
    private String descriptionOfTransaction;

    @NotNull
    @JsonProperty("transaction_type")
    private String transactionType;

    @NotNull
    @JsonProperty("breakdown")
    private RptTransactionBreakdown breakdown;

    public String getNameOfRelatedParty() {
        return nameOfRelatedParty;
    }

    public void setNameOfRelatedParty(String nameOfRelatedParty) {
        this.nameOfRelatedParty = nameOfRelatedParty;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getDescriptionOfTransaction() {
        return descriptionOfTransaction;
    }

    public void setDescriptionOfTransaction(String descriptionOfTransaction) {
        this.descriptionOfTransaction = descriptionOfTransaction;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public RptTransactionBreakdown getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(RptTransactionBreakdown breakdown) {
        this.breakdown = breakdown;
    }
}
