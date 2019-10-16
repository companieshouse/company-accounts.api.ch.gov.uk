package uk.gov.companieshouse.api.accounts.model.entity.profitloss;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;


public class ProfitLossDataEntity extends BaseDataEntity {

    @Field("gross_profit_or_loss")
    private GrossProfitOrLoss grossProfitOrLoss;

    @Field("operating_profit_or_loss")
    private OperatingProfitOrLoss operatingProfitOrLoss;

    @Field("profit_or_loss_before_tax")
    private ProfitOrLossBeforeTax profitOrLossBeforeTax;

    @Field("profit_or_loss_financial_year")
    private ProfitOrLossForFinancialYear profitOrLossForFinancialYear;

    public GrossProfitOrLoss getGrossProfitOrLoss() {
        return grossProfitOrLoss;
    }

    public void setGrossProfitOrLoss(GrossProfitOrLoss grossProfitOrLoss) {
        this.grossProfitOrLoss = grossProfitOrLoss;
    }

    public OperatingProfitOrLoss getOperatingProfitOrLoss() {
        return operatingProfitOrLoss;
    }

    public void setOperatingProfitOrLoss(OperatingProfitOrLoss operatingProfitOrLoss) {
        this.operatingProfitOrLoss = operatingProfitOrLoss;
    }

    public ProfitOrLossBeforeTax getProfitOrLossBeforeTax() {
        return profitOrLossBeforeTax;
    }

    public void setProfitOrLossBeforeTax(ProfitOrLossBeforeTax profitOrLossBeforeTax) {
        this.profitOrLossBeforeTax = profitOrLossBeforeTax;
    }

    public ProfitOrLossForFinancialYear getProfitOrLossForFinancialYear() {
        return profitOrLossForFinancialYear;
    }

    public void setProfitOrLossForFinancialYear(ProfitOrLossForFinancialYear profitOrLossForFinancialYear) {
        this.profitOrLossForFinancialYear = profitOrLossForFinancialYear;
    }

    @Override
    public String toString() {
        return "ProfitLossDataEntity{" +
                "grossProfitOrLoss=" + grossProfitOrLoss +
                ", operatingProfitOrLoss=" + operatingProfitOrLoss +
                ", profitOrLossBeforeTax=" + profitOrLossBeforeTax +
                ", profitOrLossForFinancialYear=" + profitOrLossForFinancialYear +
                '\'' + '}';


    }
}
