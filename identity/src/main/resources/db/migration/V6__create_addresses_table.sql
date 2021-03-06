CREATE TABLE addresses (
    id UUID NOT NULL UNIQUE PRIMARY KEY,
    detail VARCHAR(255) NOT NULL,
    postal_code VARCHAR(45) NOT NULL,
    recipient_name VARCHAR(255) NOT NULL,
    recipient_phone_number VARCHAR(45) NOT NULL,
    user_id UUID NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);