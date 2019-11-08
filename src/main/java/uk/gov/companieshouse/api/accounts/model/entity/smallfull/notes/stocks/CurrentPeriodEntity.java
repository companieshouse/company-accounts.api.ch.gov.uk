package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.stocks;

import org.springframework.data.mongodb.core.mapping.Field;

public class CurrentPeriodEntity {

    @Field("payments_on_account")
    private Long paymentsOnAccount;

    @Field("stocks")
    private Long stocks;

    @Field("total")
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
