package com.saksham.booking_application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.saksham.booking_application.entity.Reservation;
import com.saksham.booking_application.entity.Room;

public interface ReservationRepository extends JpaRepository<Reservation, String> {

    Page<Reservation> findByRoom(Room room, Pageable pageable);

    @Query("""
                SELECT r FROM Reservation r
                LEFT JOIN r.user u
                LEFT JOIN r.payment p
                LEFT JOIN r.room rm
                WHERE LOWER(u.email) LIKE CONCAT('%', LOWER(:searchText), '%')
                   OR LOWER(u.username) LIKE CONCAT('%' , LOWER(:searchText), '%')
                   OR LOWER(u.firstname) LIKE CONCAT('%', LOWER(:searchText), '%')
                   OR LOWER(p.paymentMethod) LIKE CONCAT('%', LOWER(:searchText), '%')
                   OR LOWER(rm.roomType) LIKE CONCAT('%', LOWER(:searchText), '%')
            """)
    Page<Reservation> searchReservations(@Param("searchText") String searchText, Pageable pageable);

    @Query("""
                SELECT r FROM Reservation r
                LEFT JOIN r.user u
                LEFT JOIN r.payment p
                LEFT JOIN r.room rm
                WHERE u.id = :userId
                AND ( LOWER(u.email) LIKE CONCAT('%', LOWER(:searchText), '%')
                   OR LOWER(u.username) LIKE CONCAT('%' , LOWER(:searchText), '%')
                   OR LOWER(u.firstname) LIKE CONCAT('%', LOWER(:searchText), '%')
                   OR LOWER(p.paymentMethod) LIKE CONCAT('%', LOWER(:searchText), '%')
                   OR LOWER(rm.roomType) LIKE CONCAT('%', LOWER(:searchText), '%') )
            """)
    Page<Reservation> searchReservationsPerUser(@Param("userId") String userId, @Param("searchText") String searchText,
            Pageable pageable);

    @Query("""
            SELECT r FROM Reservation r
            LEFT JOIN r.user u
            WHERE u.id = :userId
            """)
    Page<Reservation> findAllReservationsPerUser(@Param("userId") String userId, Pageable pageable);

}
