package org.idiologue.server.repository;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.springframework.beans.factory.FactoryBean;

public interface GraphTraversalSourceFactoryBean extends FactoryBean<GraphTraversalSource> {

    @Override
    default Class<?> getObjectType() {
        return GraphTraversalSource.class;
    }

    @Override
    default boolean isSingleton() {
        return true;
    }
}
