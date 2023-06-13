package dev.nhantd.bookingapi.controller;

import dev.nhantd.bookingapi.model.Booking;
import dev.nhantd.bookingapi.model.TimeSlotRequestBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(BookingAndBlockController.class)
@AutoConfigureMockMvc
class BookingAndBlockControllerTest {

    private LocalDate startDate1, endDate1, startDate2, endDate2;
    private String id1, name1, id2, name2;
    private Booking booking1, booking2;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingAndBlockController bookingAndBlockController;

    @BeforeEach
    void setUp() {
        startDate1 = LocalDate.of(2023, 6, 11);
        endDate1 = LocalDate.of(2023, 6, 12);
        startDate2 = LocalDate.of(2023, 7, 11);
        endDate2 = LocalDate.of(2023, 7, 12);
        id1 = "id1";
        id2 = "id2";
        name1 = "name1";
        name2 = "name2";
        booking1 = new Booking(startDate1, endDate1, name1, id1);
        booking2 = new Booking(startDate2, endDate2, name1, id2);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testGetAllBookings() throws Exception {
        List<Booking> bookings = Arrays.asList(booking1, booking2);

        when(bookingAndBlockController.getAllBookings()).thenReturn(bookings);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.size()").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("id1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("name1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].startDate").value("2023-06-11"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].endDate").value("2023-06-12"));
    }

    @Test
    public void testCreateBooking() throws Exception {
        TimeSlotRequestBody requestBody = new TimeSlotRequestBody(startDate1, endDate1, name1);
        when(bookingAndBlockController.createBooking(requestBody)).thenReturn(ResponseEntity.ok(booking1));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/booking")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"name1\",\"startDate\":\"2023-06-11\",\"endDate\":\"2023-06-12\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetBookingById() throws Exception {
        when(bookingAndBlockController.getBookingById(id1)).thenReturn(ResponseEntity.ok(booking1));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/booking/id1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(id1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(name1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.startDate").value("2023-06-11"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.endDate").value("2023-06-12"));
    }

    @Test
    public void testGetBookingByIdNotFound() throws Exception {
        when(bookingAndBlockController.getBookingById("randomid")).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/booking/randomid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testCancelBooking() throws Exception {
        when(bookingAndBlockController.cancelBooking(id1)).thenReturn(ResponseEntity.ok("Booking cancelled successfully."));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/booking/id1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Booking cancelled successfully."));
    }

    @Test
    public void testRebookBooking() throws Exception {
        TimeSlotRequestBody newBooking = new TimeSlotRequestBody(startDate2, endDate2, name1);
        when(bookingAndBlockController.rebookBooking(id1, newBooking)).thenReturn(ResponseEntity.ok(booking2));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/booking/id1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"name1\",\"startDate\":\"2023-07-11\",\"endDate\":\"2023-07-12\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}