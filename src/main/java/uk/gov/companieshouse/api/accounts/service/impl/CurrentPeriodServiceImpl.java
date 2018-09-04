package uk.gov.companieshouse.api.accounts.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.service.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
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
    public void addKind(CurrentPeriod rest) {
        rest.setKind(Kind.CURRENT_PERIOD.getValue());
    }

    @Override
    public String getResourceName() {
        return "current-period";
    }

    @Override
    public ResponseObject<CurrentPeriod> findById(String id) {
        CurrentPeriodEntity currentPeriodEntity = getMongoRepository().findById(id).orElse(null);
        if (currentPeriodEntity == null){
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }
        CurrentPeriod currentPeriod = getGenericTransformer().transform(currentPeriodEntity);
        return new ResponseObject<>(ResponseStatus.FOUND, currentPeriod);
    }
}
