package com.testco.resources.controller;

import com.testco.resources.service.VerifyAssignment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class ResourceController {

    private static final Logger LOGGER = Logger.getLogger(ResourceController.class.getName());
    private final VerifyAssignment verifyAssignment;

    public ResourceController(VerifyAssignment verifyAssignment) {
        this.verifyAssignment = verifyAssignment;
    }

    @GetMapping("/resource")
    public ResponseEntity<?> getResource() {
        try {
            verifyAssignment.authorize();
            LOGGER.info("Resources retrieved successfully!");
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        }catch (RuntimeException e){
            LOGGER.severe("Access to the resource denied. " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }



}
