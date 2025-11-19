package com.example.todak_server.dto.request;

import java.util.List;

public record ChangeReadRequest(
        List<Long> changeLogIds,
        Long memberId
) {}