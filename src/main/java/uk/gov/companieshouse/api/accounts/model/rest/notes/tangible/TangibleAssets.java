package uk.gov.companieshouse.api.accounts.model.rest.notes.tangible;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TangibleAssets extends RestObject {

    @JsonProperty("fixtures_and_fittings")
    private TangibleAssetsResource fixturesAndFittings;

    @JsonProperty("land_and_buildings")
    private TangibleAssetsResource landAndBuildings;

    @JsonProperty("motor_vehicles")
    private TangibleAssetsResource motorVehicles;

    @JsonProperty("office_equipment")
    private TangibleAssetsResource officeEquipment;

    @JsonProperty("plant_and_machinery")
    private TangibleAssetsResource plantAndMachinery;

    @JsonProperty("total")
    private TangibleAssetsResource total;

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
