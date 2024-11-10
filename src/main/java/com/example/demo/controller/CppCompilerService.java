package com.example.demo.controller;

import com.example.demo.Program.Call_Func.Call_func;
import com.example.demo.Program.user.user;
import com.example.demo.Program.main.main;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import com.example.demo.model.CompileRequest;

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
    public CompletableFuture<Map<String, Object>> compileAndVerifyCppCodeAsync(String userFunction,int problemId) throws IOException {
        List<ITestPass> testsPassed = new ArrayList<>();
        ITestPass.ECodeError errorMessage = null;
        Path filePathfun = Paths.get(outputDirectory+"p"+problemId+"/"+ "func.txt");
        String fileContent = Files.readString(filePathfun);
        Path filePathhed = Paths.get(outputDirectory+"p"+problemId+"/"+ "header.txt");
        String fileContenthed = Files.readString(filePathhed);
        try {

            // Step 1: Validate function signature

            // Step 3: Save code to a temporary file
            Path tempDir = Files.createTempDirectory("cppCode");
            saveToFile(tempDir, "user_function.h", new user().generateUserFunctionHeader(fileContenthed));
            saveToFile(tempDir, "user_function.cpp", new user().generateUserFunctionFile(userFunction));
            saveToFile(tempDir, "call_func.cpp", new Call_func().generateCallFuncFile(fileContent));
            saveToFile(tempDir, "main.cpp", new main().generateMainFile());
            for (int i = 0; i < 10; i++) {
                saveToFile(tempDir, "input" + i + ".txt", Files.readString(Paths.get(inputDirectory +  "p"+problemId+"/" + "input" + i + ".txt")));
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
                errorMessage = ITestPass.ECodeError.COMPILE_ERROR;
                throw new RuntimeException("Compilation Error:\n" + compileErrors);
            }

            // Step 5: Run the compiled program
            ProcessBuilder runProcessBuilder = new ProcessBuilder("./Main");
            runProcessBuilder.directory(tempDir.toFile());
            Process runProcess = runProcessBuilder.start();
            runProcess.waitFor();

            // Step 6: Verify outputs
            for (int i = 0; i < 10; i++) {
                Path generatedOutputFile = Paths.get(tempDir.toString(), "output" + i + ".txt");
                Path expectedOutputFile = Paths.get(expectedOutputFilePath + "p"+problemId+"/" + "output" + i + ".txt");

                String generatedOutput = Files.readString(generatedOutputFile).trim();
                String expectedOutput = Files.readString(expectedOutputFile).trim();

                ITestPass testPass = new ITestPass();
                testPass.setTestNr(i);
                testPass.setPoints(10); // Assuming 1 point per test case
                testPass.setExample(i == 0); // Assuming the first test case is an example
                testPass.setProblemId(problemId);// Set the problemId

                if (generatedOutput.equals(expectedOutput)) {
                    testPass.setError(null);
                } else {
                    testPass.setError(ITestPass.ECodeError.WRONG_ANSWER);
                }

                testsPassed.add(testPass);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return CompletableFuture.completedFuture(Map.of(
                "testsPassed", testsPassed != null ? testsPassed : new ArrayList<>(),
                "errorMessage", errorMessage != null ? errorMessage : ""
        ));
    }

    // Validate function signature, allowing line breaks and spaces more flexibly


    private void saveToFile(Path directory, String filename, String content) throws IOException {
        Path filePath = directory.resolve(filename);
        Files.writeString(filePath, content, StandardOpenOption.CREATE);
    }
}