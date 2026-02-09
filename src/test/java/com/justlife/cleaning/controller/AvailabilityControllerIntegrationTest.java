package com.justlife.cleaning.controller;

import com.justlife.cleaning.IntegrationTestBase;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

class AvailabilityControllerIntegrationTest extends IntegrationTestBase {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void byDate_returns200AndArray() throws Exception {
    LocalDate date = LocalDate.now().plusDays(1);

    mockMvc.perform(get("/v1/availability")
            .param("date", date.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  void byDate_returns200AndIncludesAvailableTimes() throws Exception {
    LocalDate date = LocalDate.now().plusDays(1);

    mockMvc.perform(get("/v1/availability")
            .param("date", date.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].professionalId").exists())
        .andExpect(jsonPath("$[0].vehicleId").exists())
        .andExpect(jsonPath("$[0].availableTimes").isArray());
  }

  @Test
  void query_returns200AndProfessionals() throws Exception {
    String body = """
        {
          "start": "2026-02-10T10:00:00",
          "durationMinutes": 120
        }
        """;

    mockMvc.perform(post("/v1/availability")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.availableProfessionals").isArray());
  }

  @Test
  void query_returns200AndProfessionalsWithVehicle() throws Exception {
    String body = """
        {
          "start": "2026-02-10T10:00:00",
          "durationMinutes": 120
        }
        """;

    mockMvc.perform(post("/v1/availability")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.availableProfessionals").isArray())
        .andExpect(jsonPath("$.availableProfessionals[0].professionalId").exists())
        .andExpect(jsonPath("$.availableProfessionals[0].vehicleId").exists());
  }

  @Test
  void query_rejectsInvalidDuration_409() throws Exception {
    String body = """
        {
          "start": "2026-02-10T10:00:00",
          "durationMinutes": 60
        }
        """;

    mockMvc.perform(post("/v1/availability")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isConflict());
  }

  @Test
  void byDate_returnsSeeded25Professionals_onKnownWorkingDay() throws Exception {
    LocalDate date = LocalDate.of(2026, 2, 10);

    mockMvc.perform(get("/v1/availability")
            .param("date", date.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(25)))
        .andExpect(jsonPath("$[0].professionalName", startsWith("VAN-")))
        .andExpect(jsonPath("$[0].vehicleId", notNullValue()))
        .andExpect(jsonPath("$[0].availableTimes").isArray());
  }

  @Test
  void byDate_availableTimes_areWithinWorkingHours_08_22() throws Exception {
    LocalDate date = LocalDate.of(2026, 2, 10);

    mockMvc.perform(get("/v1/availability")
            .param("date", date.toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].availableTimes").isArray())
        .andExpect(jsonPath("$[0].availableTimes[0].start", is("2026-02-10T08:00:00")))
        .andExpect(jsonPath("$[0].availableTimes[0].end", is("2026-02-10T22:00:00")));
  }

  @Test
  void byDate_rejectsFriday_409() throws Exception {
    mockMvc.perform(get("/v1/availability")
            .param("date", "2026-02-13"))
        .andExpect(status().isConflict());
  }

  @Test
  void query_exactSlot_returnsProfessionalsWithVehicleIds() throws Exception {
    String body = """
        {
          "start": "2026-02-10T10:00:00",
          "durationMinutes": 120
        }
        """;

    mockMvc.perform(post("/v1/availability")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.availableProfessionals").isArray())
        .andExpect(jsonPath("$.availableProfessionals.length()", greaterThan(0)))
        .andExpect(jsonPath("$.availableProfessionals[0].professionalId").isNumber())
        .andExpect(jsonPath("$.availableProfessionals[0].professionalName", startsWith("VAN-")))
        .andExpect(jsonPath("$.availableProfessionals[0].vehicleId").isNumber());
  }

  @Test
  void breakBufferBlocksAdjacentSlots_afterBookingCreated() throws Exception {
    String createBookingBody = """
        {
          "start": "2026-02-10T10:00:00",
          "durationMinutes": 120,
          "professionalCount": 1,
          "customerId": 123,
          "preferredProfessionalGender": "FEMALE",
          "notes": "buffer test"
        }
        """;

    String createdBookingJson = mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/v1/bookings/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createBookingBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bookingId").isNotEmpty())
        .andExpect(jsonPath("$.start").value("2026-02-10T10:00:00"))
        .andExpect(jsonPath("$.end").value("2026-02-10T12:00:00"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode createdBooking = objectMapper.readTree(createdBookingJson);
    JsonNode assignedProfessionals = createdBooking.get("professionals");
    assertNotNull(assignedProfessionals);
    assertTrue(assignedProfessionals.isArray());
    assertEquals(1, assignedProfessionals.size());
    long bookedProfessionalId = assignedProfessionals.get(0).get("professionalId").asLong();
    String queryAt12 = """
        {
          "start": "2026-02-10T12:00:00",
          "durationMinutes": 120
        }
        """;

    String availabilityAt12Json = mockMvc.perform(post("/v1/availability")
            .contentType(MediaType.APPLICATION_JSON)
            .content(queryAt12))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode availabilityAt12 = objectMapper.readTree(availabilityAt12Json);
    boolean bookedProReturnedAt12 = false;
    for (JsonNode n : availabilityAt12.get("availableProfessionals")) {
      if (n.get("professionalId").asLong() == bookedProfessionalId) {
        bookedProReturnedAt12 = true;
        break;
      }
    }
    assertFalse(bookedProReturnedAt12);
    String queryAt1230 = """
        {
          "start": "2026-02-10T12:30:00",
          "durationMinutes": 120
        }
        """;

    String availabilityAt1230Json = mockMvc.perform(post("/v1/availability")
            .contentType(MediaType.APPLICATION_JSON)
            .content(queryAt1230))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode availabilityAt1230 = objectMapper.readTree(availabilityAt1230Json);
    boolean bookedProReturnedAt1230 = false;
    for (JsonNode n : availabilityAt1230.get("availableProfessionals")) {
      if (n.get("professionalId").asLong() == bookedProfessionalId) {
        bookedProReturnedAt1230 = true;
        break;
      }
    }
    assertTrue(bookedProReturnedAt1230);
  }
}
