package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorsApprovalDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorsApprovalEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsApproval;

@Component
public class DirectorsApprovalTransformer implements GenericTransformer<DirectorsApproval, DirectorsApprovalEntity> {

    @Override
    public DirectorsApprovalEntity transform(DirectorsApproval entity) {
        DirectorsApprovalDataEntity directorsApprovalDataEntity = new DirectorsApprovalDataEntity();
        BeanUtils.copyProperties(entity, directorsApprovalDataEntity);

        DirectorsApprovalEntity directorsApprovalEntity = new DirectorsApprovalEntity();
        directorsApprovalEntity.setData(directorsApprovalDataEntity);

        return directorsApprovalEntity;
    }

    @Override
    public DirectorsApproval transform(DirectorsApprovalEntity entity) {
        DirectorsApproval directorsApproval = new DirectorsApproval();
        BeanUtils.copyProperties(entity.getData(), directorsApproval);

        return directorsApproval;
    }
}
