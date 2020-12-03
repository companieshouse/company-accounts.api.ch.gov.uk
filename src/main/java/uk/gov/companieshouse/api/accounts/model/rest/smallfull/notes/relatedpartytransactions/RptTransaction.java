package uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.api.accounts.validation.ValidRptTransactionType;
import uk.gov.companieshouse.charset.CharSet;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RptTransaction extends RestObject {

    private static final int MIN_FIELD_LENGTH = 1;
    private static final int MAX_FIELD_LENGTH = 160;
    private static final int DESCRIPTION_MAX_FIELD_LENGTH = 250;


    @CharSetValid(CharSet.CHARACTER_SET_2)
    @Size(min = MIN_FIELD_LENGTH, max = MAX_FIELD_LENGTH, message = "invalid.input.length")
    @JsonProperty("name_of_related_party")
    private String nameOfRelatedParty;

    @NotNull
    @CharSetValid(CharSet.CHARACTER_SET_2)
    @Size(min = MIN_FIELD_LENGTH, max = MAX_FIELD_LENGTH, message = "invalid.input.length")
    @JsonProperty("relationship")
    private String relationship;

    @NotNull
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @Size(min = MIN_FIELD_LENGTH, max = DESCRIPTION_MAX_FIELD_LENGTH, message = "invalid.input.length")
    @JsonProperty("description_of_transaction")
    private String descriptionOfTransaction;

    @NotNull
    @ValidRptTransactionType
    @CharSetValid(CharSet.CHARACTER_SET_2)
    @JsonProperty("transaction_type")
    private String transactionType;

    @NotNull
    @Valid
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
