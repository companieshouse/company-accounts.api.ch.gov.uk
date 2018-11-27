package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.DebtorsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.DebtorsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Debtors;

@Component
public class DebtorsTransformer  implements GenericTransformer<Debtors, DebtorsEntity> {

    @Override
    public DebtorsEntity transform(Debtors rest) {

        DebtorsDataEntity debtorsDataEntity = new DebtorsDataEntity();
        DebtorsEntity debtorsEntity = new DebtorsEntity();

        BeanUtils.copyProperties(rest, debtorsDataEntity);
        debtorsEntity.setData(debtorsDataEntity);

        return debtorsEntity;
    }

    @Override
    public Debtors transform(DebtorsEntity entity) {

        Debtors debtors = new Debtors();
        DebtorsDataEntity debtorsDataEntity = entity.getData();
        BeanUtils.copyProperties(debtorsDataEntity, debtors);

        return debtors;
    }
}
