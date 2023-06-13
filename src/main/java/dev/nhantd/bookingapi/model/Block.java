package dev.nhantd.bookingapi.model;

import java.time.LocalDate;

public class Block extends TimeSlot{
    private String id;
    private String name;

    public Block(LocalDate startDate, LocalDate endDate, String id, String name) {
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
