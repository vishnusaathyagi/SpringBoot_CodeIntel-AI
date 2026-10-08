package com.example.CodeIntel_AI.controller.dto;

public class CodeReviewRequest {
    private String language;
    private String context;
    private String codeSnippet;

    public CodeReviewRequest() {}

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }

    public String getCodeSnippet() { return codeSnippet; }
    public void setCodeSnippet(String codeSnippet) { this.codeSnippet = codeSnippet; }
}