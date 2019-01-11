package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.CostEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.DepreciationEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.TangibleAssetsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.TangibleAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.TangibleAssetsResourceEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.Cost;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.Depreciation;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.TangibleAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.tangible.TangibleAssetsResource;

@Component
public class TangibleAssetsTransformer implements GenericTransformer<TangibleAssets, TangibleAssetsEntity> {

    @Override
    public TangibleAssetsEntity transform(TangibleAssets entity) {

        TangibleAssetsEntity tangibleAssetsEntity = new TangibleAssetsEntity();
        TangibleAssetsDataEntity tangibleAssetsDataEntity = new TangibleAssetsDataEntity();

        BeanUtils.copyProperties(entity, tangibleAssetsDataEntity);

        if (entity.getFixturesAndFittings() != null) {

            TangibleAssetsResourceEntity fixturesAndFittings = new TangibleAssetsResourceEntity();
            BeanUtils.copyProperties(entity.getFixturesAndFittings(), fixturesAndFittings);

            if (entity.getFixturesAndFittings().getCost() != null) {

                CostEntity cost = new CostEntity();
                BeanUtils.copyProperties(entity.getFixturesAndFittings().getCost(), cost);
                fixturesAndFittings.setCost(cost);
            }

            if (entity.getFixturesAndFittings().getDepreciation() != null) {

                DepreciationEntity depreciation = new DepreciationEntity();
                BeanUtils.copyProperties(entity.getFixturesAndFittings().getDepreciation(), depreciation);
                fixturesAndFittings.setDepreciation(depreciation);
            }

            tangibleAssetsDataEntity.setFixturesAndFittings(fixturesAndFittings);
        }

        if (entity.getLandAndBuildings() != null) {

            TangibleAssetsResourceEntity landAndBuildings = new TangibleAssetsResourceEntity();
            BeanUtils.copyProperties(entity.getLandAndBuildings(), landAndBuildings);

            if (entity.getLandAndBuildings().getCost() != null) {

                CostEntity cost = new CostEntity();
                BeanUtils.copyProperties(entity.getLandAndBuildings().getCost(), cost);
                landAndBuildings.setCost(cost);
            }

            if (entity.getLandAndBuildings().getDepreciation() != null) {

                DepreciationEntity depreciation = new DepreciationEntity();
                BeanUtils.copyProperties(entity.getLandAndBuildings().getDepreciation(), depreciation);
                landAndBuildings.setDepreciation(depreciation);
            }

            tangibleAssetsDataEntity.setLandAndBuildings(landAndBuildings);
        }

        if (entity.getMotorVehicles() != null) {

            TangibleAssetsResourceEntity motorVehicles = new TangibleAssetsResourceEntity();
            BeanUtils.copyProperties(entity.getMotorVehicles(), motorVehicles);

            if (entity.getMotorVehicles().getCost() != null) {

                CostEntity cost = new CostEntity();
                BeanUtils.copyProperties(entity.getMotorVehicles().getCost(), cost);
                motorVehicles.setCost(cost);
            }

            if (entity.getMotorVehicles().getDepreciation() != null) {

                DepreciationEntity depreciation = new DepreciationEntity();
                BeanUtils.copyProperties(entity.getMotorVehicles().getDepreciation(), depreciation);
                motorVehicles.setDepreciation(depreciation);
            }

            tangibleAssetsDataEntity.setMotorVehicles(motorVehicles);
        }

        if (entity.getOfficeEquipment() != null) {

            TangibleAssetsResourceEntity officeEquipment = new TangibleAssetsResourceEntity();
            BeanUtils.copyProperties(entity.getOfficeEquipment(), officeEquipment);

            if (entity.getOfficeEquipment().getCost() != null) {

                CostEntity cost = new CostEntity();
                BeanUtils.copyProperties(entity.getOfficeEquipment().getCost(), cost);
                officeEquipment.setCost(cost);
            }

            if (entity.getOfficeEquipment().getDepreciation() != null) {

                DepreciationEntity depreciation = new DepreciationEntity();
                BeanUtils.copyProperties(entity.getOfficeEquipment().getDepreciation(), depreciation);
                officeEquipment.setDepreciation(depreciation);
            }

            tangibleAssetsDataEntity.setOfficeEquipment(officeEquipment);
        }

        if (entity.getPlantAndMachinery() != null) {

            TangibleAssetsResourceEntity plantAndMachinery = new TangibleAssetsResourceEntity();
            BeanUtils.copyProperties(entity.getPlantAndMachinery(), plantAndMachinery);

            if (entity.getPlantAndMachinery().getCost() != null) {

                CostEntity cost = new CostEntity();
                BeanUtils.copyProperties(entity.getPlantAndMachinery().getCost(), cost);
                plantAndMachinery.setCost(cost);
            }

            if (entity.getPlantAndMachinery().getDepreciation() != null) {

                DepreciationEntity depreciation = new DepreciationEntity();
                BeanUtils.copyProperties(entity.getPlantAndMachinery().getDepreciation(), depreciation);
                plantAndMachinery.setDepreciation(depreciation);
            }

            tangibleAssetsDataEntity.setPlantAndMachinery(plantAndMachinery);
        }

        if (entity.getTotal() != null) {

            TangibleAssetsResourceEntity total = new TangibleAssetsResourceEntity();
            BeanUtils.copyProperties(entity.getTotal(), total);

            if (entity.getTotal().getCost() != null) {

                CostEntity cost = new CostEntity();
                BeanUtils.copyProperties(entity.getTotal().getCost(), cost);
                total.setCost(cost);
            }

            if (entity.getTotal().getDepreciation() != null) {

                DepreciationEntity depreciation = new DepreciationEntity();
                BeanUtils.copyProperties(entity.getTotal().getDepreciation(), depreciation);
                total.setDepreciation(depreciation);
            }

            tangibleAssetsDataEntity.setTotal(total);
        }

        tangibleAssetsEntity.setData(tangibleAssetsDataEntity);
        return tangibleAssetsEntity;
    }

