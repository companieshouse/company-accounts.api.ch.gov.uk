package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Director;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsApproval;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Secretary;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorService;
import uk.gov.companieshouse.api.accounts.service.impl.SecretaryService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DirectorsApprovalValidator extends BaseValidator{

    private static final String APPROVAL_PATH = "$.directors_approval";
    private static final String APPROVAL_NAME = APPROVAL_PATH + ".name";

    @Autowired
    private SecretaryService secretaryService;

    @Autowired
    private DirectorService directorService;

    public Errors validateApproval(DirectorsApproval directorsApproval, Transaction transaction,
                                   String companyAccountId, HttpServletRequest request) throws DataException {

        Errors errors = new Errors();

        ResponseObject<Secretary> secretaryResponseObject = secretaryService.find(companyAccountId, request);

        String secretary = Optional.of(secretaryResponseObject)
                .map(ResponseObject::getData)
                .map(Secretary::getName)
                .orElse(null);

        ResponseObject<Director> directorsReportResponseObject = directorService.findAll(transaction, companyAccountId, request);

        Director[] directors = Optional.of(directorsReportResponseObject)
                .map(ResponseObject::getDataForMultipleResources)
                .orElse(null);


        List<String> allNames = new ArrayList<>();

        if (directors != null) {

            for(Director director : directors) {
                if (isValidDirector(director)) {
                    allNames.add(director.getName());
                }
            }
        }

        if (secretary != null) {
            allNames.add(secretary);
        }

        if (!allNames.isEmpty() && !allNames.contains(directorsApproval.getName())) {

            addError(errors, mustMatchDirectorOrSecretary, APPROVAL_NAME);
        }


        return errors;
    }

    private boolean isValidDirector(Director director) {

        return director.getResignationDate() == null
                || (director.getAppointmentDate() != null
                && director.getAppointmentDate().isAfter(director.getResignationDate()));
    }
}

