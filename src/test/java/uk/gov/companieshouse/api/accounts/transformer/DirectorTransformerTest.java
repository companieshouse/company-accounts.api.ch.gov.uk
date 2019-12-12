package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Director;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DirectorTransformerTest {

    private static final String NAME = "name";
    private static final LocalDate APPOINTMENT_DATE = LocalDate.of(2018, 1, 1);
    private static final LocalDate RESIGNATION_DATE = LocalDate.of(2019, 1, 1);

    private DirectorTransformer transformer = new DirectorTransformer();

    @Test
    @DisplayName("Transform rest object to entity")
    void restToEntity() {

        Director director = new Director();
        director.setName(NAME);
        director.setAppointmentDate(APPOINTMENT_DATE);
        director.setResignationDate(RESIGNATION_DATE);

        DirectorEntity directorEntity = transformer.transform(director);

        assertNotNull(directorEntity);
        assertNotNull(directorEntity.getData());
        assertEquals(NAME, directorEntity.getData().getName());
        assertEquals(APPOINTMENT_DATE, directorEntity.getData().getAppointmentDate());
        assertEquals(RESIGNATION_DATE, directorEntity.getData().getResignationDate());
    }

    @Test
    @DisplayName("Transform entity to rest object")
    void entityToRest() {

        Director director = transformer.transform(getDirectorEntity());

        assertNotNull(director);
        assertRestFieldsSet(director);
    }

    @Test
    @DisplayName("Transform entity array to rest object array")
    void entityArrayToRestArray() {

        DirectorEntity[] entities = new DirectorEntity[]{getDirectorEntity(), getDirectorEntity()};

        Director[] directors = transformer.transform(entities);

        assertNotNull(directors);
        assertEquals(2, directors.length);
        assertRestFieldsSet(directors[0]);
        assertRestFieldsSet(directors[1]);
    }

    private DirectorEntity getDirectorEntity() {

        DirectorDataEntity directorDataEntity = new DirectorDataEntity();
        directorDataEntity.setName(NAME);
        directorDataEntity.setAppointmentDate(APPOINTMENT_DATE);
        directorDataEntity.setResignationDate(RESIGNATION_DATE);

        DirectorEntity directorEntity = new DirectorEntity();
        directorEntity.setData(directorDataEntity);

        return directorEntity;
    }

    private void assertRestFieldsSet(Director director) {
        assertEquals(NAME, director.getName());
        assertEquals(APPOINTMENT_DATE, director.getAppointmentDate());
        assertEquals(RESIGNATION_DATE, director.getResignationDate());
    }
}
