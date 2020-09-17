package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Director;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.DirectorService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class DirectorValidator extends BaseValidator {

    private DirectorService directorService;

	@Autowired
    public DirectorValidator(CompanyService companyService, DirectorService directorService) {
		super(companyService);
		this.directorService = directorService;
	}

    public List<String> getValidDirectorNames(Transaction transaction,
                                              String companyAccountId, HttpServletRequest request) throws DataException {

        ResponseObject<Director> directorsReportResponseObject = directorService.findAll(transaction, companyAccountId, request);

        Director[] directors = Optional.of(directorsReportResponseObject)
                .map(ResponseObject::getDataForMultipleResources)
                .orElse(null);

        List<String> allNames = new ArrayList<>();

        if (directors != null) {
            for (Director director : directors) {
                if (isValidDirector(director)) {
                    allNames.add(director.getName());
                }
            }
        }

        return allNames;
    }

    private boolean isValidDirector(Director director) {

        return director.getResignationDate() == null
                || (director.getAppointmentDate() != null
                && director.getAppointmentDate().isAfter(director.getResignationDate()));
    }
}