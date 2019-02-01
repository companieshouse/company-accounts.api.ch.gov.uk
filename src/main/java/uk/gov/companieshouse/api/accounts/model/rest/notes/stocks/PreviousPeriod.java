package uk.gov.companieshouse.api.accounts.model.rest.notes.stocks;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PreviousPeriod {

    @JsonProperty("payments_on_account")
    private Long paymentsOnAccount;

    @JsonProperty("stocks")
    private Long stocks;

    @JsonProperty("total")
    private Long total;

    public Long getPaymentsOnAccount() {
        return paymentsOnAccount;
    }

    public void setPaymentsOnAccount(Long paymentsOnAccount) {
        this.paymentsOnAccount = paymentsOnAccount;
    }

    public Long getStocks() {
        return stocks;
    }

    public void setStocks(Long stocks) {
        this.stocks = stocks;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
