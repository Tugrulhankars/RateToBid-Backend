package org.racetobid.racetobid.service.impl;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.racetobid.racetobid.dto.request.LoginRequest;
import org.racetobid.racetobid.dto.response.AuthResponse;
import org.racetobid.racetobid.dto.request.LoginRequest;
import org.racetobid.racetobid.dto.request.RegisterRequest;
import org.racetobid.racetobid.entity.User;
import org.racetobid.racetobid.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final Tracer tracer;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenService jwtTokenService,
                           AuthenticationManager authenticationManager,
                           UserDetailsServiceImpl userDetailsService,
                           OpenTelemetry openTelemetry) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.tracer = openTelemetry.getTracer(AuthServiceImpl.class.getName());
    }


    public AuthResponse register(RegisterRequest request) {
        var span=tracer.spanBuilder("register")
                .startSpan();
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Bu email adresi zaten kullanılıyor");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        if (request.getUserType() != null) {
            try {
                user.setUserType(org.racetobid.racetobid.enums.UserType.valueOf(request.getUserType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                user.setUserType(org.racetobid.racetobid.enums.UserType.INDIVIDUAL);
            }
        }
        user.setStoreName(request.getStoreName());

        span.setAttribute("auth.register.firstName",user.getFirstName());
        span.setAttribute("auth.register.lastName",user.getLastName());
        span.setAttribute("auth.register.email",user.getEmail());
        span.setAttribute("auth.register.phoneNumber",user.getPhoneNumber());
        span.setAttribute("auth.register.date", LocalDateTime.now().toString());

        user = userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtTokenService.generateAccessToken(userDetails);
        String refreshToken = jwtTokenService.generateRefreshToken(userDetails);

        AuthResponse response = new AuthResponse();
        response.setId(user.getId());
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setUserType(user.getUserType() != null ? user.getUserType().name() : "INDIVIDUAL");
        response.setStoreName(user.getStoreName());

        span.end();
        return response;
    }

    public AuthResponse login(LoginRequest request) {
        var span=tracer.spanBuilder("login").startSpan();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String accessToken = jwtTokenService.generateAccessToken(userDetails);
        String refreshToken = jwtTokenService.generateRefreshToken(userDetails);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        span.setAttribute("auth.login.email",request.getEmail());
        span.setAttribute("auth.login.firstName",user.getFirstName());
        span.setAttribute("auth.login.lastName",user.getLastName());
        span.setAttribute("auth.login.date",LocalDateTime.now().toString());

        AuthResponse response = new AuthResponse();
        response.setId(user.getId());
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setUserType(user.getUserType() != null ? user.getUserType().name() : "INDIVIDUAL");
        response.setStoreName(user.getStoreName());
        span.end();
        return response;
    }
}

