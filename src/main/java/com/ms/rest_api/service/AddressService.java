package com.ms.rest_api.service;

import com.ms.rest_api.domain.address.Address;
import com.ms.rest_api.domain.event.Event;
import com.ms.rest_api.domain.event.dto.request.EventRequestDto;
import com.ms.rest_api.repositories.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AddressService {

    private final AddressRepository repository;

    public Address createAddress(EventRequestDto dto, Event event){
        Address address = new Address();
        address.setCity(dto.city());
        address.setUf(dto.state());
        address.setEvent(event);
        return repository.save(address);
    }
}
