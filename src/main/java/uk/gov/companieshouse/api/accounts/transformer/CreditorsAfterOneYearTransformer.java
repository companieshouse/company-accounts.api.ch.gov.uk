package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorsafteroneyearentity.CreditorsAfterOneYearDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorsafteroneyearentity.CreditorsAfterOneYearEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorsafteroneyearentity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorsafteroneyearentity.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsafteroneyear.CreditorsAfterOneYear;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsafteroneyear.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorsafteroneyear.PreviousPeriod;


@Component
public class CreditorsAfterOneYearTransformer implements GenericTransformer<CreditorsAfterOneYear, CreditorsAfterOneYearEntity>  {

    @Override
    public CreditorsAfterOneYearEntity transform(CreditorsAfterOneYear rest) {

        CreditorsAfterOneYearDataEntity creditorsAfterOneYearDataEntity = new CreditorsAfterOneYearDataEntity();
        CreditorsAfterOneYearEntity creditorsAfterOneYearEntity = new CreditorsAfterOneYearEntity();

        BeanUtils.copyProperties(rest, creditorsAfterOneYearDataEntity);

        if (rest.getCurrentPeriod() != null) {
            CurrentPeriodEntity currentPeriodEntity = new CurrentPeriodEntity();
            BeanUtils.copyProperties(rest.getCurrentPeriod(), currentPeriodEntity);
            creditorsAfterOneYearDataEntity.setCurrentPeriodEntity(currentPeriodEntity);
        }

        if (rest.getPreviousPeriod() != null) {
            PreviousPeriodEntity previousPeriodEntity = new PreviousPeriodEntity();
            BeanUtils.copyProperties(rest.getPreviousPeriod(), previousPeriodEntity);
            creditorsAfterOneYearDataEntity.setPreviousPeriodEntity(previousPeriodEntity);
        }

        creditorsAfterOneYearEntity.setData(creditorsAfterOneYearDataEntity);

        return creditorsAfterOneYearEntity;
    }

    @Override
    public CreditorsAfterOneYear transform(CreditorsAfterOneYearEntity entity) {
        CreditorsAfterOneYear creditorsAfterOneYear = new CreditorsAfterOneYear();
        CreditorsAfterOneYearDataEntity creditorsAfterOneYearDataEntity;

        if (entity.getData() != null) {
            creditorsAfterOneYearDataEntity = entity.getData();
        } else {
            creditorsAfterOneYearDataEntity = new CreditorsAfterOneYearDataEntity();
        }

        BeanUtils.copyProperties(creditorsAfterOneYearDataEntity, creditorsAfterOneYear);

        if (creditorsAfterOneYearDataEntity.getCurrentPeriodEntity() != null) {
            CurrentPeriod currentPeriod = new CurrentPeriod();
            BeanUtils.copyProperties(creditorsAfterOneYearDataEntity.getCurrentPeriodEntity(), currentPeriod);
            creditorsAfterOneYear.setCurrentPeriod(currentPeriod);
        }

        if (creditorsAfterOneYearDataEntity.getPreviousPeriodEntity() != null) {
            PreviousPeriod previousPeriod = new PreviousPeriod();
            BeanUtils.copyProperties(creditorsAfterOneYearDataEntity.getPreviousPeriodEntity(), previousPeriod);
            creditorsAfterOneYear.setPreviousPeriod(previousPeriod);
        }
        return creditorsAfterOneYear;
    }
}
