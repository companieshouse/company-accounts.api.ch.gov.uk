package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
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
import uk.gov.companieshouse.api.accounts.repository.AccountsNoteRepositoryFactory;
import uk.gov.companieshouse.api.accounts.service.NoteService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.NoteTransformerFactory;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.NoteValidatorFactory;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private KeyIdGenerator keyIdGenerator;

    @Autowired
    private NoteValidatorFactory<Note> validatorFactory;

    @Autowired
    private NoteTransformerFactory<Note, NoteEntity> transformerFactory;

    @Autowired
    private AccountsNoteRepositoryFactory<NoteEntity> repositoryFactory;

    @Autowired
    private ParentResourceFactory<LinkType> parentResourceFactory;

    @Override
    public ResponseObject<Note> create(Note note,
                                       AccountingNoteType accountingNoteType,
                                       Transaction transaction,
                                       String companyAccountId,
                                       HttpServletRequest request) throws DataException {
        if (accountingNoteType.isExplicitlyValidated()) {
            Errors errors = validatorFactory.getValidator(accountingNoteType)
                    .validateSubmission(note, transaction, companyAccountId, request);
            if (errors.hasErrors()) {
                return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
            }
        }

        String selfLink = request.getRequestURI();
        setMetadataOnRest(note, selfLink, accountingNoteType);

        NoteEntity entity = transformerFactory.getTransformer(accountingNoteType).transform(note);
        entity.setId(generateID(companyAccountId, accountingNoteType.getNoteType()));

        try {
            repositoryFactory.getRepository(accountingNoteType).insert(entity);
        } catch (DuplicateKeyException e) {
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        parentResourceFactory.getParentResource(accountingNoteType.getAccountType())
                .addLink(companyAccountId, accountingNoteType.getLinkType(), selfLink, request);

        return new ResponseObject<>(ResponseStatus.CREATED, note);
    }

    @Override
    public ResponseObject<Note> update(Note note,
                                       AccountingNoteType accountingNoteType,
                                       Transaction transaction,
                                       String companyAccountId,
                                       HttpServletRequest request) throws DataException {
        if (accountingNoteType.isExplicitlyValidated()) {
            Errors errors = validatorFactory.getValidator(accountingNoteType).validateSubmission(note, transaction, companyAccountId, request);
            if (errors.hasErrors()) {
                return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
            }
        }

        setMetadataOnRest(note, request.getRequestURI(), accountingNoteType);

        NoteEntity entity = transformerFactory.getTransformer(accountingNoteType).transform(note);
        entity.setId(generateID(companyAccountId, accountingNoteType.getNoteType()));

        try {
            repositoryFactory.getRepository(accountingNoteType).save(entity);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, note);
    }

    @Override
    public ResponseObject<Note> find(AccountingNoteType accountingNoteType,
                                     String companyAccountId) throws DataException {
        NoteEntity entity;

        try {
           entity =  repositoryFactory.getRepository(accountingNoteType)
                    .findById(generateID(companyAccountId, accountingNoteType.getNoteType()))
                            .orElse(null);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        if (entity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        Note note = transformerFactory.getTransformer(accountingNoteType).transform(entity);

        return new ResponseObject<>(ResponseStatus.FOUND, note);
    }

    @Override
    public ResponseObject<Note> delete(AccountingNoteType accountingNoteType,
                                       String companyAccountId,
                                       HttpServletRequest request) throws DataException {
        String id = generateID(companyAccountId, accountingNoteType.getNoteType());

        try {
            if (repositoryFactory.getRepository(accountingNoteType).existsById(id)) {
                repositoryFactory.getRepository(accountingNoteType).deleteById(id);
                parentResourceFactory.getParentResource(accountingNoteType.getAccountType())
                        .removeLink(companyAccountId, accountingNoteType.getLinkType(), request);
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

    private void setMetadataOnRest(Note note, String selfLink, AccountingNoteType accountingNoteType) {
        note.setLinks(createLinks(selfLink));
        note.setEtag(GenerateEtagUtil.generateEtag());
        note.setKind(accountingNoteType.getKind());
    }

    private Map<String, String> createLinks(String selfLink) {
        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), selfLink);
        return map;
    }
}
