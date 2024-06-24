package com.ms.rest_api.domain.event.dto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record EventDetailsDto(
        UUID id,
        String title,
        String description,
        Date date,
        String city,
        String uf,
        String imgUrl,
        String eventUrl,
        List<CouponDto> coupons) {

    public record CouponDto(
            String code,
            Integer discount,
            Date validUntil) {
    }
}