package org.idiologue.server.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BearerTokenAuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    private static final Logger LOG = LogManager.getLogger(BearerTokenAuthorizationManager.class);

    private Pattern bearerTokenPattern = Pattern.compile("Bearer (.+)");

    private static String masterToken = "abc123";

    public BearerTokenAuthorizationManager() {
        LOG.info("Started bearer token authorization manager");
    }

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext object) {
        LOG.info("Checking authorization");
        List<String> authHeaders = object.getExchange().getRequest().getHeaders().get("Authorization");
        LOG.info("Got auth headers {}", authHeaders);
        boolean allowed = false;
        if (authHeaders.size() == 1) {
            String header = authHeaders.get(0);
            Matcher m = bearerTokenPattern.matcher(header);
            if (m.matches()) {
                String token = m.group(1);
                if (token.equals(masterToken)) {
                    allowed = true;
                } else {
                    LOG.warn("Token {} does not match master token {}", token, masterToken);
                }
            }
        }
        AuthorizationDecision decision = new AuthorizationDecision(allowed);
        return Mono.just(decision);
    }

    @Override
    public Mono<Void> verify(Mono<Authentication> authentication, AuthorizationContext object) {
        LOG.info("Verifying authorization");
        return ReactiveAuthorizationManager.super.verify(authentication, object);
    }
}
