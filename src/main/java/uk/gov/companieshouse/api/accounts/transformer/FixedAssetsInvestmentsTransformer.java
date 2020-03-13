package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.notes.fixedassetsinvestments.FixedAssetsInvestmentsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.fixedassetsinvestments.FixedAssetsInvestmentsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.fixedassetsinvestments.FixedAssetsInvestments;

@Component
public class FixedAssetsInvestmentsTransformer implements GenericTransformer<FixedAssetsInvestments, FixedAssetsInvestmentsEntity> {

    @Override
    public FixedAssetsInvestmentsEntity transform(FixedAssetsInvestments rest) {

        FixedAssetsInvestmentsDataEntity fixedAssetsInvestmentsDataEntity = new FixedAssetsInvestmentsDataEntity();
        FixedAssetsInvestmentsEntity fixedAssetsInvestmentsEntity = new FixedAssetsInvestmentsEntity();

        BeanUtils.copyProperties(rest, fixedAssetsInvestmentsDataEntity);

        fixedAssetsInvestmentsEntity.setData(fixedAssetsInvestmentsDataEntity);

        return fixedAssetsInvestmentsEntity;
    }

    @Override
    public FixedAssetsInvestments transform(FixedAssetsInvestmentsEntity entity) {

        FixedAssetsInvestments fixedAssetsInvestments = new FixedAssetsInvestments();
        FixedAssetsInvestmentsDataEntity fixedAssetsInvestmentsDataEntity;

        if (entity.getData() != null) {
            fixedAssetsInvestmentsDataEntity = entity.getData();
        } else {
            fixedAssetsInvestmentsDataEntity = new FixedAssetsInvestmentsDataEntity();
        }

        BeanUtils.copyProperties(fixedAssetsInvestmentsDataEntity, fixedAssetsInvestments);

        return fixedAssetsInvestments;
    }
}
