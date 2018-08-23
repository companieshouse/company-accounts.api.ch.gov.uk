package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;

@Component
public class SmallFullTransformer implements
        GenericTransformer<SmallFull, SmallFullEntity> {

    @Override
    public SmallFullEntity transform(SmallFull entity) {
        SmallFullDataEntity smallFullDataEntity = new SmallFullDataEntity();
        SmallFullEntity smallFullEntity = new SmallFullEntity();
        BeanUtils.copyProperties(entity, smallFullDataEntity);
        smallFullEntity.setData(smallFullDataEntity);
        return smallFullEntity;
    }

    @Override
    public SmallFull transform(SmallFullEntity entity) {
        SmallFull smallFull = new SmallFull();
        SmallFullDataEntity smallFullDataEntity = entity.getData();
        BeanUtils.copyProperties(smallFullDataEntity, smallFull);
        return smallFull;
    }
}
