package org.idiologue.server.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.idiologue.server.service.SecurityService;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class BearerTokenAuthenticationManager implements ReactiveAuthenticationManager {

    private static final Logger LOG = LogManager.getLogger(BearerTokenAuthenticationManager.class);

    private SecurityService securityService;

    public BearerTokenAuthenticationManager(SecurityService securityService) {
        LOG.info("Created bearer token auth manager");
        this.securityService = securityService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        LOG.info("Checking authentication on {}", authentication);
        String token = authentication.getCredentials().toString();
        if (securityService.isValidAccessToken(token)) {
            LOG.info("Token {} is valid", token);
            UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(token, token, List.of(new SimpleGrantedAuthority("USER")));
            return Mono.just(result);
        } else {
            LOG.warn("Token {} is invalid", token);
            return Mono.empty();
        }
    }
}
