package uk.gov.companieshouse.api.accounts.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.AccountingPolicies;
import uk.gov.companieshouse.api.accounts.resource.SmallFullResource;
import uk.gov.companieshouse.api.accounts.service.impl.AccountingPoliciesService;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/notes/accounting-policy", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountingPoliciesController {

    @Autowired
    public AccountingPoliciesController(AccountingPoliciesService accountingPoliciesService,
                                        SmallFullResource smallFullResource,
                                        ErrorMapper errorMapper,
                                        ApiResponseMapper apiResponseMapper) {

        this.baseController =
                new BaseController<>(
                        accountingPoliciesService,
                        smallFullResource,
                        SmallFullLinkType.ACCOUNTING_POLICY_NOTE,
                        errorMapper,
                        apiResponseMapper);
    }

    private BaseController<AccountingPolicies> baseController;


    @PostMapping
    public ResponseEntity create(@Valid @RequestBody AccountingPolicies accountingPolicies,
                                 BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {

        return baseController.create(accountingPolicies, bindingResult, companyAccountId, request);
    }

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId,
                              HttpServletRequest request) {

        return baseController.get(companyAccountId, request);
    }

    @PutMapping
    public ResponseEntity update(@Valid @RequestBody AccountingPolicies accountingPolicies,
                                 BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {

        return baseController.update(accountingPolicies, bindingResult, companyAccountId, request);
    }

}
