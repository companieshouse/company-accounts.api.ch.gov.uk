package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.ProfitLossDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.ProfitLossEntity;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitLoss;

@Component
public class ProfitLossTransformer implements GenericTransformer<ProfitLoss, ProfitLossEntity> {

    @Override
    public ProfitLossEntity transform(ProfitLoss entity) {
        ProfitLossDataEntity profitLossDataEntity = new ProfitLossDataEntity();
        ProfitLossEntity profitLossEntity = new ProfitLossEntity();
        BeanUtils.copyProperties(entity, profitLossDataEntity);
        profitLossEntity.setData(profitLossDataEntity);
        return profitLossEntity;
    }

    @Override
    public ProfitLoss transform(ProfitLossEntity entity) {
        ProfitLoss profitLoss = new ProfitLoss();
        ProfitLossDataEntity profitLossDataEntity = entity.getData();
        BeanUtils.copyProperties(profitLossDataEntity, profitLoss);
        return profitLoss;

    }
}
