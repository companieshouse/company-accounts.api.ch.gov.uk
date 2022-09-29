package uk.gov.companieshouse.api.accounts.utility.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import uk.gov.companieshouse.api.accounts.utility.IdGenerator;

@Component
@RequestScope
public class KeyIdGenerator implements IdGenerator {

    private static final Base64.Encoder BASE64_ENCODER = Base64.getUrlEncoder();
    private static final String MESSAGE_DIGEST_ALGORITHM = MessageDigestAlgorithms.SHA_256;
    private final MessageDigest messageDigest;

    public KeyIdGenerator() {
        try {
            messageDigest = MessageDigest.getInstance(MESSAGE_DIGEST_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Cannot find essential MessageDigest: " + MESSAGE_DIGEST_ALGORITHM, e);
        }
    }

    @Override
    public String generate(String key) {
        byte[] id = messageDigest.digest(key.getBytes(StandardCharsets.UTF_8));
        return BASE64_ENCODER.encodeToString(id);
    }

    @Override
    public String generateRandom() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return BASE64_ENCODER.encodeToString(bytes);
    }
}