CREATE TABLE users (
    user_id SERIAL PRIMARY KEY, --Serial Auto generates the id with increasing numbers (First user has id 1, second id 2 etc.)
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('admin', 'staff'))
);

CREATE TABLE guests (
    guest_id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100)
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
    guest_id INT NOT NULL REFERENCES guests(guest_id) ON DELETE CASCADE,
    room_id INT NOT NULL REFERENCES rooms(room_id) ON DELETE RESTRICT,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL CHECK (check_out > check_in),
    created_by INT NOT NULL REFERENCES users(user_id) ON DELETE RESTRICT 
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