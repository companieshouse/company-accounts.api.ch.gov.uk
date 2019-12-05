package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.StatementsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.StatementsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Statements;

@Component
public class StatementsTransformer implements GenericTransformer<Statements, StatementsEntity> {

    @Override
    public StatementsEntity transform(Statements entity) {

        StatementsDataEntity statementsDataEntity = new StatementsDataEntity();
        BeanUtils.copyProperties(entity, statementsDataEntity);

        StatementsEntity statementsEntity = new StatementsEntity();
        statementsEntity.setData(statementsDataEntity);

        return statementsEntity;
    }

    @Override
    public Statements transform(StatementsEntity entity) {

        Statements statements = new Statements();
        BeanUtils.copyProperties(entity.getData(), statements);

        return statements;
    }
}
