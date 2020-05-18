package uk.gov.companieshouse.api.accounts.validation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.Approval;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Director;
import uk.gov.companieshouse.api.accounts.model.validation.Error;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class ApprovalValidator extends BaseValidator {

    private static final String APPROVAL_PATH = "$.approval";
    private static final String DATE_PATH = APPROVAL_PATH + ".date";

    private static final String NAME_PATH = APPROVAL_PATH + ".name";

    @Autowired
    private DirectorService directorService;

    public Errors validateApproval(Approval approval, Transaction transaction,
                                   String companyAccountId, HttpServletRequest request) throws DataException {

        Errors errors = new Errors();

        SmallFull smallFull = (SmallFull) request
            .getAttribute(AttributeName.SMALLFULL.getValue());

        LocalDate periodEndDate = smallFull.getNextAccounts().getPeriodEndOn();
        LocalDate approvalDate = approval.getDate();

        if (approvalDate.isBefore(periodEndDate) || approvalDate.isEqual(periodEndDate)) {
            errors.addError(new Error(dateInvalid, DATE_PATH, LocationType.JSON_PATH.getValue(),
                ErrorType.VALIDATION.getType()));
        }

        ResponseObject<Director> directorsReportResponseObject = directorService.findAll(transaction, companyAccountId, request);

            Director[] directors = Optional.of(directorsReportResponseObject)
                    .map(ResponseObject::getDataForMultipleResources)
                    .orElse(null);

            List<String> servingDirectors = new ArrayList<>();

            if (directors != null) {

                Arrays.stream(directors).filter(d -> d.getResignationDate() == null).forEach(director -> servingDirectors.add(director.getName()));
            }

            if ( !servingDirectors.isEmpty() && (servingDirectors.stream().noneMatch(d -> d.equalsIgnoreCase(approval.getName())))) {
                    errors.addError(new Error(invalidValue, NAME_PATH, LocationType.JSON_PATH.getValue(), ErrorType.VALIDATION.getType()));
                }

        return errors;
    }
}