package com.example.busai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AIChatRequest {

    @NotBlank(message = "message is required")
    private String message;

    /**
     * Optional session/conversation id so the client can maintain a
     * multi-turn conversation (e.g. when the AI asks a clarifying question).
     */
    private String conversationId;
}
