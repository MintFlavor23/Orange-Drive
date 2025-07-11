-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) DEFAULT 'USER' NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create files table
CREATE TABLE files (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    size BIGINT NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    upload_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Create notes table
CREATE TABLE notes (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Create credentials table
CREATE TABLE credentials (
    id BIGSERIAL PRIMARY KEY,
    service VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL,
    encrypted_password TEXT NOT NULL,
    url VARCHAR(500),
    notes TEXT,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_files_user_id ON files (user_id);

CREATE INDEX idx_files_filename ON files (filename);

CREATE INDEX idx_files_upload_date ON files (upload_date);

CREATE INDEX idx_notes_user_id ON notes (user_id);

CREATE INDEX idx_notes_title ON notes (title);

CREATE INDEX idx_notes_updated_date ON notes (updated_date);

CREATE INDEX idx_credentials_user_id ON credentials (user_id);

CREATE INDEX idx_credentials_service ON credentials (service);

CREATE INDEX idx_credentials_created_date ON credentials (created_date);

-- Create unique constraint for user-service combination
CREATE UNIQUE INDEX idx_credentials_user_service ON credentials (user_id, service);

-- Create function to update updated_at/updated_date timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_ARGV[0] = 'updated_at' THEN
        NEW.updated_at = CURRENT_TIMESTAMP;
    ELSIF TG_ARGV[0] = 'updated_date' THEN
        NEW.updated_date = CURRENT_TIMESTAMP;
    END IF;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at/updated_date
CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column('updated_at');

CREATE TRIGGER update_notes_updated_date 
    BEFORE UPDATE ON notes 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column('updated_date');

CREATE TRIGGER update_credentials_updated_date 
    BEFORE UPDATE ON credentials 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column('updated_date');

-- Insert sample data (for dev/testing only)
INSERT INTO
    users (email, password, name, role)
VALUES (
        'admin@safedrive.com',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFDYnA.eOvCvOUs9yBr3Oik',
        'Admin User',
        'ADMIN'
    ),
    (
        'user@safedrive.com',
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFDYnA.eOvCvOUs9yBr3Oik',
        'Test User',
        'USER'
    );