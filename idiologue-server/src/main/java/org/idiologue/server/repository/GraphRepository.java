package org.idiologue.server.repository;

import java.util.Map;

public interface GraphRepository {

    Map<Object, Object> vertexWithProperties(String label, Map<String, Object> properties);

}
