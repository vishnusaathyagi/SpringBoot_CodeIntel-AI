package com.example.CodeIntel_AI.controller;

import com.example.CodeIntel_AI.controller.dto.CodeReviewRequest;
import com.example.CodeIntel_AI.service.GeminiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ai")
@CrossOrigin(origins = "*")
public class CodeReviewController {

    private final GeminiService geminiService;

    public CodeReviewController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/review")
    public String reviewCode(@RequestBody CodeReviewRequest request) {
        String prompt = String.format("""
            You are an expert Senior Web Architect & Code Auditor specializing in %s.
            Perform an in-depth, production-grade review for the provided snippet.
            
            Context: %s
            Language / Format: %s
            
            Code Snippet:
            ```%s
            %s
            ```
            
            Audit Checklist based on Language:
            - **JavaScript / TS**: Event listener memory leaks, async/await error handling, DOM manipulation overhead, type safety.
            - **HTML**: Semantic structure, accessibility (aria tags/a11y), SEO optimization, broken tag hierarchy.
            - **CSS**: Reflow/Repaint performance (flex/grid efficiency), responsive design breaks, unused selectors, specificity issues.
            - **Java**: Concurrency, memory efficiency, Spring Security vulnerabilities, SQL injection/ORM traps.
            
            Format output cleanly with clear Markdown headers (###).
            """,
                request.getLanguage(),
                request.getContext(),
                request.getLanguage(),
                request.getLanguage() != null ? request.getLanguage().toLowerCase() : "",
                request.getCodeSnippet()
        );

        return geminiService.getAiResponse(prompt);
    }
}