package uk.gov.companieshouse.api.accounts.controller;

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
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.enumeration.NoteType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.model.ControllerPathProperties;
import uk.gov.companieshouse.api.accounts.model.rest.Note;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.parent.ParentResourceFactory;
import uk.gov.companieshouse.api.accounts.request.AccountTypeConverter;
import uk.gov.companieshouse.api.accounts.request.AccountsNoteConverter;
import uk.gov.companieshouse.api.accounts.request.NoteConverter;
import uk.gov.companieshouse.api.accounts.service.NoteService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;
import uk.gov.companieshouse.api.accounts.utility.LoggingHelper;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

import static uk.gov.companieshouse.api.accounts.CompanyAccountsApplication.APPLICATION_NAME_SPACE;

@RestController
@RequestMapping(value = {"${controller.paths.notes.smallfull}"}, produces = MediaType.APPLICATION_JSON_VALUE)
public class NoteController {

    @Autowired
    private AccountTypeConverter accountTypeConverter;

    @Autowired
    private NoteConverter noteConverter;

    @Autowired
    private AccountsNoteConverter accountsNoteConverter;

    @Autowired
    private ParentResourceFactory<LinkType> parentResourceFactory;

    @Autowired
    private ErrorMapper errorMapper;

    @Autowired
    private NoteService noteService;

    @Autowired
    private ApiResponseMapper apiResponseMapper;

    @Autowired
    private ControllerPathProperties controllerPathProperties;

    private static final Logger STRUCTURED_LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @InitBinder
    protected void initBinder(final WebDataBinder webDataBinder) {

        webDataBinder.registerCustomEditor(AccountType.class, accountTypeConverter);
        webDataBinder.registerCustomEditor(NoteType.class, noteConverter);

    }

    @PostMapping
    public ResponseEntity create(@Valid @RequestBody Note data,
                                 BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 @PathVariable("accountType") AccountType accountType,
                                 @PathVariable("noteType") NoteType noteType,

                                 HttpServletRequest request) {

        AccountingNoteType accountingNoteType = accountsNoteConverter.getAccountsNote(accountType, noteType);

        if (bindingResult.hasErrors()) {

            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);
            return new ResponseEntity(errors, HttpStatus.BAD_REQUEST);

        }

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {

            ResponseObject<Note> responseObject = noteService.create(data, accountingNoteType, transaction, companyAccountId, request);

            return apiResponseMapper.map(responseObject.getStatus(), responseObject.getData(), responseObject.getErrors());

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction, "Failed to create resource: " + accountingNoteType.getLinkType().getLink(), ex, request);

            return apiResponseMapper.getErrorResponse();

        }

    }

    @PutMapping
    public ResponseEntity update(@RequestBody @Valid Note data,
                                 BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 @PathVariable("accountType") AccountType accountType,
                                 @PathVariable("noteType") NoteType noteType,
                                 HttpServletRequest request) {

        AccountingNoteType accountingNoteType = accountsNoteConverter.getAccountsNote(accountType, noteType);

        if (!parentResourceFactory.getParentResource(accountType).childExists(request, accountingNoteType.getLinkType())) {

            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        }

        if (bindingResult.hasErrors()) {

            Errors errors = errorMapper.mapBindingResultErrorsToErrorModel(bindingResult);
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);

        }

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {

            ResponseObject<Note> response = noteService.update(data, accountingNoteType, transaction, companyAccountId, request);

            return apiResponseMapper.map(response.getStatus(), response.getData(), response.getErrors());

        } catch (DataException e) {

            LoggingHelper.logException(companyAccountId, transaction, "Failed to update resource: " + accountingNoteType.getLinkType().getLink(), e, request);
            return apiResponseMapper.getErrorResponse();

        }

    }

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId,
                              @PathVariable("accountType") AccountType accountType,
                              @PathVariable("noteType") NoteType noteType,
                              HttpServletRequest request) {

        AccountingNoteType accountingNoteType = accountsNoteConverter.getAccountsNote(accountType, noteType);

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {

            ResponseObject<Note> response = noteService.find(accountingNoteType, companyAccountId);
            return apiResponseMapper.mapGetResponse(response.getData(), request);

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction, "Failed to retrieve resource: " + accountingNoteType.getLinkType().getLink(), ex, request);
            return apiResponseMapper.getErrorResponse();
        }

    }

    @DeleteMapping
    public ResponseEntity delete(@PathVariable("companyAccountId") String companyAccountId,
                                 @PathVariable("accountType") AccountType accountType,
                                 @PathVariable("noteType") NoteType noteType,
                                 HttpServletRequest request) {

        AccountingNoteType accountingNoteType = accountsNoteConverter.getAccountsNote(accountType, noteType);

        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        try {

            ResponseObject<Note> response = noteService.delete(accountingNoteType, companyAccountId, request);
            return apiResponseMapper.map(response.getStatus(), response.getData(), response.getErrors());

        } catch (DataException ex) {

            LoggingHelper.logException(companyAccountId, transaction, "Failed to delete resource: " + accountingNoteType.getLinkType().getLink(), ex, request);
            return apiResponseMapper.getErrorResponse();

        }

    }


    @PostConstruct
    void init() {

        String[] requestMappings = AnnotationUtils
                .findAnnotation(this.getClass(), RequestMapping.class).value();

        for (Map.Entry<String, String> entry : controllerPathProperties.getPaths().entrySet()) {

            boolean matched = false;

            for (String requestMapping : requestMappings) {

                if (requestMapping.equals("${controller.paths." + entry.getKey() + "}")) {

                    matched = true;
                    break;
                }
            }

            if (!matched) {

                STRUCTURED_LOGGER.error(
                        "No RequestMapping value for property: ${controller.paths." + entry.getKey() + "} in NoteController; "
                                + "This must be added for requests of this type to route to this controller");
            }
        }
    }

}
