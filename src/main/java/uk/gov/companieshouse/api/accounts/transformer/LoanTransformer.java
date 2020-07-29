package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoanBreakdownResourceEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoanDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoanEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.Loan;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoanBreakdownResource;

@Component
public class LoanTransformer implements GenericTransformerForMultipleResources<Loan, LoanEntity> {


    @Override
    public Loan[] transform(LoanEntity[] entity) {

        Loan[] loan = new Loan[entity.length];

        for(int i = 0; i < loan.length; i++) {

            loan[i] = transform(entity[i]);
        }

        return loan;
    }

    @Override
    public LoanEntity transform(Loan entity) {

        LoanDataEntity loanDataEntity = new LoanDataEntity();
        BeanUtils.copyProperties(entity, loanDataEntity);

        if (entity.getBreakdown() != null) {

            loanDataEntity.setBreakdown(
                    mapRestResourceToEntityResource(entity.getBreakdown()));
        }

        LoanEntity loanEntity = new LoanEntity();
        loanEntity.setData(loanDataEntity);

        return loanEntity;
    }

    @Override
    public Loan transform(LoanEntity entity) {

        Loan loan = new Loan();
        LoanDataEntity dataEntity = entity.getData();

        BeanUtils.copyProperties(dataEntity, loan);

        if (dataEntity.getBreakdown() != null) {

            loan.setBreakdown(
                    mapEntityResourceToRestResource(dataEntity.getBreakdown()));
        }

        return loan;
    }

    private LoanBreakdownResourceEntity mapRestResourceToEntityResource(LoanBreakdownResource restResource) {

        LoanBreakdownResourceEntity entityResource = new LoanBreakdownResourceEntity();

        if (restResource != null) {

            BeanUtils.copyProperties(restResource, entityResource);
            return entityResource;
        }

        return null;
    }

    private LoanBreakdownResource mapEntityResourceToRestResource(LoanBreakdownResourceEntity entityResource) {

        LoanBreakdownResource restResource = new LoanBreakdownResource();

        if (entityResource != null) {

            BeanUtils.copyProperties(entityResource, restResource);
            return restResource;
        }

        return null;
    }
}
