package uk.gov.companieshouse.api.accounts.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.PayableResource;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.Cost;
import uk.gov.companieshouse.api.accounts.model.rest.Costs;
import uk.gov.companieshouse.api.accounts.service.CostService;
import uk.gov.companieshouse.api.accounts.service.TransactionService;
import uk.gov.companieshouse.api.accounts.utility.YamlResourceMapper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Service
public class CostServiceImpl implements CostService {

    private TransactionService transactionService;

    private YamlResourceMapper yamlResourceMapper;

    @Autowired
    public CostServiceImpl(TransactionService transactionService, YamlResourceMapper yamlResourceMapper) {

        this.transactionService = transactionService;
        this.yamlResourceMapper = yamlResourceMapper;
        this.costs =
                yamlResourceMapper
                        .fetchObjectFromYaml(COSTS_YAML_FILE, Costs.class);
    }

    private static final String COSTS_YAML_FILE = "/costs/costs.yaml";

    private Costs costs;

    @Override
    public Cost[] getCosts(Transaction transaction) throws DataException {

        try {
            List<PayableResource> payableResources = transactionService.getPayableResources(transaction);

            if (payableResources.isEmpty()) {
                return new Cost[0];
            }

            Cost[] costArray = new Cost[payableResources.size()];

            for (int i = 0; i < payableResources.size(); i++) {

                costArray[i] = costs.getCosts().get(payableResources.get(i).getResource());
            }

            return costArray;

        } catch (ServiceException e) {

            throw new DataException(e);
        }
    }
}
