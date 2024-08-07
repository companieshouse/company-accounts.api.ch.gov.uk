package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.employees.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.employees.EmployeesDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.employees.EmployeesEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.employees.PreviousPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.employees.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.employees.Employees;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.employees.PreviousPeriod;

@Component
public class EmployeesTransformer implements NoteTransformer<Employees, EmployeesEntity> {

    @Override
    public EmployeesEntity transform(Employees rest) {
        EmployeesDataEntity employeesDataEntity = new EmployeesDataEntity();
        EmployeesEntity employeesEntity = new EmployeesEntity();
        
        BeanUtils.copyProperties(rest, employeesDataEntity);

        if (rest.getCurrentPeriod() != null) {
            CurrentPeriodEntity currentPeriodEntity = new CurrentPeriodEntity();
            BeanUtils.copyProperties(rest.getCurrentPeriod(), currentPeriodEntity);
            employeesDataEntity.setCurrentPeriodEntity(currentPeriodEntity);
        }

        if (rest.getPreviousPeriod() != null) {
            PreviousPeriodEntity previousPeriodEntity = new PreviousPeriodEntity();
            BeanUtils.copyProperties(rest.getPreviousPeriod(), previousPeriodEntity);
            employeesDataEntity.setPreviousPeriodEntity(previousPeriodEntity);
        }

        employeesEntity.setData(employeesDataEntity);

        return employeesEntity;
    }

    @Override
    public Employees transform(EmployeesEntity entity) {
        Employees employees = new Employees();
        EmployeesDataEntity employeesDataEntity;

        if (entity.getData() != null) {
            employeesDataEntity = entity.getData();
        } else {
            employeesDataEntity = new EmployeesDataEntity();
        }
        BeanUtils.copyProperties(employeesDataEntity, employees);

        if (employeesDataEntity.getCurrentPeriodEntity() != null) {
            CurrentPeriod currentPeriod = new CurrentPeriod();
            BeanUtils.copyProperties(employeesDataEntity.getCurrentPeriodEntity(), currentPeriod);
            employees.setCurrentPeriod(currentPeriod);
        }

        if (employeesDataEntity.getPreviousPeriodEntity() != null) {
            PreviousPeriod previousPeriod = new PreviousPeriod();
            BeanUtils.copyProperties(employeesDataEntity.getPreviousPeriodEntity(), previousPeriod);
            employees.setPreviousPeriod(previousPeriod);
        }
        return employees;
    }

    @Override
    public AccountingNoteType getAccountingNoteType() {
        return AccountingNoteType.SMALL_FULL_EMPLOYEES;
    }
}
