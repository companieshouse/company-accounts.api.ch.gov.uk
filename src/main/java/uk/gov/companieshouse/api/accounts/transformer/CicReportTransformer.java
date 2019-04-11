package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CicReport;

public class CicReportTransformer implements GenericTransformer<CicReport, CicReportEntity> {

    @Override
    public CicReportEntity transform(CicReport entity) {

        CicReportDataEntity cicReportDataEntity = new CicReportDataEntity();
        BeanUtils.copyProperties(entity, cicReportDataEntity);

        CicReportEntity cicReportEntity = new CicReportEntity();
        cicReportEntity.setData(cicReportDataEntity);
        return cicReportEntity;
    }

    @Override
    public CicReport transform(CicReportEntity entity) {

        CicReport cicReport = new CicReport();
        BeanUtils.copyProperties(entity.getData(), cicReport);

        return cicReport;
    }
}
