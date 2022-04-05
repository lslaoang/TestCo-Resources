package com.testco.resources.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {

    @GetMapping("/resource")
    public ResponseEntity<?> getResource(){
        System.out.println("Successfully retrieved!");
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
}
