package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportStatementsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.ReportStatementsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportStatementsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CicReportStatements;
import uk.gov.companieshouse.api.accounts.model.rest.ReportStatements;

@Component
public class CicReportStatementsTransformer implements GenericTransformer<CicReportStatements, CicReportStatementsEntity> {

    @Override
    public CicReportStatementsEntity transform(CicReportStatements entity) {

        CicReportStatementsDataEntity cicReportStatementsDataEntity = new CicReportStatementsDataEntity();
        BeanUtils.copyProperties(entity, cicReportStatementsDataEntity);

        if (entity.getReportStatements() != null) {

            ReportStatementsEntity reportStatementsEntity = new ReportStatementsEntity();
            BeanUtils.copyProperties(entity.getReportStatements(), reportStatementsEntity);
            cicReportStatementsDataEntity.setReportStatements(reportStatementsEntity);
        }

        CicReportStatementsEntity cicReportStatementsEntity = new CicReportStatementsEntity();
        cicReportStatementsEntity.setData(cicReportStatementsDataEntity);
        return cicReportStatementsEntity;
    }

    @Override
    public CicReportStatements transform(CicReportStatementsEntity entity) {

        CicReportStatements cicReportStatements = new CicReportStatements();
        BeanUtils.copyProperties(entity.getData(), cicReportStatements);

        if (entity.getData().getReportStatements() != null) {

            ReportStatements reportStatements = new ReportStatements();
            BeanUtils.copyProperties(entity.getData().getReportStatements(), reportStatements);
            cicReportStatements.setReportStatements(reportStatements);
        }

        return cicReportStatements;
    }
}
