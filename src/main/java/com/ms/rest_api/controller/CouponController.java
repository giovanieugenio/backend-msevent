package com.ms.rest_api.controller;

import com.ms.rest_api.domain.coupon.Coupon;
import com.ms.rest_api.domain.event.dto.request.CouponRequestDto;
import com.ms.rest_api.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/coupon")
public class CouponController {

    private final CouponService service;

    @PostMapping("/event/{eventId}")
    public ResponseEntity<Coupon> linkCouponToEvent(@PathVariable UUID eventId, @RequestBody CouponRequestDto dto){
        Coupon coupon = service.create(eventId, dto);
        return ResponseEntity.ok(coupon);
    }


}
