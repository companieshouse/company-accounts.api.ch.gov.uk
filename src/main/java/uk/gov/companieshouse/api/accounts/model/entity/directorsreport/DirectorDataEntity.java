package uk.gov.companieshouse.api.accounts.model.entity.directorsreport;

import java.time.LocalDate;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.accounts.model.entity.BaseDataEntity;

public class DirectorDataEntity extends BaseDataEntity {

    @Field("name")
    private String name;

    @Field("appointment_date")
    private LocalDate appointmentDate;

    @Field("resignation_date")
    private LocalDate resignationDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalDate getResignationDate() {
        return resignationDate;
    }

    public void setResignationDate(LocalDate resignationDate) {
        this.resignationDate = resignationDate;
    }

    @Override
    public String toString() {
        return "DirectorDataEntity{" +
                "name=" + name +
                ", appointmentDate=" + appointmentDate +
                ", resignationDate=" + resignationDate +
                "}";
    }
}
