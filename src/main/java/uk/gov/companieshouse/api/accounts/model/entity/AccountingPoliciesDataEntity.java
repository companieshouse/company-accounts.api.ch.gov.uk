package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;

public class AccountingPoliciesDataEntity extends BaseDataEntity {

    @NotNull
    @Field("basis_of_measurement_and_preparation")
    private String basisOfMeasurementAndPreparation;

    @Field("turnover_policy")
    private String turnoverPolicy;

    @Field("tangible_fixed_assets_deprecation_policy")
    private String tangibleFixedAssetsDeprecationPolicy;

    @Field("intangible_fixed_assets_deprecation_policy")
    private String intangibleFixedAssetsDeprecationPolicy;

    @Field("valuation_information_and_policy")
    private String valuationInformationAndPolicy;

    @Field("other_accounting_policies")
    private String otherAccountingPolicies;

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

    public String getTangibleFixedAssetsDeprecationPolicy() {
        return tangibleFixedAssetsDeprecationPolicy;
    }

    public void setTangibleFixedAssetsDeprecationPolicy(String tangibleFixedAssetsDeprecationPolicy) {
        this.tangibleFixedAssetsDeprecationPolicy = tangibleFixedAssetsDeprecationPolicy;
    }

    public String getIntangibleFixedAssetsDeprecationPolicy() {
        return intangibleFixedAssetsDeprecationPolicy;
    }

    public void setIntangibleFixedAssetsDeprecationPolicy(String intangibleFixedAssetsDeprecationPolicy) {
        this.intangibleFixedAssetsDeprecationPolicy = intangibleFixedAssetsDeprecationPolicy;
    }

    public String getValuationInformationAndPolicy() {
        return valuationInformationAndPolicy;
    }

    public void setValuationInformationAndPolicy(String valuationInformationAndPolicy) {
        this.valuationInformationAndPolicy = valuationInformationAndPolicy;
    }

    public String getOtherAccountingPolicies() {
        return otherAccountingPolicies;
    }

    public void setOtherAccountingPolicies(String otherAccountingPolicies) {
        this.otherAccountingPolicies = otherAccountingPolicies;
    }

    @Override
    public String toString() {
        return "AccountingPoliciesDataEntity{" +
                "basisOfMeasurementAndPreparation='" + basisOfMeasurementAndPreparation + '\'' +
                ", turnoverPolicy='" + turnoverPolicy + '\'' +
                ", tangibleFixedAssetsDeprecationPolicy='" + tangibleFixedAssetsDeprecationPolicy + '\'' +
                ", intangibleFixedAssetsDeprecationPolicy='" + intangibleFixedAssetsDeprecationPolicy + '\'' +
                ", valuationInformationAndPolicy='" + valuationInformationAndPolicy + '\'' +
                ", otherAccountingPolicies='" + otherAccountingPolicies + '\'' +
                '}';
    }
}
