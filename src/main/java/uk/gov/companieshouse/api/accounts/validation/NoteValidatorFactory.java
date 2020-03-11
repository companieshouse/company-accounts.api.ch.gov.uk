package uk.gov.companieshouse.api.accounts.validation;

import java.util.EnumMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.exception.MissingInfrastructureException;
import uk.gov.companieshouse.api.accounts.model.rest.Note;

@Component
public class NoteValidatorFactory<N extends Note> {

    private final EnumMap<AccountingNoteType, NoteValidator<N>> validatorMap =
            new EnumMap<>(AccountingNoteType.class);

    @Autowired
    public NoteValidatorFactory(List<NoteValidator<N>> noteValidators) {

        noteValidators.forEach(validator ->
                validatorMap.put(validator.getAccountingNoteType(),
                        validator));
    }

    public NoteValidator<N> getValidator(AccountingNoteType type) {

        NoteValidator<N> validator = validatorMap.get(type);

        if (validator == null) {
            throw new MissingInfrastructureException("No validator for AccountingNoteType: " + type.toString());
        }
        return validator;
    }
}
