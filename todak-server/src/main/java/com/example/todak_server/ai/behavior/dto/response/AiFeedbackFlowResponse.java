package com.example.todak_server.ai.behavior.dto.response;

import com.example.todak_server.entity.FeedbackNextStep;

public record AiFeedbackFlowResponse(
        FeedbackNextStep nextStep
) {}
