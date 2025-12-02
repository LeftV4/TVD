CREATE or REPLACE FUNCTION register_user(
    r_email VARCHAR,
    r_role VARCHAR,
    r_pass VARCHAR
) RETURNS INT
language plpgsql
as $$
BEGIN
    if exists (select 1 from users where email = r_email) THEN
        RETURN 1; -- 1 means exists
    END IF;

    INSERT INTO users(email, password, role)
    VALUES (r_email, r_pass, r_role);

    RETURN 0; --0 means success

    EXCEPTION
        WHEN OTHERS THEN
            RETURN 2;
END;
$$;

create or replace function register_info(
    r_fname VARCHAR,
    r_lname VARCHAR,
    r_phone VARCHAR,
    r_role VARCHAR,
    r_email VARCHAR
)returns int
language plpgsql
as $$
    begin
    IF r_role = 'admin' THEN
        insert into admins(first_name, last_name, phone, email) values (r_fname, r_lname, r_phone, r_email);
    ELSIF r_role = 'staff' then
        insert into staff(first_name, last_name, phone, email) values (r_fname, r_lname, r_phone,r_email);
    else
        insert into guests(first_name, last_name, phone, email) values (r_fname, r_lname, r_phone, r_email);
    end if;
    return 0;
    EXCEPTION
        WHEN OTHERS THEN
            RAISE NOTICE 'Error: %', SQLERRM;
            RETURN 1;
    END;
$$;

create or replace function update_info(
    r_fname VARCHAR,
    r_lname VARCHAR,
    r_phone VARCHAR,
    r_role VARCHAR,
    r_email VARCHAR,
    r_email2 VARCHAR
)returns int
    language plpgsql
as $$
begin
    if r_email != r_email2 then
        update users set email = r_email2 where email = r_email;
        IF r_role = 'admin' THEN
            update admins set first_name = r_fname, last_name = r_lname, phone = r_phone where email = r_email2;
            return 1;
        ELSIF r_role = 'staff' then
            update staff set first_name = r_fname, last_name = r_lname, phone = r_phone where email = r_email2;
            return 1;
        else
            update guests set first_name = r_fname, last_name = r_lname, phone = r_phone where email = r_email2;
            return 1;
        end if;
    else
        IF r_role = 'admin' THEN
            update admins set first_name = r_fname, last_name = r_lname, phone = r_phone where email = r_email;
            return 1;
        ELSIF r_role = 'staff' then
            update staff set first_name = r_fname, last_name = r_lname, phone = r_phone where email = r_email;
            return 1;
        else
            update guests set first_name = r_fname, last_name = r_lname, phone = r_phone where email = r_email;
            return 1;
        end if;
    end if;
EXCEPTION
    WHEN OTHERS THEN
        RAISE NOTICE 'Error: %', SQLERRM;
        RETURN -1;
END;
$$;

create or replace function deleteUser_by_email(r_email VARCHAR)
    returns int
    language plpgsql
as $$
begin
    delete from users where email = r_email;
    return 0;
EXCEPTION
    WHEN OTHERS THEN
        RAISE NOTICE 'Error: %', SQLERRM;
        RETURN 1;
END;
$$;

create or replace function deleteRes_by_resid(r_resid INT)
    returns int
    language plpgsql
as $$
begin
    delete from reservations where reservation_id = r_resid;
    return 0;
EXCEPTION
    WHEN OTHERS THEN
        RAISE NOTICE 'Error: %', SQLERRM;
        RETURN 1;
END;
$$;




create or replace function login (
    r_email VARCHAR,
    r_password VARCHAR
) returns VARCHAR
language plpgsql
as $$
    DECLARE
        user_role VARCHAR;
    BEGIN

        select role INTO user_role from users where email = r_email and password = r_password;
        return user_role;
        EXCEPTION
            WHEN OTHERS THEN
                RETURN NULL;
   END
$$;

CREATE OR REPLACE FUNCTION get_users()
    RETURNS VARCHAR
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN (SELECT STRING_AGG(CAST(email AS VARCHAR), E'\n') FROM users);

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
$$;

CREATE OR REPLACE FUNCTION get_fname_by_email(r_email VARCHAR)
    RETURNS VARCHAR
    LANGUAGE plpgsql
