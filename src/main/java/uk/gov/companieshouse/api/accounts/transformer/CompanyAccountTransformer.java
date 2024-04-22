package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;

@Component
public class CompanyAccountTransformer implements GenericTransformer<CompanyAccount, CompanyAccountEntity> {

    @Override
    public CompanyAccountEntity transform(CompanyAccount companyAccount) {
        CompanyAccountDataEntity companyAccountDataEntity = new CompanyAccountDataEntity();
        BeanUtils.copyProperties(companyAccount, companyAccountDataEntity);

        CompanyAccountEntity companyAccountEntity = new CompanyAccountEntity();
        companyAccountEntity.setData(companyAccountDataEntity);
        return companyAccountEntity;
    }

    @Override
    public CompanyAccount transform(CompanyAccountEntity entity) {
        CompanyAccount companyAccount = new CompanyAccount();

        CompanyAccountDataEntity companyAccountDataEntity = entity.getData();
        BeanUtils.copyProperties(companyAccountDataEntity, companyAccount);

        return companyAccount;
    }
}
