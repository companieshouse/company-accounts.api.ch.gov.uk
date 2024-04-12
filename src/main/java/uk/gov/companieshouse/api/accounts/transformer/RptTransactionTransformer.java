package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.RptTransactionBreakdownEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.RptTransactionDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.RptTransactionEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RptTransaction;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RptTransactionBreakdown;

@Component
public class RptTransactionTransformer implements GenericTransformerForMultipleResources<RptTransaction, RptTransactionEntity> {

    @Override
    public RptTransaction[] transform(RptTransactionEntity[] entity) {
        RptTransaction[] rptTransaction = new RptTransaction[entity.length];

        for (int i = 0; i < rptTransaction.length; i++) {
            rptTransaction[i] = transform(entity[i]);
        }

        return rptTransaction;
    }

    @Override
    public RptTransactionEntity transform(RptTransaction rest) {
        RptTransactionDataEntity dataEntity = new RptTransactionDataEntity();
        BeanUtils.copyProperties(rest, dataEntity);

        if (rest.getBreakdown() != null) {
            dataEntity.setBreakdown(mapRestResourceToEntityResource(rest.getBreakdown()));
        }

        RptTransactionEntity rptTransactionEntity = new RptTransactionEntity();
        rptTransactionEntity.setData(dataEntity);

        return rptTransactionEntity;
    }

    @Override
    public RptTransaction transform(RptTransactionEntity entity) {
        RptTransaction rptTransaction = new RptTransaction();
        RptTransactionDataEntity dataEntity = entity.getData();
        BeanUtils.copyProperties(dataEntity, rptTransaction);

        if (dataEntity.getBreakdown() != null) {
            rptTransaction.setBreakdown(mapEntityResourceToRestResource(dataEntity.getBreakdown()));
        }

        return rptTransaction;
    }

    private RptTransactionBreakdownEntity mapRestResourceToEntityResource(RptTransactionBreakdown restResource) {
        RptTransactionBreakdownEntity entityResource = new RptTransactionBreakdownEntity();

        BeanUtils.copyProperties(restResource, entityResource);
        return entityResource;
    }

    private RptTransactionBreakdown mapEntityResourceToRestResource(RptTransactionBreakdownEntity entityResource) {
        RptTransactionBreakdown restResource = new RptTransactionBreakdown();

        BeanUtils.copyProperties(entityResource, restResource);
        return restResource;
    }
}
