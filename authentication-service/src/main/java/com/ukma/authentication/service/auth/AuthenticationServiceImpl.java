package com.ukma.authentication.service.auth;

import com.ukma.authentication.service.auth.dto.LoginDto;
import com.ukma.authentication.service.auth.dto.RegistrationDto;
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
public class AuthenticationServiceImpl implements AuthenticationService {

    JwtService jwtService;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    public TokenDto login(LoginDto authDto) {
        User userCheck = userRepository.findByEmail(authDto.getEmail()).orElse(null);
        if (userCheck != null && passwordEncoder.matches(authDto.getPassword(), userCheck.getPassword())) {
            return new TokenDto(jwtService.generateToken(Map.of(), userCheck.getUsername()));
        }
        throw new IllegalArgumentException("user is not found");
    }

    public TokenDto register(RegistrationDto authDto) {
        Optional<User> userCheck = userRepository.findByEmail(authDto.getEmail());
        if (userCheck.isEmpty()) {
            User newUser = new User(authDto.getEmail(), authDto.getFullName(), passwordEncoder.encode(authDto.getPassword()));
            userRepository.save(newUser);
            return new TokenDto(jwtService.generateToken(Map.of(), newUser.getUsername()));
        } else {
            throw new IllegalArgumentException("user is already exists");
        }
    }

}
