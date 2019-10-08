package uk.gov.companieshouse.api.accounts.service.impl.smallfull;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.employees.EmployeesEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.employees.Employees;
import uk.gov.companieshouse.api.accounts.repository.EmployeesRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.impl.BaseService;
import uk.gov.companieshouse.api.accounts.service.impl.SmallFullService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transformer.EmployeesTransformer;
import uk.gov.companieshouse.api.accounts.utility.SelfLinkGenerator;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.smallfull.EmployeesValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class EmployeesService implements ResourceService<Employees> {

    private BaseService<Employees, EmployeesEntity, SmallFullLinkType> baseService;

    @Autowired
    public EmployeesService(EmployeesRepository repository,
                            EmployeesTransformer transformer,
                            EmployeesValidator validator,
                            KeyIdGenerator keyIdGenerator,
                            SmallFullService smallFullService) {

        this.baseService =
                new BaseService<>(
                        repository,
                        transformer,
                        validator,
                        keyIdGenerator,
                        smallFullService,
                        SmallFullLinkType.EMPLOYEES_NOTE,
                        Kind.EMPLOYEES_NOTE,
                        ResourceName.EMPLOYEES
                );
    }

    @Override
    public ResponseObject<Employees> create(Employees rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {

        String selfLink =
                SelfLinkGenerator.generateSelfLink(
                        transaction, companyAccountId, ResourceName.SMALL_FULL, true, ResourceName.EMPLOYEES);

        return baseService.create(rest, transaction, companyAccountId, request, selfLink);
    }

    @Override
    public ResponseObject<Employees> update(Employees rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {

        String selfLink =
                SelfLinkGenerator.generateSelfLink(
                        transaction, companyAccountId, ResourceName.SMALL_FULL, true, ResourceName.EMPLOYEES);

        return baseService.update(rest, transaction, companyAccountId, request, selfLink);
    }

    @Override
    public ResponseObject<Employees> find(String companyAccountsId, HttpServletRequest request) throws DataException {

        return baseService.find(companyAccountsId);
    }

    @Override
    public ResponseObject<Employees> delete(String companyAccountsId, HttpServletRequest request) throws DataException {

        return baseService.delete(companyAccountsId, request);
    }
}
