package uk.gov.companieshouse.api.accounts.controller.smallfull;

import org.springframework.beans.factory.annotation.Autowired;
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
import uk.gov.companieshouse.api.accounts.controller.BaseController;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.notes.stocks.Stocks;
import uk.gov.companieshouse.api.accounts.resource.SmallFullResource;
import uk.gov.companieshouse.api.accounts.service.impl.smallfull.StocksService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/notes/stocks", produces = MediaType.APPLICATION_JSON_VALUE)
public class StocksController {

    @Autowired
    public StocksController(StocksService stocksService,
                            SmallFullResource smallFullResource,
                            ErrorMapper errorMapper,
                            ApiResponseMapper apiResponseMapper) {

        this.baseController =
                new BaseController<>(
                        stocksService,
                        smallFullResource,
                        SmallFullLinkType.STOCKS_NOTE,
                        errorMapper,
                        apiResponseMapper
                );
    }

    private BaseController<Stocks> baseController;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody Stocks stocks,
                                 BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {

        return baseController.create(stocks, bindingResult, companyAccountId, request);
    }

    @PutMapping
    public ResponseEntity update(@Valid @RequestBody Stocks stocks,
                                 BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {

        return baseController.update(stocks, bindingResult, companyAccountId, request);
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
