package uk.gov.companieshouse.api.accounts.transformer;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Director;

@Component
public class DirectorTransformer implements GenericTransformerForMultipleResources<Director, DirectorEntity> {

    @Override
    public DirectorEntity transform(Director entity) {
        DirectorDataEntity directorDataEntity = new DirectorDataEntity();
        BeanUtils.copyProperties(entity, directorDataEntity);

        DirectorEntity directorEntity = new DirectorEntity();
        directorEntity.setData(directorDataEntity);

        return directorEntity;
    }

    @Override
    public Director transform(DirectorEntity entity) {
        Director director = new Director();
        BeanUtils.copyProperties(entity.getData(), director);

        return director;
    }

    @Override
    public Director[] transform(DirectorEntity[] entity) {
        Director[] directors = new Director[entity.length];

        for (int i = 0; i < entity.length; i++) {
            directors[i] = transform(entity[i]);
        }

        return directors;
    }
}
