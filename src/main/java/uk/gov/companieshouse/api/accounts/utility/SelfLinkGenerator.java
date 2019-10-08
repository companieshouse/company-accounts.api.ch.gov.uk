package uk.gov.companieshouse.api.accounts.utility;

import uk.gov.companieshouse.api.accounts.ResourceName;
import uk.gov.companieshouse.api.model.transaction.Transaction;

public class SelfLinkGenerator {

    private SelfLinkGenerator() {}

    public static String generateSelfLink(
            Transaction transaction, String companyAccountsId, ResourceName parentResource, boolean isNote, ResourceName resource) {

        String link =
                transaction.getLinks().getSelf() + "/" +
                ResourceName.COMPANY_ACCOUNT.getName() + "/" +
                companyAccountsId + "/" +
                parentResource.getName() + "/";

        if (isNote) {
            link += "notes/";
        }

        link += resource.getName();

        return link;
    }
}
