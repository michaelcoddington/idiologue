package org.idiologue.server.service;

public interface SecurityService {

    boolean isValidAccessToken(String token);

}
