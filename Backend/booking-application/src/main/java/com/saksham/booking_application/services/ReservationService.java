package com.saksham.booking_application.services;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.saksham.booking_application.common.exceptions.ErrorCode;
import com.saksham.booking_application.common.exceptions.ServiceException;
import com.saksham.booking_application.dto.CreateReservationDTO;
import com.saksham.booking_application.dto.PaginatedResponse;
import com.saksham.booking_application.dto.ReservationResponseDTO;
import com.saksham.booking_application.dto.UpdateReservationDTO;
import com.saksham.booking_application.entity.Payment;
import com.saksham.booking_application.entity.Reservation;
import com.saksham.booking_application.entity.Reservation.ReservationStatus;
import com.saksham.booking_application.entity.Room;
import com.saksham.booking_application.entity.User;
import com.saksham.booking_application.entity.Payment.PaymentMethod;
import com.saksham.booking_application.repository.PaymentRepository;
import com.saksham.booking_application.repository.ReservationRepository;
import com.saksham.booking_application.repository.RoomRepository;
import com.saksham.booking_application.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final List<String> validOrderBys = List.of("checkInDate", "checkOutDate", "status", "paymentMethod");

    public PaginatedResponse<ReservationResponseDTO> getAllReservations(String searchText, int limit, int offset,
            String order, String orderBy) throws ServiceException {
        int page = offset / limit;

        // Determine sort direction
        Sort.Direction direction = Sort.Direction.ASC;
        if ("desc".equalsIgnoreCase(order))
            direction = Sort.Direction.DESC;

        orderBy = validOrderBys
                .contains(orderBy) ? orderBy : "status";

        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by(direction, orderBy));
        Page<Reservation> reservationPage;

        if (!searchText.isBlank())
            reservationPage = reservationRepository.searchReservations(searchText.trim(), pageRequest);
        else
            reservationPage = reservationRepository.findAll(pageRequest);

        // Convert to DTO
        Page<ReservationResponseDTO> response = reservationPage.map(ReservationResponseDTO::new);
        return new PaginatedResponse<>(response);
    }

    public PaginatedResponse<ReservationResponseDTO> getReservationPerUser(String userId, String searchText, int limit,
            int offset,
            String order, String orderBy) throws ServiceException {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ServiceException(ErrorCode.BAD_REQUEST, "User not found with given id :" + userId));
        int page = offset / limit;

        // Determine sort direction
        Sort.Direction direction = Sort.Direction.ASC;
        if ("desc".equalsIgnoreCase(order))
            direction = Sort.Direction.DESC;

        orderBy = validOrderBys
                .contains(orderBy) ? orderBy : "status";

        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by(direction, orderBy));
        Page<Reservation> reservationPage;

        if (!searchText.isBlank())
            reservationPage = reservationRepository.searchReservationsPerUser(user.getId(), searchText.trim(),
                    pageRequest);
        else
            reservationPage = reservationRepository.findAllReservationsPerUser(user.getId(), pageRequest);

        // Convert to DTO
        Page<ReservationResponseDTO> response = reservationPage.map(ReservationResponseDTO::new);
        return new PaginatedResponse<>(response);
    }

    @Transactional
    public Map<?, ?> createReservation(@NonNull CreateReservationDTO createReservationDTO) throws ServiceException {

        Room room = roomRepository.findById(createReservationDTO.getRoomId())
                .orElseThrow(() -> new ServiceException(ErrorCode.BAD_REQUEST, "Room could not be found."));

        User user = userRepository.findById(createReservationDTO.getUserId())
                .orElseThrow(() -> new ServiceException(ErrorCode.BAD_REQUEST,
                        "User not found with given id : " + createReservationDTO.getUserId()));

        Reservation reservation = new Reservation(room, user,
                createReservationDTO.getCheckInDate(), createReservationDTO.getCheckOutDate());

        Payment payment = new Payment(PaymentMethod.valueOf(createReservationDTO.getPaymentMethod().toUpperCase()),
                createReservationDTO.getAmount(),
                reservation);
        reservation.setPayment(payment);
        reservation.setStatus(ReservationStatus.BOOKED);
        reservationRepository.save(reservation);
        paymentRepository.save(payment);
        return Map.of("msg", "Reservation created.");

    }

    @Transactional
    public Map<?, ?> updateReservation(UpdateReservationDTO updateReservationDTO) throws ServiceException {

        Reservation reservation = reservationRepository.findById(updateReservationDTO.getReservationId())
                .orElseThrow(() -> new ServiceException(ErrorCode.BAD_REQUEST,
                        "Reservation not found with ID: " + updateReservationDTO.getReservationId()));

        boolean updated = false;

        // Update room if provided
        if (updateReservationDTO.getNewRoomId() != null) {

            Room newRoom = roomRepository.findById(updateReservationDTO.getNewRoomId())
                    .orElseThrow(() -> new ServiceException(ErrorCode.BAD_REQUEST,
                            "Room not found with id: " + updateReservationDTO.getNewRoomId()));
            reservation.setRoom(newRoom);
            updated = true;
        }

        // Update dates
        if (updateReservationDTO.getNewCheckInDate() != null) {
            reservation.setCheckInDate(updateReservationDTO.getNewCheckInDate());
            updated = true;
        }

        if (updateReservationDTO.getNewCheckOutDate() != null) {
            reservation.setCheckOutDate(updateReservationDTO.getNewCheckOutDate());
            updated = true;
        }

        // Update status
        if (updateReservationDTO.getNewStatus() != null
                && ReservationStatus.valueOf(updateReservationDTO.getNewStatus()) != reservation.getStatus()) {
            reservation.setStatus(ReservationStatus.valueOf(updateReservationDTO.getNewStatus()));
            updated = true;
        }

        if (updated) {
            reservationRepository.save(reservation);
        }

        return Map.of("msg", "Reservation updated successfully.");
    }

    @Transactional
    public Map<?, ?> cancelReservation(String reservationId) throws ServiceException {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new ServiceException(ErrorCode.BAD_REQUEST, "Reservation not found with id : " + reservationId));
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        return Map.of("msg", "Reservation cancelled successfully.");
    }

}
