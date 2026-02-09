ALTER TABLE booking_assignments
  ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

UPDATE booking_assignments
SET updated_at = COALESCE(updated_at, assigned_at, NOW())
WHERE updated_at IS NULL;

ALTER TABLE booking_assignments
  ALTER COLUMN updated_at SET NOT NULL;

