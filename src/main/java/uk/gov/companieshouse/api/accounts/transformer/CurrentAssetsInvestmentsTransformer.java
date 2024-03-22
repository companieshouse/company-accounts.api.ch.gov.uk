package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.currentassetsinvestments.CurrentAssetsInvestmentsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.currentassetsinvestments.CurrentAssetsInvestmentsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.currentassetsinvestments.CurrentAssetsInvestments;

@Component
public class CurrentAssetsInvestmentsTransformer implements NoteTransformer<CurrentAssetsInvestments,
        CurrentAssetsInvestmentsEntity> {

    @Override
    public CurrentAssetsInvestmentsEntity transform(CurrentAssetsInvestments rest) {
        CurrentAssetsInvestmentsDataEntity currentAssetsInvestmentsDataEntity = new CurrentAssetsInvestmentsDataEntity();
        CurrentAssetsInvestmentsEntity currentAssetsInvestmentsEntity = new CurrentAssetsInvestmentsEntity();

        BeanUtils.copyProperties(rest, currentAssetsInvestmentsDataEntity);

        currentAssetsInvestmentsEntity.setData(currentAssetsInvestmentsDataEntity);

        return currentAssetsInvestmentsEntity;
    }

    @Override
    public CurrentAssetsInvestments transform(CurrentAssetsInvestmentsEntity entity) {

        CurrentAssetsInvestments currentAssetsInvestments = new CurrentAssetsInvestments();
        CurrentAssetsInvestmentsDataEntity currentAssetsInvestmentsDataEntity;

        if (entity.getData() != null) {
            currentAssetsInvestmentsDataEntity = entity.getData();
        } else {
            currentAssetsInvestmentsDataEntity = new CurrentAssetsInvestmentsDataEntity();
        }

        BeanUtils.copyProperties(currentAssetsInvestmentsDataEntity, currentAssetsInvestments);

        return currentAssetsInvestments;
    }

    @Override
    public AccountingNoteType getAccountingNoteType() {
        return AccountingNoteType.SMALL_FULL_CURRENT_ASSETS_INVESTMENTS;
    }
}
