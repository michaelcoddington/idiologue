package org.idiologue.server.repository.impl;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.idiologue.server.repository.GraphTraversalSourceFactoryBean;
import org.springframework.stereotype.Component;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

@Component
public class JanusGraphTraversalSourceFactoryBean implements GraphTraversalSourceFactoryBean {

    @Override
    public GraphTraversalSource getObject() throws Exception {
        return traversal().withRemote("conf/remote-graph.properties");
    }

}
