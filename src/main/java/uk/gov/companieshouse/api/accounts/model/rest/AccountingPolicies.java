package uk.gov.companieshouse.api.accounts.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Size;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.charset.CharSet;

import javax.validation.constraints.NotBlank;

@JsonInclude(Include.NON_NULL)
public class AccountingPolicies extends RestObject {

    private static final int MAX_FIELD_LENGTH = 20000;

    @NotBlank
    @Size(max = MAX_FIELD_LENGTH, message = "max.length.exceeded")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("basis_of_measurement_and_preparation")
    private String basisOfMeasurementAndPreparation;

    @Size(max = MAX_FIELD_LENGTH, message = "max.length.exceeded")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("turnover_policy")
    private String turnoverPolicy;

    @Size(max = MAX_FIELD_LENGTH, message = "max.length.exceeded")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("tangible_fixed_assets_deprecation_policy")
    private String tangibleFixedAssetsDeprecationPolicy;

    @Size(max = MAX_FIELD_LENGTH, message = "max.length.exceeded")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("intangible_fixed_assets_deprecation_policy")
    private String intangibleFixedAssetsDeprecationPolicy;

    @Size(max = MAX_FIELD_LENGTH, message = "max.length.exceeded")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("valuation_information_and_policy")
    private String valuationInformationAndPolicy;

    @Size(max = MAX_FIELD_LENGTH, message = "max.length.exceeded")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("other_accounting_policies")
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
        return "AccountingPolicies{" +
                "basisOfMeasurementAndPreparation='" + basisOfMeasurementAndPreparation + '\'' +
                ", turnoverPolicy='" + turnoverPolicy + '\'' +
                ", tangibleFixedAssetsDeprecationPolicy='" + tangibleFixedAssetsDeprecationPolicy + '\'' +
                ", intangibleFixedAssetsDeprecationPolicy='" + intangibleFixedAssetsDeprecationPolicy + '\'' +
                ", valuationInformationAndPolicy='" + valuationInformationAndPolicy + '\'' +
                ", otherAccountingPolicies='" + otherAccountingPolicies + '\'' +
                '}';
    }
}
