package com.jw.dto.reservation;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReservationResult {
    private Long orderId;
    private String status;
    private String message;
}
