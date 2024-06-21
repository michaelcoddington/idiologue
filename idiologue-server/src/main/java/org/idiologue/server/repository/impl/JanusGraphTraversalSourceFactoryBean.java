package org.idiologue.server.repository.impl;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.idiologue.server.repository.GraphTraversalSourceFactoryBean;
import org.springframework.stereotype.Component;

import static org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal;

@Component
public class JanusGraphTraversalSourceFactoryBean implements GraphTraversalSourceFactoryBean {

    @Override
    public GraphTraversalSource getObject() throws Exception {

        /*
        Configuration config = new PropertiesConfiguration();
        config.setProperty("gremlin.graph", "org.janusgraph.core.JanusGraphFactory");
        config.setProperty("storage.backend", "berkeleyje");
        config.setProperty("storage.directory", "db/berkeleyje");
        config.setProperty("index.search.backend", "lucene");
        config.setProperty("index.search.directory", "db/lucene");

        return JanusGraphFactory.open(config).traversal();

         */

        Configuration config = new PropertiesConfiguration();
        config.setProperty("gremlin.remote.remoteConnectionClass", "org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection");
        config.setProperty("gremlin.remote.driver.sourceName", "g");
        config.setProperty("clusterConfiguration.hosts", "localhost");
        config.setProperty("clusterConfiguration.port", 8182);
        config.setProperty("clusterConfiguration.serializer.className", "org.apache.tinkerpop.gremlin.util.ser.GraphBinaryMessageSerializerV1");
        config.setProperty("clsuterConfiguration.serializer.config.ioRegistries", "org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry");

        return traversal().withRemote(config);
    }

}
