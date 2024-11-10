package com.example.demo.model;

public class CompileRequest {
    private String cppCode;
    private int problemId;  // New field for problem ID

    // Getters and Setters
    public String getCppCode() {
        return cppCode;
    }

    public void setCppCode(String cppCode) {
        this.cppCode = cppCode;
    }

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }
}
