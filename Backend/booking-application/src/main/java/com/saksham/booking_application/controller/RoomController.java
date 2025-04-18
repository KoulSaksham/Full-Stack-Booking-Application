package com.saksham.booking_application.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saksham.booking_application.common.exceptions.ErrorCode;
import com.saksham.booking_application.common.exceptions.ServiceException;
import com.saksham.booking_application.dto.PaginatedResponse;
import com.saksham.booking_application.entity.Room;
import com.saksham.booking_application.entity.Room.RoomType;
import com.saksham.booking_application.services.RoomService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaginatedResponse<Room>> getAvailableRooms(
            @RequestParam(required = false, defaultValue = "") String searchText,
            @RequestParam(name = "limit") int limit,
            @RequestParam(name = "offset") int offset, @RequestParam(required = false, defaultValue = "") String order,
            @RequestParam(required = false, defaultValue = "") String orderBy,
            @RequestParam LocalDate checkInDate, @RequestParam LocalDate checkOutDate,
            @RequestParam(required = false) Map<String, String> filters) throws ServiceException {
        try {
            return ResponseEntity.ok().body(
                    roomService.getAvailableRooms(searchText, filters, limit, offset, order, orderBy, checkInDate,
                            checkOutDate));
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching rooms : {}", e.getMessage());
            e.printStackTrace();
            throw new ServiceException(ErrorCode.SERVER_ERROR, "Unexpected error occured.");
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getParticularRoomDetails(@PathVariable String id) throws ServiceException {
        return ResponseEntity.ok().body(roomService.getRoomById(id));
    }

    @GetMapping("/filters")
    public ResponseEntity<Map<?, ?>> getAllAvailableFilters() {
        return ResponseEntity.ok().body(roomService.getAllFilters());
    }

    @GetMapping("/location")
    public ResponseEntity<List<String>> getAllTheLocationValues() {
        return ResponseEntity.ok().body(roomService.getAllTheRoomLocations());
    }

    @GetMapping("/roomType")
    public ResponseEntity<RoomType[]> getAllRoomTypesValues() {
        return ResponseEntity.ok().body(RoomType.values());
    }

    @GetMapping("/price")
    public ResponseEntity<List<String>> getAllPriceValues() {
        return ResponseEntity.ok().body(roomService.getAllPrices());
    }

    @GetMapping("/occupancy")
    public ResponseEntity<List<String>> getAllOccupancies() {
        return ResponseEntity.ok().body(roomService.getAllOccupancies());
    }

}
