package com.saksham.booking_application.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saksham.booking_application.common.exceptions.ErrorCode;
import com.saksham.booking_application.common.exceptions.ServiceException;
import com.saksham.booking_application.dto.CreateReservationDTO;
import com.saksham.booking_application.dto.PaginatedResponse;
import com.saksham.booking_application.dto.ReservationResponseDTO;
import com.saksham.booking_application.dto.UpdateReservationDTO;
import com.saksham.booking_application.jwt.UserContextHolder;
import com.saksham.booking_application.services.ReservationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservation")
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    private ResponseEntity<PaginatedResponse<ReservationResponseDTO>> getAllReservations(
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(name = "limit") int limit,
            @RequestParam(name = "offset") int offset, @RequestParam(required = false, defaultValue = "") String order,
            @RequestParam(required = false, defaultValue = "") String orderBy) throws ServiceException {
        if (UserContextHolder.get().isAdmin()) {
            try {
                return ResponseEntity.ok()
                        .body(reservationService.getAllReservations(searchText, limit, offset, order, orderBy));
            } catch (ServiceException e) {
                throw e;
            } catch (Exception e) {
                log.error("Unexpected error while fetching reservations : ", e.getMessage());
                e.printStackTrace();
                throw new ServiceException(ErrorCode.SERVER_ERROR);
            }
        } else {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "User needs to be admin to access this resource.");
        }

    }

    @GetMapping("/{userId}")
    public ResponseEntity<PaginatedResponse<ReservationResponseDTO>> getReservationByUserId(@PathVariable String userId,
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(name = "limit") int limit,
            @RequestParam(name = "offset") int offset, @RequestParam(required = false, defaultValue = "") String order,
            @RequestParam(required = false, defaultValue = "") String orderBy) throws ServiceException {
        try {
            return ResponseEntity.ok()
                    .body(reservationService.getReservationPerUser(userId, searchText, limit, offset, order, orderBy));
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while fetching reservations : ", e.getMessage());
            e.printStackTrace();
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
    }

    @PostMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<?, ?>> createNewReservation(@RequestBody CreateReservationDTO createReservationDTO)
            throws ServiceException {
        try {
            return ResponseEntity.ok().body(reservationService.createReservation(createReservationDTO));
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while creating reservation for userId : {}",
                    createReservationDTO.getUserId());
            e.printStackTrace();
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
    }

    @PatchMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<?, ?>> updateReservationDetails(@RequestBody UpdateReservationDTO updateReservationDTO)
            throws ServiceException {
        try {
            return ResponseEntity.ok().body(reservationService.updateReservation(updateReservationDTO));
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating reservation with id : {}",
                    updateReservationDTO.getReservationId());
            e.printStackTrace();
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<?, ?>> cancelReservation(@PathVariable(name = "id") String reservationId)
            throws ServiceException {
        try {
            return ResponseEntity.ok().body(reservationService.cancelReservation(reservationId));
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error while updating reservation with id : {}",
                    reservationId);
            e.printStackTrace();
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
    }

}
