package uk.gov.companieshouse.api.accounts.service.impl;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.LinkType;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CompanyAccountEntity;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.SmallFullService;
import uk.gov.companieshouse.api.accounts.transformer.GenericTransformer;

@Service
public class SmallFullServiceImpl extends
        AbstractServiceImpl<SmallFull, SmallFullEntity, CompanyAccountEntity> implements
        SmallFullService {


    @Autowired
    public SmallFullServiceImpl(
            @Qualifier("smallFullRepository") MongoRepository<SmallFullEntity, String> mongoRepository,
            @Qualifier("smallFullTransformer") GenericTransformer<SmallFull, SmallFullEntity> transformer,
            @Qualifier("companyAccountRepository") MongoRepository<CompanyAccountEntity, String> parentMongoRepository) {
        super(mongoRepository, transformer, parentMongoRepository);
    }

    @Override
    public void addKind(SmallFull rest) {
        rest.setKind(Kind.SMALL_FULL_ACCOUNT.getValue());
    }

    @Override
    public String getResourceName() {
        return ResourceName.SMALL_FULL.getName();
    }

    @Override
    public void addParentLink(String parentId, String link) {
        CompanyAccountEntity companyAccountEntity = getParentMongoRepository().findById(parentId)
                .orElse(null);
        CompanyAccountDataEntity companyAccountDataEntity = companyAccountEntity.getData();
        Map<String, String> map = companyAccountDataEntity.getLinks();
        map.put(LinkType.SMALL_FULL.getLink(), link);
        companyAccountDataEntity.setLinks(map);
        companyAccountEntity.setData(companyAccountDataEntity);
        getParentMongoRepository().insert(companyAccountEntity);
    }

}