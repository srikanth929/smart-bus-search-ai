package com.example.busai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIChatResponse {
    private String message;

    @Builder.Default
    private List<BusResponse> buses = Collections.emptyList();
}
