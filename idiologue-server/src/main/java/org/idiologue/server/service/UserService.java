package org.idiologue.server.service;

public interface UserService {

    /**
     * Ensures that the admin user exists and has a permanent access token.
     */
    void ensureAdminUser();

}
