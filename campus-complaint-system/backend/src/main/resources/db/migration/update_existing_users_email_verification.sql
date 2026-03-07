-- Migration script to update existing users with email verification status
-- Run this script to allow existing users to login after email verification feature is added

-- Set emailVerified to true for all existing users (created before email verification was implemented)
UPDATE users 
SET email_verified = true 
WHERE email_verified = false OR email_verified IS NULL;

-- Optional: Clear any null verification tokens
UPDATE users 
SET verification_token = NULL, verification_token_expiry = NULL 
WHERE verification_token IS NOT NULL;
