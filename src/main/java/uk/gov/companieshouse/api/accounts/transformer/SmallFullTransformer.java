package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.accounts.model.entity.AccountingPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.LastAccounts;
import uk.gov.companieshouse.api.accounts.model.rest.NextAccounts;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;

@Component
public class SmallFullTransformer implements
        GenericTransformer<SmallFull, SmallFullEntity> {

    @Override
    public SmallFullEntity transform(SmallFull entity) {
        SmallFullDataEntity smallFullDataEntity = new SmallFullDataEntity();
        SmallFullEntity smallFullEntity = new SmallFullEntity();
        BeanUtils.copyProperties(entity, smallFullDataEntity);

        if (entity.getNextAccounts() != null) {
            AccountingPeriodEntity nextAccounts = new AccountingPeriodEntity();
            BeanUtils.copyProperties(entity.getNextAccounts(), nextAccounts);
            smallFullDataEntity.setNextAccounts(nextAccounts);
        }

        if (entity.getLastAccounts() != null) {
            AccountingPeriodEntity lastAccounts = new AccountingPeriodEntity();
            BeanUtils.copyProperties(entity.getLastAccounts(), lastAccounts);
            smallFullDataEntity.setLastAccounts(lastAccounts);
        }
        smallFullEntity.setData(smallFullDataEntity);
        return smallFullEntity;
    }

    @Override
    public SmallFull transform(SmallFullEntity entity) {
        SmallFull smallFull = new SmallFull();
        SmallFullDataEntity smallFullDataEntity = entity.getData();
        BeanUtils.copyProperties(smallFullDataEntity, smallFull);

        if (smallFullDataEntity.getNextAccounts() != null) {
        	NextAccounts nextAccounts = new NextAccounts();
            BeanUtils.copyProperties(smallFullDataEntity.getNextAccounts(), nextAccounts);
            smallFull.setNextAccounts(nextAccounts);
        }

        if (smallFullDataEntity.getLastAccounts() != null) {
            LastAccounts lastAccounts = new LastAccounts();
            BeanUtils.copyProperties(smallFullDataEntity.getLastAccounts(), lastAccounts);
            smallFull.setLastAccounts(lastAccounts);
        }

        return smallFull;
    }
}
