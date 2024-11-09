package com.example.demo.controller;

import com.example.demo.Program.Call_Func.Call_func;
import com.example.demo.Program.user.user;
import com.example.demo.Program.main.main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/compile")
public class CppCompilerController {

    @Autowired
    private CppCompilerService cppCompilerService;

    @PostMapping
    public ResponseEntity<String> compileAndVerifyCppCode(@RequestBody String userFunction) {
        cppCompilerService.compileAndVerifyCppCodeAsync(userFunction);
        return ResponseEntity.ok("Request is being processed");
    }
}

