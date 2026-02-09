package com.justlife.cleaning.entity;

import com.justlife.cleaning.domain.dto.constant.ProfessionalGender;
import com.justlife.cleaning.domain.dto.constant.ProfessionalVerificationStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "professionals",
    uniqueConstraints = @UniqueConstraint(name = "uq_professional_code_region", columnNames = {"code", "region_id"}),
    indexes = {
        @Index(name = "idx_professionals_region", columnList = "region_id"),
        @Index(name = "idx_professionals_category", columnList = "service_category_id"),
        @Index(name = "idx_professionals_vehicle", columnList = "vehicle_id"),
        @Index(name = "idx_professionals_active", columnList = "is_active"),
        @Index(name = "idx_professionals_verification", columnList = "verification_status")
    }
)
public class Professional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "service_category_id", nullable = false)
    private ServiceCategory serviceCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 20)
    @Enumerated(EnumType.STRING)
    private ProfessionalGender gender;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "verification_status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ProfessionalVerificationStatus verificationStatus = ProfessionalVerificationStatus.PENDING;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected Professional() {}

    public Professional(Region region, ServiceCategory serviceCategory, String code, String fullName) {
        this.region = region;
        this.serviceCategory = serviceCategory;
        this.code = code;
        this.fullName = fullName;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void verify() {
        this.verificationStatus = ProfessionalVerificationStatus.VERIFIED;
        this.verifiedAt = LocalDateTime.now();
    }

    public void reject() {
        this.verificationStatus = ProfessionalVerificationStatus.REJECTED;
    }
}
