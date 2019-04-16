package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.CicStatementsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.ReportStatementsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CicStatementsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CicStatements;
import uk.gov.companieshouse.api.accounts.model.rest.ReportStatements;

@Component
public class CicStatementsTransformer implements GenericTransformer<CicStatements, CicStatementsEntity> {

    @Override
    public CicStatementsEntity transform(CicStatements entity) {

        CicStatementsDataEntity cicStatementsDataEntity = new CicStatementsDataEntity();
        BeanUtils.copyProperties(entity, cicStatementsDataEntity);

        if (entity.getReportStatements() != null) {

            ReportStatementsEntity reportStatementsEntity = new ReportStatementsEntity();
            BeanUtils.copyProperties(entity.getReportStatements(), reportStatementsEntity);
            cicStatementsDataEntity.setReportStatements(reportStatementsEntity);
        }

        CicStatementsEntity cicStatementsEntity = new CicStatementsEntity();
        cicStatementsEntity.setData(cicStatementsDataEntity);
        return cicStatementsEntity;
    }

    @Override
    public CicStatements transform(CicStatementsEntity entity) {

        CicStatements cicStatements = new CicStatements();
        BeanUtils.copyProperties(entity.getData(), cicStatements);

        if (entity.getData().getReportStatements() != null) {

            ReportStatements reportStatements = new ReportStatements();
            BeanUtils.copyProperties(entity.getData().getReportStatements(), reportStatements);
            cicStatements.setReportStatements(reportStatements);
        }

        return cicStatements;
    }
}
