package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoansToDirectorsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoansToDirectorsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoansToDirectors;

@Component
public class LoansToDirectorsTransformer implements GenericTransformer<LoansToDirectors, LoansToDirectorsEntity> {

    @Override
    public LoansToDirectorsEntity transform(LoansToDirectors rest) {

        LoansToDirectorsDataEntity loansToDirectorsDataEntity = new LoansToDirectorsDataEntity();
        BeanUtils.copyProperties(rest, loansToDirectorsDataEntity);

        LoansToDirectorsEntity loansToDirectorsEntity = new LoansToDirectorsEntity();
        loansToDirectorsEntity.setData(loansToDirectorsDataEntity);
        return loansToDirectorsEntity;
    }

    @Override
    public LoansToDirectors transform(LoansToDirectorsEntity entity) {

        LoansToDirectors loansToDirectors = new LoansToDirectors();

        LoansToDirectorsDataEntity loansToDirectorsDataEntity = entity.getData();
        BeanUtils.copyProperties(loansToDirectorsDataEntity, loansToDirectors);

        return loansToDirectors;
    }
}
