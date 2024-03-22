package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportApprovalDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportApprovalEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CicApproval;

@Component
public class CicApprovalTransformer implements GenericTransformer<CicApproval, CicReportApprovalEntity> {

    @Override
    public CicReportApprovalEntity transform(CicApproval approval) {
        CicReportApprovalDataEntity dataEntity = new CicReportApprovalDataEntity();
        CicReportApprovalEntity entity  = new CicReportApprovalEntity();
        BeanUtils.copyProperties(approval, dataEntity);
        entity.setData(dataEntity);
        return entity;
    }

    @Override
    public CicApproval transform(CicReportApprovalEntity approval) {
        CicApproval cicApproval = new CicApproval();
        BeanUtils.copyProperties(approval.getData(), cicApproval);
        return cicApproval;
    }
}
