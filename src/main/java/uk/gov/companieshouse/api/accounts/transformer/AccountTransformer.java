package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.AccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.AccountEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Account;

/**
 * AccountTransformer is the class used to handle the transformation between rest and entity objects
 */
@Component
public class AccountTransformer {

    public AccountEntity transform(Account account) {
        AccountDataEntity accountDataEntity = new AccountDataEntity();
        AccountEntity accountEntity = new AccountEntity();
        BeanUtils.copyProperties(account, accountDataEntity);

        accountEntity.setData(accountDataEntity);

        return accountEntity;
    }
}
