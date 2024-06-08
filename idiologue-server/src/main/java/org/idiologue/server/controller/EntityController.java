package org.idiologue.server.controller;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.idiologue.api.Entity;
import org.idiologue.api.Metadata;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping("/v1/entity")
public class EntityController {

    private GraphTraversalSource g;

    public EntityController(GraphTraversalSource traversalSource) {
        this.g = traversalSource;
    }

    @GetMapping("/{id}")
    Map<String, Object> getEntity(@PathVariable("id") Long id) {
        Map<String, Object> projection = g.V(id).
                project("id", "properties")
                .by(__.id())
                .by(__.valueMap())
                .next();

        return projection;
    }

    @GetMapping
    Flux<Map<String, Object>> getEntities() {
        Iterator<Map<String, Object>> projectionIterator = g.V().
                project("id", "properties")
                .by(__.id())
                .by(__.valueMap());

        return Flux.fromIterable(() -> projectionIterator);
    }

    @PutMapping("/mutate")
    String mutateRepository(@RequestBody Entity entity) {
        try {
            System.out.println("Inserting...");

            GraphTraversal<Vertex, Vertex> traversal = g.addV(entity.getType());
            for (Map.Entry<String, Metadata> entry: entity.getProperties().entrySet()) {
                String key = entry.getKey();
                Metadata value = entry.getValue();
                traversal = traversal.property(key, value.getVablue());
            }
            traversal.next();
            g.tx().commit();

        } catch (Exception er) {
            er.printStackTrace();
        }
        return "ok";
    }

}
