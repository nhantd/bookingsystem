package dev.nhantd.bookingapi.model;

import java.time.LocalDate;

public class Booking extends TimeSlot {
    private String id;
    private String name;

    public Booking(LocalDate startDate, LocalDate endDate, String name, String id) {
        super(startDate, endDate);
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
