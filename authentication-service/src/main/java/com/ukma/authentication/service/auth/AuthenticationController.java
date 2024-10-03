package com.ukma.authentication.service.auth;

import com.ukma.authentication.service.auth.dto.AuthDto;
import com.ukma.authentication.service.auth.dto.TokenDto;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody AuthDto authDto) {
        try {
            return ResponseEntity.ok(authenticationService.login(authDto));
        } catch (Exception exception) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<TokenDto> register(@RequestBody AuthDto authDto) {
        try {
            return ResponseEntity.ok(authenticationService.register(authDto));
        } catch (Exception exception) {
            return ResponseEntity.badRequest().build();
        }
    }
}
