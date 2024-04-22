package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.SecretaryDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.SecretaryEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Secretary;

@Component
public class SecretaryTransformer implements GenericTransformer<Secretary, SecretaryEntity> {

    @Override
    public SecretaryEntity transform(Secretary entity) {
        SecretaryDataEntity secretaryDataEntity = new SecretaryDataEntity();
        BeanUtils.copyProperties(entity, secretaryDataEntity);

        SecretaryEntity secretaryEntity = new SecretaryEntity();
        secretaryEntity.setData(secretaryDataEntity);

        return secretaryEntity;
    }

    @Override
    public Secretary transform(SecretaryEntity entity) {
        Secretary secretary = new Secretary();
        BeanUtils.copyProperties(entity.getData(), secretary);

        return secretary;
    }
}
