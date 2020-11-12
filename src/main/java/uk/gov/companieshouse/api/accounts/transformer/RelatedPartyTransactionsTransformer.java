package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoansToDirectorsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoansToDirectorsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.RelatedPartyTransactionsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.RelatedPartyTransactionsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoansToDirectors;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RelatedPartyTransactions;

@Component
public class RelatedPartyTransactionsTransformer implements GenericTransformer<RelatedPartyTransactions, RelatedPartyTransactionsEntity> {

    @Override
    public RelatedPartyTransactionsEntity transform(RelatedPartyTransactions rest) {
        RelatedPartyTransactionsDataEntity dataEntity = new RelatedPartyTransactionsDataEntity();
        BeanUtils.copyProperties(rest, dataEntity);

        RelatedPartyTransactionsEntity relatedPartyTransactionsEntity = new RelatedPartyTransactionsEntity();
        relatedPartyTransactionsEntity.setData(dataEntity);

        return relatedPartyTransactionsEntity;
    }

    @Override
    public RelatedPartyTransactions transform(RelatedPartyTransactionsEntity entity) {

        RelatedPartyTransactions relatedPartyTransactions = new RelatedPartyTransactions();

        RelatedPartyTransactionsDataEntity dataEntity = entity.getData();
        BeanUtils.copyProperties(dataEntity, relatedPartyTransactions);

        return relatedPartyTransactions;
    }
}
