package uk.gov.companieshouse.api.accounts.service.impl;

import com.mongodb.MongoException;
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


        String selfLink = createSelfLink(transaction, companyAccountId);
        initLinks(rest, selfLink);
        rest.setEtag(GenerateEtagUtil.generateEtag());
        rest.setKind(Kind.PROFIT_LOSS.getValue());
        ProfitLossEntity profitLossEntity = profitLossTransformer.transform(rest);

        String id = generateID(companyAccountId);
        profitLossEntity.setId(id);


        try {
            profitLossRepository.insert(profitLossEntity);
        } catch (DuplicateKeyException dke) {
            return new ResponseObject<>(ResponseStatus.DUPLICATE_KEY_ERROR);
        } catch (MongoException e) {
            throw new DataException(e);
        }

        smallFullService
                .addLink(companyAccountId, SmallFullLinkType.PROFIT_LOSS, selfLink, request);
        return new ResponseObject<>(ResponseStatus.CREATED, rest);
    }

    public String createSelfLink(Transaction transaction, String companyAccountId) {
        return transaction.getLinks().getSelf() + "/"
                + ResourceName.COMPANY_ACCOUNT.getName() + "/"
                + companyAccountId + "/" + ResourceName.SMALL_FULL.getName() + "/"
                + ResourceName.PROFIT_LOSS.getName();
    }

    private void initLinks(ProfitLoss profitLoss, String link) {
        Map<String, String> map = new HashMap<>();
        map.put(BasicLinkType.SELF.getLink(), link);
        profitLoss.setLinks(map);
    }

    private String generateID(String companyAccountId) {
        return keyIdGenerator.generate(companyAccountId + "-" + ResourceName.PROFIT_LOSS.getName());
    }
}
