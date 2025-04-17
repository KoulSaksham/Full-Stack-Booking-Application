package com.saksham.booking_application.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.saksham.booking_application.entity.Room;

public interface RoomRepository extends JpaRepository<Room, String> {

    @Query(value = """
            SELECT * FROM rooms
            WHERE id NOT IN (
                SELECT room_id FROM reservations
                WHERE status = 'BOOKED'
                AND check_in_date <= :checkOutDate
                AND check_out_date >= :checkInDate
            )
            AND (
                :searchText IS NULL OR (
                    LOWER(location) LIKE LOWER(CONCAT('%', :searchText, '%')) OR
                    LOWER(room_type) LIKE LOWER(CONCAT('%', :searchText, '%')) OR
                    LOWER(name) LIKE LOWER(CONCAT('%', :searchText, '%'))
                )
            )
            AND (:roomType IS NULL OR room_type = :roomType)
            AND (:occupancy IS NULL OR occupancy >= :occupancy)
            AND (:price IS NULL OR price = :price)
            AND (:location IS NULL OR location = :location)
            """, nativeQuery = true)
    Page<Room> findAvailableRoomsWithFilters(
            @Param("searchText") String searchText,
            @Param("roomType") String roomType,
            @Param("occupancy") String occupancy,
            @Param("price") Double price,
            @Param("location") String location,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            Pageable pageable);

    @Query("SELECT DISTINCT r.location from Room r ")
    List<String> findAllTheLocations();

    @Query("SELECT DISTINCT r.occupancy from Room r ")
    List<String> findDistinctOccupancies();

    @Query("SELECT DISTINCT r.price from Room r ")
    List<String> findDistinctPrices();

}
