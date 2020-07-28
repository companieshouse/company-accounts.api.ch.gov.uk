package uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors;

import org.springframework.data.mongodb.core.mapping.Field;

public class LoanDataEntity {

    @Field("director_name")
    private String directorName;

    @Field("description")
    private String description;

    @Field("breakdown")
    private LoanBreakdownResourceEntity breakdown;

    public String getDirectorName() {
        return directorName;
    }

    public void setDirectorName(String directorName) {
        this.directorName = directorName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LoanBreakdownResourceEntity getBreakdown() {
        return breakdown;
    }

    public void setBreakdown(LoanBreakdownResourceEntity breakdown) {
        this.breakdown = breakdown;
    }

    @Override
    public String toString() {
        return "LoanDataEntity{" +
                "directorName='" + directorName + '\'' +
                ", description='" + description + '\'' +
                ", breakdown=" + breakdown +
                "}";
    }
}
