package uk.gov.companieshouse.api.accounts.request;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.NoteType;
import uk.gov.companieshouse.api.accounts.exception.InvalidPathParameterException;

import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class NoteConverter extends PropertyEditorSupport {

    private static final Map<String, NoteType> NOTES_MAP = new HashMap<>();

    NoteConverter() {
        Arrays.stream(NoteType.values()).forEach(noteType -> NOTES_MAP.put(noteType.getType(), noteType));
    }

    @Override
    public void setAsText(final String type) {
        NoteType noteType = NOTES_MAP.get(type);

        if (noteType == null) {
            throw new InvalidPathParameterException("No resource found for: " + type);
        }

        setValue(noteType);
    }
}
