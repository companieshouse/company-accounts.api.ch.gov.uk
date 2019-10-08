package uk.gov.companieshouse.api.accounts.service.impl.smallfull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.stocks.StocksEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.stocks.Stocks;
import uk.gov.companieshouse.api.accounts.repository.StocksRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.impl.BaseService;
import uk.gov.companieshouse.api.accounts.service.impl.SmallFullService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.utility.SelfLinkGenerator;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transformer.StocksTransformer;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.smallfull.StocksValidator;

import javax.servlet.http.HttpServletRequest;

@Service
public class StocksService implements ResourceService<Stocks> {

    private BaseService<Stocks, StocksEntity, SmallFullLinkType> baseService;

    @Autowired
    public StocksService(StocksRepository repository,
                         StocksTransformer transformer,
                         StocksValidator validator,
                         KeyIdGenerator keyIdGenerator,
                         SmallFullService smallFullService) {

        this.baseService =
                new BaseService<>(
                        repository,
                        transformer,
                        validator,
                        keyIdGenerator,
                        smallFullService,
                        SmallFullLinkType.STOCKS_NOTE,
                        Kind.STOCKS_NOTE,
                        ResourceName.STOCKS
                );
    }

    @Override
    public ResponseObject<Stocks> create(Stocks rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {

        String selfLink =
                SelfLinkGenerator.generateSelfLink(
                        transaction, companyAccountId, ResourceName.SMALL_FULL, true, ResourceName.STOCKS);

        return baseService.create(rest, transaction, companyAccountId, request, selfLink);
    }

    @Override
    public ResponseObject<Stocks> update(Stocks rest, Transaction transaction, String companyAccountId, HttpServletRequest request) throws DataException {

        String selfLink =
                SelfLinkGenerator.generateSelfLink(
                        transaction, companyAccountId, ResourceName.SMALL_FULL, true, ResourceName.STOCKS);

        return baseService.update(rest, transaction, companyAccountId, request, selfLink);
    }

    @Override
    public ResponseObject<Stocks> find(String companyAccountsId, HttpServletRequest request) throws DataException {

        return baseService.find(companyAccountsId);
    }

    @Override
    public ResponseObject<Stocks> delete(String companyAccountsId, HttpServletRequest request) throws DataException {

        return baseService.delete(companyAccountsId, request);
    }
}
