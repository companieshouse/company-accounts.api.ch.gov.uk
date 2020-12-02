package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.AdditionalInformationDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.AdditionalInformationEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.AdditionalInformation;

@Component
public class RelatedPartyTransactionsAdditionalInformationTransformer implements GenericTransformer<AdditionalInformation, AdditionalInformationEntity> {

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
