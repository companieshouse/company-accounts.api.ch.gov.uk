package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

public class RptTransactionDataEntity extends BaseDataEntity {

    @Field("name_of_related_party")
    private String nameOfRelatedParty;

    @Field("relationship")
    private String relationship;

    @Field("description_of_transaction")
    private String descriptionOfTransaction;

    @Field("transaction_type")
    private String transactionType;

    @Field("breakdown")
    private RptTransactionBreakdownEntity breakdown;

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

    public RptTransactionBreakdownEntity getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(RptTransactionBreakdownEntity breakdown) {
        this.breakdown = breakdown;
    }

    @Override
    public String toString() {
        return "RptTransactionDataEntity{" +
                "nameOfRelatedParty='" + nameOfRelatedParty + '\'' +
                ", relationship='" + relationship + '\'' + 
                ", description='" + descriptionOfTransaction + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", breakdown=" + breakdown +
                "}";
    }
}
