package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorsReportDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorsReportEntity;
import uk.gov.companieshouse.api.accounts.model.rest.DirectorsReport;

import java.util.Map;

@Component
public class DirectorsReportTransformer implements GenericTransformer<DirectorsReport, DirectorsReportEntity> {

    @Override
    public DirectorsReportEntity transform(DirectorsReport rest) {

        DirectorsReportDataEntity directorsReportDataEntity = new DirectorsReportDataEntity();
        BeanUtils.copyProperties(rest, directorsReportDataEntity);

        DirectorsReportEntity directorsReportEntity = new DirectorsReportEntity();
        directorsReportEntity.setData(directorsReportDataEntity);
        return directorsReportEntity;
    }

    @Override
    public DirectorsReport transform(DirectorsReportEntity entity) {

        DirectorsReport directorsReport = new DirectorsReport();

        DirectorsReportDataEntity directorsReportDataEntity = entity.getData();
        BeanUtils.copyProperties(directorsReportDataEntity, directorsReport);

        return directorsReport;
    }
}
