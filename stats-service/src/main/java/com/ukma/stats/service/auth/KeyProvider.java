package com.ukma.stats.service.auth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class KeyProvider {

    ResourceLoader resourceLoader;
    PublicKey publicKey;
    KeyFactory keyFactory;

    @Autowired
    public KeyProvider(ResourceLoader resourceLoader) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        this.resourceLoader = resourceLoader;
        this.keyFactory = KeyFactory.getInstance("RSA");
        this.publicKey = loadPublicKey();
    }

    private PublicKey loadPublicKey() throws IOException, InvalidKeySpecException {
        Resource resource = resourceLoader.getResource("classpath:/keys/public_key.pem");
        String key = new String(resource.getContentAsByteArray())
            .replaceAll("-----\\w+ PUBLIC KEY-----", "")
            .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return keyFactory.generatePublic(spec);
    }
}
