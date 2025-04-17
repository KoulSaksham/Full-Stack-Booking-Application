package com.saksham.booking_application.dto;

import java.time.LocalDate;

import com.saksham.booking_application.entity.Reservation;

import lombok.Getter;

@Getter
public class ReservationResponseDTO {
    private String reservationId;
    private String roomNumber;
    private String roomName;
    private String userId;
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String paymentMethod;
    private Double amount;
    private String status;

    public ReservationResponseDTO(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.roomNumber = reservation.getRoom().getRoomNumber();
        this.roomName = reservation.getRoom().getName();
        this.userEmail = reservation.getUser().getEmail();
        this.userId = reservation.getUser().getId();
        this.userFirstName = reservation.getUser().getFirstname();
        this.userLastName = reservation.getUser().getLastname();
        this.checkInDate = reservation.getCheckInDate();
        this.checkOutDate = reservation.getCheckOutDate();
        this.status = reservation.getStatus().name();

        if (reservation.getPayment() != null) {
            this.paymentMethod = reservation.getPayment().getPaymentMethod().name();
            this.amount = reservation.getPayment().getAmount();
        }
    }
}
