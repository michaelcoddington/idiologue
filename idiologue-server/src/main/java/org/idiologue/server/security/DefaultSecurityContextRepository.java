package org.idiologue.server.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DefaultSecurityContextRepository implements ServerSecurityContextRepository {

    private static final Logger LOG = LogManager.getLogger(DefaultSecurityContextRepository.class);

    private Pattern bearerTokenPattern = Pattern.compile("Bearer (.+)");

    private BearerTokenAuthenticationManager bearerTokenAuthenticationManager;

    public DefaultSecurityContextRepository(BearerTokenAuthenticationManager bearerTokenAuthenticationManager) {
        this.bearerTokenAuthenticationManager = bearerTokenAuthenticationManager;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        LOG.info("Called save for security context {}", context);
        return null;
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        LOG.info("Loading security context");
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null) {
            Matcher m = bearerTokenPattern.matcher(authHeader);
            if (m.matches()) {
                String token = m.group(1);
                Authentication auth = new UsernamePasswordAuthenticationToken(token, token);
                LOG.info("Constructed authentication {}", auth);
                return bearerTokenAuthenticationManager.authenticate(auth).map(SecurityContextImpl::new);
            }
        }
        return null;
    }

}
