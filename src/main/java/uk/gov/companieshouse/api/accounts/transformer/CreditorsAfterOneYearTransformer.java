package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.creditorsaftermorethanoneyear.CreditorsAfterMoreThanOneYearDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.creditorsaftermorethanoneyear.CreditorsAfterMoreThanOneYearEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.creditorsaftermorethanoneyear.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.creditorsaftermorethanoneyear.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.creditorsaftermorethanoneyear.CreditorsAfterMoreThanOneYear;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.creditorsaftermorethanoneyear.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.creditorsaftermorethanoneyear.PreviousPeriod;


@Component
public class CreditorsAfterOneYearTransformer implements NoteTransformer<CreditorsAfterMoreThanOneYear, CreditorsAfterMoreThanOneYearEntity>  {

    @Override
    public CreditorsAfterMoreThanOneYearEntity transform(CreditorsAfterMoreThanOneYear rest) {

        CreditorsAfterMoreThanOneYearDataEntity creditorsAfterMoreThanOneYearDataEntity = new CreditorsAfterMoreThanOneYearDataEntity();
        CreditorsAfterMoreThanOneYearEntity creditorsAfterMoreThanOneYearEntity = new CreditorsAfterMoreThanOneYearEntity();

        BeanUtils.copyProperties(rest, creditorsAfterMoreThanOneYearDataEntity);

        if (rest.getCurrentPeriod() != null) {
            CurrentPeriodEntity currentPeriodEntity = new CurrentPeriodEntity();
            BeanUtils.copyProperties(rest.getCurrentPeriod(), currentPeriodEntity);
            creditorsAfterMoreThanOneYearDataEntity.setCurrentPeriodEntity(currentPeriodEntity);
        }

        if (rest.getPreviousPeriod() != null) {
            PreviousPeriodEntity previousPeriodEntity = new PreviousPeriodEntity();
            BeanUtils.copyProperties(rest.getPreviousPeriod(), previousPeriodEntity);
            creditorsAfterMoreThanOneYearDataEntity.setPreviousPeriodEntity(previousPeriodEntity);
        }

        creditorsAfterMoreThanOneYearEntity.setData(creditorsAfterMoreThanOneYearDataEntity);

        return creditorsAfterMoreThanOneYearEntity;
    }

    @Override
    public CreditorsAfterMoreThanOneYear transform(CreditorsAfterMoreThanOneYearEntity entity) {
        CreditorsAfterMoreThanOneYear creditorsAfterMoreThanOneYear = new CreditorsAfterMoreThanOneYear();
        CreditorsAfterMoreThanOneYearDataEntity creditorsAfterMoreThanOneYearDataEntity;

        if (entity.getData() != null) {
            creditorsAfterMoreThanOneYearDataEntity = entity.getData();
        } else {
            creditorsAfterMoreThanOneYearDataEntity = new CreditorsAfterMoreThanOneYearDataEntity();
        }

        BeanUtils.copyProperties(creditorsAfterMoreThanOneYearDataEntity, creditorsAfterMoreThanOneYear);

        if (creditorsAfterMoreThanOneYearDataEntity.getCurrentPeriodEntity() != null) {
            CurrentPeriod currentPeriod = new CurrentPeriod();
            BeanUtils.copyProperties(creditorsAfterMoreThanOneYearDataEntity.getCurrentPeriodEntity(), currentPeriod);
            creditorsAfterMoreThanOneYear.setCurrentPeriod(currentPeriod);
        }

        if (creditorsAfterMoreThanOneYearDataEntity.getPreviousPeriodEntity() != null) {
            PreviousPeriod previousPeriod = new PreviousPeriod();
            BeanUtils.copyProperties(creditorsAfterMoreThanOneYearDataEntity.getPreviousPeriodEntity(), previousPeriod);
            creditorsAfterMoreThanOneYear.setPreviousPeriod(previousPeriod);
        }
        return creditorsAfterMoreThanOneYear;
    }

    @Override
    public AccountingNoteType getAccountingNoteType() {
        return AccountingNoteType.SMALL_FULL_CREDITORS_AFTER;
    }
}
