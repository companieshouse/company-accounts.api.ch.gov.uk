package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Cost {

    @JsonProperty("amount")
    private String amount;

    @JsonProperty("available_payment_methods")
    private String[] availablePaymentMethods;

    @JsonProperty("class_of_payment")
    private String[] classOfPayment;

    @JsonProperty("description")
    private String description;

    @JsonProperty("description_identifier")
    private String descriptionIdentifier;

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("resource_kind")
    private String resourceKind;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String[] getAvailablePaymentMethods() {
        return availablePaymentMethods;
    }

    public void setAvailablePaymentMethods(String[] availablePaymentMethods) {
        this.availablePaymentMethods = availablePaymentMethods;
    }

    public String[] getClassOfPayment() {
        return classOfPayment;
    }

    public void setClassOfPayment(String[] classOfPayment) {
        this.classOfPayment = classOfPayment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionIdentifier() {
        return descriptionIdentifier;
    }

    public void setDescriptionIdentifier(String descriptionIdentifier) {
        this.descriptionIdentifier = descriptionIdentifier;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getResourceKind() {
        return resourceKind;
    }

    public void setResourceKind(String resourceKind) {
        this.resourceKind = resourceKind;
    }
}
