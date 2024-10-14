package com.ukma.main.service.auth;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeyProvider {

    Environment env;

    final PublicKey PUBLIC_KEY;
    final KeyFactory KEY_FACTORY;

    public KeyProvider(@Autowired Environment env) throws Exception {
        this.env = env;
        this.KEY_FACTORY = KeyFactory.getInstance("RSA");
        boolean hasTestProfile = Arrays.asList(env.getActiveProfiles()).contains("test");
        this.PUBLIC_KEY = loadPublicKey(hasTestProfile ? "src/main/resources/keys/public_key.pem" : "main-service/src/main/resources/keys/public_key.pem");
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
