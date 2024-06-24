package com.ms.rest_api.service;

import com.ms.rest_api.domain.coupon.Coupon;
import com.ms.rest_api.domain.event.Event;
import com.ms.rest_api.domain.event.dto.request.CouponRequestDto;
import com.ms.rest_api.repositories.CouponRepository;
import com.ms.rest_api.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CouponService {

    private final CouponRepository couponRepository;

    private final EventRepository eventRepository;

    public Coupon create(UUID eventId, CouponRequestDto dto){
        Event event = eventRepository.findById(eventId).orElseThrow(
                ()-> new IllegalArgumentException("Event not found."));
        Coupon newCoupon = new Coupon();
        newCoupon.setCode(dto.code());
        newCoupon.setDiscount(dto.discount());
        newCoupon.setValid(new Date(dto.valid()));
        newCoupon.setEvent(event);
        return couponRepository.save(newCoupon);
    }

    public List<Coupon> consultCoupons(UUID eventId, Date currentDate) {
        return couponRepository.findByEventIdAndValidAfter(eventId, currentDate);
    }
}
