package com.ukma.authentication.service.auth;

import com.ukma.authentication.service.auth.dto.LoginDto;
import com.ukma.authentication.service.auth.dto.RegistrationDto;
import com.ukma.authentication.service.auth.dto.TokenDto;

public interface AuthenticationService {

    TokenDto login(LoginDto authDto);

    TokenDto register(RegistrationDto authDto);
}
