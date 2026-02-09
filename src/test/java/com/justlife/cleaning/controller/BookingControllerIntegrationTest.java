package com.justlife.cleaning.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.justlife.cleaning.IntegrationTestBase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookingControllerIntegrationTest extends IntegrationTestBase {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void create_get_update_flow() throws Exception {
    String createBody = """
        {
          "start": "2026-02-10T10:00:00",
          "durationMinutes": 120,
          "professionalCount": 2,
          "customerId": 123,
          "preferredProfessionalGender": "FEMALE",
          "notes": "integration test"
        }
        """;

    String createJson = mockMvc.perform(post("/v1/bookings/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(createBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bookingId").isNotEmpty())
        .andExpect(jsonPath("$.start").value("2026-02-10T10:00:00"))
        .andExpect(jsonPath("$.end").value("2026-02-10T12:00:00"))
        .andExpect(jsonPath("$.durationMinutes").value(120))
        .andExpect(jsonPath("$.professionalCount").value(2))
        .andExpect(jsonPath("$.status").value("CONFIRMED"))
        .andExpect(jsonPath("$.vehicleId").isNumber())
        .andExpect(jsonPath("$.professionals").isArray())
        .andExpect(jsonPath("$.professionals.length()").value(2))
        .andReturn()
        .getResponse()
        .getContentAsString();

    JsonNode created = objectMapper.readTree(createJson);
    String bookingId = created.get("bookingId").asText();
    assertNotNull(bookingId);
    assertTrue(bookingId.length() > 10);
    JsonNode professionals = created.get("professionals");
    assertNotNull(professionals);
    assertTrue(professionals.isArray());
    assertEquals(2, professionals.size());
    long vehicle0 = professionals.get(0).get("vehicleId").asLong();
    long vehicle1 = professionals.get(1).get("vehicleId").asLong();
    assertEquals(vehicle0, vehicle1);

    mockMvc.perform(get("/v1/bookings/id/{id}", bookingId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bookingId").value(bookingId))
        .andExpect(jsonPath("$.start").value("2026-02-10T10:00:00"))
        .andExpect(jsonPath("$.end").value("2026-02-10T12:00:00"));

    String updateBody = """
        {
          "start": "2026-02-10T12:00:00",
          "durationMinutes": 120,
          "preferredProfessionalGender": "MALE",
          "notes": "updated"
        }
        """;

    mockMvc.perform(put("/v1/bookings/id/{id}", bookingId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(updateBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bookingId").value(bookingId))
        .andExpect(jsonPath("$.start").value("2026-02-10T12:00:00"))
        .andExpect(jsonPath("$.end").value("2026-02-10T14:00:00"))
        .andExpect(jsonPath("$.notes").value("updated"));
  }
}
