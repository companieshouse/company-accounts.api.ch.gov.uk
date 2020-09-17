package uk.gov.companieshouse.api.accounts.validation;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsApproval;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Secretary;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.SecretaryService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class DirectorsApprovalValidator extends BaseValidator{

	private static final String APPROVAL_PATH = "$.directors_approval";
    private static final String APPROVAL_NAME = APPROVAL_PATH + ".name";

    private SecretaryService secretaryService;

    private DirectorValidator directorValidator;

    @Autowired
    public DirectorsApprovalValidator(CompanyService companyService,
    		SecretaryService secretaryService,
    		DirectorValidator directorValidator) {
		super(companyService);
		this.secretaryService = secretaryService;
		this.directorValidator = directorValidator;
	}

    public Errors validateApproval(DirectorsApproval directorsApproval, Transaction transaction,
                                   String companyAccountId, HttpServletRequest request) throws DataException {

        Errors errors = new Errors();

        ResponseObject<Secretary> secretaryResponseObject = secretaryService.find(companyAccountId, request);

        String secretary = Optional.of(secretaryResponseObject)
                .map(ResponseObject::getData)
                .map(Secretary::getName)
                .orElse(null);

        List<String> allNames = directorValidator.getValidDirectorNames(transaction, companyAccountId, request);

        if (secretary != null) {
            allNames.add(secretary);
        }

        if (!allNames.isEmpty() && !allNames.contains(directorsApproval.getName())) {

            addError(errors, mustMatchDirectorOrSecretary, APPROVAL_NAME);
        }

        return errors;
    }
}

