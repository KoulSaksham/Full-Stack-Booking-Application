package com.saksham.booking_application.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.saksham.booking_application.entity.Room;
import com.saksham.booking_application.entity.Room.RoomType;
import com.saksham.booking_application.entity.User;
import com.saksham.booking_application.repository.RoomRepository;
import com.saksham.booking_application.repository.UserRepository;

import jakarta.transaction.Transactional;

@Component
@Transactional
public class DataIntializer implements ApplicationRunner {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public DataIntializer(RoomRepository roomRepository, UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        seedAdminUser();
        seedRooms();
    }

    private void seedAdminUser() {
        final String adminEmail = "admin@hotelapp.com";
        if (userRepository.existsByEmail(adminEmail))
            return;

        User admin = new User(
                "admin",
                "Demo",
                "Admin User",
                adminEmail,
                "$2a$10$xgcnTqnJhAzOTOqLc7g.Puo7ma0KQLOfFUYcu8sjp6gORsojKTFq.");
        admin.setIsAdmin(true);
        userRepository.save(admin);
    }

    private void seedRooms() {
        if (roomRepository.count() > 0)
            return;
        List<String> imageUrls = List.of(
                "https://images.pexels.com/photos/271624/pexels-photo-271624.jpeg",
                "https://images.pexels.com/photos/271639/pexels-photo-271639.jpeg",
                "https://images.pexels.com/photos/271618/pexels-photo-271618.jpeg",
                "https://images.pexels.com/photos/271619/pexels-photo-271619.jpeg",
                "http://localhost:8082/backend/room1.jpg",
                "http://localhost:8082/backend/room2.jpg",
                "http://localhost:8082/backend/room3.jpg",
                "http://localhost:8082/backend/room4.jpg",
                "http://localhost:8082/backend/room5.jpg",
                "http://localhost:8082/backend/room11.jpg");
        List<String> roomNames = List.of(
                "City Center", "Downtown", "Airport Zone", "Beachside", "Hill View", "Old Town", "Business District");

        List<String> locations = List.of("New Delhi", "Mumbai", "Bangalore","Kolkata");
        List<Room> rooms = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i <= 10; i++) {
            String roomId = String.format("RM-%03d", i);
            String name = roomNames.get(i % roomNames.size());
            String location = locations.get(i % locations.size());
            RoomType type = RoomType.values()[i % RoomType.values().length];

            // Logical occupancy based on type
            int occupancy = switch (type) {
                case STANDARD -> 1 + random.nextInt(2); // 1-2
                case DELUXE -> 2 + random.nextInt(2); // 2-3
                case SUITE -> 2 + random.nextInt(3); // 2-4
                case EXECUTIVE -> 1 + random.nextInt(2); // 1-2
                case FAMILY -> 3 + random.nextInt(3); // 3-5
            };

            // Base price logic
            double basePrice = switch (type) {
                case STANDARD -> 100.0;
                case DELUXE -> 150.0;
                case SUITE -> 200.0;
                case EXECUTIVE -> 175.0;
                case FAMILY -> 225.0;
            };
            double price = basePrice + (occupancy * 20) + (i % 5) * 10;

            String imageUrl = imageUrls.get(i % imageUrls.size());

            // Generate smart description
            String description = String.format(
                    "Enjoy a %s room located in %s, ideal for %d guest%s. Features modern amenities and comfort at just $%.2f per night.",
                    type.name().toLowerCase(), name, occupancy, occupancy > 1 ? "s" : "", price);

            Room room = new Room(
                    name,
                    roomId,
                    location,
                    type,
                    occupancy,
                    price);

            room.setImageUrl(imageUrl);
            room.setDescription(description);
            rooms.add(room);
        }
        roomRepository.saveAll(rooms);
    }

}
