package org.idiologue.server.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.idiologue.api.RepositoryModificationRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/repository")
public class RepositoryModificationController {

    private static final Logger LOG = LogManager.getLogger(RepositoryModificationController.class);

    @PutMapping("/modify")
    void modifyRepository(Principal principal, @RequestBody RepositoryModificationRequest modificationRequest) {
        LOG.info("Got modification request {} from principal {}", modificationRequest, principal);
    }

}
