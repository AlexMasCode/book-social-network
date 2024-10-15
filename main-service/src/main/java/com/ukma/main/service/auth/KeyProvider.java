package com.ukma.main.service.auth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeyProvider {

    Environment env;
    ResourceLoader resourceLoader;
    final PublicKey PUBLIC_KEY;
    final KeyFactory KEY_FACTORY;

    @Autowired
    public KeyProvider(Environment env, ResourceLoader resourceLoader) throws Exception {
        this.resourceLoader = resourceLoader;
        this.env = env;
        this.KEY_FACTORY = KeyFactory.getInstance("RSA");
        this.PUBLIC_KEY = loadPublicKey();
    }

    private PublicKey loadPublicKey() throws Exception {
        Resource resource = resourceLoader.getResource("classpath:/keys/public_key.pem");
        String key = new String(resource.getContentAsByteArray())
            .replaceAll("-----\\w+ PUBLIC KEY-----", "")
            .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return KEY_FACTORY.generatePublic(spec);
    }

    public PublicKey getPublicKey() {
        return PUBLIC_KEY;
    }
}
