package com.ms.rest_api.domain.event.dto.request;

public record CouponRequestDto(
        String code,
        Integer discount,
        Long valid) {}
