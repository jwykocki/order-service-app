package com.jw.service;

import com.jw.dto.reservation.ProductReservationRequest;
import com.jw.dto.reservation.ReservationResult;
import com.jw.resources.ReservationResource;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationResource reservationResource;

    public ReservationResult sendReservationRequest(
            ProductReservationRequest productReservationRequest) {
        log.info("Sending reservation request %s".formatted(productReservationRequest));
        return reservationResource.sendReservationRequest(productReservationRequest);
    }
}
