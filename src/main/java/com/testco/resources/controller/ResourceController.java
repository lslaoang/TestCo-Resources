package com.testco.resources.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class ResourceController {

    private static final Logger LOGGER = Logger.getLogger(ResourceController.class.getName());

    @GetMapping("/resource")
    public ResponseEntity<?> getResource() {
        LOGGER.info("Resources retrieved successfully!");
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
