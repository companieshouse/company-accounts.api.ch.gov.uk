package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.AccountingPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.AccountingPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;

@Component
public class CompanyAccountTransformer implements
        GenericTransformer<CompanyAccount, CompanyAccountEntity> {

    @Override
    public CompanyAccountEntity transform(CompanyAccount companyAccount) {

        CompanyAccountDataEntity companyAccountDataEntity = new CompanyAccountDataEntity();
        BeanUtils.copyProperties(companyAccount, companyAccountDataEntity);

        if (companyAccount.getNextAccounts() != null) {
            AccountingPeriodEntity nextAccounts = new AccountingPeriodEntity();
            BeanUtils.copyProperties(companyAccount.getNextAccounts(), nextAccounts);
            companyAccountDataEntity.setNextAccounts(nextAccounts);
        }

        if (companyAccount.getLastAccounts() != null) {
            AccountingPeriodEntity lastAccounts = new AccountingPeriodEntity();
            BeanUtils.copyProperties(companyAccount.getLastAccounts(), lastAccounts);
            companyAccountDataEntity.setLastAccounts(lastAccounts);
        }

        CompanyAccountEntity companyAccountEntity = new CompanyAccountEntity();
        companyAccountEntity.setData(companyAccountDataEntity);
        return companyAccountEntity;
    }

    @Override
    public CompanyAccount transform(CompanyAccountEntity entity) {

        CompanyAccount companyAccount = new CompanyAccount();

        CompanyAccountDataEntity companyAccountDataEntity = entity.getData();
        BeanUtils.copyProperties(companyAccountDataEntity, companyAccount);

        if (companyAccountDataEntity.getNextAccounts() != null) {
            AccountingPeriod nextAccounts = new AccountingPeriod();
            BeanUtils.copyProperties(companyAccountDataEntity.getNextAccounts(), nextAccounts);
            companyAccount.setNextAccounts(nextAccounts);
        }

        if (companyAccountDataEntity.getLastAccounts() != null) {
            AccountingPeriod lastAccounts = new AccountingPeriod();
            BeanUtils.copyProperties(companyAccountDataEntity.getLastAccounts(), lastAccounts);
            companyAccount.setLastAccounts(lastAccounts);
        }

        return companyAccount;
    }
}
