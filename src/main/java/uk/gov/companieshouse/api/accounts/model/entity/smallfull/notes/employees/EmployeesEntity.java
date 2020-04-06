package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.employees;

import java.util.Objects;
import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;

public class EmployeesEntity extends NoteEntity {

    private EmployeesDataEntity data;

    public EmployeesDataEntity getData() {
        return data;
    }

    public void setData(EmployeesDataEntity data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EmployeesEntity)) {
            return false;
        }
        EmployeesEntity other = (EmployeesEntity) obj;
        return Objects.equals(data, other.data);
    }

    @Override
    public String toString() {
        return "EmployeesEntity [data=" + data + "]";
    }
    
}
