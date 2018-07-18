package uk.gov.companieshouse.api.accounts.service;

import java.util.Map;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.model.entity.BaseEntity;
import uk.gov.companieshouse.api.accounts.model.rest.RestObject;

@Service
public interface AbstractService<C extends RestObject, E extends BaseEntity, K> {

    public C save(C rest);

    public void addEtag(C rest);

    public void addLinks(C rest);

    public void addKind(C rest);

}