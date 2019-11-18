package uk.gov.companieshouse.api.accounts.controller;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

import java.util.Map.Entry;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;
import uk.gov.companieshouse.api.accounts.enumeration.AccountsResource;
import uk.gov.companieshouse.api.accounts.enumeration.Period;
import uk.gov.companieshouse.api.accounts.enumeration.Resource;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.model.ControllerPathProperties;
import uk.gov.companieshouse.api.accounts.model.rest.Rest;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.parent.ParentResourceFactory;
import uk.gov.companieshouse.api.accounts.request.AccountTypeConverter;
import uk.gov.companieshouse.api.accounts.request.AccountsResourceConverter;
import uk.gov.companieshouse.api.accounts.request.PeriodConverter;
import uk.gov.companieshouse.api.accounts.request.ResourceConverter;
import uk.gov.companieshouse.api.accounts.service.AccountsResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.accounts.utility.LoggingHelper;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = {"${controller.paths.smallfull.notes}"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountsResourceController {

    @Autowired
    private AccountTypeConverter accountTypeConverter;

    @Autowired
    private ResourceConverter resourceConverter;

    @Autowired
    private PeriodConverter periodConverter;

    @Autowired
    private AccountsResourceConverter accountsResourceConverter;

    @Autowired
    private ParentResourceFactory<LinkType> parentResourceFactory;

    @Autowired
    private ErrorMapper errorMapper;
    
    @Autowired
    private AccountsResourceService accountsResourceService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @Autowired
    private ControllerPathProperties controllerPathProperties;

    private static final Logger STRUCTURED_LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @InitBinder
    protected void initBinder(final WebDataBinder webdataBinder) {
        webdataBinder.registerCustomEditor(AccountType.class, accountTypeConverter);
        webdataBinder.registerCustomEditor(Resource.class, resourceConverter);
        webdataBinder.registerCustomEditor(Period.class, periodConverter);
    }

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody Rest data,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 @PathVariable("accountType") AccountType accountType,
                                 @PathVariable("resource") Resource resource,
                                 @PathVariable(value = "period", required = false) Period period,
                                 BindingResult bindingResult,
                                 HttpServletRequest request) {

        AccountsResource accountsResource = accountsResourceConverter.getAccountsResource(accountType, resource, period);

        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction =
                (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<Rest> response =
                    accountsResourceService.create(data, accountsResource, transaction, companyAccountId, request);

            return apiResponseMapper.map(response.getStatus(), response.getData(), response.getErrors());

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to create resource: " + accountsResource.getLinkType().getLink(), ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId,
                              @PathVariable("accountType") AccountType accountType,
                              @PathVariable("resource") Resource resource,
                              @PathVariable(value = "period", required = false) Period period,
                              HttpServletRequest request) {

        AccountsResource accountsResource = accountsResourceConverter.getAccountsResource(accountType, resource, period);

        Transaction transaction = (Transaction) request
                .getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<Rest> response = accountsResourceService.find(accountsResource, companyAccountId);

            return apiResponseMapper.mapGetResponse(response.getData(), request);

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to retrieve resource: " + accountsResource.getLinkType().getLink(), ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @PutMapping
    public ResponseEntity update(@Valid @RequestBody Rest data,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 @PathVariable("accountType") AccountType accountType,
                                 @PathVariable("resource") Resource resource,
                                 @PathVariable(value = "period", required = false) Period period,
                                 BindingResult bindingResult,
                                 HttpServletRequest request) {

        AccountsResource accountsResource = accountsResourceConverter.getAccountsResource(accountType, resource, period);

        if (!parentResourceFactory.getParentResource(accountsResource.getParent())
                .childExists(request, accountsResource.getLinkType())) {

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (bindingResult.hasErrors()) {
            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {
            ResponseObject<Rest> response =
                    accountsResourceService.update(data, accountsResource, transaction, companyAccountId, request);

            return apiResponseMapper
                    .map(response.getStatus(), response.getData(), response.getErrors());

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to update resource: " + accountsResource.getLinkType().getLink(), ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @DeleteMapping
    public ResponseEntity delete(@PathVariable("companyAccountId") String companyAccountsId,
                                 @PathVariable("accountType") AccountType accountType,
                                 @PathVariable("resource") Resource resource,
                                 @PathVariable(value = "period", required = false) Period period,
                                 HttpServletRequest request) {

        AccountsResource accountsResource = accountsResourceConverter.getAccountsResource(accountType, resource, period);

        Transaction transaction = (Transaction) request
                .getAttribute(AttributeName.TRANSACTION.getValue());

        try {

            ResponseObject<Rest> response = accountsResourceService.delete(accountsResource, companyAccountsId, request);

            return apiResponseMapper.map(response.getStatus(), response.getData(), response.getErrors());
        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountsId, transaction,
                    "Failed to delete resource: " + accountsResource.getLinkType().getLink(), ex, request);
            return apiResponseMapper.getErrorResponse();
        }
    }

    @PostConstruct
    void init () {

        String[] requestMappings = AnnotationUtils
                .findAnnotation(this.getClass(), RequestMapping.class).value();

        for (Entry<String, String> entry : controllerPathProperties.getPaths().entrySet()) {

            boolean matched = false;

            for (String requestMapping : requestMappings) {

                if (requestMapping.equals("${controller.paths." + entry.getKey() + "}")) {

                    matched = true;
                    break;
                }
            }

            if (!matched) {

                STRUCTURED_LOGGER.error(
                        "No RequestMapping value for property: ${controller.paths." + entry.getKey() + "} in AccountsResourceController; "
                        + "This must be added for requests of this type to route to this controller");
            }
        }
    }
}