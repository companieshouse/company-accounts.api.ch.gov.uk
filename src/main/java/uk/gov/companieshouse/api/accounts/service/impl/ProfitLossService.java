package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.GenerateEtagUtil;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.ProfitLossEntity;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitLoss;
import uk.gov.companieshouse.api.accounts.repository.ProfitLossRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.transformer.ProfitLossTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProfitLossService implements ResourceService<ProfitLoss> {

    private static final Pattern CURRENT_PERIOD_PATTERN =
            Pattern.compile("/transactions/[^/].*/company-accounts/[^/].*/small-full/current-period/profit-and-loss");

    private ProfitLossRepository profitLossRepository;

    private ProfitLossTransformer profitLossTransformer;

    private SmallFullService smallFullService;

    private KeyIdGenerator keyIdGenerator;

    @Autowired
    public ProfitLossService(
            ProfitLossRepository profitLossRepository,
            ProfitLossTransformer profitLossTransformer,
            SmallFullService smallFullService,
            KeyIdGenerator keyIdGenerator) {
        this.profitLossRepository = profitLossRepository;
        this.profitLossTransformer = profitLossTransformer;
        this.smallFullService = smallFullService;
        this.keyIdGenerator = keyIdGenerator;
    }

    @Override
    public ResponseObject<ProfitLoss> create(ProfitLoss rest, Transaction transaction,
                                             String companyAccountId, HttpServletRequest request)
        throws DataException {

        String selfLink = createSelfLink(transaction, companyAccountId, request);
        setMetaData(rest, selfLink, request);

        ProfitLossEntity profitLossEntity = profitLossTransformer.transform(rest);
        profitLossEntity.setId(generateID(companyAccountId, request));

        try {
            profitLossRepository.insert(profitLossEntity);

        } catch (DuplicateKeyException dke) {

            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        if (isCurrentPeriod(request)) {
            smallFullService
                    .addLink(companyAccountId, SmallFullLinkType.CURRENT_PERIOD_PROFIT_LOSS, selfLink, request);
        } else {
            smallFullService
                    .addLink(companyAccountId, SmallFullLinkType.PREVIOUS_PERIOD_PROFIT_LOSS, selfLink, request);
        }
        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    @Override
    public ResponseObject<ProfitLoss> update(ProfitLoss rest, Transaction transaction,
                                             String companyAccountId, HttpServletRequest request)
        throws DataException {

        String selfLink = createSelfLink(transaction, companyAccountId, request);
        setMetaData(rest, selfLink, request);

        ProfitLossEntity profitLossEntity = profitLossTransformer.transform(rest);
        profitLossEntity.setId(generateID(companyAccountId, request));

        try {
            profitLossRepository.save(profitLossEntity);

        } catch (MongoException e) {

            throw new DataException(e);
        }

        return new ResponseObject<>(ResponseStatus.UPDATED, rest);

    }

    @Override
    public ResponseObject<ProfitLoss> find(String companyAccountsId, HttpServletRequest request)
        throws DataException {

        ProfitLossEntity profitLossEntity;

        try {
            profitLossEntity =
                    profitLossRepository.findById(generateID(companyAccountsId, request))
                            .orElse(null);
        } catch (MongoException e) {

            throw new DataException(e);
        }

        if (profitLossEntity == null) {
            return new ResponseObject<>(ResponseStatus.NOT_FOUND);
        }

        return new ResponseObject<>(ResponseStatus.FOUND, profitLossTransformer.transform(profitLossEntity));
    }

    @Override
    public ResponseObject<ProfitLoss> delete(String companyAccountsId, HttpServletRequest request)
        throws DataException {

        String profitLossId = generateID(companyAccountsId, request);

        try {
            if (profitLossRepository.existsById(profitLossId)) {
                profitLossRepository.deleteById(profitLossId);
                if (isCurrentPeriod(request)) {
                    smallFullService
                            .removeLink(companyAccountsId, SmallFullLinkType.CURRENT_PERIOD_PROFIT_LOSS, request);
                } else {
                    smallFullService
                            .removeLink(companyAccountsId, SmallFullLinkType.PREVIOUS_PERIOD_PROFIT_LOSS, request);
                }
                return new ResponseObject<>(ResponseStatus.UPDATED);

            } else {

                return new ResponseObject<>(ResponseStatus.NOT_FOUND);
            }
        } catch (MongoException e) {

            throw new DataException(e);
        }
    }

    private void setMetaData(ProfitLoss rest, String selfLink, HttpServletRequest request) {

        initLinks(rest, selfLink);
        rest.setEtag(GenerateEtagUtil.generateEtag());

        if (isCurrentPeriod(request)) {
            rest.setKind(Kind.PROFIT_LOSS_CURRENT.getValue());
        } else {
            rest.setKind(Kind.PROFIT_LOSS_PREVIOUS.getValue());
        }
    }

    private String createSelfLink(Transaction transaction, String companyAccountId, HttpServletRequest request) {
        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/"
                + companyAccountId + "/" + ResourceName.SMALL_FULL.getName() + "/" +
                (isCurrentPeriod(request) ? ResourceName.CURRENT_PERIOD.getName() : ResourceName.PREVIOUS_PERIOD.getName()) + "/"
                + ResourceName.PROFIT_LOSS.getName();
    }

    private void initLinks(ProfitLoss profitLoss, String link) {
        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), link);
        profitLoss.setLinks(map);
    }

    private String generateID(String companyAccountId, HttpServletRequest request) {

        return keyIdGenerator.generate(companyAccountId + "-" +
                (isCurrentPeriod(request) ? ResourceName.CURRENT_PERIOD.getName() : ResourceName.PREVIOUS_PERIOD.getName()) +
                "-" + ResourceName.PROFIT_LOSS.getName());
    }

    private boolean isCurrentPeriod(HttpServletRequest request) {

        return CURRENT_PERIOD_PATTERN.matcher(request.getRequestURI()).matches();
    }
}
