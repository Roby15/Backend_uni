package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compile")
@CrossOrigin(origins = "*") // Allow all origins
public class CppCompilerController {

    @Autowired
    private CppCompilerService cppCompilerService;

    @PostMapping
    public ResponseEntity<String> compileAndVerifyCppCode(@RequestBody CodeRequest codeRequest) {
        cppCompilerService.compileAndVerifyCppCodeAsync(codeRequest.getCode());
        return ResponseEntity.ok("Request is being processed");
    }
}