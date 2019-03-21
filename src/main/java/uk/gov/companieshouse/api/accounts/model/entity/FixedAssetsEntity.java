package uk.gov.companieshouse.api.accounts.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class FixedAssetsEntity {

    @Field("tangible")
    private Long tangible;

    @Field("investments")
    private Long investments;

    @Field("total")
    private Long total;

    public Long getTangible() {
        return tangible;
    }

    public void setTangible(Long tangible) {
        this.tangible = tangible;
    }

    public Long getInvestments() {
        return investments;
    }

    public void setInvestments(Long investments) {
        this.investments = investments;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
