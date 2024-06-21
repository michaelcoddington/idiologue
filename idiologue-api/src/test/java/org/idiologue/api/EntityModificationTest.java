package org.idiologue.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("An entity modification")
public class EntityModificationTest {

    @Test
    @DisplayName("should be marshalled to JSON and back again")
    void testMarshalAndUnmarshal() throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        var create = new EntityCreateModification();
        create.setType("test:node");
        create.setProperties(Map.of("propA", new StringMetadata("valueA")));
        var createString = mapper.writeValueAsString(create);
        var createBack = mapper.readValue(createString, EntityModification.class);
        assertEquals(create, createBack);

        var update = new EntityUpdateModification();
        update.setProperties(Map.of("propA", new StringMetadata("valueA")));
        var updateString = mapper.writeValueAsString(update);
        var updateBack = mapper.readValue(updateString, EntityModification.class);
        assertEquals(update, updateBack);

        var delete = new EntityDeleteModification();
        delete.setId(1234L);
        var deleteString = mapper.writeValueAsString(delete);
        var deleteBack = mapper.readValue(deleteString, EntityModification.class);
        assertEquals(delete, deleteBack);
    }

}
