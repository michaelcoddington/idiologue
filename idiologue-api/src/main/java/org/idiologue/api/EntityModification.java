package org.idiologue.api;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "action")
@JsonSubTypes({
        @JsonSubTypes.Type(value = EntityCreateModification.class, name = ActionConstants.CREATE),
        @JsonSubTypes.Type(value = EntityUpdateModification.class, name = ActionConstants.UPDATE),
        @JsonSubTypes.Type(value = EntityDeleteModification.class, name = ActionConstants.DELETE)
})
public class EntityModification {

}
