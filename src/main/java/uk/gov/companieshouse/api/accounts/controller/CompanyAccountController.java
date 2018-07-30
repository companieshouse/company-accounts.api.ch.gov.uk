package uk.gov.companieshouse.api.accounts.controller;

<<<<<<<HEAD
    =======
import java.security.NoSuchAlgorithmException;
>>>>>>>origin/develop
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

@RestController
public class CompanyAccountController {

  @Autowired
  private CompanyAccountService companyAccountService;

  @PostMapping(value = "/transactions/{transactionId}/company-accounts",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity createCompanyAccount(HttpServletRequest request,
      @PathVariable String transactionId, @Valid @RequestBody CompanyAccount companyAccount
  )
      throws NoSuchAlgorithmException {
    Transaction transaction = (Transaction) request.getSession()
        .getAttribute(AttributeName.TRANSACTION.getValue());
    CompanyAccount result = companyAccountService
        .save(companyAccount, transaction.getCompanyNumber());
    return ResponseEntity.status(HttpStatus.CREATED).body(result);
  }
}
