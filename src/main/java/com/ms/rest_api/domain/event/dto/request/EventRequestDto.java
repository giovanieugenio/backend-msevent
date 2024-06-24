package com.ms.rest_api.domain.event.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record EventRequestDto(
        String title,
        String description,
        Long date,
        String city,
        String state,
        Boolean remote,
        String eventUrl,
        MultipartFile image) {}
