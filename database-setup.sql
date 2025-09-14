-- MySQL Database Setup for RideShare Application
-- Run this script to set up the database

-- Create database
CREATE DATABASE IF NOT EXISTS carpool_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- Use the database
USE carpool_db;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('USER', 'DRIVER') NOT NULL DEFAULT 'USER',
    rating DECIMAL(3,2) DEFAULT 0.0,
    total_reviews INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_rating (rating)
);

-- Create rides table
CREATE TABLE IF NOT EXISTS rides (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    driver_id BIGINT NOT NULL,
    origin VARCHAR(255) NOT NULL,
    destination VARCHAR(255) NOT NULL,
    date_time DATETIME NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    available_seats INT NOT NULL,
    total_seats INT NOT NULL,
    description TEXT,
    status ENUM('ACTIVE', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (driver_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_driver (driver_id),
    INDEX idx_origin (origin),
    INDEX idx_destination (destination),
    INDEX idx_date_time (date_time),
    INDEX idx_status (status),
    INDEX idx_available_seats (available_seats),
    INDEX idx_origin_destination (origin, destination),
    INDEX idx_search (origin, destination, date_time, status, available_seats)
);

-- Create reservations table
CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ride_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    seats_reserved INT NOT NULL DEFAULT 1,
    status ENUM('CONFIRMED', 'CANCELLED', 'COMPLETED') NOT NULL DEFAULT 'CONFIRMED',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (ride_id) REFERENCES rides(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_ride (ride_id, user_id),
    INDEX idx_ride (ride_id),
    INDEX idx_user (user_id),
    INDEX idx_status (status)
);

-- Create reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ride_id BIGINT NOT NULL,
    reviewer_id BIGINT NOT NULL,
    driver_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (ride_id) REFERENCES rides(id) ON DELETE CASCADE,
    FOREIGN KEY (reviewer_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (driver_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_reviewer_ride (ride_id, reviewer_id),
    INDEX idx_ride (ride_id),
    INDEX idx_reviewer (reviewer_id),
    INDEX idx_driver (driver_id),
    INDEX idx_rating (rating)
);

-- Insert sample data for testing

-- Sample users (passwords are hashed for 'password123')
INSERT INTO users (name, email, password, role, rating, total_reviews) VALUES
('John Driver', 'john.driver@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'DRIVER', 4.5, 25),
('Sarah Passenger', 'sarah.passenger@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', 4.8, 15),
('Mike Driver', 'mike.driver@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'DRIVER', 4.2, 18),
('Emma User', 'emma.user@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'USER', 4.6, 8),
('David Driver', 'david.driver@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'DRIVER', 4.0, 12);

-- Sample rides (using future dates)
INSERT INTO rides (driver_id, origin, destination, date_time, price, available_seats, total_seats, description, status) VALUES
(1, 'New York, NY', 'Boston, MA', '2024-01-15 09:00:00', 35.00, 3, 4, 'Comfortable ride in a sedan. Music and AC available. Pickup from Manhattan.', 'ACTIVE'),
(3, 'Los Angeles, CA', 'San Francisco, CA', '2024-01-16 14:30:00', 45.00, 2, 3, 'Highway route, no stops. Non-smoking car.', 'ACTIVE'),
(5, 'Chicago, IL', 'Detroit, MI', '2024-01-17 08:00:00', 28.00, 3, 4, 'Early morning departure. Coffee stops allowed.', 'ACTIVE'),
(1, 'Boston, MA', 'New York, NY', '2024-01-18 16:00:00', 38.00, 4, 4, 'Return trip. Flexible pickup locations in Boston area.', 'ACTIVE'),
(3, 'San Diego, CA', 'Los Angeles, CA', '2024-01-19 11:00:00', 25.00, 1, 2, 'Quick trip up the coast. Great for weekend travelers.', 'ACTIVE');

-- Sample reservations
INSERT INTO reservations (ride_id, user_id, seats_reserved, status, notes) VALUES
(1, 2, 1, 'CONFIRMED', 'Will be waiting at Penn Station'),
(2, 4, 2, 'CONFIRMED', 'Traveling with a friend'),
(3, 2, 1, 'CONFIRMED', NULL);

-- Sample reviews
INSERT INTO reviews (ride_id, reviewer_id, driver_id, rating, comment) VALUES
(1, 2, 1, 5, 'Excellent driver! Very punctual and friendly. The car was clean and comfortable.'),
(2, 4, 3, 4, 'Good trip overall. Driver was on time and the route was efficient.'),
(3, 2, 5, 4, 'Nice ride, though the music was a bit loud. Otherwise great experience.');

-- Create a view for ride search with driver information
CREATE OR REPLACE VIEW ride_search_view AS
SELECT 
    r.id,
    r.origin,
    r.destination,
    r.date_time,
    r.price,
    r.available_seats,
    r.total_seats,
    r.description,
    r.status,
    r.created_at,
    u.id as driver_id,
    u.name as driver_name,
    u.rating as driver_rating,
    u.total_reviews as driver_total_reviews
FROM rides r
JOIN users u ON r.driver_id = u.id
WHERE r.status = 'ACTIVE' 
AND r.available_seats > 0 
AND r.date_time > NOW();

-- Create indexes for better performance
CREATE INDEX idx_rides_search_optimized ON rides (status, available_seats, date_time, origin, destination);
CREATE INDEX idx_users_role_rating ON users (role, rating DESC);
CREATE INDEX idx_reservations_user_status ON reservations (user_id, status);
CREATE INDEX idx_reviews_driver_rating ON reviews (driver_id, rating DESC);

COMMIT;
