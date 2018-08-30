package uk.gov.companieshouse.api.accounts.service;

/**
 * Created by ltaylor on 30/08/2018.
 */
public interface TnepValidationService {

    boolean validate(String data, String location);

}
