package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.notes.intangible.*;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.Cost;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.Amortisation;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.IntangibleAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.IntangibleAssetsResource;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.IntangibleAssets;
import uk.gov.companieshouse.api.accounts.model.rest.notes.intangible.IntangibleAssetsResource;

@Component
public class IntangibleAssetsTransformer implements GenericTransformer<IntangibleAssets, IntangibleAssetsEntity> {

    @Override
    public IntangibleAssetsEntity transform(IntangibleAssets rest) {

        IntangibleAssetsEntity intangibleAssetsEntity = new IntangibleAssetsEntity();
        IntangibleAssetsDataEntity intangibleAssetsDataEntity = new IntangibleAssetsDataEntity();

        BeanUtils.copyProperties(rest, intangibleAssetsDataEntity);

        if (rest.getGoodwill() != null) {

            intangibleAssetsDataEntity.setGoodwill(
                    mapRestResourceToEntityResource(rest.getGoodwill()));
        }

        if (rest.getOther_intangible_assets() != null) {

            intangibleAssetsDataEntity.setOtherIntangibleAssets(
                    mapRestResourceToEntityResource(rest.getOther_intangible_assets()));
        }

        if (rest.getTotal() != null) {

            intangibleAssetsDataEntity.setTotal(
                    mapRestResourceToEntityResource(rest.getTotal()));
        }

        intangibleAssetsEntity.setData(intangibleAssetsDataEntity);
        return intangibleAssetsEntity;
    }

    private IntangibleAssetsResourceEntity mapRestResourceToEntityResource(IntangibleAssetsResource restResource) {

        IntangibleAssetsResourceEntity entityResource = new IntangibleAssetsResourceEntity();
        BeanUtils.copyProperties(restResource, entityResource);

        if (restResource.getCost() != null) {

            CostEntity cost = new CostEntity();
            BeanUtils.copyProperties(restResource.getCost(), cost);
            entityResource.setCost(cost);
        }

        if (restResource.getAmortisation() != null) {

            AmortisationEntity amortisation = new AmortisationEntity();
            BeanUtils.copyProperties(restResource.getAmortisation(), amortisation);
            entityResource.setAmortisation(amortisation);
        }

        return entityResource;
    }

    @Override
    public IntangibleAssets transform(IntangibleAssetsEntity entity) {

        IntangibleAssets intangibleAssets = new IntangibleAssets();
        IntangibleAssetsDataEntity dataEntity = entity.getData();

        BeanUtils.copyProperties(dataEntity, intangibleAssets);

        if (dataEntity.getGoodwill() != null) {

            intangibleAssets.setGoodWill(
                    mapEntityResourceToRestResource(dataEntity.getGoodwill()));
        }

        if (dataEntity.getOtherIntangibleAssets() != null) {

            intangibleAssets.setOther_intangible_assets(
                    mapEntityResourceToRestResource(dataEntity.getOtherIntangibleAssets()));
        }

        if (dataEntity.getTotal() != null) {

            intangibleAssets.setTotal(
                    mapEntityResourceToRestResource(dataEntity.getTotal()));
        }

        return intangibleAssets;
    }

    private IntangibleAssetsResource mapEntityResourceToRestResource(IntangibleAssetsResourceEntity entityResource) {

        IntangibleAssetsResource restResource = new IntangibleAssetsResource();
        BeanUtils.copyProperties(entityResource, restResource);

        if (entityResource.getCost() != null) {

            Cost cost = new Cost();
            BeanUtils.copyProperties(entityResource.getCost(), cost);
            restResource.setCost(cost);
        }

        if (entityResource.getAmortisation() != null) {

            Amortisation amortisation = new Amortisation();
            BeanUtils.copyProperties(entityResource.getAmortisation(), amortisation);
            restResource.setAmortisation(amortisation);
        }

        return restResource;
    }
}