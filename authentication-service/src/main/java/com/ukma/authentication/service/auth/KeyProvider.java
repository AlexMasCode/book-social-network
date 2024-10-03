package com.ukma.authentication.service.auth;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KeyProvider {

    final PrivateKey PRIVATE_KEY;
    final PublicKey PUBLIC_KEY;
    final KeyFactory KEY_FACTORY;

    public KeyProvider() throws Exception {
        this.KEY_FACTORY = KeyFactory.getInstance("RSA");
        this.PRIVATE_KEY = loadPrivateKey("src/main/resources/keys/private_key.pem");
        this.PUBLIC_KEY = loadPublicKey("src/main/resources/keys/public_key.pem");
    }

    private PrivateKey loadPrivateKey(String path) throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(new File(path).getAbsolutePath())))
            .replaceAll("-----\\w+ PRIVATE KEY-----", "")
            .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KEY_FACTORY.generatePrivate(spec);
    }

    private PublicKey loadPublicKey(String path) throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(new File(path).getAbsolutePath())))
            .replaceAll("-----\\w+ PUBLIC KEY-----", "")
            .replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return KEY_FACTORY.generatePublic(spec);
    }

    public PrivateKey getPrivateKey() {
        return PRIVATE_KEY;
    }

    public PublicKey getPublicKey() {
        return PUBLIC_KEY;
    }
}
