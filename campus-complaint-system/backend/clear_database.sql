-- Clear all data except base admin
-- Run this script to reset the database

-- Delete all comments first (foreign key dependency)
DELETE FROM complaint_comments;

-- Delete all complaints
DELETE FROM complaints;

-- Delete all users except the base admin
DELETE FROM users WHERE username != 'admin';

-- Verify remaining data
SELECT 'Users remaining:' as info, COUNT(*) as count FROM users;
SELECT 'Complaints remaining:' as info, COUNT(*) as count FROM complaints;
SELECT 'Comments remaining:' as info, COUNT(*) as count FROM complaint_comments;