    @Override
    public TangibleAssets transform(TangibleAssetsEntity entity) {

        TangibleAssets tangibleAssets = new TangibleAssets();
        TangibleAssetsDataEntity dataEntity = entity.getData();

        BeanUtils.copyProperties(dataEntity, tangibleAssets);

        if (dataEntity.getFixturesAndFittings() != null) {

            TangibleAssetsResource fixturesAndFittings = new TangibleAssetsResource();
            BeanUtils.copyProperties(dataEntity.getFixturesAndFittings(), fixturesAndFittings);

            if (dataEntity.getFixturesAndFittings().getCost() != null) {

                Cost cost = new Cost();
                BeanUtils.copyProperties(dataEntity.getFixturesAndFittings().getCost(), cost);
                fixturesAndFittings.setCost(cost);
            }

            if (dataEntity.getFixturesAndFittings().getDepreciation() != null) {

                Depreciation depreciation = new Depreciation();
                BeanUtils.copyProperties(dataEntity.getFixturesAndFittings().getDepreciation(), depreciation);
                fixturesAndFittings.setDepreciation(depreciation);
            }

            tangibleAssets.setFixturesAndFittings(fixturesAndFittings);
        }

        if (dataEntity.getLandAndBuildings() != null) {

            TangibleAssetsResource landAndBuildings = new TangibleAssetsResource();
            BeanUtils.copyProperties(dataEntity.getLandAndBuildings(), landAndBuildings);

            if (dataEntity.getLandAndBuildings().getCost() != null) {

                Cost cost = new Cost();
                BeanUtils.copyProperties(dataEntity.getLandAndBuildings().getCost(), cost);
                landAndBuildings.setCost(cost);
            }

            if (dataEntity.getLandAndBuildings().getDepreciation() != null) {

                Depreciation depreciation = new Depreciation();
                BeanUtils.copyProperties(dataEntity.getLandAndBuildings().getDepreciation(), depreciation);
                landAndBuildings.setDepreciation(depreciation);
            }

            tangibleAssets.setLandAndBuildings(landAndBuildings);
        }

        if (dataEntity.getMotorVehicles() != null) {

            TangibleAssetsResource motorVehicles = new TangibleAssetsResource();
            BeanUtils.copyProperties(dataEntity.getMotorVehicles(), motorVehicles);

            if (dataEntity.getMotorVehicles().getCost() != null) {

                Cost cost = new Cost();
                BeanUtils.copyProperties(dataEntity.getMotorVehicles().getCost(), cost);
                motorVehicles.setCost(cost);
            }

            if (dataEntity.getMotorVehicles().getDepreciation() != null) {

                Depreciation depreciation = new Depreciation();
                BeanUtils.copyProperties(dataEntity.getMotorVehicles().getDepreciation(), depreciation);
                motorVehicles.setDepreciation(depreciation);
            }

            tangibleAssets.setMotorVehicles(motorVehicles);
        }

        if (dataEntity.getOfficeEquipment() != null) {

            TangibleAssetsResource officeEquipment = new TangibleAssetsResource();
            BeanUtils.copyProperties(dataEntity.getOfficeEquipment(), officeEquipment);

            if (dataEntity.getOfficeEquipment().getCost() != null) {

                Cost cost = new Cost();
                BeanUtils.copyProperties(dataEntity.getOfficeEquipment().getCost(), cost);
                officeEquipment.setCost(cost);
            }

            if (dataEntity.getOfficeEquipment().getDepreciation() != null) {

                Depreciation depreciation = new Depreciation();
                BeanUtils.copyProperties(dataEntity.getOfficeEquipment().getDepreciation(), depreciation);
                officeEquipment.setDepreciation(depreciation);
            }

            tangibleAssets.setOfficeEquipment(officeEquipment);
        }

        if (dataEntity.getPlantAndMachinery() != null) {

            TangibleAssetsResource plantAndMachinery = new TangibleAssetsResource();
            BeanUtils.copyProperties(dataEntity.getPlantAndMachinery(), plantAndMachinery);

            if (dataEntity.getPlantAndMachinery().getCost() != null) {

                Cost cost = new Cost();
                BeanUtils.copyProperties(dataEntity.getPlantAndMachinery().getCost(), cost);
                plantAndMachinery.setCost(cost);
            }

            if (dataEntity.getPlantAndMachinery().getDepreciation() != null) {

                Depreciation depreciation = new Depreciation();
                BeanUtils.copyProperties(dataEntity.getPlantAndMachinery().getDepreciation(), depreciation);
                plantAndMachinery.setDepreciation(depreciation);
            }

            tangibleAssets.setPlantAndMachinery(plantAndMachinery);
        }

        if (dataEntity.getTotal() != null) {

            TangibleAssetsResource total = new TangibleAssetsResource();
            BeanUtils.copyProperties(dataEntity.getTotal(), total);

            if (dataEntity.getTotal().getCost() != null) {

                Cost cost = new Cost();
                BeanUtils.copyProperties(dataEntity.getTotal().getCost(), cost);
                total.setCost(cost);
            }

            if (dataEntity.getTotal().getDepreciation() != null) {

                Depreciation depreciation = new Depreciation();
                BeanUtils.copyProperties(dataEntity.getTotal().getDepreciation(), depreciation);
                total.setDepreciation(depreciation);
            }

            tangibleAssets.setTotal(total);
        }

        return tangibleAssets;
    }
}