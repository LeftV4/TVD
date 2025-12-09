drop table if exists users cascade;

CREATE TABLE users (
    email VARCHAR(50) PRIMARY KEY NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('admin', 'staff', 'guest'))
);

drop table if exists guests cascade;
CREATE TABLE guests (
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(50) PRIMARY KEY NOT NULL REFERENCES users(email) ON DELETE CASCADE ON UPDATE CASCADE
);

drop table if exists admins cascade;
CREATE TABLE admins (
    first_name varchar(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone varchar(20),
    email VARCHAR(50) PRIMARY KEY NOT NULL REFERENCES users(email) ON DELETE CASCADE ON UPDATE CASCADE
);

drop table if exists staff cascade;
CREATE TABLE staff (
    first_name varchar(50) NOT NULL,
    last_name VARCHAR(50) not null,
    phone varchar(20),
    email VARCHAR(50) PRIMARY KEY NOT NULL REFERENCES users(email) ON DELETE CASCADE ON UPDATE CASCADE
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
    room_number VARCHAR(10) PRIMARY KEY NOT NULL,
    type_id INT NOT NULL REFERENCES room_types(type_id) ON DELETE RESTRICT
);

insert into rooms (room_number, type_id) values ('100', 3);
insert into rooms (room_number, type_id) values ('101', 2);
insert into rooms (room_number, type_id) values ('102', 2);
insert into rooms (room_number, type_id) values ('103', 2);
insert into rooms (room_number, type_id) values ('104', 2);
insert into rooms (room_number, type_id) values ('105', 2);
insert into rooms (room_number, type_id) values ('106', 1);
insert into rooms (room_number, type_id) values ('107', 1);
insert into rooms (room_number, type_id) values ('108', 1);
insert into rooms (room_number, type_id) values ('109', 1);

insert into rooms (room_number, type_id) values ('200', 3);
insert into rooms (room_number, type_id) values ('201', 2);
insert into rooms (room_number, type_id) values ('202', 2);
insert into rooms (room_number, type_id) values ('203', 2);
insert into rooms (room_number, type_id) values ('204', 2);
insert into rooms (room_number, type_id) values ('205', 2);
insert into rooms (room_number, type_id) values ('206', 1);
insert into rooms (room_number, type_id) values ('207', 1);
insert into rooms (room_number, type_id) values ('208', 1);
insert into rooms (room_number, type_id) values ('209', 1);

insert into rooms (room_number, type_id) values ('300', 3);
insert into rooms (room_number, type_id) values ('301', 2);
insert into rooms (room_number, type_id) values ('302', 2);
insert into rooms (room_number, type_id) values ('303', 2);
insert into rooms (room_number, type_id) values ('304', 2);
insert into rooms (room_number, type_id) values ('305', 2);
insert into rooms (room_number, type_id) values ('306', 1);
insert into rooms (room_number, type_id) values ('307', 1);
insert into rooms (room_number, type_id) values ('308', 1);
insert into rooms (room_number, type_id) values ('309', 1);


drop table if exists reservations cascade;
CREATE TABLE reservations (
    reservation_id SERIAL PRIMARY KEY,
    guest_email VARCHAR(50) NOT NULL REFERENCES guests(email) ON DELETE CASCADE ON UPDATE CASCADE,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL CHECK (check_out > check_in)
);



drop table if exists reservation_rooms cascade;
CREATE TABLE reservation_rooms (
    reservation_id INT NOT NULL REFERENCES reservations(reservation_id) ON DELETE CASCADE ON UPDATE CASCADE,
    room_number VARCHAR(10) NOT NULL REFERENCES rooms(room_number) ON DELETE CASCADE,
    PRIMARY KEY(reservation_id, room_number)
);

drop table if exists payments cascade;
CREATE TABLE payments (
    payment_id SERIAL PRIMARY KEY,
    reservation_id INT NOT NULL REFERENCES reservations(reservation_id) ON DELETE CASCADE,
    amount NUMERIC(10,2) NOT NULL CHECK (amount >= 0),
    payment_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
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

