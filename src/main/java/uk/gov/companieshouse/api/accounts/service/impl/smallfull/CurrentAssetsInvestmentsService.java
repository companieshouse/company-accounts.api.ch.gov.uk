package uk.gov.companieshouse.api.accounts.service.impl.smallfull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.Kind;
import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.entity.notes.currentassetsinvestments.CurrentAssetsInvestmentsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.notes.CurrentAssetsInvestments;
import uk.gov.companieshouse.api.accounts.repository.CurrentAssetsInvestmentsRepository;
import uk.gov.companieshouse.api.accounts.service.ResourceService;
import uk.gov.companieshouse.api.accounts.service.impl.BaseService;
import uk.gov.companieshouse.api.accounts.service.impl.SmallFullService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transformer.CurrentAssetsInvestmentsTransformer;
import uk.gov.companieshouse.api.accounts.utility.SelfLinkGenerator;
import uk.gov.companieshouse.api.accounts.utility.impl.KeyIdGenerator;
import uk.gov.companieshouse.api.accounts.validation.smallfull.CurrentAssetsInvestmentsValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;

@Service
public class CurrentAssetsInvestmentsService implements ResourceService<CurrentAssetsInvestments> {

    private BaseService<CurrentAssetsInvestments, CurrentAssetsInvestmentsEntity, SmallFullLinkType> baseService;

    @Autowired
    public CurrentAssetsInvestmentsService(CurrentAssetsInvestmentsRepository repository,
                                           CurrentAssetsInvestmentsTransformer transformer,
                                           KeyIdGenerator keyIdGenerator,
                                           SmallFullService smallFullService,
                                           CurrentAssetsInvestmentsValidator validator) {

        this.baseService =
                new BaseService<>(
                        repository,
                        transformer,
                        validator,
                        keyIdGenerator,
                        smallFullService,
                        SmallFullLinkType.CURRENT_ASSETS_INVESTMENTS_NOTE,
                        Kind.CURRENT_ASSETS_INVESTMENT_NOTE,
                        ResourceName.CURRENT_ASSETS_INVESTMENTS
                );
    }

    @Override
    public ResponseObject<CurrentAssetsInvestments> create(
            CurrentAssetsInvestments rest, Transaction transaction, String companyAccountId, HttpServletRequest request)
                    throws DataException {

        String selfLink =
                SelfLinkGenerator.generateSelfLink(
                        transaction, companyAccountId, ResourceName.SMALL_FULL, true, ResourceName.CURRENT_ASSETS_INVESTMENTS);

        return baseService.create(rest, transaction, companyAccountId, request, selfLink);
    }

    @Override
    public ResponseObject<CurrentAssetsInvestments> update(
            CurrentAssetsInvestments rest, Transaction transaction, String companyAccountId, HttpServletRequest request)
                    throws DataException {

        String selfLink =
                SelfLinkGenerator.generateSelfLink(
                        transaction, companyAccountId, ResourceName.SMALL_FULL, true, ResourceName.CURRENT_ASSETS_INVESTMENTS);

        return baseService.update(rest, transaction, companyAccountId, request, selfLink);
    }

    @Override
    public ResponseObject<CurrentAssetsInvestments> find(String companyAccountsId, HttpServletRequest request) throws DataException {

        return baseService.find(companyAccountsId);
    }

    @Override
    public ResponseObject<CurrentAssetsInvestments> delete(String companyAccountsId, HttpServletRequest request) throws DataException {

        return baseService.delete(companyAccountsId, request);
    }

}
