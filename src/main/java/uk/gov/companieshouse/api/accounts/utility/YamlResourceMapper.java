package uk.gov.companieshouse.api.accounts.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.YamlMappingException;

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
     */
    public <T> T fetchObjectFromYaml(String resourceFilePath, Class<T> clazz) {

        try {
            return MAPPER.readValue(
                    getClass().getClassLoader()
                            .getResourceAsStream(resourceFilePath), clazz);

        } catch (IOException e) {

            throw new YamlMappingException("Could not fetch yaml resource for path: [" + resourceFilePath + "] and class [" + clazz + "]", e);
        }
    }
}
