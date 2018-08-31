package uk.gov.companieshouse.api.accounts.service;


public interface TnepValidationService {

    boolean validate(String data, String location);

}
