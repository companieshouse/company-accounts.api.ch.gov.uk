package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.ApprovalDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.ApprovalEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Approval;

@Component
public class ApprovalTransformer implements GenericTransformer<Approval, ApprovalEntity> {

    @Override
    public ApprovalEntity transform(Approval entity) {
        ApprovalDataEntity approvalDataEntity = new ApprovalDataEntity();
        ApprovalEntity approvalEntity = new ApprovalEntity();
        BeanUtils.copyProperties(entity, approvalDataEntity);
        approvalEntity.setData(approvalDataEntity);
        return approvalEntity;
    }

    @Override
    public Approval transform(ApprovalEntity entity) {
        Approval approval = new Approval();
        ApprovalDataEntity approvalDataEntity = entity.getData();
        BeanUtils.copyProperties(approvalDataEntity, approval);
        return approval;
    }
}
