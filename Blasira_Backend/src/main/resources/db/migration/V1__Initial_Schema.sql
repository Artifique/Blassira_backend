
/*
 * V1__Initial_Schema.sql
 * 
 * Ce script représente le schéma de base initial de la base de données au moment
 * de l'introduction de Flyway dans le projet. Il a été généré à partir des entités JPA
 * existantes et sert de point de départ pour toutes les migrations futures.
 * Ne pas modifier ce fichier après son exécution.
 */

CREATE TABLE bookings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    booked_seats INTEGER NOT NULL,
    created_at DATETIME(6),
    discount_amount DECIMAL(38,2),
    status ENUM('CANCELLED_BY_PASSENGER','CONFIRMED_BY_DRIVER','REJECTED_BY_DRIVER','REQUESTED_BY_PASSENGER') NOT NULL,
    total_price DECIMAL(38,2) NOT NULL,
    passenger_id BIGINT NOT NULL,
    promo_code_id BIGINT,
    trip_id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE conversation_participants (
    conversation_id BIGINT NOT NULL,
    user_profile_id BIGINT NOT NULL,
    PRIMARY KEY (conversation_id, user_profile_id)
) ENGINE=InnoDB;

CREATE TABLE conversations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE driver_profiles (
    id BIGINT NOT NULL,
    average_rating FLOAT(53),
    rating_sum BIGINT NOT NULL,
    review_count INTEGER NOT NULL,
    status ENUM('NOT_SUBMITTED','PENDING_REVIEW','REJECTED','SUSPENDED','VERIFIED') NOT NULL,
    total_trips_driven INTEGER NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE incident_reports (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6),
    description LONGTEXT,
    reason VARCHAR(255) NOT NULL,
    status ENUM('CLOSED','OPEN','RESOLVED','UNDER_REVIEW') NOT NULL,
    booking_id BIGINT,
    reporter_id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE messages (
    id BIGINT NOT NULL AUTO_INCREMENT,
    content TINYTEXT NOT NULL,
    is_read BIT NOT NULL,
    sent_at DATETIME(6),
    conversation_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE payments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    amount DECIMAL(38,2) NOT NULL,
    created_at DATETIME(6),
    payment_method ENUM('CASH','CREDIT_CARD','MOBILE_MONEY') NOT NULL,
    status ENUM('CANCELLED','FAILED','PENDING','SUCCESSFUL') NOT NULL,
    transaction_id VARCHAR(255),
    booking_id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE promo_codes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    active BIT NOT NULL,
    code VARCHAR(255) NOT NULL,
    discount_type ENUM('FIXED_AMOUNT','PERCENTAGE') NOT NULL,
    discount_value DECIMAL(38,2) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    max_uses INTEGER NOT NULL,
    use_count INTEGER NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE reviews (
    id BIGINT NOT NULL AUTO_INCREMENT,
    comment LONGTEXT,
    created_at DATETIME(6),
    rating INTEGER NOT NULL,
    review_type ENUM('DRIVER_TO_PASSENGER','PASSENGER_TO_DRIVER') NOT NULL,
    author_id BIGINT NOT NULL,
    booking_id BIGINT NOT NULL,
    recipient_id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE shared_trip_links (
    id BIGINT NOT NULL AUTO_INCREMENT,
    expires_at DATETIME(6) NOT NULL,
    token VARCHAR(255) NOT NULL,
    trip_id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE trips (
    id BIGINT NOT NULL AUTO_INCREMENT,
    available_seats INTEGER NOT NULL,
    departure_address VARCHAR(255) NOT NULL,
    departure_coordinates VARCHAR(255),
    departure_time DATETIME(6) NOT NULL,
    destination_address VARCHAR(255) NOT NULL,
    destination_coordinates VARCHAR(255),
    price_per_seat DECIMAL(38,2) NOT NULL,
    status ENUM('CANCELLED','COMPLETED','IN_PROGRESS','PLANNED') NOT NULL,
    driver_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE user_accounts (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6),
    email VARCHAR(255) NOT NULL,
    is_email_verified BIT NOT NULL,
    is_phone_verified BIT NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    trust_charter_accepted_at DATETIME(6),
    updated_at DATETIME(6),
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE user_profiles (
    id BIGINT NOT NULL,
    bio LONGTEXT,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    member_since DATETIME(6),
    profile_picture_url VARCHAR(255),
    student_verified BIT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    roles ENUM('ROLE_ADMIN','ROLE_USER','ROLE_STUDENT','ROLE_DRIVER')
) ENGINE=InnoDB;

CREATE TABLE vehicles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    color VARCHAR(255),
    license_plate VARCHAR(255) NOT NULL,
    make VARCHAR(255) NOT NULL,
    model VARCHAR(255) NOT NULL,
    type ENUM('CAR','MOTORCYCLE') NOT NULL,
    verification_status ENUM('PENDING_REVIEW','REJECTED','VERIFIED') NOT NULL,
    year INTEGER NOT NULL,
    owner_user_id BIGINT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

ALTER TABLE payments ADD CONSTRAINT UKnuscjm6x127hkb15kcb8n56wo UNIQUE (booking_id);
ALTER TABLE promo_codes ADD CONSTRAINT UKj9mo0xgfs34t6e3c17anidd83 UNIQUE (code);
ALTER TABLE reviews ADD CONSTRAINT UK3p9j9vyr1qofbcxju65es206r UNIQUE (booking_id);
ALTER TABLE shared_trip_links ADD CONSTRAINT UKi2a8s7ylp316p4x70vve5x0d1 UNIQUE (token);
ALTER TABLE shared_trip_links ADD CONSTRAINT UKgvnqfc1p61weuryn4dm52sucs UNIQUE (trip_id);
ALTER TABLE user_accounts ADD CONSTRAINT UKf9sl209luxhu4rylls0h1m625 UNIQUE (email);
ALTER TABLE user_accounts ADD CONSTRAINT UK32118ao3yprt9j4dqlcnwuf5v UNIQUE (phone_number);
ALTER TABLE vehicles ADD CONSTRAINT UK9vovnbiegxevdhqfcwvp2g8pj UNIQUE (license_plate);
ALTER TABLE bookings ADD CONSTRAINT FKae90jixn459854kxkii3e9a09 FOREIGN KEY (passenger_id) REFERENCES user_profiles (id);
ALTER TABLE bookings ADD CONSTRAINT FK88eyq095hps8dgyrprdmoelge FOREIGN KEY (promo_code_id) REFERENCES promo_codes (id);
ALTER TABLE bookings ADD CONSTRAINT FK76g5jpvf8bcqejvp5d2vgrnjb FOREIGN KEY (trip_id) REFERENCES trips (id);
ALTER TABLE conversation_participants ADD CONSTRAINT FK1e7cc420omf1ficfjcbwtavwb FOREIGN KEY (user_profile_id) REFERENCES user_profiles (id);
ALTER TABLE conversation_participants ADD CONSTRAINT FK84npv3fo2vwl7ut63im0p417q FOREIGN KEY (conversation_id) REFERENCES conversations (id);
ALTER TABLE driver_profiles ADD CONSTRAINT FKsff8hplj067t5t351uswcb542 FOREIGN KEY (id) REFERENCES user_accounts (id);
ALTER TABLE incident_reports ADD CONSTRAINT FK4myd1ljdiq24g0msk9uxtsf39 FOREIGN KEY (booking_id) REFERENCES bookings (id);
ALTER TABLE incident_reports ADD CONSTRAINT FK6gpqskoujmve74p7x8nlip81n FOREIGN KEY (reporter_id) REFERENCES user_profiles (id);
ALTER TABLE messages ADD CONSTRAINT FKt492th6wsovh1nush5yl5jj8e FOREIGN KEY (conversation_id) REFERENCES conversations (id);
ALTER TABLE messages ADD CONSTRAINT FK9gy4r61d2j8ir772fk9rdibhu FOREIGN KEY (sender_id) REFERENCES user_profiles (id);
ALTER TABLE payments ADD CONSTRAINT FKc52o2b1jkxttngufqp3t7jr3h FOREIGN KEY (booking_id) REFERENCES bookings (id);
ALTER TABLE reviews ADD CONSTRAINT FKcjr0hqb9kvap6fxrgrm3a3vtp FOREIGN KEY (author_id) REFERENCES user_profiles (id);
ALTER TABLE reviews ADD CONSTRAINT FK28an517hrxtt2bsg93uefugrm FOREIGN KEY (booking_id) REFERENCES bookings (id);
ALTER TABLE reviews ADD CONSTRAINT FKjwrr6h52g0b9s77t47ln8tms4 FOREIGN KEY (recipient_id) REFERENCES user_profiles (id);
ALTER TABLE shared_trip_links ADD CONSTRAINT FKarbe60jhmpa059qky10anh1s2 FOREIGN KEY (trip_id) REFERENCES trips (id);
ALTER TABLE trips ADD CONSTRAINT FKlys16ik6qq2ja65b23o728c66 FOREIGN KEY (driver_id) REFERENCES driver_profiles (id);
ALTER TABLE trips ADD CONSTRAINT FKqahsaodjirbk4if91c9bfnlgg FOREIGN KEY (vehicle_id) REFERENCES vehicles (id);
ALTER TABLE user_profiles ADD CONSTRAINT FK96l76pubaegmvwwi5nab736jy FOREIGN KEY (id) REFERENCES user_accounts (id);
ALTER TABLE user_roles ADD CONSTRAINT FKnb9ceyh529oqh9n3aiw68twme FOREIGN KEY (user_id) REFERENCES user_accounts (id);
ALTER TABLE vehicles ADD CONSTRAINT FKj0uidnbp2qus8oojhrrdu6i2w FOREIGN KEY (owner_user_id) REFERENCES user_accounts (id);
