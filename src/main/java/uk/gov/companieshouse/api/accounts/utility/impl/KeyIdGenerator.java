package uk.gov.companieshouse.api.accounts.utility.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.utility.IdGenerator;

@Component
public class KeyIdGenerator implements IdGenerator {

    @Autowired
    private MessageDigest messageDigest;

    @Override
    public String generate(String key) {
        byte[] id = messageDigest.digest(key.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().encodeToString(id);
    }
}