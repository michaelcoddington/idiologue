package org.idiologue.server.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.idiologue.api.Entity;
import org.idiologue.api.Metadata;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@RestController
@RequestMapping("/v1/entity")
public class EntityController {

    private static final Logger LOG = LogManager.getLogger(EntityController.class);

    private GraphTraversalSource g;

    public EntityController(GraphTraversalSource traversalSource) {
        this.g = traversalSource;
    }

    @GetMapping("/{id}")
    Map<String, Object> getEntity(@PathVariable("id") Long id) {
        Map<String, Object> projection = g.V(id).
                project("id", "type", "properties")
                .by(__.id())
                .by(__.label())
                .by(__.valueMap())
                .next();

        return projection;
    }

    @GetMapping
    Flux<Map<String, Object>> getEntities() {
        Iterator<Map<String, Object>> projectionIterator = g.V().
                project("id", "type", "properties")
                .by(__.id())
                .by(__.label())
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

    @PutMapping("/upload")
    Mono<Void> testUpload(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String contentType = request.getHeaders().getFirst("Content-Type");
        LOG.info("Uploading content type {}", contentType);
        // this is kinda ok, in that it writes a complete file, but unfortunately it also includes the multipart
        Flux<DataBuffer> buffer = request.getBody();
        File outfile = new File("/tmp/test.dat");
        try {
            LOG.info("Writing data");
            FileOutputStream fos = new FileOutputStream(outfile);
            Flux<DataBuffer> bufferFlux = DataBufferUtils.write(buffer, fos);
            Flux<Boolean> closeFlux = bufferFlux.map(DataBufferUtils::release);
            return closeFlux.then(Mono.fromRunnable(() -> {
                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException ioe2) {
                        LOG.error(ioe2);
                    }
                }
            }));
        } catch (IOException ioe) {
            LOG.error(ioe);
            throw new RuntimeException(ioe);
        }
    }

}
