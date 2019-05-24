package uk.gov.companieshouse.api.accounts.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;

/**
 * Provide functionality to map the contents of a yaml file listed under the resources directory
 * to an abject
 */
@Component
public class YamlResourceMapper {

    private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory());

    /**
     * Deserializes the contents of a yaml file listed under the resources directory to an object
     *
     * @param resourceFilePath The path of the file to deserialize, relative to the 'resources' directory
     * @param clazz The class of object for which to deserialize the yaml file
     * @return an object containing the data from a yaml file
     * @throws DataException if there's an error when deserializing the file to an object
     */
    public <T> T fetchObjectFromYaml(String resourceFilePath, Class<T> clazz) throws DataException {

        try {
            return MAPPER.readValue(
                    getClass().getClassLoader()
                            .getResourceAsStream(resourceFilePath), clazz);

        } catch (IOException e) {

            throw new DataException(e);
        }
    }
}
