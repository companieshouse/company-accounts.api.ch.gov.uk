package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;

/**
 * CompanyAccountTransformer is the class used to handle the transformation between rest and entity objects
 */
@Component
public class CompanyAccountTransformer implements
        GenericTransformer<CompanyAccount, CompanyAccountEntity> {


    @Override
    public CompanyAccountEntity transform(CompanyAccount companyAccount) {
        CompanyAccountDataEntity companyAccountDataEntity = new CompanyAccountDataEntity();
        CompanyAccountEntity companyAccountEntity = new CompanyAccountEntity();
        BeanUtils.copyProperties(companyAccount, companyAccountDataEntity);
        companyAccountEntity.setData(companyAccountDataEntity);
        return companyAccountEntity;
    }

    @Override
    public CompanyAccount transform(CompanyAccountEntity companyAccountEntity) {
        CompanyAccount companyAccount = new CompanyAccount();
        BeanUtils.copyProperties(companyAccountEntity.getData(), companyAccount);
        return companyAccount;
    }

}
