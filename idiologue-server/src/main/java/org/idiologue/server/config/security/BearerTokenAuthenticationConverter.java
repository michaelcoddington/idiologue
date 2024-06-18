package org.idiologue.server.config.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BearerTokenAuthenticationConverter implements ServerAuthenticationConverter {

    private Pattern bearerTokenPattern = Pattern.compile("Bearer (.+)");
    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null) {
            Matcher m = bearerTokenPattern.matcher(authHeader);
            if (m.matches()) {
                String token = m.group(1);
                Authentication auth = new UsernamePasswordAuthenticationToken(token, token);
                return Mono.just(auth);
            }
        }
        return Mono.empty();

    }

}
