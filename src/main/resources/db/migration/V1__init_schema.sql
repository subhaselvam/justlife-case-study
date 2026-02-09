CREATE EXTENSION IF NOT EXISTS pgcrypto;


CREATE TABLE IF NOT EXISTS regions (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    timezone VARCHAR(50) NOT NULL,
    currency_code VARCHAR(3) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS service_categories (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name_en VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS services (
    id BIGSERIAL PRIMARY KEY,
    service_category_id BIGINT NOT NULL REFERENCES service_categories(id),
    code VARCHAR(50) NOT NULL,
    name_en VARCHAR(100) NOT NULL,
    base_duration_minutes INT NOT NULL,
    base_price DECIMAL(10,2) DEFAULT 0,
    requires_gender_preference BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_service_code_category UNIQUE (code, service_category_id)
);


CREATE TABLE IF NOT EXISTS vehicles (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    driver_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);


CREATE TABLE IF NOT EXISTS professionals (
    id BIGSERIAL PRIMARY KEY,
    region_id BIGINT NOT NULL REFERENCES regions(id),
    service_category_id BIGINT NOT NULL REFERENCES service_categories(id),
    vehicle_id BIGINT REFERENCES vehicles(id),
    code VARCHAR(50) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    gender VARCHAR(20),
    profile_image_url VARCHAR(255),
    date_of_birth DATE,
    years_experience INT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    verified_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_professional_code_region UNIQUE (code, region_id),
    CONSTRAINT chk_professional_verification CHECK (verification_status IN ('PENDING', 'VERIFIED', 'REJECTED')),
    CONSTRAINT chk_professional_gender CHECK (gender IN ('MALE', 'FEMALE', 'OTHER') OR gender IS NULL)
);


CREATE TABLE IF NOT EXISTS bookings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    region_id BIGINT REFERENCES regions(id),
    service_id BIGINT REFERENCES services(id),
    vehicle_id BIGINT REFERENCES vehicles(id),
    customer_id BIGINT,
    start_time TIMESTAMP NOT NULL,
    duration_minutes INT NOT NULL,
    professional_count INT NOT NULL DEFAULT 1,
    preferred_professional_gender VARCHAR(20),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    notes TEXT,
    total_amount DECIMAL(10,2),
    currency_code VARCHAR(3),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    cancellation_reason VARCHAR(255),
    CONSTRAINT chk_bookings_duration_positive CHECK (duration_minutes > 0 AND duration_minutes <= 480),
    CONSTRAINT chk_professional_count CHECK (professional_count BETWEEN 1 AND 10),
    CONSTRAINT chk_bookings_status CHECK (status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT chk_booking_gender_preference CHECK (preferred_professional_gender IN ('MALE', 'FEMALE', 'OTHER') OR preferred_professional_gender IS NULL)
);


CREATE TABLE IF NOT EXISTS booking_assignments (
    id BIGSERIAL PRIMARY KEY,
    booking_id UUID NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    professional_id BIGINT NOT NULL REFERENCES professionals(id),
    assignment_status VARCHAR(20) NOT NULL DEFAULT 'ASSIGNED',
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    accepted_at TIMESTAMP,
    rejected_at TIMESTAMP,
    rejection_reason VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_booking_professional UNIQUE (booking_id, professional_id),
    CONSTRAINT chk_assignment_status CHECK (assignment_status IN ('ASSIGNED', 'ACCEPTED', 'REJECTED', 'COMPLETED'))
);


CREATE TABLE IF NOT EXISTS booking_history (
    id BIGSERIAL PRIMARY KEY,
    booking_id UUID NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    status_from VARCHAR(20),
    status_to VARCHAR(20) NOT NULL,
    changed_by VARCHAR(50),
    reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE INDEX IF NOT EXISTS idx_bookings_start ON bookings(start_time);
CREATE INDEX IF NOT EXISTS idx_bookings_created_at ON bookings(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_bookings_vehicle_nullable ON bookings(vehicle_id) WHERE vehicle_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_bookings_currency ON bookings(currency_code) WHERE currency_code IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_professionals_vehicle ON professionals(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_booking_assignments_professional ON booking_assignments(professional_id);
CREATE INDEX IF NOT EXISTS idx_booking_history_created_at ON booking_history(created_at DESC);


CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_regions_updated_at
    BEFORE UPDATE ON regions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_service_categories_updated_at
    BEFORE UPDATE ON service_categories
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_professionals_updated_at
    BEFORE UPDATE ON professionals
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_bookings_updated_at
    BEFORE UPDATE ON bookings
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- COMMENTS
-- ============================================
COMMENT ON TABLE professionals IS 'Unified worker table supporting multi-region, multi-category professionals with verification workflow';
COMMENT ON TABLE booking_assignments IS 'Links bookings to professionals with assignment workflow';
COMMENT ON TABLE bookings IS 'Core booking table with flexible duration and professional count (not hard-coded to 2h/4h or 1-3 cleaners)';
