package uk.gov.companieshouse.api.accounts.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.PayableResource;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.Cost;
import uk.gov.companieshouse.api.accounts.service.CostService;
import uk.gov.companieshouse.api.accounts.service.TransactionService;
import uk.gov.companieshouse.api.accounts.utility.YamlResourceMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class CostServiceImpl implements CostService {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private YamlResourceMapper yamlResourceMapper;

    private static final String COSTS_RESOURCE_PACKAGE = "/costs/";

    @Override
    public Cost[] getCosts(Transaction transaction) throws DataException {

        try {
            List<PayableResource> payableResources = transactionService.getPayableResources(transaction);

            if (payableResources.isEmpty()) {
                return new Cost[0];
            }

            Cost[] costs = new Cost[payableResources.size()];

            for (int i = 0; i < payableResources.size(); i++) {

                Cost cost =
                        yamlResourceMapper.fetchObjectFromYaml(
                                COSTS_RESOURCE_PACKAGE + payableResources.get(i).getYamlFile(),
                                        Cost.class);

                costs[i] = cost;
            }

            return costs;

        } catch (ServiceException e) {

            throw new DataException(e);
        }
    }
}
