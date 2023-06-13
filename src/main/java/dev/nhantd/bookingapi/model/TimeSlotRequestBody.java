package dev.nhantd.bookingapi.model;

import java.time.LocalDate;

public class TimeSlotRequestBody extends TimeSlot {
    private String name;

    public TimeSlotRequestBody(LocalDate startDate, LocalDate endDate, String name) {
        super(startDate, endDate);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
