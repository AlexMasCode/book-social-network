package com.ukma.main.service.auth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeyProvider {

    final PublicKey PUBLIC_KEY;
    final KeyFactory KEY_FACTORY;

    public KeyProvider() throws Exception {
        this.KEY_FACTORY = KeyFactory.getInstance("RSA");
        this.PUBLIC_KEY = loadPublicKey("main-service/src/main/resources/keys/public_key.pem");
    }

    private PublicKey loadPublicKey(String path) throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(new File(path).getAbsolutePath())))
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
