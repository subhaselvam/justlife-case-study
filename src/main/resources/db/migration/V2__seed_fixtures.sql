
INSERT INTO regions (code, name, country, timezone, currency_code, is_active) VALUES
    ('DUBAI', 'Dubai', 'United Arab Emirates', 'Asia/Dubai', 'AED', TRUE),
    ('ABU_DHABI', 'Abu Dhabi', 'United Arab Emirates', 'Asia/Dubai', 'AED', TRUE),
    ('RIYADH', 'Riyadh', 'Saudi Arabia', 'Asia/Riyadh', 'SAR', TRUE),
    ('MUMBAI', 'Mumbai', 'India', 'Asia/Kolkata', 'INR', TRUE),
    ('BANGALORE', 'Bangalore', 'India', 'Asia/Kolkata', 'INR', TRUE)
ON CONFLICT (code) DO NOTHING;

INSERT INTO service_categories (code, name_en, description, is_active) VALUES
    ('CLEANING', 'Cleaning Services', 'Professional home and office cleaning services', TRUE),
    ('MAINTENANCE', 'Maintenance Services', 'General maintenance and repair services', TRUE),
    ('BEAUTY', 'Beauty Services', 'Professional beauty and grooming services', TRUE),
    ('MOVING', 'Moving Services', 'Home and office moving services', TRUE)
ON CONFLICT (code) DO NOTHING;

INSERT INTO services (service_category_id, code, name_en, base_duration_minutes, requires_gender_preference, is_active, base_price)
SELECT sc.id, 'DEEP_CLEANING', 'Deep Cleaning', 240, FALSE, TRUE, 150.00
FROM service_categories sc WHERE sc.code = 'CLEANING'
ON CONFLICT (code, service_category_id) DO NOTHING;

INSERT INTO services (service_category_id, code, name_en, base_duration_minutes, requires_gender_preference, is_active, base_price)
SELECT sc.id, 'REGULAR_CLEANING', 'Regular Cleaning', 120, FALSE, TRUE, 80.00
FROM service_categories sc WHERE sc.code = 'CLEANING'
ON CONFLICT (code, service_category_id) DO NOTHING;

INSERT INTO services (service_category_id, code, name_en, base_duration_minutes, requires_gender_preference, is_active, base_price)
SELECT sc.id, 'WINDOW_CLEANING', 'Window Cleaning', 120, FALSE, TRUE, 60.00
FROM service_categories sc WHERE sc.code = 'CLEANING'
ON CONFLICT (code, service_category_id) DO NOTHING;

INSERT INTO services (service_category_id, code, name_en, base_duration_minutes, requires_gender_preference, is_active, base_price)
SELECT sc.id, 'AC_MAINTENANCE', 'AC Maintenance', 120, FALSE, TRUE, 100.00
FROM service_categories sc WHERE sc.code = 'MAINTENANCE'
ON CONFLICT (code, service_category_id) DO NOTHING;

INSERT INTO services (service_category_id, code, name_en, base_duration_minutes, requires_gender_preference, is_active, base_price)
SELECT sc.id, 'PLUMBING', 'Plumbing', 120, FALSE, TRUE, 120.00
FROM service_categories sc WHERE sc.code = 'MAINTENANCE'
ON CONFLICT (code, service_category_id) DO NOTHING;

INSERT INTO services (service_category_id, code, name_en, base_duration_minutes, requires_gender_preference, is_active, base_price)
SELECT sc.id, 'HAIRCUT', 'Haircut', 60, TRUE, TRUE, 50.00
FROM service_categories sc WHERE sc.code = 'BEAUTY'
ON CONFLICT (code, service_category_id) DO NOTHING;

INSERT INTO services (service_category_id, code, name_en, base_duration_minutes, requires_gender_preference, is_active, base_price)
SELECT sc.id, 'FACIAL', 'Facial Treatment', 90, TRUE, TRUE, 80.00
FROM service_categories sc WHERE sc.code = 'BEAUTY'
ON CONFLICT (code, service_category_id) DO NOTHING;


INSERT INTO vehicles (code, driver_name) VALUES
  ('VAN-1', 'Driver 1'),
  ('VAN-2', 'Driver 2'),
  ('VAN-3', 'Driver 3'),
  ('VAN-4', 'Driver 4'),
  ('VAN-5', 'Driver 5')
ON CONFLICT (code) DO NOTHING;


DO $$
DECLARE
    dubai_region_id BIGINT;
    cleaning_category_id BIGINT;
    v RECORD;
    i INT;
BEGIN
    SELECT id INTO dubai_region_id FROM regions WHERE code = 'DUBAI';
    SELECT id INTO cleaning_category_id FROM service_categories WHERE code = 'CLEANING';

    IF dubai_region_id IS NOT NULL AND cleaning_category_id IS NOT NULL THEN
        FOR v IN SELECT id, code FROM vehicles ORDER BY id LOOP
            FOR i IN 1..5 LOOP
                INSERT INTO professionals (
                    region_id,
                    service_category_id,
                    vehicle_id,
                    code,
                    full_name,
                    email,
                    phone,
                    gender,
                    is_active,
                    verification_status
                )
                VALUES (
                    dubai_region_id,
                    cleaning_category_id,
                    v.id,
                    'PRO-' || v.code || '-' || i,
                    v.code || '-Professional-' || i,
                    'pro' || v.id || '_' || i || '@justlife.com',
                    '+971500000' || LPAD(v.id::TEXT, 2, '0') || LPAD(i::TEXT, 2, '0'),
                    CASE WHEN i % 2 = 0 THEN 'FEMALE' ELSE 'MALE' END,
                    TRUE,
                    'VERIFIED'
                )
                ON CONFLICT (code, region_id) DO NOTHING;
            END LOOP;
        END LOOP;
    END IF;
END $$;