AS $$
DECLARE
    fname VARCHAR;
BEGIN
    IF ( (SELECT role FROM users where r_email = email) = 'admin') THEN
        select first_name INTO fname from admins where email = r_email;
    ELSIF ((SELECT role FROM users where r_email = email) = 'staff') THEN
        select first_name INTO fname from staff where email = r_email;
    ELSE
        select first_name INTO fname from guests where email = r_email;
    END IF;
    RETURN fname;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
$$;

CREATE OR REPLACE FUNCTION get_lname_by_email(r_email VARCHAR)
    RETURNS VARCHAR
    LANGUAGE plpgsql
AS $$
DECLARE
    fname VARCHAR;
BEGIN
    IF ( (SELECT role FROM users where r_email = email) = 'admin') THEN
        select last_name INTO fname from admins where email = r_email;
    ELSIF ((SELECT role FROM users where r_email = email) = 'staff') THEN
        select last_name INTO fname from staff where email = r_email;
    ELSE
        select last_name INTO fname from guests where email = r_email;
    END IF;
    RETURN fname;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
$$;

CREATE OR REPLACE FUNCTION get_role_by_email(r_email VARCHAR)
    RETURNS VARCHAR
    LANGUAGE plpgsql
AS $$
DECLARE
    role VARCHAR;
BEGIN
    select role INTO role from users where email = r_email;
end;
$$;

CREATE OR REPLACE FUNCTION get_phone_by_email(r_email VARCHAR)
    RETURNS VARCHAR
    LANGUAGE plpgsql
AS $$
DECLARE
    phonenum VARCHAR;
BEGIN
    IF ( (SELECT role FROM users where r_email = email) = 'admin') THEN
        select phone INTO phonenum from admins where email = r_email;
    ELSIF ((SELECT role FROM users where r_email = email) = 'staff') THEN
        select phone INTO phonenum from staff where email = r_email;
    ELSE
        select phone INTO phonenum from guests where email = r_email;
    END IF;
    RETURN phonenum;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
$$;

CREATE OR REPLACE FUNCTION get_reservations()
    RETURNS VARCHAR
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN (SELECT STRING_AGG(CAST(reservation_id AS VARCHAR), E'\n') FROM reservations);


EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
$$;

CREATE OR REPLACE FUNCTION get_reservation_rooms(r_resid int)
    RETURNS VARCHAR
    LANGUAGE plpgsql
AS $$
BEGIN
    return(select STRING_AGG(CAST(room_number AS VARCHAR), E'\n') from reservation_rooms where reservation_id = r_resid);

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
$$;

CREATE OR REPLACE FUNCTION get_guest_email_by_resid(r_resid int)
    RETURNS VARCHAR
    LANGUAGE plpgsql
AS $$
DECLARE
     r_email VARCHAR;
BEGIN
    select guest_email into r_email from reservations where reservation_id = r_resid;
    return r_email;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
$$;

CREATE OR REPLACE FUNCTION get_checkIn_by_resid(r_resid int)
    RETURNS VARCHAR
    LANGUAGE plpgsql
AS $$
DECLARE
    r_checkIn DATE;
BEGIN
    select check_in into r_checkIn from reservations where reservation_id = r_resid;
    return r_checkIn;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
$$;

CREATE OR REPLACE FUNCTION get_checkOut_by_resid(r_resid int)
    RETURNS VARCHAR
    LANGUAGE plpgsql
AS $$
DECLARE
    r_checkOut DATE;
BEGIN
    select check_out into r_checkOut from reservations where reservation_id = r_resid;
    return r_checkOut;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
END;
$$;



CREATE OR REPLACE FUNCTION get_single_price()
    RETURNS INT
    LANGUAGE plpgsql
AS $$
    DECLARE
        price INT;
BEGIN
    select price_per_night into price from room_types where type_id = 1;
    RETURN price;
END;
$$;

CREATE OR REPLACE FUNCTION get_suite_price()
    RETURNS INT
    LANGUAGE plpgsql
AS $$
DECLARE
    price INT;
BEGIN
    select price_per_night into price from room_types where type_id = 3;
    RETURN price;
