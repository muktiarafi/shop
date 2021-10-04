CREATE TABLE users (
    id UUID NOT NULL UNIQUE PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE CHECK (char_length(username) > 4),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(72) NOT NULL,
    phone_number VARCHAR(255) NOT NULL UNIQUE,
    profile_image_url TEXT,
    address TEXT,
    province VARCHAR(255),
    city VARCHAR(255),
    zip_code VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);