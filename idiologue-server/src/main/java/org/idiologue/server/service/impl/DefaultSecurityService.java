package org.idiologue.server.service.impl;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.idiologue.server.service.SecurityService;
import org.springframework.stereotype.Service;

@Service
public class DefaultSecurityService implements SecurityService {

    private GraphTraversalSource g;

    DefaultSecurityService(GraphTraversalSource source) {
        this.g = source;
    }

    @Override
    public boolean isValidAccessToken(String token) {
        return g.V().hasLabel("idiologue:user").has("accessToken", token).hasNext();
    }
}
