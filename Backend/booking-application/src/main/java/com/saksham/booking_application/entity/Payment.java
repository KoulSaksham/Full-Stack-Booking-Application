package com.saksham.booking_application.entity;


import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table
public class Payment {

    @Id
    @Column(length = 36)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Column(nullable = false)
    private Double amount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    public enum PaymentMethod {
        CARD, UPI, CASH, NETBANKING
    }

    public Payment() {
        this.id = UUID.randomUUID().toString();
    }

    public Payment(PaymentMethod method, Double amount, Reservation reservation) {
        this();
        this.paymentMethod = method;
        this.amount = amount;
        this.reservation = reservation;
    }

    // Getters
    public String getId() {
        return id;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public Double getAmount() {
        return amount;
    }

    public Reservation getReservation() {
        return reservation;
    }
}
