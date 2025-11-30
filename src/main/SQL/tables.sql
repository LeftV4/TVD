drop table if exists users cascade;
CREATE TABLE users (
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('admin', 'staff', 'guest'))
);

drop table if exists guests cascade;
CREATE TABLE guests (
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(50) PRIMARY KEY NOT NULL REFERENCES users(email)
);

drop table if exists admins cascade;
CREATE TABLE admins (
    first_name varchar(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone varchar(20),
    email VARCHAR(50) PRIMARY KEY NOT NULL REFERENCES users(email)
);

drop table if exists staff cascade;
CREATE TABLE staff (
    first_name varchar(50) NOT NULL,
    last_name VARCHAR(50) not null,
    phone varchar(20),
    email VARCHAR(50) PRIMARY KEY NOT NULL REFERENCES users(email)
);

drop table if exists room_types cascade;
CREATE TABLE room_types (
    type_id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    price_per_night NUMERIC(10,2) NOT NULL CHECK (price_per_night >= 0) --numeric(6,2) means 6 digits max, 2 max decimals
);
insert into room_types (name, price_per_night) values ('Single', 75);
insert into room_types (name, price_per_night) values ('Double', 150);
insert into room_types (name, price_per_night) values ('Suite', 300);

drop table if exists rooms cascade;
CREATE TABLE rooms (
    room_number VARCHAR(10) PRIMARY KEY UNIQUE NOT NULL,
    type_id INT NOT NULL REFERENCES room_types(type_id) ON DELETE RESTRICT,
    status VARCHAR(20) NOT NULL CHECK (status IN ('available', 'occupied'))
);

insert into rooms (room_number, type_id, status) values ('100', 3, 'available');
insert into rooms (room_number, type_id, status) values ('101', 2, 'available');
insert into rooms (room_number, type_id, status) values ('102', 2, 'available');
insert into rooms (room_number, type_id, status) values ('103', 2, 'available');
insert into rooms (room_number, type_id, status) values ('104', 2, 'available');
insert into rooms (room_number, type_id, status) values ('105', 2, 'available');
insert into rooms (room_number, type_id, status) values ('106', 1, 'available');
insert into rooms (room_number, type_id, status) values ('107', 1, 'available');
insert into rooms (room_number, type_id, status) values ('108', 1, 'available');
insert into rooms (room_number, type_id, status) values ('109', 1, 'available');

insert into rooms (room_number, type_id, status) values ('200', 3, 'available');
insert into rooms (room_number, type_id, status) values ('201', 2, 'available');
insert into rooms (room_number, type_id, status) values ('202', 2, 'available');
insert into rooms (room_number, type_id, status) values ('203', 2, 'available');
insert into rooms (room_number, type_id, status) values ('204', 2, 'available');
insert into rooms (room_number, type_id, status) values ('205', 2, 'available');
insert into rooms (room_number, type_id, status) values ('206', 1, 'available');
insert into rooms (room_number, type_id, status) values ('207', 1, 'available');
insert into rooms (room_number, type_id, status) values ('208', 1, 'available');
insert into rooms (room_number, type_id, status) values ('209', 1, 'available');

insert into rooms (room_number, type_id, status) values ('300', 3, 'available');
insert into rooms (room_number, type_id, status) values ('301', 2, 'available');
insert into rooms (room_number, type_id, status) values ('302', 2, 'available');
insert into rooms (room_number, type_id, status) values ('303', 2, 'available');
insert into rooms (room_number, type_id, status) values ('304', 2, 'available');
insert into rooms (room_number, type_id, status) values ('305', 2, 'available');
insert into rooms (room_number, type_id, status) values ('306', 1, 'available');
insert into rooms (room_number, type_id, status) values ('307', 1, 'available');
insert into rooms (room_number, type_id, status) values ('308', 1, 'available');
insert into rooms (room_number, type_id, status) values ('309', 1, 'available');


drop table if exists reservations cascade;
CREATE TABLE reservations (
    reservation_id SERIAL PRIMARY KEY,
    guest_email VARCHAR(50) NOT NULL REFERENCES guests(email) ON DELETE CASCADE,
    room_number VARCHAR(10) NOT NULL REFERENCES rooms(room_number) ON DELETE CASCADE,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL CHECK (check_out > check_in)
);

drop table if exists payments cascade;
CREATE TABLE payments (
    payment_id SERIAL PRIMARY KEY,
    reservation_id INT NOT NULL REFERENCES reservations(reservation_id) ON DELETE CASCADE,
    amount NUMERIC(10,2) NOT NULL CHECK (amount >= 0),
    payment_date DATE NOT NULL DEFAULT CURRENT_DATE,
    method VARCHAR(20) NOT NULL CHECK (method IN ('cash', 'card'))
);

drop table if exists logfile cascade;
CREATE TABLE logfile (
    log_id SERIAL PRIMARY KEY,
    table_name TEXT NOT NULL,
    operation TEXT NOT NULL,
    old_data JSONB,
    new_data JSONB,
    modified_by TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

