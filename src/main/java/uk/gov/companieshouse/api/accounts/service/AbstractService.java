package uk.gov.companieshouse.api.accounts.service;

import java.security.NoSuchAlgorithmException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

@Service
public interface AbstractService<C extends RestObject, E extends BaseEntity> {

    public C save(C rest, String companyAccountId) throws NoSuchAlgorithmException;

    public void addEtag(C rest);

    public void addLinks(C rest);

    public void addKind(C rest);

    public String getResourceName();

    public String generateID(String value) throws NoSuchAlgorithmException;
}