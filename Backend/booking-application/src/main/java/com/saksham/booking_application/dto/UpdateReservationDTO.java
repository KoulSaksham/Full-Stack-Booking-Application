package com.saksham.booking_application.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class UpdateReservationDTO {

    @NotBlank
    private String reservationId;

    private String newRoomId = null;

    private LocalDate newCheckInDate = null;

    private LocalDate newCheckOutDate = null;

    private String newStatus = null;
}
