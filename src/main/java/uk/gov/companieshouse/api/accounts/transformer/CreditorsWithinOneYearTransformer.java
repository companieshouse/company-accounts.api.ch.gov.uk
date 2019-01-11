package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorswithinoneyear.CreditorsWithinOneYearDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorswithinoneyear.CreditorsWithinOneYearEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorswithinoneyear.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.notes.creditorswithinoneyear.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.CreditorsWithinOneYear;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.creditorswithinoneyear.PreviousPeriod;

@Component
public class CreditorsWithinOneYearTransformer implements GenericTransformer<CreditorsWithinOneYear, CreditorsWithinOneYearEntity> {

    @Override
    public CreditorsWithinOneYearEntity transform(CreditorsWithinOneYear rest) {

        CreditorsWithinOneYearDataEntity creditorsWithinOneYearDataEntity = new CreditorsWithinOneYearDataEntity();
        CreditorsWithinOneYearEntity creditorsWithinOneYearEntity = new CreditorsWithinOneYearEntity();

        BeanUtils.copyProperties(rest, creditorsWithinOneYearDataEntity);

        if (rest.getCurrentPeriod() != null) {
            CurrentPeriodEntity currentPeriodEntity = new CurrentPeriodEntity();
            BeanUtils.copyProperties(rest.getCurrentPeriod(), currentPeriodEntity);
            creditorsWithinOneYearDataEntity.setCurrentPeriodEntity(currentPeriodEntity);
        }

        BeanUtils.copyProperties(rest, creditorsWithinOneYearDataEntity);
        if (rest.getPreviousPeriod() != null) {
            PreviousPeriodEntity previousPeriodEntity = new PreviousPeriodEntity();
            BeanUtils.copyProperties(rest.getPreviousPeriod(), previousPeriodEntity);
            creditorsWithinOneYearDataEntity.setPreviousPeriodEntity(previousPeriodEntity);
        }

        creditorsWithinOneYearEntity.setData(creditorsWithinOneYearDataEntity);

        return creditorsWithinOneYearEntity;
    }

    @Override
    public CreditorsWithinOneYear transform(CreditorsWithinOneYearEntity entity) {

        CreditorsWithinOneYear creditorsWithinOneYear = new CreditorsWithinOneYear();
        CreditorsWithinOneYearDataEntity creditorsWithinOneYearDataEntity;

        if (entity.getData() != null) {
            creditorsWithinOneYearDataEntity = entity.getData();
        } else {
            creditorsWithinOneYearDataEntity = new CreditorsWithinOneYearDataEntity();
        }

        BeanUtils.copyProperties(creditorsWithinOneYearDataEntity, creditorsWithinOneYear);

        if (creditorsWithinOneYearDataEntity.getCurrentPeriodEntity() != null) {
            CurrentPeriod currentPeriod = new CurrentPeriod();
            BeanUtils.copyProperties(creditorsWithinOneYearDataEntity.getCurrentPeriodEntity(), currentPeriod);
            creditorsWithinOneYear.setCurrentPeriod(currentPeriod);
        }

        if (creditorsWithinOneYearDataEntity.getPreviousPeriodEntity() != null) {
            PreviousPeriod previousPeriod = new PreviousPeriod();
            BeanUtils.copyProperties(creditorsWithinOneYearDataEntity.getPreviousPeriodEntity(), previousPeriod);
            creditorsWithinOneYear.setPreviousPeriod(previousPeriod);
        }
        return creditorsWithinOneYear;
    }
}
