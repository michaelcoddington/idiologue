package org.idiologue.server.controller;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.idiologue.api.Entity;
import org.idiologue.api.Metadata;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

@RestController
@RequestMapping("/v1/entity")
public class EntityController {

    @GetMapping("/{id}")
    String getEntity(@PathVariable("id") String id) {
        return String.format("I'm entity %s", id);
    }

    @PutMapping("/mutate")
    String mutateRepository(@RequestBody Entity entity) {
        try {
            System.out.println("Inserting...");

            GraphTraversalSource g = traversal().withRemote("conf/remote-graph.properties");

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
