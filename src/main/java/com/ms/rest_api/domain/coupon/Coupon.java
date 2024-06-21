package com.ms.rest_api.domain.coupon;

import com.ms.rest_api.domain.event.Event;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter @Getter
@Table(name = "coupon")
@Entity
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private String code;
    private String discount;
    private Date valid;

    @ManyToOne
    @JoinColumn(referencedColumnName = "event_id")
    private Event event;
}
