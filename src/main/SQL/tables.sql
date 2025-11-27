DROP TABLE IF EXISTS users cascade;
DROP TABLE IF EXISTS guests cascade;
DROP TABLE IF EXISTS admins cascade;
DROP TABLE IF EXISTS staff CASCADE;
DROP TABLE IF EXISTS room_types cascade;
DROP TABLE IF EXISTS rooms cascade;
DROP TABLE IF EXISTS reservations cascade;
DROP TABLE IF EXISTS payments cascade;
DROP TABLE IF EXISTS logfile cascade;



CREATE TABLE users (
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('admin', 'staff', 'guest'))
);

CREATE TABLE guests (
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(50) PRIMARY KEY NOT NULL REFERENCES users(email)
);

CREATE TABLE admins (
    first_name varchar(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone varchar(20),
    email VARCHAR(50) PRIMARY KEY NOT NULL REFERENCES users(email)
);

CREATE TABLE staff (
    first_name varchar(50) NOT NULL,
    last_name VARCHAR(50) not null,
    phone varchar(20),
    email VARCHAR(50) PRIMARY KEY NOT NULL REFERENCES users(email)
);

CREATE TABLE room_types (
    type_id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    price_per_night NUMERIC(10,2) NOT NULL CHECK (price_per_night >= 0) --numeric(6,2) means 6 digits max, 2 max decimals
);

CREATE TABLE rooms (
    room_id SERIAL PRIMARY KEY,
    room_number VARCHAR(10) UNIQUE NOT NULL,
    type_id INT NOT NULL REFERENCES room_types(type_id) ON DELETE RESTRICT,
    status VARCHAR(20) NOT NULL CHECK (status IN ('available', 'occupied'))
);

CREATE TABLE reservations (
    reservation_id SERIAL PRIMARY KEY,
    guest_email VARCHAR(50) NOT NULL REFERENCES guests(email) ON DELETE CASCADE,
    room_id INT NOT NULL REFERENCES rooms(room_id) ON DELETE RESTRICT,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL CHECK (check_out > check_in)
);

CREATE TABLE payments (
    payment_id SERIAL PRIMARY KEY,
    reservation_id INT NOT NULL REFERENCES reservations(reservation_id) ON DELETE CASCADE,
    amount NUMERIC(10,2) NOT NULL CHECK (amount >= 0),
    payment_date DATE NOT NULL DEFAULT CURRENT_DATE,
    method VARCHAR(20) NOT NULL CHECK (method IN ('cash', 'card', 'online'))
);

CREATE TABLE logfile (
    log_id SERIAL PRIMARY KEY,
    table_name TEXT NOT NULL,
    operation TEXT NOT NULL,
    old_data JSONB,
    new_data JSONB,
    modified_by TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

