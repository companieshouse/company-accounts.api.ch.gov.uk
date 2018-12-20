package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.StatementDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.StatementEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Statement;

@Component
public class StatementTransformer implements GenericTransformer<Statement, StatementEntity> {

    @Override
    public StatementEntity transform(Statement entity) {
        StatementDataEntity statementDataEntity = new StatementDataEntity();
        StatementEntity statementEntity = new StatementEntity();
        BeanUtils.copyProperties(entity, statementDataEntity);
        statementEntity.setData(statementDataEntity);
        return statementEntity;
    }

    @Override
    public Statement transform(StatementEntity entity) {
        Statement statement = new Statement();
        StatementDataEntity statementDataEntity = entity.getData();
        BeanUtils.copyProperties(statementDataEntity, statement);
        return statement;
    }
}
