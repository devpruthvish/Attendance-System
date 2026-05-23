-- Run this in MySQL to fix existing users who have enabled = 0 (false)
-- This is the root cause of student/teacher login failure

USE attendance_db;

-- Fix all existing users to have enabled = true
UPDATE users SET enabled = 1 WHERE enabled = 0 OR enabled IS NULL;

-- Verify
SELECT id, username, role, enabled FROM users;
