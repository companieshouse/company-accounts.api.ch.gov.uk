package uk.gov.companieshouse.api.accounts.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

public class AccountsRepositoryImpl {

    @Autowired
    private MongoTemplate mongoTemplate;

    public AccountsRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
}