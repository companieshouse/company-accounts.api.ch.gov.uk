package uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.accountingpolicies;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.Note;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.api.accounts.validation.ValidRegulatoryStandards;
import uk.gov.companieshouse.charset.CharSet;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@JsonInclude(Include.NON_NULL)
public class AccountingPolicies extends Note {

    @NotBlank
    @Size(max = MAX_FIELD_LENGTH, message = "max.length.exceeded")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("basis_of_measurement_and_preparation")
    @ValidRegulatoryStandards
    private String basisOfMeasurementAndPreparation;

    @Size(min = MIN_FIELD_LENGTH, max = MAX_FIELD_LENGTH, message="invalid.input.length")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("turnover_policy")
    private String turnoverPolicy;

    @Size(min = MIN_FIELD_LENGTH, max = MAX_FIELD_LENGTH, message="invalid.input.length")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("tangible_fixed_assets_depreciation_policy")
    private String tangibleFixedAssetsDepreciationPolicy;

    @Size(min = MIN_FIELD_LENGTH, max = MAX_FIELD_LENGTH, message="invalid.input.length")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("intangible_fixed_assets_amortisation_policy")
    private String intangibleFixedAssetsAmortisationPolicy;

    @Size(min = MIN_FIELD_LENGTH, max = MAX_FIELD_LENGTH, message="invalid.input.length")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("valuation_information_and_policy")
    private String valuationInformationAndPolicy;

    @Size(min = MIN_FIELD_LENGTH, max = MAX_FIELD_LENGTH, message="invalid.input.length")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("other_accounting_policy")
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
        return "AccountingPolicies{" +
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
