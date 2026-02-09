package com.justlife.cleaning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CleaningBookingApplication {
  public static void main(String[] args) {
    SpringApplication.run(CleaningBookingApplication.class, args);
  }
}
