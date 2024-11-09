package com.example.demo.controller;

import com.example.demo.Program.Call_Func.Call_func;
import com.example.demo.Program.main.main;
import com.example.demo.Program.user.user;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;

@Service
public class CppCompilerService {

    @Value("${custom.output.directory}")
    private String outputDirectory;

    @Value("${custom.input.directory}")
    private String inputDirectory;

    @Value("${custom.expected.output.file}")
    private String expectedOutputFilePath;

    private final String expectedFunctionSignature = "int add_nums(int, int)";

    @Async("taskExecutor")
    public CompletableFuture<Void> compileAndVerifyCppCodeAsync(String userFunction) {
        try {
            // Step 1: Validate function signature
            if (!validateFunctionSignature(userFunction, expectedFunctionSignature)) {
                throw new IllegalArgumentException("Function signature does not match the expected format.");
            }

            // Step 3: Save code to a temporary file
            Path tempDir = Files.createTempDirectory("cppCode");
            saveToFile(tempDir, "user_function.h", new user().generateUserFunctionHeader());
            saveToFile(tempDir, "user_function.cpp", new user().generateUserFunctionFile(userFunction));
            saveToFile(tempDir, "call_func.cpp", new Call_func().generateCallFuncFile());
            saveToFile(tempDir, "main.cpp", new main().generateMainFile());
            for(int i=0;i<10;i++) {
                saveToFile(tempDir, "input"+i+".txt", Files.readString(Paths.get(inputDirectory + "input"+i+".txt")));

            }

            ProcessBuilder compileProcessBuilder = new ProcessBuilder(
                    "g++",
                    tempDir.resolve("main.cpp").toString(),
                    tempDir.resolve("call_func.cpp").toString(),
                    tempDir.resolve("user_function.cpp").toString(),
                    "-o", tempDir.resolve("Main").toString()
            );
            Process compileProcess = compileProcessBuilder.start();
            compileProcess.waitFor();

            // Capture compilation errors
            String compileErrors = new String(compileProcess.getErrorStream().readAllBytes());
            if (!compileErrors.isEmpty()) {
                throw new RuntimeException("Compilation Error:\n" + compileErrors);
            }

            // Step 5: Run the compiled program
            ProcessBuilder runProcessBuilder = new ProcessBuilder("./Main");
            runProcessBuilder.directory(tempDir.toFile());
            Process runProcess = runProcessBuilder.start();
            runProcess.waitFor();

            // Step 6: Verify outputs
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                Path generatedOutputFile = Paths.get(tempDir.toString(), "output" + i + ".txt");
                Path expectedOutputFile = Paths.get(expectedOutputFilePath + "output"+i+".txt");

                String generatedOutput = Files.readString(generatedOutputFile).trim();
                String expectedOutput = Files.readString(expectedOutputFile).trim();

                if (generatedOutput.equals(expectedOutput)) {
                    result.append("Test case ").append(i).append(": Passed\n");
                } else {
                    result.append("Test case ").append(i).append(": Failed\nGenerated Output:\n")
                            .append(generatedOutput).append("\nExpected Output:\n").append(expectedOutput).append("\n");
                }
            }

            System.out.println(result.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(null);
    }

    // Validate function signature, allowing line breaks and spaces more flexibly
    private boolean validateFunctionSignature(String userFunction, String expectedSignature) {
        // Remove all newline characters and extra spaces to make validation easier
        String normalizedFunction = userFunction.replaceAll("\\s+", " ").trim();

        // Updated regex to allow parameter names with flexible formatting
        String regex = "^int\\s+add_nums\\s*\\(\\s*int\\s+\\w+\\s*,\\s*int\\s+\\w+\\s*\\)\\s*\\{.*";

        return normalizedFunction.matches(regex);
    }

    private void saveToFile(Path directory, String filename, String content) throws IOException {
        Path filePath = directory.resolve(filename);
        Files.writeString(filePath, content, StandardOpenOption.CREATE);
    }
}
