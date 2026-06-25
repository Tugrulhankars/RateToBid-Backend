package org.racetobid.racetobid.service;

import org.racetobid.racetobid.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(String email, String password);
    AuthResponse register(String email, String password);
    AuthResponse logout(String email);
}
