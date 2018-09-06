package uk.gov.companieshouse.api.accounts.service;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;

@Service
public interface CurrentPeriodService extends AbstractService<CurrentPeriod, CurrentPeriodEntity> {

}
