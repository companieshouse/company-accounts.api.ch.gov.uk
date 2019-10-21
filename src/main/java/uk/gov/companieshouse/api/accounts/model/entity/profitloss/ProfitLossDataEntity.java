package uk.gov.companieshouse.api.accounts.model.entity.profitloss;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;


public class ProfitLossDataEntity extends BaseDataEntity {

    @Field("gross_profit_or_loss")
    private GrossProfitOrLossEntity grossProfitOrLoss;

    @Field("operating_profit_or_loss")
    private OperatingProfitOrLossEntity operatingProfitOrLoss;

    @Field("profit_or_loss_before_tax")
    private ProfitOrLossBeforeTaxEntity profitOrLossBeforeTax;

    @Field("profit_or_loss_financial_year")
    private ProfitOrLossForFinancialYearEntity profitOrLossForFinancialYear;

    public GrossProfitOrLossEntity getGrossProfitOrLoss() {
        return grossProfitOrLoss;
    }

    public void setGrossProfitOrLoss(GrossProfitOrLossEntity grossProfitOrLoss) {
        this.grossProfitOrLoss = grossProfitOrLoss;
    }

    public OperatingProfitOrLossEntity getOperatingProfitOrLoss() {
        return operatingProfitOrLoss;
    }

    public void setOperatingProfitOrLoss(OperatingProfitOrLossEntity operatingProfitOrLoss) {
        this.operatingProfitOrLoss = operatingProfitOrLoss;
    }

    public ProfitOrLossBeforeTaxEntity getProfitOrLossBeforeTax() {
        return profitOrLossBeforeTax;
    }

    public void setProfitOrLossBeforeTax(ProfitOrLossBeforeTaxEntity profitOrLossBeforeTax) {
        this.profitOrLossBeforeTax = profitOrLossBeforeTax;
    }

    public ProfitOrLossForFinancialYearEntity getProfitOrLossForFinancialYear() {
        return profitOrLossForFinancialYear;
    }

    public void setProfitOrLossForFinancialYear(ProfitOrLossForFinancialYearEntity profitOrLossForFinancialYear) {
        this.profitOrLossForFinancialYear = profitOrLossForFinancialYear;
    }

    @Override
    public String toString() {
        return "ProfitLossDataEntity{" +
                "grossProfitOrLoss=" + grossProfitOrLoss +
                ", operatingProfitOrLoss=" + operatingProfitOrLoss +
                ", profitOrLossBeforeTax=" + profitOrLossBeforeTax +
                ", profitOrLossForFinancialYear=" + profitOrLossForFinancialYear +
                "}";


    }
}
