package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.financialcommitments.FinancialCommitmentsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.financialcommitments.FinancialCommitmentsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.financialcommitments.FinancialCommitments;

@Component
public class FinancialCommitmentsTransformer implements NoteTransformer<FinancialCommitments, FinancialCommitmentsEntity> {

    @Override
    public AccountingNoteType getAccountingNoteType() {
        return AccountingNoteType.SMALL_FULL_FINANCIAL_COMMITMENTS;
    }

    @Override
    public FinancialCommitmentsEntity transform(FinancialCommitments rest) {

        FinancialCommitmentsDataEntity financialCommitmentsDataEntity = new FinancialCommitmentsDataEntity();
        FinancialCommitmentsEntity financialCommitmentsEntity = new FinancialCommitmentsEntity();

        BeanUtils.copyProperties(rest, financialCommitmentsDataEntity);

        financialCommitmentsEntity.setData(financialCommitmentsDataEntity);

        return financialCommitmentsEntity;

    }

    @Override
    public FinancialCommitments transform(FinancialCommitmentsEntity entity) {

        FinancialCommitments financialCommitments = new FinancialCommitments();

        FinancialCommitmentsDataEntity financialCommitmentsDataEntity;
        if (entity.getData() != null) {
            financialCommitmentsDataEntity = entity.getData();
        } else {
            financialCommitmentsDataEntity = new FinancialCommitmentsDataEntity();
        }
        BeanUtils.copyProperties(financialCommitmentsDataEntity, financialCommitments);

        return financialCommitments;

    }
}
