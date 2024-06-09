package org.idiologue.server.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class TestBearerTokenAuthenticationProviderFactory implements FactoryBean<AuthenticationProvider> {

    private class TestProvider implements AuthenticationProvider {
        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            return null;
        }

        @Override
        public boolean supports(Class<?> authentication) {
            return false;
        }
    }

    private AuthenticationProvider authenticationProvider;

    @PostConstruct
    private void init() {
        this.authenticationProvider = new TestProvider();
    }

    @Override
    public AuthenticationProvider getObject() throws Exception {
        return this.authenticationProvider;
    }

    @Override
    public Class<?> getObjectType() {
        return AuthenticationProvider.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
