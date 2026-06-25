package org.racetobid.racetobid.interceptor;

import org.racetobid.racetobid.service.impl.JwtTokenService;
import org.racetobid.racetobid.service.impl.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {
    private static final Logger log = LoggerFactory.getLogger(JwtChannelInterceptor.class);
    
    private final JwtTokenService jwtTokenService;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtChannelInterceptor(JwtTokenService jwtTokenService, UserDetailsServiceImpl userDetailsService) {
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor != null) {
            StompCommand command = accessor.getCommand();
            
            // CONNECT komutunda token'ı al ve session'a kaydet
            if (command == StompCommand.CONNECT) {
                String token = extractTokenFromHeaders(accessor);
                if (token != null) {
                    try {
                        String email = jwtTokenService.extractEmail(token);
                        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                        if (jwtTokenService.validateToken(token, userDetails)) {
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            accessor.setUser(authentication);
                            // Session attribute'a token'ı kaydet
                            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                            if (sessionAttributes != null) {
                                sessionAttributes.put("jwt_token", token);
                                sessionAttributes.put("user_email", email);
                            }
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            log.info("CONNECT: User authenticated: {}", email);
                        }
                    } catch (Exception e) {
                        log.error("CONNECT authentication failed:", e);
                    }
                }
            }
            
            // SEND komutunda session'dan user bilgisini al
            if (command == StompCommand.SEND) {
                // Önce session'dan user bilgisini al
                Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
                if (sessionAttributes != null) {
                    String userEmail = (String) sessionAttributes.get("user_email");
                    if (userEmail != null) {
                        try {
                            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            accessor.setUser(authentication);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            log.info("SEND: User authenticated from session: {}", userEmail);
                        } catch (Exception e) {
                            log.error("SEND authentication from session failed:", e);
                        }
                    } else {
                        // Session'da yoksa, header'dan tekrar dene
                        String token = extractTokenFromHeaders(accessor);
                        if (token != null) {
                            try {
                                String email = jwtTokenService.extractEmail(token);
                                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                                if (jwtTokenService.validateToken(token, userDetails)) {
                                    UsernamePasswordAuthenticationToken authentication =
                                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                    accessor.setUser(authentication);
                                    SecurityContextHolder.getContext().setAuthentication(authentication);
                                    log.info("SEND: User authenticated from token: {}", email);
                                }
                            } catch (Exception e) {
                                log.error("SEND authentication from token failed:", e);
                            }
                        }
                    }
                }
            }
        }
        return message;
    }
    
    private String extractTokenFromHeaders(StompHeaderAccessor accessor) {
        // 1. Authorization header
        List<String> authHeaders = accessor.getNativeHeader("Authorization");
        if (authHeaders != null && !authHeaders.isEmpty()) {
            String authHeader = authHeaders.get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }
        
        // 2. Query string'den token
        String nativeQuery = accessor.getFirstNativeHeader("token");
        if (nativeQuery == null && accessor.getNativeHeader("token") != null && !accessor.getNativeHeader("token").isEmpty()) {
            nativeQuery = accessor.getNativeHeader("token").get(0);
        }
        if (nativeQuery != null) {
            return nativeQuery;
        }
        
        return null;
    }
}

