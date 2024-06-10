package org.idiologue.server.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.idiologue.server.service.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class DefaultUserService implements UserService, ApplicationListener<ContextRefreshedEvent> {

    private static final Logger LOG = LogManager.getLogger(DefaultUserService.class);

    private GraphTraversalSource g;

    DefaultUserService(GraphTraversalSource source) {
        this.g = source;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ensureAdminUser();
    }

    @Override
    public void ensureAdminUser() {
        if (!g.V().hasLabel("idiologue:user").has("name", "admin").hasNext()) {
            Vertex adminUser = g.addV("idiologue:user").property(Map.of("name", "admin", "accessToken", UUID.randomUUID().toString())).next();
            LOG.info("Created admin user with access token {}", adminUser.property("accessToken").value());
        }
    }

}
