package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportApprovalDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportApprovalEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CicReportApproval;

@Component
public class CicReportApprovalTransformer implements GenericTransformer<CicReportApproval, CicReportApprovalEntity> {

    @Override
    public CicReportApprovalEntity transform(CicReportApproval approval) {

        CicReportApprovalDataEntity dataEntity = new CicReportApprovalDataEntity();
        CicReportApprovalEntity entity  = new CicReportApprovalEntity();
        BeanUtils.copyProperties(approval, dataEntity);
        entity.setData(dataEntity);
        return entity;
    }

    @Override
    public CicReportApproval transform(CicReportApprovalEntity approval) {

        CicReportApproval cicReportApproval = new CicReportApproval();
        BeanUtils.copyProperties(approval.getData(), cicReportApproval);
        return cicReportApproval;
    }
}
