package uk.gov.companieshouse.api.accounts.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;

@Service
public interface SmallFullService extends AbstractService<SmallFull, SmallFullEntity, CompanyAccountEntity> {

}
