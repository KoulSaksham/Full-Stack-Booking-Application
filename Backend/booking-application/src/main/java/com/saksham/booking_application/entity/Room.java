package com.saksham.booking_application.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @Column(length = 36, updatable = false)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "room_number", nullable = false, unique = true)
    private String roomNumber;

    @Column(length = 255, nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", nullable = false, length = 20)
    private RoomType roomType;

    @Column(nullable = false)
    private int occupancy;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = true)
    private String description;

    public enum RoomType {
        STANDARD, DELUXE, SUITE, EXECUTIVE, FAMILY
    }

    public Room() {
        this.id = UUID.randomUUID().toString();
    }

    public Room(String name, String roomNumber, String location, RoomType roomType, int occupancy, Double price) {
        this();
        this.name = name;
        this.roomNumber = roomNumber;
        this.location = location;
        this.roomType = roomType;
        this.occupancy = occupancy;
        this.price = price;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getLocation() {
        return location;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public int getOccupancy() {
        return occupancy;
    }

    public Double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String url) {
        this.imageUrl = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

}
