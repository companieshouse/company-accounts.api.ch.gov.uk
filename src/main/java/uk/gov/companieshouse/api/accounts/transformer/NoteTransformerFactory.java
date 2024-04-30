package uk.gov.companieshouse.api.accounts.transformer;

import java.util.EnumMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.exception.MissingInfrastructureException;
import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Note;

@Component
public class NoteTransformerFactory<N extends Note, E extends NoteEntity> {

    private final EnumMap<AccountingNoteType, NoteTransformer<N, E>> transformerMap =
            new EnumMap<>(AccountingNoteType.class);

    @Autowired
    public NoteTransformerFactory(List<NoteTransformer<N, E>> noteTransformers) {
        noteTransformers.forEach(transformer -> transformerMap.put(transformer.getAccountingNoteType(), transformer));
    }

    public NoteTransformer<N, E> getTransformer(AccountingNoteType type) {
        NoteTransformer<N, E> transformer = transformerMap.get(type);

        if (transformer == null) {
            throw new MissingInfrastructureException("No transformer for AccountingNoteType: " + type.toString());
        }
        return transformer;
    }
}
