package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.accountingpolicies;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

import jakarta.validation.constraints.NotNull;

public class AccountingPoliciesDataEntity extends BaseDataEntity {

    @NotNull
    @Field("basis_of_measurement_and_preparation")
    private String basisOfMeasurementAndPreparation;

    @Field("turnover_policy")
    private String turnoverPolicy;

    @Field("tangible_fixed_assets_depreciation_policy")
    private String tangibleFixedAssetsDepreciationPolicy;

    @Field("intangible_fixed_assets_amortisation_policy")
    private String intangibleFixedAssetsAmortisationPolicy;

    @Field("valuation_information_and_policy")
    private String valuationInformationAndPolicy;

    @Field("other_accounting_policy")
    private String otherAccountingPolicy;

    public String getBasisOfMeasurementAndPreparation() {
        return basisOfMeasurementAndPreparation;
    }

    public void setBasisOfMeasurementAndPreparation(String basisOfMeasurementAndPreparation) {
        this.basisOfMeasurementAndPreparation = basisOfMeasurementAndPreparation;
    }

    public String getTurnoverPolicy() {
        return turnoverPolicy;
    }

    public void setTurnoverPolicy(String turnoverPolicy) {
        this.turnoverPolicy = turnoverPolicy;
    }

    public String getTangibleFixedAssetsDepreciationPolicy() {
        return tangibleFixedAssetsDepreciationPolicy;
    }

    public void setTangibleFixedAssetsDepreciationPolicy(String tangibleFixedAssetsDepreciationPolicy) {
        this.tangibleFixedAssetsDepreciationPolicy = tangibleFixedAssetsDepreciationPolicy;
    }

    public String getIntangibleFixedAssetsAmortisationPolicy() {
        return intangibleFixedAssetsAmortisationPolicy;
    }

    public void setIntangibleFixedAssetsAmortisationPolicy(String intangibleFixedAssetsAmortisationPolicy) {
        this.intangibleFixedAssetsAmortisationPolicy = intangibleFixedAssetsAmortisationPolicy;
    }

    public String getValuationInformationAndPolicy() {
        return valuationInformationAndPolicy;
    }

    public void setValuationInformationAndPolicy(String valuationInformationAndPolicy) {
        this.valuationInformationAndPolicy = valuationInformationAndPolicy;
    }

    public String getOtherAccountingPolicy() {
        return otherAccountingPolicy;
    }

    public void setOtherAccountingPolicy(String otherAccountingPolicy) {
        this.otherAccountingPolicy = otherAccountingPolicy;
    }

    @Override
    public String toString() {
        return "AccountingPoliciesDataEntity{" +
                "basisOfMeasurementAndPreparation='" + basisOfMeasurementAndPreparation + '\'' +
                ", turnoverPolicy='" + turnoverPolicy + '\'' +
                ", tangibleFixedAssetsDepreciationPolicy='" + tangibleFixedAssetsDepreciationPolicy + '\'' +
                ", intangibleFixedAssetsAmortisationPolicy='" + intangibleFixedAssetsAmortisationPolicy
                + '\'' +
                ", valuationInformationAndPolicy='" + valuationInformationAndPolicy + '\'' +
                ", otherAccountingPolicy='" + otherAccountingPolicy + '\'' +
                '}';
    }
}
