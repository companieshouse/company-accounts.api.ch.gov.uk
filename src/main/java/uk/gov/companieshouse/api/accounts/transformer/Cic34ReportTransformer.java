package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.Cic34ReportDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.Cic34ReportEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Cic34Report;

@Component
public class Cic34ReportTransformer implements GenericTransformer<Cic34Report, Cic34ReportEntity> {

    @Override
    public Cic34ReportEntity transform(Cic34Report entity) {

        Cic34ReportDataEntity cic34ReportDataEntity = new Cic34ReportDataEntity();
        BeanUtils.copyProperties(entity, cic34ReportDataEntity);

        Cic34ReportEntity cic34ReportEntity = new Cic34ReportEntity();
        cic34ReportEntity.setData(cic34ReportDataEntity);
        return cic34ReportEntity;
    }

    @Override
    public Cic34Report transform(Cic34ReportEntity entity) {

        Cic34Report cic34Report = new Cic34Report();
        BeanUtils.copyProperties(entity.getData(), cic34Report);
        return cic34Report;
    }
}
