package uk.gov.companieshouse.api.accounts.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.service.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.transformer.GenericTransformer;

@Service
public class CurrentPeriodServiceImpl extends
        AbstractServiceImpl<CurrentPeriod, CurrentPeriodEntity> implements CurrentPeriodService {

    @Autowired
    public CurrentPeriodServiceImpl(
            @Qualifier("currentPeriodRepository") MongoRepository<CurrentPeriodEntity, String> mongoRepository,
            @Qualifier("currentPeriodTransformer") GenericTransformer<CurrentPeriod, CurrentPeriodEntity> transformer) {
        super(mongoRepository, transformer);
    }

    @Override
    public void addLinks(CurrentPeriod rest) {

    }

    @Override
    public void addKind(CurrentPeriod rest) {
        rest.setKind("accounts#small-full#current-period");
    }
}