END;
$$;

CREATE OR REPLACE FUNCTION get_double_price()
    RETURNS INT
    LANGUAGE plpgsql
AS $$
DECLARE
    price INT;
BEGIN
    select price_per_night into price from room_types where type_id = 2;
    RETURN price;
END;
$$;


CREATE OR REPLACE FUNCTION count_available_single(
    p_check_in DATE,
    p_check_out DATE
)
    RETURNS INT AS $$
DECLARE
    available_count INT;
BEGIN
    SELECT COUNT(*)
    INTO available_count
    FROM rooms r
    WHERE r.type_id = 1
      AND NOT EXISTS (
        SELECT 1
        FROM reservation_rooms rr
                 JOIN reservations res ON rr.reservation_id = res.reservation_id
        WHERE rr.room_number = r.room_number
          AND res.check_in < p_check_out
          AND res.check_out > p_check_in
    );

    RETURN available_count;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION count_available_double(
    p_check_in DATE,
    p_check_out DATE
)
    RETURNS INT AS $$
DECLARE
    available_count INT;
BEGIN
    SELECT COUNT(*)
    INTO available_count
    FROM rooms r
    WHERE r.type_id = 2
      AND NOT EXISTS (
        SELECT 1
        FROM reservation_rooms rr
                 JOIN reservations res ON rr.reservation_id = res.reservation_id
        WHERE rr.room_number = r.room_number
          AND res.check_in < p_check_out
          AND res.check_out > p_check_in
    );

    RETURN available_count;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION count_available_suite(
    p_check_in DATE,
    p_check_out DATE
)
    RETURNS INT AS $$
DECLARE
    available_count INT;
BEGIN
    SELECT COUNT(*)
    INTO available_count
    FROM rooms r
    WHERE r.type_id = 3
      AND NOT EXISTS (
        SELECT 1
        FROM reservation_rooms rr
                 JOIN reservations res ON rr.reservation_id = res.reservation_id
        WHERE rr.room_number = r.room_number
          AND res.check_in < p_check_out
          AND res.check_out > p_check_in
    );

    RETURN available_count;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION make_reservation(
    p_guest_email VARCHAR,
    p_check_in DATE,
    p_check_out DATE,
    p_type1_count INT,
    p_type2_count INT,
    p_type3_count INT
)
    RETURNS INT AS $$
DECLARE
    res_id INT;
    r_type INT;
    r_count INT;
    available_room RECORD;
    reserved_count INT;
    type_counts INT[] := ARRAY[p_type1_count, p_type2_count, p_type3_count];
    type_ids INT[] := ARRAY[1,2,3];  -- adjust if your type IDs differ
BEGIN
    -- Insert reservation first
    INSERT INTO reservations(guest_email, check_in, check_out)
    VALUES (p_guest_email, p_check_in, p_check_out)
    RETURNING reservation_id INTO res_id;

    -- Loop over each room type
    FOR r_type, r_count IN SELECT unnest(type_ids), unnest(type_counts)
        LOOP
            IF r_count = 0 THEN
                CONTINUE;
            END IF;

            reserved_count := 0;

            -- Find first available rooms for this type
            FOR available_room IN
                SELECT room_number
                FROM rooms r
                WHERE r.type_id = r_type
                  AND NOT EXISTS (
                    SELECT 1
                    FROM reservation_rooms rr
                             JOIN reservations res ON rr.reservation_id = res.reservation_id
                    WHERE rr.room_number = r.room_number
                      AND res.check_in < p_check_out
                      AND res.check_out > p_check_in
                )
                ORDER BY r.room_number
                LIMIT r_count
                LOOP
                    -- Insert into reservation_rooms
                    INSERT INTO reservation_rooms(reservation_id, room_number)
                    VALUES (res_id, available_room.room_number);

                    reserved_count := reserved_count + 1;
                END LOOP;

            IF reserved_count < r_count THEN
                RAISE EXCEPTION 'Not enough rooms available for type %', r_type;
            END IF;
        END LOOP;

    RETURN res_id;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION register_payment(
    p_reservation_id INT,
    p_amount DOUBLE PRECISION,
    p_method VARCHAR
)
    RETURNS VOID AS $$
