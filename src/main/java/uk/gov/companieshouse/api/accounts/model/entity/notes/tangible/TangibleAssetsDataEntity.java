package uk.gov.companieshouse.api.accounts.model.entity.notes.tangible;

import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

public class TangibleAssetsDataEntity extends BaseDataEntity {

    @Field("fixtures_and_fittings")
    private TangibleAssetsResourceEntity fixturesAndFittings;

    @Field("land_and_buildings")
    private TangibleAssetsResourceEntity landAndBuildings;

    @Field("motor_vehicles")
    private TangibleAssetsResourceEntity motorVehicles;

    @Field("office_equipment")
    private TangibleAssetsResourceEntity officeEquipment;

    @Field("plant_and_machinery")
    private TangibleAssetsResourceEntity plantAndMachinery;

    @Field("total")
    private TangibleAssetsResourceEntity total;

    @Field("additional_information")
    private String additionalInformation;

    public TangibleAssetsResourceEntity getFixturesAndFittings() {
        return fixturesAndFittings;
    }

    public void setFixturesAndFittings(
            TangibleAssetsResourceEntity fixturesAndFittings) {
        this.fixturesAndFittings = fixturesAndFittings;
    }

    public TangibleAssetsResourceEntity getLandAndBuildings() {
        return landAndBuildings;
    }

    public void setLandAndBuildings(
            TangibleAssetsResourceEntity landAndBuildings) {
        this.landAndBuildings = landAndBuildings;
    }

    public TangibleAssetsResourceEntity getMotorVehicles() {
        return motorVehicles;
    }

    public void setMotorVehicles(
            TangibleAssetsResourceEntity motorVehicles) {
        this.motorVehicles = motorVehicles;
    }

    public TangibleAssetsResourceEntity getOfficeEquipment() {
        return officeEquipment;
    }

    public void setOfficeEquipment(
            TangibleAssetsResourceEntity officeEquipment) {
        this.officeEquipment = officeEquipment;
    }

    public TangibleAssetsResourceEntity getPlantAndMachinery() {
        return plantAndMachinery;
    }

    public void setPlantAndMachinery(
            TangibleAssetsResourceEntity plantAndMachinery) {
        this.plantAndMachinery = plantAndMachinery;
    }

    public TangibleAssetsResourceEntity getTotal() {
        return total;
    }

    public void setTotal(TangibleAssetsResourceEntity total) {
        this.total = total;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    @Override
    public String toString() {
        return "TangibleAssetsDataEntity{" +
                "additionalInformation='" + additionalInformation + '\'' +
                ", fixturesAndFittings=" + fixturesAndFittings +
                ", landAndBuildings=" + landAndBuildings +
                ", motorVehicles=" + motorVehicles +
                ", officeEquipment=" + officeEquipment +
                ", plantAndMachinery=" + plantAndMachinery +
                ", total=" + total +
                '}';
    }
}
