package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.intangibleassets.*;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.intangibleassets.Cost;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.intangibleassets.Amortisation;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.intangibleassets.IntangibleAssets;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.intangibleassets.IntangibleAssetsResource;

@Component
public class IntangibleAssetsTransformer implements NoteTransformer<IntangibleAssets, IntangibleAssetsEntity> {

    @Override
    public IntangibleAssetsEntity transform(IntangibleAssets rest) {

        IntangibleAssetsEntity intangibleAssetsEntity = new IntangibleAssetsEntity();
        IntangibleAssetsDataEntity intangibleAssetsDataEntity = new IntangibleAssetsDataEntity();

        BeanUtils.copyProperties(rest, intangibleAssetsDataEntity);

        if (rest.getGoodwill() != null) {

            intangibleAssetsDataEntity.setGoodwill(
                    mapRestResourceToEntityResource(rest.getGoodwill()));
        }

        if (rest.getOtherIntangibleAssets() != null) {

            intangibleAssetsDataEntity.setOtherIntangibleAssets(
                    mapRestResourceToEntityResource(rest.getOtherIntangibleAssets()));
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

            intangibleAssets.setGoodwill(
                    mapEntityResourceToRestResource(dataEntity.getGoodwill()));
        }

        if (dataEntity.getOtherIntangibleAssets() != null) {

            intangibleAssets.setOtherIntangibleAssets(
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

    @Override
    public AccountingNoteType getAccountingNoteType() {
        return AccountingNoteType.SMALL_FULL_INTANGIBLE_ASSETS;
    }
}