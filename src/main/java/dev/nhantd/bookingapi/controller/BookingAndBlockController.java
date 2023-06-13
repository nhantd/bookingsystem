package dev.nhantd.bookingapi.controller;

import dev.nhantd.bookingapi.model.Block;
import dev.nhantd.bookingapi.model.Booking;
import dev.nhantd.bookingapi.model.TimeSlotRequestBody;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api")
public class BookingAndBlockController {
    static final Logger LOGGER = LoggerFactory.getLogger(BookingAndBlockController.class);

    private List<Booking> bookings = new ArrayList<>();
    private List<Block> blocks = new ArrayList<>();

    @PostMapping("/booking")
    public ResponseEntity createBooking(@RequestBody TimeSlotRequestBody requestBody) {
        ResponseEntity validation = validateBadRequestBody(requestBody);
        if (validation != null) {
            return validation;
        }
        if (isBookingOverlappingWithBlocksOrExistingBookings(requestBody)) {
            LOGGER.info("Request time slot is overlapping with existing blocks or bookings, request {}", requestBody);
            return ResponseEntity.badRequest().body("Booking overlaps with existing existing blocks or bookings.");
        }
        Booking booking = new Booking(
                requestBody.getStartDate(),
                requestBody.getEndDate(),
                requestBody.getName(),
                UUID.randomUUID().toString());
        bookings.add(booking);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/bookings")
    public List<Booking> getAllBookings() {
        return bookings.stream().sorted(Comparator.comparing(Booking::getStartDate)).collect(Collectors.toList());
    }

    @GetMapping("/booking/{id}")
    public ResponseEntity<Booking> getBookingById(@PathVariable("id") String id) {
        Booking booking = findBookingById(id);
        if (booking != null) {
            return ResponseEntity.ok(booking);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/booking/{id}")
    public ResponseEntity<String> cancelBooking(@PathVariable("id") String id) {
        Booking booking = findBookingById(id);
        if (booking != null) {
            bookings.remove(booking);
            return ResponseEntity.ok("Booking cancelled successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/booking/{id}")
    public ResponseEntity<Object> rebookBooking(@PathVariable("id") String id, @RequestBody TimeSlotRequestBody requestBody) {
        ResponseEntity validation = validateBadRequestBody(requestBody);
        if (validation != null) {
            return validation;
        }
        Booking existingBooking = findBookingById(id);
        if (existingBooking != null) {
            bookings.remove(existingBooking);
            if (isBookingOverlappingWithBlocksOrExistingBookings(requestBody)) {
                LOGGER.info("Request time slot is overlapping with existing blocks or bookings, request {}", requestBody);
                bookings.add(existingBooking);
                return ResponseEntity.badRequest().body("Booking overlaps with existing existing blocks or bookings.");
            }
            Booking booking = new Booking(
                    requestBody.getStartDate(),
                    requestBody.getEndDate(),
                    requestBody.getName(),
                    existingBooking.getId());
            bookings.add(booking);
            return ResponseEntity.ok(booking);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/block")
    public ResponseEntity<Block> createBlock(@RequestBody TimeSlotRequestBody requestBody) {
        Block block = new Block(
                requestBody.getStartDate(),
                requestBody.getEndDate(),
                requestBody.getName(),
                UUID.randomUUID().toString());
        blocks.add(block);
        return ResponseEntity.ok(block);
    }

    @PutMapping("/block/{id}")
    public ResponseEntity<Block> updateBlock(@PathVariable("id") String id, @RequestBody TimeSlotRequestBody requestBody) {
        Block block = findBlockById(id);
        if (block != null) {
            block.setStartDate(requestBody.getStartDate());
            block.setEndDate(requestBody.getEndDate());
            block.setName(requestBody.getName());
            return ResponseEntity.ok(block);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/block/{id}")
    public ResponseEntity<String> deleteBlock(@PathVariable("id") String id) {
        Block block = findBlockById(id);
        if (block != null) {
            blocks.remove(block);
            return ResponseEntity.ok("Block deleted successfully.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private Booking findBookingById(String id) {
        return bookings.stream()
                .filter(booking -> booking.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private Block findBlockById(String id) {
        return blocks.stream()
                .filter(block -> block.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    private boolean isBookingOverlappingWithBlocksOrExistingBookings(TimeSlotRequestBody timeSlotRequestBody) {
        return Stream.concat(blocks.stream(), bookings.stream())
                .anyMatch(block -> timeSlotRequestBody.getStartDate().isBefore(block.getEndDate())
                        && timeSlotRequestBody.getEndDate().isAfter(block.getStartDate()));
    }

    private ResponseEntity validateBadRequestBody(TimeSlotRequestBody timeSlotRequestBody) {
        if (StringUtils.isEmpty(timeSlotRequestBody.getName()) ||
                timeSlotRequestBody.getStartDate() == null ||
                timeSlotRequestBody.getEndDate() == null) {
            return ResponseEntity.badRequest().body("Empty parameters passed in, please verify all fields");
        }
        if (timeSlotRequestBody.getStartDate().isBefore(LocalDate.now()) ||
                timeSlotRequestBody.getEndDate().isBefore(timeSlotRequestBody.getStartDate()) ||
                timeSlotRequestBody.getEndDate().isEqual(timeSlotRequestBody.getStartDate())) {
            return ResponseEntity.badRequest().body("Invalid start date and end date, please verify fields");
        }
        return null;
    }
}
