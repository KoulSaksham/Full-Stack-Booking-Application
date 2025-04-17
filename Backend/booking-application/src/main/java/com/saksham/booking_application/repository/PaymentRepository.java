package com.saksham.booking_application.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saksham.booking_application.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, String> {

}
