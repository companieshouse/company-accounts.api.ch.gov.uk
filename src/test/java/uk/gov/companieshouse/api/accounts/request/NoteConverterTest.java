package uk.gov.companieshouse.api.accounts.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.enumeration.NoteType;
import uk.gov.companieshouse.api.accounts.exception.InvalidPathParameterException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NoteConverterTest {

    private NoteType noteType = NoteType.OFF_BALANCE_SHEET_ARRANGEMENTS;
    private String otherType = "otherType";
    private Map<String, NoteType> ACCOUNT_TYPE_MAP = new HashMap<>();

    private NoteConverter converter;

    @BeforeEach
    private void setup() {

        ACCOUNT_TYPE_MAP.put(noteType.getType(), noteType);
        converter = new NoteConverter();
    }

    @Test
    @DisplayName("Get note type for the converter with the value from the map")
    void getNoteTypeForTheConverterSuccess() {

        assertEquals(noteType,
                ACCOUNT_TYPE_MAP.get(noteType.getType()));
        converter.setAsText(noteType.getType());
        assertEquals(noteType, converter.getValue());
    }

    @Test
    @DisplayName("Get note type for the converter throws invalid parameter exception")
    void getNoteTypeForTheConverterThrowsInvalidParameterException() {

        NoteType type = ACCOUNT_TYPE_MAP.get(otherType);
        converter.setValue(type);

        assertThrows(InvalidPathParameterException.class,
                () -> converter.setAsText(otherType));
        assertNull(converter.getValue());
    }
}