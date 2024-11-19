package com.ukma.authentication.service.auth;

import com.ukma.authentication.service.auth.dto.AuthDto;
import com.ukma.authentication.service.auth.dto.TokenDto;

public interface AuthenticationService {

    TokenDto login(AuthDto authDto);
}
