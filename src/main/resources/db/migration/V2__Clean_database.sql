-- Clean all data from the database
-- This migration removes all user data while preserving the schema structure

-- Delete all files (this will also delete the physical files from disk)
DELETE FROM files;

-- Delete all notes
DELETE FROM notes;

-- Delete all credentials
DELETE FROM credentials;

-- Delete all users (except keep the admin user for testing)
DELETE FROM users WHERE email != 'admin@safedrive.com';

-- Reset sequences to start from 1
ALTER SEQUENCE files_id_seq RESTART WITH 1;

ALTER SEQUENCE notes_id_seq RESTART WITH 1;

ALTER SEQUENCE credentials_id_seq RESTART WITH 1;

ALTER SEQUENCE users_id_seq RESTART WITH 2;
-- Start from 2 since admin user has ID 1

-- Verify cleanup
DO $$ BEGIN RAISE NOTICE 'Database cleanup completed. Remaining records:';

RAISE NOTICE 'Users: %',
(
    SELECT COUNT(*)
    FROM users
);

RAISE NOTICE 'Files: %',
(
    SELECT COUNT(*)
    FROM files
);

RAISE NOTICE 'Notes: %',
(
    SELECT COUNT(*)
    FROM notes
);

RAISE NOTICE 'Credentials: %',
(
    SELECT COUNT(*)
    FROM credentials
);

END $$;