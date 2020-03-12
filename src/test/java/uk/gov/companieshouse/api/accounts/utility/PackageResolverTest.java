package uk.gov.companieshouse.api.accounts.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PackageResolverTest {

    private PackageResolver packageResolver = new PackageResolver();

    private static final String ACCOUNT_TYPE = "account-type";

    private static final String ACCOUNT_TYPE_PACKAGE = "accounttype";

    private static final String NOTE_TYPE = "note-type";

    private static final String NOTE_TYPE_PACKAGE = "notetype";

    private static final String NOTE_TYPE_CLASS = "NoteType";

    @Test
    @DisplayName("Get note package")
    void getNotePackage() {

        String returned = packageResolver.getNotePackage(ACCOUNT_TYPE, NOTE_TYPE);

        assertEquals("." + ACCOUNT_TYPE_PACKAGE + ".notes." + NOTE_TYPE_PACKAGE + "." + NOTE_TYPE_CLASS, returned);
    }
}
