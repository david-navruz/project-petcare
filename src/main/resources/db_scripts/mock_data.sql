-- 1. Insert into photo
INSERT INTO photo (id, file_name, file_type, image)
VALUES (1, 'profile1.jpg', 'image/jpeg', NULL),
       (2, 'profile2.jpg', 'image/jpeg', NULL);

-- 2. Insert roles
INSERT INTO role (id, role_name)
VALUES (1, 'ROLE_ADMIN'),
       (2, 'ROLE_VET'),
       (3, 'ROLE_PETOWNER');

-- 3. Insert users
INSERT INTO user (id, created_at, email, first_name, gender, is_enabled, last_name, password, mobile, user_type, photo_id)
VALUES
    (1, '2024-06-01', 'admin@vetapp.com', 'Alice', 'F', b'1', 'Admin', 'admin123', '1234567890', 'ADMIN', 1),
    (2, '2024-06-02', 'vet@vetapp.com', 'Bob', 'M', b'1', 'Veterinarian', 'vet123', '1234567891', 'VET', 2),
    (3, '2024-06-03', 'owner@vetapp.com', 'Charlie', 'M', b'1', 'Owner', 'owner123', '1234567892', 'PETOWNER', NULL);

-- 4. Insert user_roles
INSERT INTO user_roles (user_id, role_id)
VALUES (1, 1),  -- Alice: ADMIN
       (2, 2),  -- Bob: VET
       (3, 3);  -- Charlie: OWNER

-- 5. Insert into admin, veterinarian, pet_owner
INSERT INTO admin (admin_id) VALUES (1);
INSERT INTO veterinarian (vet_id, specialization) VALUES (2, 'Dermatology');
INSERT INTO pet_owner (pet_id) VALUES (1);

-- 6. Insert into appointment
INSERT INTO appointment (id, appointment_date, appointment_no, appointment_time, created_at, reason, status, sender, recipient)
VALUES (1, '2024-06-10', 'APT-001', '10:30:00', '2024-06-01', 'Regular check-up', 'PENDING', 3, 2);

-- 7. Insert into pet
INSERT INTO pet (id, age, breed, color, name, type, appointment_id)
VALUES (1, 3, 'Golden Retriever', 'Golden', 'Buddy', 'Dog', 1);

-- 8. Insert into review
INSERT INTO review (id, feedback, stars, reviewer_id, veterinarian_id)
VALUES (1, 'Great service!', 5, 3, 2);

-- 9. Insert into verification_token
INSERT INTO verification_token (id, expiration_date, token, user_id)
VALUES (1, '2025-06-01 12:00:00', 'abc123xyz', 3);
