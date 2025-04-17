package com.saksham.booking_application.controller;

import org.springframework.web.bind.annotation.RestController;

import com.saksham.booking_application.entity.Payment.PaymentMethod;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    @GetMapping(path = "/types", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentMethod[]> getAvailablePaymentMethods() {
        return ResponseEntity.ok().body(PaymentMethod.values());
    }

}
