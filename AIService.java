package com.example.busai.service;

import com.example.busai.dto.AIChatResponse;
import com.example.busai.exception.AIProviderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {

    private final ChatClient chatClient;

    public AIChatResponse chat(String userMessage, String conversationId) {
        try {
            String todayContext = "Today's date is " + LocalDate.now() +
                    ". Resolve any relative dates in the user's message against this date.";

            String reply = chatClient.prompt()
                    .system(todayContext)
                    .user(userMessage)
                    .advisors(a -> a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,
                            conversationId != null ? conversationId : "default"))
                    .call()
                    .content();

            return AIChatResponse.builder()
                    .message(reply)
                    .build();

        } catch (Exception ex) {
            log.error("AI chat/tool-calling failure for message='{}'", userMessage, ex);
            throw new AIProviderException("Failed to process the chat request with the AI provider", ex);
        }
    }
}
