package com.example.busai.controller;

import com.example.busai.dto.AIChatRequest;
import com.example.busai.dto.AIChatResponse;
import com.example.busai.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AI Chat", description = "Natural-language bus search powered by Spring AI tool calling")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;

    @Operation(summary = "Send a natural-language message and get an AI-generated response " +
            "backed by real tool-called bus/seat data")
    @PostMapping("/chat")
    public ResponseEntity<AIChatResponse> chat(@Valid @RequestBody AIChatRequest request) {
        AIChatResponse response = aiService.chat(request.getMessage(), request.getConversationId());
        return ResponseEntity.ok(response);
    }
}
