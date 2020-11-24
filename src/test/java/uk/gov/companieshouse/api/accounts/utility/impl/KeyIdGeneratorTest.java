package uk.gov.companieshouse.api.accounts.utility.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.security.MessageDigest;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KeyIdGeneratorTest {

    @InjectMocks
    private KeyIdGenerator keyIdGenerator;

    @Mock
    private MessageDigest messageDigest;

    @Test
    @DisplayName("Test generate a Base 64 encoded String")
    void testGenerate() {
       when(messageDigest.digest(any())).thenReturn(new byte[] { 0 });
       String value = keyIdGenerator.generate("string");
       assertNotNull(value);
       assertTrue(Base64.isBase64(value));
    }

    @Test
    @DisplayName("Test generate a random String")
    void testGenerateRandom() {

        String value = keyIdGenerator.generateRandom();
        assertNotNull(value);
    }
}
