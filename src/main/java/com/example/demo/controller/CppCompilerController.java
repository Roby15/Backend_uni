package com.example.demo.controller;

import com.example.demo.model.CompileRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@RestController
@RequestMapping("/api/compile")
public class CppCompilerController {

    @PostMapping
    public ResponseEntity<String> compileCppCode(@RequestBody CompileRequest compileRequest) {
        String cppCode = compileRequest.getCppCode();
        Path tempDir = null;
        try {
            // Step 1: Save the code to a temporary file
            tempDir = Files.createTempDirectory("cppCode");
            Path sourceFile = tempDir.resolve("Main.cpp");
            Files.writeString(sourceFile, cppCode, StandardOpenOption.CREATE);

            // Step 2: Compile the code using g++
            ProcessBuilder compileProcessBuilder = new ProcessBuilder("g++", sourceFile.toString(), "-o", tempDir.resolve("Main").toString());
            Process compileProcess = compileProcessBuilder.start();
            int compileExitCode = compileProcess.waitFor();

            // Capture compilation errors
            String compileErrors = new String(compileProcess.getErrorStream().readAllBytes());
            if (compileExitCode != 0) {
                return ResponseEntity.badRequest().body("Compilation Error:\n" + compileErrors);
            }

            // Step 3: Run the compiled program if compilation succeeded
            ProcessBuilder runProcessBuilder = new ProcessBuilder(tempDir.resolve("Main").toString());
            Process runProcess = runProcessBuilder.start();
            int runExitCode = runProcess.waitFor();

            // Capture runtime output and errors
            String output = new String(runProcess.getInputStream().readAllBytes());
            String errors = new String(runProcess.getErrorStream().readAllBytes());

            // Return output or errors
            if (runExitCode != 0) {
                return ResponseEntity.ok("Runtime Error:\n" + errors);
            } else {
                return ResponseEntity.ok("Output:\n" + output);
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during compilation or execution: " + e.getMessage());
        } finally {
            // Clean up temporary files
            if (tempDir != null) {
                try {
                    Files.walk(tempDir)
                            .map(Path::toFile)
                            .forEach(File::delete);
                } catch (IOException e) {
                    // Log the error
                }
            }
        }
    }
}