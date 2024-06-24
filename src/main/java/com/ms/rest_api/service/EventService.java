package com.ms.rest_api.service;

import com.amazonaws.services.s3.AmazonS3;
import com.ms.rest_api.domain.coupon.Coupon;
import com.ms.rest_api.domain.event.Event;
import com.ms.rest_api.domain.event.dto.EventDetailsDto;
import com.ms.rest_api.domain.event.dto.request.EventRequestDto;
import com.ms.rest_api.domain.event.dto.response.EventResponseDto;
import com.ms.rest_api.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventService {

    private final AmazonS3 s3Client;

    private final EventRepository repository;

    private final AddressService addressService;

    private final CouponService couponService;

    @Value("${aws.bucket.name}")
    private String bucketName;

    public Event createEvent(EventRequestDto dto){
        String imgUrl = null;
        if (dto.image() != null){
            imgUrl = this.uploadImg(dto.image());
        }
        Event newEvent = new Event();
        newEvent.setTitle(dto.title());
        newEvent.setDescription(dto.description());
        newEvent.setEventUrl(dto.eventUrl());
        newEvent.setDate(new Date(dto.date()));
        newEvent.setImgUrl(imgUrl);
        newEvent.setRemote(dto.remote());
        repository.save(newEvent);
        if (!dto.remote()){
            this.addressService.createAddress(dto, newEvent);
        }
        return newEvent;
    }

    private String uploadImg(MultipartFile image) {
        String fileName = UUID.randomUUID() + "-" + image.getOriginalFilename();
        try{
            File file = this.convertMultipartFile(image);
            s3Client.putObject(bucketName, fileName, file);
            file.delete();
            return s3Client.getUrl(bucketName, fileName).toString();
        } catch (Exception e) {
            System.out.println("Error upload");
            return "";
        }
    }

    private File convertMultipartFile(MultipartFile image) throws IOException {
        File convFile = new File(Objects.requireNonNull(image.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(image.getBytes());
        fos.close();
        return convFile;
    }

    public List<EventResponseDto> getUpComingEvents(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventPage = this.repository.findUpComingEvents(new Date(), pageable);
        return eventPage.map(event -> new EventResponseDto(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getAddress() != null ? event.getAddress().getCity() : "",
                        event.getAddress() != null ? event.getAddress().getUf() : "",
                        event.getRemote(),
                        event.getEventUrl(),
                        event.getImgUrl()))
                .stream().toList();
    }

    public List<EventResponseDto> getFilteredEvents(int page, int size, String title, String city, String uf, Date startDate, Date endDate) {
        title = (title != null) ? title : "";
        city = (city != null) ? city : "";
        uf = (uf != null) ? uf : "";
        startDate = (startDate != null) ? startDate : new Date(0);
        endDate = (endDate != null) ? startDate : new Date();

        Pageable pageable = PageRequest.of(page, size);
        Page<Event> eventPage = this.repository.findFilteredEvents(title, city, uf, startDate, endDate, pageable);
        return eventPage.map(event -> new EventResponseDto(
                        event.getId(),
                        event.getTitle(),
                        event.getDescription(),
                        event.getDate(),
                        event.getAddress() != null ? event.getAddress().getCity() : "",
                        event.getAddress() != null ? event.getAddress().getUf() : "",
                        event.getRemote(),
                        event.getEventUrl(),
                        event.getImgUrl()))
                .stream().toList();

    }

    public EventDetailsDto getEventDetails(UUID eventId) {
        Event event = repository.findById(eventId).orElseThrow(
                ()-> new IllegalArgumentException("Event not found."));
        List<Coupon> coupons = couponService.consultCoupons(eventId, new Date());
        List<EventDetailsDto.CouponDto> couponDtos = coupons.stream()
                .map(coupon -> new EventDetailsDto.CouponDto(
                        coupon.getCode(),
                        coupon.getDiscount(),
                        coupon.getValid())).collect(Collectors.toList());
        return new EventDetailsDto(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getAddress() != null ? event.getAddress().getCity() : "",
                event.getAddress() != null ? event.getAddress().getUf() : "",
                event.getImgUrl(),
                event.getEventUrl(),
                couponDtos);
    }
}
