package com.example.demo.controller;

public class ITestPass {
    private int testNr;
    private ECodeError error;
    private int points;
    private boolean example;
    private int problemId; // Add this field

    // Getters and Setters
    public int getTestNr() {
        return testNr;
    }

    public void setTestNr(int testNr) {
        this.testNr = testNr;
    }

    public ECodeError getError() {
        return error;
    }

    public void setError(ECodeError error) {
        this.error = error;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isExample() {
        return example;
    }

    public void setExample(boolean example) {
        this.example = example;
    }



    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public enum ECodeError {
        COMPILE_ERROR,
        TIME_LIMIT_EXCEEDED,
        MEMORY_LIMIT_EXCEEDED,
        JSON_PARSE_ERROR, WRONG_ANSWER
    }
}