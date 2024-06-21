package org.idiologue.server.config.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManagerResolver;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfiguration {

    private static final Logger LOG = LogManager.getLogger(WebSecurityConfiguration.class);

    @Autowired
    private BearerTokenAuthenticationManager tokenAuthenticationManager;

    @Autowired
    private BearerTokenAuthenticationConverter authenticationConverter;

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth.pathMatchers("/api/**").authenticated())
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .addFilterAfter(authenticationWebFilter(), SecurityWebFiltersOrder.REACTOR_CONTEXT)
                .authorizeExchange(auth -> auth.anyExchange().permitAll())
                .build();
    }

    public AuthenticationWebFilter authenticationWebFilter() {
        LOG.debug("Creating auth web filter");
        AuthenticationWebFilter filter =  new AuthenticationWebFilter(resolver());
        filter.setServerAuthenticationConverter(authenticationConverter);
        return filter;
    }

    public ReactiveAuthenticationManagerResolver<ServerWebExchange> resolver() {
        return exchange ->  Mono.just(tokenAuthenticationManager);
    }

}
