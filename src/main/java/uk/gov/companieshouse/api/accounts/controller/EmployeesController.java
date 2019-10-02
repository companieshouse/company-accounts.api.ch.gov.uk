package uk.gov.companieshouse.api.accounts.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.notes.employees.Employees;
import uk.gov.companieshouse.api.accounts.service.impl.EmployeesService;
import uk.gov.companieshouse.api.accounts.utility.ApiResponseMapper;
import uk.gov.companieshouse.api.accounts.utility.ErrorMapper;

@RestController
@RequestMapping(value = "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full/notes/employees", produces = MediaType.APPLICATION_JSON_VALUE)
public class EmployeesController {

    @Autowired
    public EmployeesController(EmployeesService employeesService, ErrorMapper errorMapper, ApiResponseMapper apiResponseMapper) {

        this.smallFullResourceController =
                new SmallFullResourceController<>(
                        employeesService,
                                SmallFullLinkType.EMPLOYEES_NOTE,
                                        errorMapper,
                                                apiResponseMapper);
    }

    private SmallFullResourceController<Employees> smallFullResourceController;


    @PostMapping
    public ResponseEntity create(@Valid @RequestBody Employees employees,
                                 BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {

        return smallFullResourceController.create(employees, bindingResult, companyAccountId, request);
    }
    
    @PutMapping
    public ResponseEntity update(@RequestBody @Valid Employees employees,
                                 BindingResult bindingResult,
                                 @PathVariable("companyAccountId") String companyAccountId,
                                 HttpServletRequest request) {

        return smallFullResourceController.update(employees, bindingResult, companyAccountId, request);
    }

    @GetMapping
    public ResponseEntity get(@PathVariable("companyAccountId") String companyAccountId,
                              HttpServletRequest request) {

        return smallFullResourceController.get(companyAccountId, request);
    }

    @DeleteMapping
    public ResponseEntity delete(@PathVariable("companyAccountId") String companyAccountsId,
                                 HttpServletRequest request) {

        return smallFullResourceController.delete(companyAccountsId, request);
    }
}
