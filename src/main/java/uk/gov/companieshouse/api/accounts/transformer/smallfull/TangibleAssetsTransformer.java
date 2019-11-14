package uk.gov.companieshouse.api.accounts.transformer.smallfull;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountsResource;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.CostEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.DepreciationEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.TangibleAssetsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.TangibleAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.tangible.TangibleAssetsResourceEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.tangibleassets.Cost;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.tangibleassets.Depreciation;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.tangibleassets.TangibleAssets;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.tangibleassets.TangibleAssetsResource;
import uk.gov.companieshouse.api.accounts.transformer.AccountsResourceTransformer;

@Component
public class TangibleAssetsTransformer implements AccountsResourceTransformer<TangibleAssets, TangibleAssetsEntity> {

    @Override
    public TangibleAssetsEntity transform(TangibleAssets entity) {

        TangibleAssetsEntity tangibleAssetsEntity = new TangibleAssetsEntity();
        TangibleAssetsDataEntity tangibleAssetsDataEntity = new TangibleAssetsDataEntity();

        BeanUtils.copyProperties(entity, tangibleAssetsDataEntity);

        if (entity.getFixturesAndFittings() != null) {

            tangibleAssetsDataEntity.setFixturesAndFittings(
                    mapRestResourceToEntityResource(entity.getFixturesAndFittings()));
        }

        if (entity.getLandAndBuildings() != null) {

            tangibleAssetsDataEntity.setLandAndBuildings(
                    mapRestResourceToEntityResource(entity.getLandAndBuildings()));
        }

        if (entity.getMotorVehicles() != null) {

            tangibleAssetsDataEntity.setMotorVehicles(
                    mapRestResourceToEntityResource(entity.getMotorVehicles()));
        }

        if (entity.getOfficeEquipment() != null) {

            tangibleAssetsDataEntity.setOfficeEquipment(
                    mapRestResourceToEntityResource(entity.getOfficeEquipment()));
        }

        if (entity.getPlantAndMachinery() != null) {

            tangibleAssetsDataEntity.setPlantAndMachinery(
                    mapRestResourceToEntityResource(entity.getPlantAndMachinery()));
        }

        if (entity.getTotal() != null) {

            tangibleAssetsDataEntity.setTotal(
                    mapRestResourceToEntityResource(entity.getTotal()));
        }

        tangibleAssetsEntity.setData(tangibleAssetsDataEntity);
        return tangibleAssetsEntity;
    }

    private TangibleAssetsResourceEntity mapRestResourceToEntityResource(TangibleAssetsResource restResource) {

        TangibleAssetsResourceEntity entityResource = new TangibleAssetsResourceEntity();
        BeanUtils.copyProperties(restResource, entityResource);

        if (restResource.getCost() != null) {

            CostEntity cost = new CostEntity();
            BeanUtils.copyProperties(restResource.getCost(), cost);
            entityResource.setCost(cost);
        }

        if (restResource.getDepreciation() != null) {

            DepreciationEntity depreciation = new DepreciationEntity();
            BeanUtils.copyProperties(restResource.getDepreciation(), depreciation);
            entityResource.setDepreciation(depreciation);
        }

        return entityResource;
    }

    @Override
    public TangibleAssets transform(TangibleAssetsEntity entity) {

        TangibleAssets tangibleAssets = new TangibleAssets();
        TangibleAssetsDataEntity dataEntity = entity.getData();

        BeanUtils.copyProperties(dataEntity, tangibleAssets);

        if (dataEntity.getFixturesAndFittings() != null) {

            tangibleAssets.setFixturesAndFittings(
                    mapEntityResourceToRestResource(dataEntity.getFixturesAndFittings()));
        }

        if (dataEntity.getLandAndBuildings() != null) {

            tangibleAssets.setLandAndBuildings(
                    mapEntityResourceToRestResource(dataEntity.getLandAndBuildings()));
        }

        if (dataEntity.getMotorVehicles() != null) {

            tangibleAssets.setMotorVehicles(
                    mapEntityResourceToRestResource(dataEntity.getMotorVehicles()));
        }

        if (dataEntity.getOfficeEquipment() != null) {

            tangibleAssets.setOfficeEquipment(
                    mapEntityResourceToRestResource(dataEntity.getOfficeEquipment()));
        }

        if (dataEntity.getPlantAndMachinery() != null) {

            tangibleAssets.setPlantAndMachinery(
                    mapEntityResourceToRestResource(dataEntity.getPlantAndMachinery()));
        }

        if (dataEntity.getTotal() != null) {

            tangibleAssets.setTotal(
                    mapEntityResourceToRestResource(dataEntity.getTotal()));
        }

        return tangibleAssets;
    }

    private TangibleAssetsResource mapEntityResourceToRestResource(TangibleAssetsResourceEntity entityResource) {

        TangibleAssetsResource restResource = new TangibleAssetsResource();
        BeanUtils.copyProperties(entityResource, restResource);

        if (entityResource.getCost() != null) {

            Cost cost = new Cost();
            BeanUtils.copyProperties(entityResource.getCost(), cost);
            restResource.setCost(cost);
        }

        if (entityResource.getDepreciation() != null) {

            Depreciation depreciation = new Depreciation();
            BeanUtils.copyProperties(entityResource.getDepreciation(), depreciation);
            restResource.setDepreciation(depreciation);
        }

        return restResource;
    }

    @Override
    public AccountsResource getAccountsResource() { return AccountsResource.SMALL_FULL_TANGIBLE_ASSETS; }
}