BEGIN
    INSERT INTO payments(reservation_id, amount, method)
    VALUES (p_reservation_id, p_amount, p_method);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION update_room_prices(
    p_price1 NUMERIC(10,2),
    p_price2 NUMERIC(10,2),
    p_price3 NUMERIC(10,2)
)
    RETURNS VOID AS $$
BEGIN


    UPDATE room_types SET price_per_night = p_price1 WHERE type_id = 1;
    UPDATE room_types SET price_per_night = p_price2 WHERE type_id = 2;
    UPDATE room_types SET price_per_night = p_price3 WHERE type_id = 3;
END;
$$ LANGUAGE plpgsql;





--LOG FILE FUNCTIONS

CREATE OR REPLACE FUNCTION log_changes_function()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
BEGIN
    IF (TG_OP = 'INSERT') THEN
        INSERT INTO logfile (table_name, operation, new_data, modified_by)
        VALUES (TG_TABLE_NAME, TG_OP, row_to_json(NEW), current_user);
        RETURN NEW;
    ELSIF (TG_OP = 'UPDATE') THEN
        INSERT INTO logfile (table_name, operation, old_data, new_data, modified_by)
        VALUES (TG_TABLE_NAME, TG_OP, row_to_json(OLD), row_to_json(NEW), current_user);
        RETURN NEW;
    ELSIF (TG_OP = 'DELETE') THEN
        INSERT INTO logfile (table_name, operation, old_data, modified_by)
        VALUES (TG_TABLE_NAME, TG_OP, row_to_json(OLD), current_user);
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$;

CREATE OR REPLACE FUNCTION get_logs()
RETURNS TABLE(
                log_id INT,
                table_name TEXT,
                operation TEXT,
                old_data JSONB,
                new_data JSONB,
                modified_by TEXT,
                timestamp_val TIMESTAMP
             )
    LANGUAGE plpgsql
AS $$
    BEGIN
RETURN QUERY SELECT * FROM logfile ORDER BY timestamp;
    END;
$$;



DROP TRIGGER IF EXISTS log_users_changes ON users;
CREATE TRIGGER log_users_changes
    AFTER INSERT OR UPDATE OR DELETE ON users
    FOR EACH ROW EXECUTE FUNCTION log_changes_function();

DROP TRIGGER IF EXISTS log_guests_changes ON guests;
CREATE TRIGGER log_guests_changes
    AFTER INSERT OR UPDATE OR DELETE ON guests
    FOR EACH ROW EXECUTE FUNCTION log_changes_function();

DROP TRIGGER IF EXISTS log_admins_changes ON admins;
CREATE TRIGGER log_admins_changes
    AFTER INSERT OR UPDATE OR DELETE ON admins
    FOR EACH ROW EXECUTE FUNCTION log_changes_function();

DROP TRIGGER IF EXISTS log_staff_changes ON staff;
CREATE TRIGGER log_staff_changes
    AFTER INSERT OR UPDATE OR DELETE ON staff
    FOR EACH ROW EXECUTE FUNCTION log_changes_function();

DROP TRIGGER IF EXISTS log_room_types_changes ON room_types;
CREATE TRIGGER log_room_types_changes
    AFTER INSERT OR UPDATE OR DELETE ON room_types
    FOR EACH ROW EXECUTE FUNCTION log_changes_function();

DROP TRIGGER IF EXISTS log_rooms_changes ON rooms;
CREATE TRIGGER log_rooms_changes
    AFTER INSERT OR UPDATE OR DELETE ON rooms
    FOR EACH ROW EXECUTE FUNCTION log_changes_function();

DROP TRIGGER IF EXISTS log_reservations_changes ON reservations;
CREATE TRIGGER log_reservations_changes
    AFTER INSERT OR UPDATE OR DELETE ON reservations
    FOR EACH ROW EXECUTE FUNCTION log_changes_function();

DROP TRIGGER IF EXISTS log_payments_changes ON payments;
CREATE TRIGGER log_payments_changes
    AFTER INSERT OR UPDATE OR DELETE ON payments
    FOR EACH ROW EXECUTE FUNCTION log_changes_function();