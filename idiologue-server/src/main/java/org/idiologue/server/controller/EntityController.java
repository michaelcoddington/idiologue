package org.idiologue.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/entity")
public class EntityController {

    @GetMapping("/{id}")
    String getEntity(@PathVariable("id") String id) {
        return String.format("I'm entity %s", id);
    }

}
