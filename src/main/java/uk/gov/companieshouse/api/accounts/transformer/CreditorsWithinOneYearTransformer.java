package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.notes.CreditorsWithinOneYearDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.CreditorsWithinOneYearEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.CreditorsWithinOneYear;

@Component
public class CreditorsWithinOneYearTransformer implements GenericTransformer<CreditorsWithinOneYear, CreditorsWithinOneYearEntity> {

    @Override
    public CreditorsWithinOneYearEntity transform(CreditorsWithinOneYear entity) {

        CreditorsWithinOneYearDataEntity creditorsWithinOneYearDataEntity = new CreditorsWithinOneYearDataEntity();
        CreditorsWithinOneYearEntity creditorsWithinOneYearEntity = new CreditorsWithinOneYearEntity();
        BeanUtils.copyProperties(entity, creditorsWithinOneYearDataEntity);
        creditorsWithinOneYearEntity.setData(creditorsWithinOneYearDataEntity);

        return creditorsWithinOneYearEntity;
    }

    @Override
    public CreditorsWithinOneYear transform(CreditorsWithinOneYearEntity entity) {

        CreditorsWithinOneYear creditorsWithinOneYear = new CreditorsWithinOneYear();
        CreditorsWithinOneYearDataEntity creditorsWithinOneYearDataEntity = entity.getData();
        BeanUtils.copyProperties(creditorsWithinOneYearDataEntity, creditorsWithinOneYear);

        return creditorsWithinOneYear;
    }
}
