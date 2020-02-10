package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.enumeration.NoteType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.LinkType;
import uk.gov.companieshouse.api.accounts.model.entity.NoteEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Note;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.parent.ParentResourceFactory;
import uk.gov.companieshouse.api.accounts.repository.NoteRepository;
import uk.gov.companieshouse.api.accounts.service.NoteService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.NoteTransformerFactory;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.NoteValidatorFactory;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private KeyIdGenerator keyIdGenerator;

    @Autowired
    private NoteValidatorFactory<Note> validatorFactory;

    @Autowired
    private NoteTransformerFactory<Note, NoteEntity> transformerFactory;

    @Autowired
    private NoteRepository repository;

    @Autowired
    private ParentResourceFactory<LinkType> parentResourceFactory;

    @Override
    public ResponseObject<Note> create(Note note, AccountingNoteType type, Transaction transaction,
            String companyAccountId, HttpServletRequest request) throws DataException {
        
        if (type.isExplicitlyValidated()) {
            Errors errors = validatorFactory.getValidator(type).validateSubmission(note, transaction, companyAccountId, request);
            if (errors.hasErrors()) {
                return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
            }
        }

        setMetadataOnRest(note, request.getRequestURI(), type);

        NoteEntity entity = transformerFactory.getTransformer(type).transform(note);
        entity.setId(generateID(companyAccountId, type.getNoteType()));

        try {
            repository.insert(entity);
        } catch (DuplicateKeyException e) {
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        parentResourceFactory.getParentResource(type.getAccountType())
                .addLink(companyAccountId, type.getLinkType(), getSelfLink(note), request);

        return new ResponseObject<>(ResponseStatus.CREATED, note);
    }

    @Override
    public ResponseObject<Note> update(Note note, AccountingNoteType type, Transaction transaction,
            String companyAccountId, HttpServletRequest request) throws DataException {

        if (type.isExplicitlyValidated()) {
            Errors errors = validatorFactory.getValidator(type).validateSubmission(note, transaction, companyAccountId, request);
            if (errors.hasErrors()) {
                return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
            }
        }

        setMetadataOnRest(note, request.getRequestURI(), type);

        NoteEntity entity = transformerFactory.getTransformer(type).transform(note);
        entity.setId(generateID(companyAccountId, type.getNoteType()));

        try {
            repository.save(entity);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, note);
    }

    @Override
    public ResponseObject<Note> find(AccountingNoteType type, String companyAccountId)
            throws DataException {

        NoteEntity entity;

        try {
            entity = repository
                    .findById(generateID(companyAccountId, type.getNoteType()))
                            .orElse(null);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND,
                transformerFactory.getTransformer(type).transform(entity));
    }

    @Override
    public ResponseObject<Note> delete(AccountingNoteType type, String companyAccountId,
            HttpServletRequest request) throws DataException {

        String id = generateID(companyAccountId, type.getNoteType());

        try {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                parentResourceFactory.getParentResource(type.getAccountType())
                        .removeLink(companyAccountId, type.getLinkType(), request);
                return new ResponseObject<>(ResponseStatus.UPDATED);
            } else {
                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {
            throw new DataException(e);
        }
    }

    private String generateID(String companyAccountId, NoteType noteType) {
        
        return keyIdGenerator.generate(companyAccountId + "-" + noteType.getType());
    }

    private void setMetadataOnRest(Note note, String selfLink, AccountingNoteType type) {

        note.setLinks(createLinks(selfLink));
        note.setEtag(GenerateEtagUtil.generateEtag());
        note.setKind(type.getKind());
    }

    private Map<String, String> createLinks(String selfLink) {

        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), selfLink);
        return map;
    }

    private String getSelfLink(Note note) {
        
        return note.getLinks().get(BasicLinkType.SELF.getLink());
    }
}
