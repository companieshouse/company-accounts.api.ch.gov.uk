package uk.gov.companieshouse.api.accounts.service.impl;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.service.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.GenericTransformer;

@Service
public class CurrentPeriodServiceImpl extends
        AbstractServiceImpl<CurrentPeriod, CurrentPeriodEntity, SmallFullEntity> implements CurrentPeriodService {

    @Autowired
    public CurrentPeriodServiceImpl(
            @Qualifier("currentPeriodRepository") MongoRepository<CurrentPeriodEntity, String> mongoRepository,
            @Qualifier("currentPeriodTransformer") GenericTransformer<CurrentPeriod, CurrentPeriodEntity> transformer,
            @Qualifier("smallFullRepository") MongoRepository<SmallFullEntity, String> parentMongoRepository) {
        super(mongoRepository, transformer, parentMongoRepository);
    }

    @Override
    public void addParentLink(String parentId, String link) {
        SmallFullEntity smallFullEntity = getParentMongoRepository().findById(generateID(parentId, ResourceName.SMALL_FULL.getName())).orElse(null);
        SmallFullDataEntity smallFullDataEntity = smallFullEntity.getData();
        Map<String, String> map = smallFullDataEntity.getLinks();
        map.put(LinkType.CURRENT_PERIOD.getLink(), link);
        smallFullDataEntity.setLinks(map);
        smallFullEntity.setData(smallFullDataEntity);
        getParentMongoRepository().save(smallFullEntity);
    }

    @Override
    public String createSelfLink(Transaction transaction, String companyAccountId) {
        return transaction.getLinks().get(LinkType.SELF.getLink()) + "/company-account/" + companyAccountId + "/small-full/" + getResourceName();
    }

    @Override
    public void addKind(CurrentPeriod rest) {
        rest.setKind(Kind.CURRENT_PERIOD.getValue());
    }

    @Override
    public String getResourceName() {
        return ResourceName.CURRENT_PERIOD.getName();
    }
}
