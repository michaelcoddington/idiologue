package org.idiologue.server.service;

import org.idiologue.api.Revision;

import java.security.Principal;

public interface RevisionService {

    void executeRevision(Principal principal, Revision revision);

}
