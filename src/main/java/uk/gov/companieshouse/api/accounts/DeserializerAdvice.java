package uk.gov.companieshouse.api.accounts;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class DeserializerAdvice extends SimpleModule {

    public DeserializerAdvice() {
        addDeserializer(String.class, new StdScalarDeserializer<String>(String.class) {
            @Override
            public String deserialize(JsonParser jsonParser, DeserializationContext ctx)
                throws IOException, JsonProcessingException {
                return jsonParser.getValueAsString().trim();
            }
        });
    }
}