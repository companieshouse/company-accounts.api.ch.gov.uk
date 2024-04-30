package uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.tangibleassets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import uk.gov.companieshouse.api.accounts.model.rest.Note;
import uk.gov.companieshouse.api.accounts.validation.CharSetValid;
import uk.gov.companieshouse.charset.CharSet;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TangibleAssets extends Note {

    @Valid
    @JsonProperty("fixtures_and_fittings")
    private TangibleAssetsResource fixturesAndFittings;

    @Valid
    @JsonProperty("land_and_buildings")
    private TangibleAssetsResource landAndBuildings;

    @Valid
    @JsonProperty("motor_vehicles")
    private TangibleAssetsResource motorVehicles;

    @Valid
    @JsonProperty("office_equipment")
    private TangibleAssetsResource officeEquipment;

    @Valid
    @JsonProperty("plant_and_machinery")
    private TangibleAssetsResource plantAndMachinery;

    @Valid
    @JsonProperty("total")
    private TangibleAssetsResource total;

    @Size(min = MIN_FIELD_LENGTH, max = MAX_FIELD_LENGTH, message="invalid.input.length")
    @CharSetValid(CharSet.CHARACTER_SET_3)
    @JsonProperty("additional_information")
    private String additionalInformation;

    public TangibleAssetsResource getFixturesAndFittings() {
        return fixturesAndFittings;
    }

    public void setFixturesAndFittings(TangibleAssetsResource fixturesAndFittings) {
        this.fixturesAndFittings = fixturesAndFittings;
    }

    public TangibleAssetsResource getLandAndBuildings() {
        return landAndBuildings;
    }

    public void setLandAndBuildings(TangibleAssetsResource landAndBuildings) {
        this.landAndBuildings = landAndBuildings;
    }

    public TangibleAssetsResource getMotorVehicles() {
        return motorVehicles;
    }

    public void setMotorVehicles(TangibleAssetsResource motorVehicles) {
        this.motorVehicles = motorVehicles;
    }

    public TangibleAssetsResource getOfficeEquipment() {
        return officeEquipment;
    }

    public void setOfficeEquipment(TangibleAssetsResource officeEquipment) {
        this.officeEquipment = officeEquipment;
    }

    public TangibleAssetsResource getPlantAndMachinery() {
        return plantAndMachinery;
    }

    public void setPlantAndMachinery(TangibleAssetsResource plantAndMachinery) {
        this.plantAndMachinery = plantAndMachinery;
    }

    public TangibleAssetsResource getTotal() {
        return total;
    }

    public void setTotal(TangibleAssetsResource total) {
        this.total = total;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }
}
