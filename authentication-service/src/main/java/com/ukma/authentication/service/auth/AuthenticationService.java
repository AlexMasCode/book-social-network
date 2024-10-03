package com.ukma.authentication.service.auth;

import com.ukma.authentication.service.auth.dto.AuthDto;
import com.ukma.authentication.service.auth.dto.TokenDto;
import com.ukma.authentication.service.user.User;
import com.ukma.authentication.service.user.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthenticationService {

    JwtService jwtService;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    public TokenDto login(AuthDto authDto) {
        User userCheck = userRepository.findByUsername(authDto.getUsername()).orElse(null);
        if (userCheck != null) {
            if (passwordEncoder.matches(authDto.getPassword(), userCheck.getPassword())) {
                return new TokenDto(jwtService.generateToken(Map.of(), userCheck.getUsername()));
            }
        }
        throw new IllegalArgumentException("user is not found");
    }


    public TokenDto register(AuthDto authDto) {
        Optional<User> userCheck = userRepository.findByUsername(authDto.getUsername());
        if (userCheck.isEmpty()) {
            User newUser = new User(authDto.getUsername(), passwordEncoder.encode(authDto.getPassword()));
            userRepository.save(newUser);
            return new TokenDto(jwtService.generateToken(Map.of(), newUser.getUsername()));
        } else {
            throw new IllegalArgumentException("user is already exists");
        }
    }

}
