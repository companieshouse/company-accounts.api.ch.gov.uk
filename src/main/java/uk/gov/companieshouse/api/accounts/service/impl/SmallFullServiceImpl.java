package uk.gov.companieshouse.api.accounts.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.SmallFullService;
import uk.gov.companieshouse.api.accounts.transformer.GenericTransformer;

@Service
public class SmallFullServiceImpl extends
        AbstractServiceImpl<SmallFull, SmallFullEntity> implements SmallFullService {

    @Autowired
    public SmallFullServiceImpl(
            @Qualifier("smallFullRepository") MongoRepository<SmallFullEntity, String> mongoRepository,
            @Qualifier("smallFullTransformer") GenericTransformer<SmallFull, SmallFullEntity> transformer) {
        super(mongoRepository, transformer);
    }

    @Override
    public void addLinks(SmallFull rest) {

    }

    @Override
    public void addKind(SmallFull rest) {
        rest.setKind(Kind.SMALL_FULL_ACCOUNT.getValue());
    }

    @Override
    public String getResourceName() {
        return "small-full";
    }


}

