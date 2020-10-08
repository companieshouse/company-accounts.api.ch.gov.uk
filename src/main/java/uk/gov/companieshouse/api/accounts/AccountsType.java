package uk.gov.companieshouse.api.accounts;

import java.util.HashSet;
import java.util.Set;

/**
 * An enumeration of account types
 */
public enum AccountsType {

    SMALL_FULL_ACCOUNTS("small-full", "small-full-accounts.html", "accounts#smallfull", "small_full_accounts", "small-full-accounts"),
    ABRIDGED_ACCOUNTS("abridged", "abridged-accounts.html", "accounts#abridged", "abridged_accounts", "abridged-accounts");

    private static final String ASSET_ID = "accounts";

    private String fooDoNotMergeTestingForPr;
    private String accountType;
    private String templateName;
    private String kind;
    private String resourceKey;
    private String filingDescriptionKey;

    AccountsType(String accountType, String templateName, String kind, String resourceKey,
        String filingDescriptionKey) {
        this.accountType = accountType;
        this.templateName = templateName;
        this.kind = kind;
        this.resourceKey = resourceKey;
        this.filingDescriptionKey = filingDescriptionKey;
    }

    /**
     * Get the {@link Set} of resource keys
     *
     * @return A {@link Set} of {@link String}s
     */
    public static Set<String> getResourceKeys() {
        Set<String> resourceKeys = new HashSet<>();
        for (AccountsType accountsType : values()) {
            resourceKeys.add(accountsType.getResourceKey());
        }
        return resourceKeys;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getAssetId() {
        return ASSET_ID;
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getKind() {
        return kind;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public String getFilingDescriptionKey() {
        return filingDescriptionKey;
    }
}
