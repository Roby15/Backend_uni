package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/compile")
@CrossOrigin(origins = "*") // Allow all origins
public class CppCompilerController {

    private static final Logger logger = LoggerFactory.getLogger(CppCompilerController.class);

    @Autowired
    private CppCompilerService cppCompilerService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> compileAndVerifyCppCode(@RequestBody CodeRequest codeRequest) {
        try {
            if (cppCompilerService == null) {
                logger.error("cppCompilerService is null");
                return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
            }
            if (codeRequest == null) {
                logger.error("codeRequest is null");
                return ResponseEntity.status(400).body(Map.of("error", "Bad request"));
            }
            CompletableFuture<Map<String, Object>> futureResult = cppCompilerService.compileAndVerifyCppCodeAsync(codeRequest.getCode(), codeRequest.getProblemID());
            Map<String, Object> result = futureResult.get(); // Wait for the result
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Exception occurred: ", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}