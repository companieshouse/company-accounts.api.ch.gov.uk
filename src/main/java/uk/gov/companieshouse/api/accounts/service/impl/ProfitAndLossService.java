package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.enumerator.AccountingPeriod;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.CurrentPeriodLinkType;
import uk.gov.companieshouse.api.accounts.links.PreviousPeriodLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.ProfitAndLossEntity;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitAndLoss;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.repository.ProfitAndLossRepository;
import uk.gov.companieshouse.api.accounts.service.PeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.ProfitAndLossTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.ProfitAndLossValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProfitAndLossService implements PeriodService<ProfitAndLoss> {

    private ProfitAndLossRepository profitAndLossRepository;
    private ProfitAndLossTransformer profitAndLossTransformer;
    private CurrentPeriodService currentPeriodService;
    private PreviousPeriodService previousPeriodService;
    private KeyIdGenerator keyIdGenerator;
    private ProfitAndLossValidator profitLossValidator;

    @Autowired
    public ProfitAndLossService(
            ProfitAndLossRepository profitAndLossRepository, ProfitAndLossTransformer profitAndLossTransformer,
            CurrentPeriodService currentPeriodService, PreviousPeriodService previousPeriodService,
            KeyIdGenerator keyIdGenerator, ProfitAndLossValidator validator) {
        this.profitAndLossRepository = profitAndLossRepository;
        this.profitAndLossTransformer = profitAndLossTransformer;
        this.currentPeriodService = currentPeriodService;
        this.previousPeriodService = previousPeriodService;
        this.keyIdGenerator = keyIdGenerator;
        this.profitLossValidator = validator;
    }


    @Override
    public ResponseObject<ProfitAndLoss> create(ProfitAndLoss rest, Transaction transaction,
                                                String companyAccountId, HttpServletRequest request, AccountingPeriod period)
        throws DataException {

        Errors errors = profitLossValidator.validateProfitLoss(rest, companyAccountId, request, transaction);
        if(errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        String selfLink = createSelfLink(transaction, companyAccountId, period);
        setMetaData(rest, selfLink, period);

        ProfitAndLossEntity profitAndLossEntity = profitAndLossTransformer.transform(rest);
        profitAndLossEntity.setId(generateID(companyAccountId, period));

        try {
            profitAndLossRepository.insert(profitAndLossEntity);

        } catch (DuplicateKeyException dke) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        if (isCurrentPeriod(period)) {
            currentPeriodService
                    .addLink(companyAccountId, CurrentPeriodLinkType.PROFIT_AND_LOSS, selfLink, request);
        } else {
            previousPeriodService
                    .addLink(companyAccountId, PreviousPeriodLinkType.PROFIT_AND_LOSS, selfLink, request);
        }
        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<ProfitAndLoss> update(ProfitAndLoss rest, Transaction transaction,
                                                String companyAccountId, HttpServletRequest request, AccountingPeriod period)
        throws DataException {

        Errors errors = profitLossValidator.validateProfitLoss(rest, companyAccountId, request, transaction);
        if(errors.hasErrors()) {
            return new ResponseObject<>(ResponseStatus.VALIDATION_ERROR, errors);
        }

        String selfLink = createSelfLink(transaction, companyAccountId, period);
        setMetaData(rest, selfLink, period);

        ProfitAndLossEntity profitAndLossEntity = profitAndLossTransformer.transform(rest);
        profitAndLossEntity.setId(generateID(companyAccountId, period));

        try {
            profitAndLossRepository.save(profitAndLossEntity);

        } catch (MongoException e) {

            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);

    }

    @Override
    public ResponseObject<ProfitAndLoss> find(String companyAccountsId, AccountingPeriod period)
        throws DataException {

        ProfitAndLossEntity profitAndLossEntity;

        try {
            profitAndLossEntity =
                    profitAndLossRepository.findById(generateID(companyAccountsId, period))
                            .orElse(null);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        if (profitAndLossEntity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, profitAndLossTransformer.transform(profitAndLossEntity));
    }

    @Override
    public ResponseObject<ProfitAndLoss> delete(String companyAccountsId, HttpServletRequest request, AccountingPeriod period )
        throws DataException {

        String profitLossId = generateID(companyAccountsId, period);

        try {
            if (profitAndLossRepository.existsById(profitLossId)) {
                profitAndLossRepository.deleteById(profitLossId);
                if (isCurrentPeriod(period)) {
                    currentPeriodService
                            .removeLink(companyAccountsId, CurrentPeriodLinkType.PROFIT_AND_LOSS, request);
                } else {
                    previousPeriodService
                            .removeLink(companyAccountsId, PreviousPeriodLinkType.PROFIT_AND_LOSS, request);
                }
                return new ResponseObject<>(ResponseStatus.UPDATED);

            } else {

                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    private void setMetaData(ProfitAndLoss rest, String selfLink, AccountingPeriod period) {

        initLinks(rest, selfLink);
        rest.setEtag(GenerateEtagUtil.generateEtag());

        if (isCurrentPeriod(period)) {
            rest.setKind(Kind.PROFIT_LOSS_CURRENT.getValue());
        } else {
            rest.setKind(Kind.PROFIT_LOSS_PREVIOUS.getValue());
        }
    }

    private String createSelfLink(Transaction transaction, String companyAccountId, AccountingPeriod period) {
        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/"
                + companyAccountId + "/" + ResourceName.SMALL_FULL.getName() + "/" +
                (isCurrentPeriod(period) ? ResourceName.CURRENT_PERIOD.getName() : ResourceName.PREVIOUS_PERIOD.getName()) + "/"
                + ResourceName.PROFIT_LOSS.getName();
    }

    private void initLinks(ProfitAndLoss profitAndLoss, String link) {
        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), link);
        profitAndLoss.setLinks(map);
    }

    private String generateID(String companyAccountId, AccountingPeriod period) {

        return keyIdGenerator.generate(companyAccountId + "-" +
                (isCurrentPeriod(period) ? ResourceName.CURRENT_PERIOD.getName() : ResourceName.PREVIOUS_PERIOD.getName()) +
                "-" + ResourceName.PROFIT_LOSS.getName());
    }

    private boolean isCurrentPeriod(AccountingPeriod period) {

        return period.equals(AccountingPeriod.CURRENT_PERIOD);
    }
}
