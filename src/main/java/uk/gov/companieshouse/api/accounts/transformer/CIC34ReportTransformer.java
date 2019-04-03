package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.CIC34ReportDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CIC34ReportEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CIC34Report;

@Component
public class CIC34ReportTransformer implements GenericTransformer<CIC34Report, CIC34ReportEntity> {

    @Override
    public CIC34ReportEntity transform(CIC34Report entity) {

        CIC34ReportDataEntity cic34ReportDataEntity = new CIC34ReportDataEntity();
        BeanUtils.copyProperties(entity, cic34ReportDataEntity);

        CIC34ReportEntity cic34ReportEntity = new CIC34ReportEntity();
        cic34ReportEntity.setData(cic34ReportDataEntity);
        return cic34ReportEntity;
    }

    @Override
    public CIC34Report transform(CIC34ReportEntity entity) {

        CIC34Report cic34Report = new CIC34Report();
        BeanUtils.copyProperties(entity.getData(), cic34Report);
        return cic34Report;
    }
}
