package com.saksham.booking_application.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.saksham.booking_application.common.exceptions.ErrorCode;
import com.saksham.booking_application.common.exceptions.ServiceException;
import com.saksham.booking_application.dto.PaginatedResponse;
import com.saksham.booking_application.entity.Room;
import com.saksham.booking_application.entity.Room.RoomType;
import com.saksham.booking_application.repository.RoomRepository;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    private final List<String> filterList = List.of(ROOM_TYPE, OCCUPANCY, PRICE, LOCATION);
    private final List<String> validOrderBys = List.of("roomId", "location", "roomType", "occupancy", "price");
    private static final String CONTENT = "content";
    private static final String ROOM_TYPE = "roomType";
    private static final String OCCUPANCY = "occupancy";
    private static final String PRICE = "price";
    private static final String LOCATION = "location";

    public PaginatedResponse<Room> getAvailableRooms(String searchText, Map<String, String> filter, int limit,
            int offset,
            String order,
            String orderBy, LocalDate checkInDate, LocalDate checkOutDate) throws ServiceException {
        int page = offset / limit;

        // Determine sort direction (default to ASC if invalid)
        Sort.Direction direction = Sort.Direction.ASC;
        if ("desc".equalsIgnoreCase(order)) {
            direction = Sort.Direction.DESC;
        }
        // Sanitize searchText
        searchText = (searchText != null && !searchText.isBlank()) ? searchText.trim() : null;

        orderBy = validOrderBys
                .contains(orderBy) ? orderBy : "price";

        PageRequest pageRequest = PageRequest.of(page, limit, Sort.by(direction, orderBy));

        // Validate and extract filters
        RoomType roomType = null;
        String roomTypeString = null;
        String occupancy = null;
        String location = null;
        Double price = null;

        if (filter != null) {
            if (filter.get(ROOM_TYPE) != null) {
                try {
                    roomType = RoomType.valueOf(filter.get(ROOM_TYPE));
                    roomTypeString = roomType.name();
                } catch (IllegalArgumentException e) {
                    throw new ServiceException(ErrorCode.BAD_REQUEST, "Invalid room type : " + filter.get(ROOM_TYPE));
                }

            } else if (filter.get(OCCUPANCY) != null)
                occupancy = filter.get(OCCUPANCY);
            else if (filter.get(PRICE) != null) {
                try {
                    price = Double.parseDouble(filter.get(PRICE));
                } catch (NumberFormatException ignored) {
                }
            } else if (filter.get(LOCATION) != null) {
                location = filter.get(LOCATION);
            }

        }

        Page<Room> response = roomRepository.findAvailableRoomsWithFilters(
                searchText, roomTypeString, occupancy, price, location, checkInDate, checkOutDate, pageRequest);

        return new PaginatedResponse<>(response);
    }

    public Room getRoomById(String id) throws ServiceException {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ServiceException(ErrorCode.BAD_REQUEST, "Room not found with id : " + id));
    }

    public Map<?, ?> getAllFilters() {
        List<Map<String, String>> contentList = filterList.stream()
                .map(value -> Map.of("filter", value))
                .collect(Collectors.toList());

        return Map.of(CONTENT, contentList);
    }

    public List<String> getAllTheRoomLocations() {
        return roomRepository.findAllTheLocations();
    }
}
