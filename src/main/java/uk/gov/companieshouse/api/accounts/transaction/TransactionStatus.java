package uk.gov.companieshouse.api.accounts.transaction;

public enum TransactionStatus {
    OPEN("open"), DELETED("deleted"), CLOSED("closed");

    private String status;

    private TransactionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

}