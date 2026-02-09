package com.justlife.cleaning.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.scheduling")
public record SchedulingProperties(
    @NotBlank String workdayStart,
    @NotBlank String workdayEnd,
    @Min(0) int breakMinutes,
    @NotBlank String timezone,
    @Min(120) int minBookableMinutes
) {}
