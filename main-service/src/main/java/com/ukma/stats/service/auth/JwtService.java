package com.ukma.stats.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class JwtService {

    KeyProvider keyProvider;

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(keyProvider.getPublicKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public <T> T getClaim(String token, Function<Claims, T> claimFunction) {
        Claims allClaims = extractAllClaims(token);

        return claimFunction.apply(allClaims);
    }

    private Date getTokenExpirationDate(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    public String getUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        return getTokenExpirationDate(token).before(new Date());
    }

    public boolean isTokenValid(String token) {
        return !isTokenExpired(token);
    }
}
