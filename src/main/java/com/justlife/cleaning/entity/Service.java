package com.justlife.cleaning.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "services",
    uniqueConstraints = @UniqueConstraint(name = "uq_service_code_category", columnNames = {"code", "service_category_id"}),
    indexes = {
        @Index(name = "idx_services_category", columnList = "service_category_id"),
        @Index(name = "idx_services_active", columnList = "is_active")
    }
)
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_category_id", nullable = false)
    private ServiceCategory serviceCategory;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(name = "name_en", nullable = false, length = 100)
    private String nameEn;

    @Column(name = "base_duration_minutes", nullable = false)
    private int baseDurationMinutes;

    @Column(name = "base_price")
    private java.math.BigDecimal basePrice;

    @Column(name = "requires_gender_preference", nullable = false)
    private boolean requiresGenderPreference = false;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Service() {}

    public Service(ServiceCategory serviceCategory, String code, String nameEn, int baseDurationMinutes) {
        this.serviceCategory = serviceCategory;
        this.code = code;
        this.nameEn = nameEn;
        this.baseDurationMinutes = baseDurationMinutes;
        this.basePrice = java.math.BigDecimal.ZERO;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
