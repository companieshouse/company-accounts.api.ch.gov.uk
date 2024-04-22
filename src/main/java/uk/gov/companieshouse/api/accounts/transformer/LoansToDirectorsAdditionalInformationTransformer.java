package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.AdditionalInformationDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.AdditionalInformationEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.AdditionalInformation;

@Component
public class LoansToDirectorsAdditionalInformationTransformer implements GenericTransformer<AdditionalInformation,
        AdditionalInformationEntity> {

    @Override
    public AdditionalInformationEntity transform(AdditionalInformation rest) {
        AdditionalInformationDataEntity additionalInformationDataEntity = new AdditionalInformationDataEntity();
        AdditionalInformationEntity additionalInformationEntity = new AdditionalInformationEntity();

        BeanUtils.copyProperties(rest, additionalInformationDataEntity);

        additionalInformationEntity.setData(additionalInformationDataEntity);

        return additionalInformationEntity;

    }

    @Override
    public AdditionalInformation transform(AdditionalInformationEntity entity) {
        AdditionalInformation additionalInformation = new AdditionalInformation();
        BeanUtils.copyProperties(entity.getData(), additionalInformation);

        return additionalInformation;

    }
}
