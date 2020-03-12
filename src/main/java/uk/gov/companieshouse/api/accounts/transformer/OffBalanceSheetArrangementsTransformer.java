package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.offbalancesheetarrangements.OffBalanceSheetArrangementsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.offbalancesheetarrangements.OffBalanceSheetArrangementsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.offbalancesheetarrangements.OffBalanceSheetArrangements;

@Component
public class OffBalanceSheetArrangementsTransformer implements NoteTransformer<OffBalanceSheetArrangements, OffBalanceSheetArrangementsEntity> {

    @Override
    public AccountingNoteType getAccountingNoteType() {
        return AccountingNoteType.SMALL_FULL_OFF_BALANCE_SHEET_ARRANGEMENTS;
    }

    @Override
    public OffBalanceSheetArrangementsEntity transform(OffBalanceSheetArrangements rest) {

        OffBalanceSheetArrangementsDataEntity offBalanceSheetArrangementsDataEntity = new OffBalanceSheetArrangementsDataEntity();
        OffBalanceSheetArrangementsEntity offBalanceSheetArrangementsEntity = new OffBalanceSheetArrangementsEntity();

        BeanUtils.copyProperties(rest, offBalanceSheetArrangementsDataEntity);

       offBalanceSheetArrangementsEntity.setData(offBalanceSheetArrangementsDataEntity);

        return offBalanceSheetArrangementsEntity;

    }

    @Override
    public OffBalanceSheetArrangements transform(OffBalanceSheetArrangementsEntity entity) {

        OffBalanceSheetArrangements offBalanceSheetArrangements = new OffBalanceSheetArrangements();

        OffBalanceSheetArrangementsDataEntity offBalanceSheetArrangementsDataEntity;
        if (entity.getData() != null) {
            offBalanceSheetArrangementsDataEntity = entity.getData();
        } else {
            offBalanceSheetArrangementsDataEntity = new OffBalanceSheetArrangementsDataEntity();
        }
        BeanUtils.copyProperties(offBalanceSheetArrangementsDataEntity, offBalanceSheetArrangements);

        return offBalanceSheetArrangements;

    }
}
