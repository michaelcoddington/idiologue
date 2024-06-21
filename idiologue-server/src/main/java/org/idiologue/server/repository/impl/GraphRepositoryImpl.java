package org.idiologue.server.repository.impl;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.idiologue.server.repository.GraphRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public class GraphRepositoryImpl implements GraphRepository {

    private GraphTraversalSource g;

    public GraphRepositoryImpl(GraphTraversalSource traversalSource) {
        this.g = traversalSource;
    }

    @Override
    public Map<Object, Object> vertexWithProperties(String label, Map<String, Object> properties) {
        var traversal = g.V();
        if (label != null) {
            traversal = traversal.hasLabel(label);
        }
        for (Map.Entry<String, Object> entry: properties.entrySet()) {
            traversal = traversal.has(entry.getKey(), entry.getValue());
        }
        return traversal.elementMap().next();
    }

}
