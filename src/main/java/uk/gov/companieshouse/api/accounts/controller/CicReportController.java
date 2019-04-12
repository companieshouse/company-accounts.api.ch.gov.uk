package uk.gov.companieshouse.api.accounts.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.CicReport;
import uk.gov.companieshouse.api.accounts.service.impl.CicReportService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.LoggingHelper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/cic-report", produces = MediaType.APPLICATION_JSON_VALUE)
public class CicReportController {

    @Autowired
    private CicReportService cicReportService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody CicReport cicReport,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {

        Transaction transaction = (Transaction) request
                .getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<CicReport> responseObject =
                    cicReportService.create(cicReport, transaction, companyAccountId, request);

            return apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                    responseObject.getErrors());

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to create cic report resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId,
                              HttpServletRequest request) {

        try {
            ResponseObject<CicReport> responseObject =
                    cicReportService.find(companyAccountId, request);

            return apiResponseMapper.mapGetResponse(responseObject.getData(), request);

        } catch (DataException ex) {

            Transaction transaction = (Transaction) request
                    .getAttribute(AttributeName.TRANSACTION.getValue());

            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to retrieve cic report resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @DeleteMapping
    public ResponseEntity delete(@PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {

        try {
            ResponseObject<CicReport> responseObject =
                    cicReportService.delete(companyAccountId, request);

            return apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(),
                    responseObject.getErrors());

        } catch (DataException ex) {

            Transaction transaction = (Transaction) request
                    .getAttribute(AttributeName.TRANSACTION.getValue());

            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to delete cic report resource", ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }
}
