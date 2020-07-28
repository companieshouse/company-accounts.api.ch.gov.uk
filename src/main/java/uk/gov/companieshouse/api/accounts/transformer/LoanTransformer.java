package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoanDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoanEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.Loan;

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

        LoanEntity loanEntity = new LoanEntity();
        loanEntity.setData(loanDataEntity);

        return loanEntity;
    }

    @Override
    public Loan transform(LoanEntity entity) {

        Loan loan = new Loan();
        BeanUtils.copyProperties(entity.getData(), loan);

        return loan;
    }
}
