package uk.gov.companieshouse.api.accounts.validation;

import java.time.LocalDate;
import javax.validation.Valid;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.rest.Approval;

@Component
public class ApprovalValidator extends BaseValidator {



    public Errors validateApproval(@Valid Approval approval) {

        Errors errors = new Errors();
        LocalDate currentPeriod;


        return errors;
    }

}
