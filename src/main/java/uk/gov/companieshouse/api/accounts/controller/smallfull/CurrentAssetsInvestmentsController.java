package uk.gov.companieshouse.api.accounts.controller.smallfull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.controller.BaseController;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.rest.notes.CurrentAssetsInvestments;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.resource.SmallFullResource;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentAssetsInvestmentsService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.accounts.utility.LoggingHelper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/notes/current-assets-investments", produces = MediaType.APPLICATION_JSON_VALUE)
public class CurrentAssetsInvestmentsController {

    @Autowired
    public CurrentAssetsInvestmentsController(CurrentAssetsInvestmentsService currentAssetsInvestmentsService,
                                                  SmallFullResource smallFullResource,
                                                  ErrorMapper errorMapper,
                                                  ApiResponseMapper apiResponseMapper) {
        this.baseController =
                    new BaseController<>(
                            currentAssetsInvestmentsService,
                            smallFullResource,
                            SmallFullLinkType.CURRENT_ASSETS_INVESTMENTS_NOTE,
                            errorMapper,
                            apiResponseMapper);
    }

    private BaseController<CurrentAssetsInvestments> baseController;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody CurrentAssetsInvestments currentAssetsInvestments,
                                     BindingResult bindingResult,
                                     @PathVariable("companyAccountId") String companyAccountId,
                                     HttpServletRequest request) {

            return baseController.create(currentAssetsInvestments, bindingResult, companyAccountId, request);
    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid CurrentAssetsInvestments currentAssetsInvestments,
                                     BindingResult bindingResult,
                                     @PathVariable("companyAccountId") String companyAccountId,
                                     HttpServletRequest request) {

            return baseController.update(currentAssetsInvestments, bindingResult, companyAccountId, request);
    }

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId,
                              HttpServletRequest request) {

            return baseController.get(companyAccountId, request);
    }

    @DeleteMapping
    public ResponseEntity delete(@PathVariable("companyAccountId") String companyAccountsId,
                                 HttpServletRequest request) {

            return baseController.delete(companyAccountsId, request);
    }
}

