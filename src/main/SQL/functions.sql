CREATE or REPLACE FUNCTION register_user(
    r_email VARCHAR,
    r_role VARCHAR,
    r_pass VARCHAR
) RETURNS INT
    language plpgsql
as $$
BEGIN
    if exists (select 1 from users where email = r_email) THEN
        RETURN 1;
    END IF;

    EXECUTE 'INSERT INTO users(email, password, role) VALUES ($1, $2, $3)'
        USING r_email, r_pass, r_role;

    RETURN 0;

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
DECLARE
    table_name VARCHAR;
    query_string TEXT;
begin
    IF r_role = 'admin' THEN
        table_name := 'admins';
    ELSIF r_role = 'staff' then
        table_name := 'staff';
    else
        table_name := 'guests';
    end if;

    query_string := 'insert into ' || quote_ident(table_name) || '(first_name, last_name, phone, email) values ($1, $2, $3, $4)';

    EXECUTE query_string USING r_fname, r_lname, r_phone, r_email;

    return 0;
EXCEPTION
    WHEN OTHERS THEN
        RETURN 1;
END;
$$;



create or replace function update_info(
    r_fname VARCHAR,
    r_lname VARCHAR,
    r_phone VARCHAR,
    r_role VARCHAR,
    r_email_old VARCHAR,
    r_email_new VARCHAR
)returns int
    language plpgsql
as $$
DECLARE
    table_name VARCHAR;
    query_string TEXT;
    current_email VARCHAR;
begin
    current_email := r_email_old;

    if r_email_old != r_email_new then
        EXECUTE 'UPDATE users SET email = $1 WHERE email = $2' USING r_email_new, r_email_old;
        current_email := r_email_new;
    end if;

    IF r_role = 'admin' THEN
        table_name := 'admins';
    ELSIF r_role = 'staff' then
        table_name := 'staff';
    else
        table_name := 'guests';
    end if;

    query_string := 'UPDATE ' || quote_ident(table_name) || ' SET first_name = $1, last_name = $2, phone = $3 WHERE email = $4';

    EXECUTE query_string USING r_fname, r_lname, r_phone, current_email;

    return 1;
EXCEPTION
    WHEN OTHERS THEN
        RETURN -1;
END;
$$;

create or replace function delete_entity(
    entity_id INT,
    identifier VARCHAR
)
    returns int
    language plpgsql
as $$
DECLARE
    query_string TEXT;
    table_name VARCHAR;
    where_column VARCHAR;
    delete_id INT;
begin
    CASE entity_id
        WHEN 1 THEN
            table_name := 'users';
            where_column := 'email';
        WHEN 2 THEN
            table_name := 'reservations';
            where_column := 'reservation_id';
        ELSE
            RETURN -1;
        END CASE;

    IF entity_id = 1 THEN
        query_string := 'DELETE FROM ' || quote_ident(table_name) || ' WHERE ' || quote_ident(where_column) || ' = $1';
        EXECUTE query_string USING identifier;

    ELSIF entity_id = 2 THEN
        BEGIN
            delete_id := identifier::INT;
        EXCEPTION WHEN invalid_text_representation THEN
            RETURN -2;
        END;

        query_string := 'DELETE FROM ' || quote_ident(table_name) || ' WHERE ' || quote_ident(where_column) || ' = $1';
        EXECUTE query_string USING delete_id;
    END IF;

    IF FOUND THEN
        RETURN 0;
    ELSE
        RETURN 1;
    END IF;

EXCEPTION
    WHEN OTHERS THEN
        RETURN 1;
end;
$$;



create or replace function login(
    r_email VARCHAR,
    r_password VARCHAR
) returns VARCHAR
    language plpgsql
as $$
DECLARE
    user_role VARCHAR;
BEGIN

    EXECUTE 'SELECT role FROM users WHERE email = $1 AND password = $2'
        INTO user_role
        USING r_email, r_password;

    return user_role;
EXCEPTION
    WHEN OTHERS THEN
        RETURN NULL;
END
$$;

CREATE OR REPLACE FUNCTION get_entity_list(
    entity_id INT -- 1: all users, 2: guests, 3: bills, 4: reservations
)
    RETURNS VARCHAR
    LANGUAGE plpgsql
AS $$
DECLARE
    result_str VARCHAR;
    query_string TEXT;
    table_name VARCHAR;
    select_column VARCHAR;
    where_clause TEXT := '';
BEGIN
    CASE entity_id
        WHEN 1 THEN
            table_name := 'users';
            select_column := 'email';
        WHEN 2 THEN
            table_name := 'users';
            select_column := 'email';
            where_clause := ' WHERE role = ''guest''';
        WHEN 3 THEN
            table_name := 'payments';
            select_column := 'payment_id';
        WHEN 4 THEN
            table_name := 'reservations';
            select_column := 'reservation_id';
        ELSE
            RETURN NULL; -- Άγνωστος ID
        END CASE;

    -- Σύνθεση του δυναμικού ερωτήματος
    query_string := 'SELECT STRING_AGG(CAST(' || quote_ident(select_column) || ' AS VARCHAR), E''\n'') FROM ' || quote_ident(table_name) || where_clause;

    EXECUTE query_string INTO result_str;

    RETURN result_str;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
    WHEN OTHERS THEN
        RETURN NULL;
END;
$$;


CREATE OR REPLACE FUNCTION get_role_by_email(r_email VARCHAR)
    RETURNS VARCHAR
    LANGUAGE plpgsql
AS $$
DECLARE
    r_role VARCHAR;
BEGIN
    select role INTO r_role from users where email = r_email;
    return r_role;
end;
$$;

CREATE OR REPLACE FUNCTION get_pass_by_email(r_email VARCHAR)
    RETURNS VARCHAR
    LANGUAGE plpgsql
AS $$
DECLARE
    r_password VARCHAR;
BEGIN
    select password INTO r_password from users where email = r_email;
    return r_password;
end;
$$;

create or replace function update_pass(
    r_email VARCHAR,
    new_password VARCHAR
) returns int
    language plpgsql
as $$
DECLARE
    query_string TEXT;
BEGIN
    query_string := 'UPDATE users SET password = $1 WHERE email = $2';

    EXECUTE query_string USING new_password, r_email;

    IF FOUND THEN
        RETURN 0;
    ELSE
        RETURN 1;
    END IF;

EXCEPTION
    WHEN OTHERS THEN
        RETURN -1;
END;
$$;


CREATE OR REPLACE FUNCTION get_user_info_by_email(
    r_email VARCHAR,
    field_id INT
)
    RETURNS VARCHAR
    LANGUAGE plpgsql
AS $$
DECLARE
    info_val VARCHAR;
    r_role VARCHAR;
    table_name VARCHAR;
    field_name VARCHAR;
BEGIN
    CASE field_id
        WHEN 1 THEN field_name := 'first_name';
        WHEN 2 THEN field_name := 'last_name';
        WHEN 3 THEN field_name := 'phone';
        ELSE RAISE EXCEPTION 'Invalid field ID: %', field_id;
        END CASE;

    SELECT role INTO r_role FROM users WHERE email = r_email;

    IF r_role = 'admin' THEN
        table_name := 'admins';
    ELSIF r_role = 'staff' THEN
        table_name := 'staff';
    ELSE
        table_name := 'guests';
    END IF;

    EXECUTE 'SELECT ' || quote_ident(field_name) || ' FROM ' || quote_ident(table_name) || ' WHERE email = $1'
        INTO info_val
        USING r_email;

    RETURN info_val;

EXCEPTION
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
    WHEN OTHERS THEN
        RAISE NOTICE 'Error in get_user_info_by_email_id_dynamic: %', SQLERRM;
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

CREATE OR REPLACE FUNCTION get_reservations_by_email(r_email VARCHAR)
    RETURNS VARCHAR
    LANGUAGE plpgsql
AS $$
BEGIN
    RETURN (SELECT STRING_AGG(CAST(reservation_id AS VARCHAR), E'\n') FROM reservations where guest_email = r_email);
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

CREATE OR REPLACE FUNCTION get_field_by_id(
    id_value VARCHAR,
    id_type INT,
    target_field INT
)
    RETURNS VARCHAR
    LANGUAGE plpgsql
AS $$
DECLARE
    result_val VARCHAR;
    query_string TEXT;
    table_name VARCHAR;
    filter_column VARCHAR;
    select_column VARCHAR;
    id_int INT;
BEGIN
    CASE id_type
        WHEN 1 THEN
        filter_column := 'reservation_id';
        id_int := id_value::INT;
        CASE target_field
            WHEN 1 THEN table_name := 'reservations'; select_column := 'guest_email';
            WHEN 2 THEN table_name := 'reservations'; select_column := 'check_in';
            WHEN 3 THEN table_name := 'reservations'; select_column := 'check_out';
            WHEN 4 THEN table_name := 'payments'; select_column := 'amount';
            ELSE RETURN NULL;
            END CASE;

        WHEN 2 THEN
        filter_column := 'payment_id';
        id_int := id_value::INT;
        CASE target_field
            WHEN 5 THEN table_name := 'payments'; select_column := 'reservation_id';
            WHEN 6 THEN table_name := 'payments'; select_column := 'payment_date';
            WHEN 7 THEN table_name := 'payments'; select_column := 'method';
            ELSE RETURN NULL;
            END CASE;

        ELSE
            RETURN NULL;
        END CASE;

    -- 2. Δυναμική Εκτέλεση
    query_string := 'SELECT CAST(' || quote_ident(select_column) || ' AS VARCHAR) FROM ' || quote_ident(table_name) || ' WHERE ' || quote_ident(filter_column) || ' = $1';

    EXECUTE query_string INTO result_val USING id_int;

    RETURN result_val;

EXCEPTION
    WHEN invalid_text_representation THEN
        RETURN '-2';
    WHEN NO_DATA_FOUND THEN
        RETURN NULL;
    WHEN OTHERS THEN
        RETURN '-1';
END;
$$;


create or replace function get_amount_by_resid(r_resid int)
    returns double precision
    language plpgsql
as $$
begin
    return (select amount from payments where reservation_id = r_resid);
end;
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

create or replace function get_resid_by_paymentid(r_paymentid int)
    returns int
    language plpgsql
as $$
    DECLARE
        resid INT;
begin
    select reservation_id into resid from payments where payment_id = r_paymentid;
    return resid;

    EXCEPTION
        WHEN OTHERS THEN
            RAISE NOTICE 'Error: %', SQLERRM;
            RETURN 1;
end;
$$;

create or replace function get_paydate_by_paymentid(r_paymentid int)
    returns timestamp
    language plpgsql
as $$
    DECLARE
        paydate timestamp;
begin
    select payment_date into paydate from payments where payment_id = r_paymentid;
    return paydate;

    exception
    when others then
            raise notice 'Error: %', SQLERRM;
            return null;
end;
$$;

create or replace function get_method_by_paymentid(r_paymentid int)
    returns varchar
    language plpgsql
as $$
DECLARE
    r_method varchar;
begin
    select method into r_method from payments where payment_id = r_paymentid;
    return r_method;

exception
    when others then
        raise notice 'Error: %', SQLERRM;
        return null;
end;
$$;


CREATE OR REPLACE FUNCTION get_price_by_type(
    p_type_id INT
)
    RETURNS INT
    LANGUAGE plpgsql
AS $$
DECLARE
    price INT;
BEGIN
    EXECUTE 'SELECT price_per_night FROM room_types WHERE type_id = $1'
        INTO price
        USING p_type_id;
    RETURN price;
END;
$$;

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
    type_ids INT[] := ARRAY[1,2,3];
    find_room_query TEXT;
    insert_room_query TEXT := 'INSERT INTO reservation_rooms(reservation_id, room_number) VALUES ($1, $2)';
BEGIN
    EXECUTE 'INSERT INTO reservations(guest_email, check_in, check_out) VALUES ($1, $2, $3) RETURNING reservation_id'
        INTO res_id
        USING p_guest_email, p_check_in, p_check_out;

    FOR r_type, r_count IN SELECT unnest(type_ids), unnest(type_counts)
        LOOP
            IF r_count = 0 THEN
                CONTINUE;
            END IF;

            reserved_count := 0;

            find_room_query := '
                SELECT room_number
                FROM rooms r
                WHERE r.type_id = $1
                  AND NOT EXISTS (
                    SELECT 1
                    FROM reservation_rooms rr
                             JOIN reservations res ON rr.reservation_id = res.reservation_id
                    WHERE rr.room_number = r.room_number
                      AND res.check_in < $3
                      AND res.check_out > $2
                )
                ORDER BY r.room_number
                LIMIT ' || r_count;

            FOR available_room IN EXECUTE find_room_query USING r_type, p_check_in, p_check_out
                LOOP
                    EXECUTE insert_room_query USING res_id, available_room.room_number;
                    reserved_count := reserved_count + 1;
                END LOOP;

            IF reserved_count < r_count THEN
                RAISE EXCEPTION 'Not enough rooms available for type %', r_type;
            END IF;
        END LOOP;

    RETURN res_id;

EXCEPTION
    WHEN OTHERS THEN
        RETURN -1;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION register_payment(
    p_reservation_id INT,
    p_amount DOUBLE PRECISION,
    p_method VARCHAR
)
    RETURNS VOID AS $$
BEGIN
    EXECUTE 'INSERT INTO payments(reservation_id, amount, method, payment_date) VALUES ($1, $2, $3, NOW())'
        USING p_reservation_id, p_amount, p_method;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION count_available(
    p_check_in DATE,
    p_check_out DATE,
    p_type_id INT
)
    RETURNS INT AS $$
DECLARE
    available_count INT;
    query_string TEXT;
BEGIN
    query_string := '
        SELECT COUNT(*)
        FROM rooms r
        WHERE r.type_id = $1
          AND NOT EXISTS (
            SELECT 1
            FROM reservation_rooms rr
                     JOIN reservations res ON rr.reservation_id = res.reservation_id
            WHERE rr.room_number = r.room_number
              AND res.check_in < $3
              AND res.check_out > $2
        )';

    EXECUTE query_string
        INTO available_count
        USING p_type_id, p_check_in, p_check_out;

    RETURN available_count;
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
    EXECUTE 'UPDATE room_types SET price_per_night = $1 WHERE type_id = 1' USING p_price1;
    EXECUTE 'UPDATE room_types SET price_per_night = $1 WHERE type_id = 2' USING p_price2;
    EXECUTE 'UPDATE room_types SET price_per_night = $1 WHERE type_id = 3' USING p_price3;
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

CREATE TRIGGER log_rooms_changes
    AFTER INSERT OR UPDATE OR DELETE ON reservation_rooms
    FOR EACH ROW EXECUTE FUNCTION log_changes_function();

DROP TRIGGER IF EXISTS log_reservations_changes ON reservations;
CREATE TRIGGER log_reservations_changes
    AFTER INSERT OR UPDATE OR DELETE ON reservations
    FOR EACH ROW EXECUTE FUNCTION log_changes_function();

DROP TRIGGER IF EXISTS log_payments_changes ON payments;
CREATE TRIGGER log_payments_changes
    AFTER INSERT OR UPDATE OR DELETE ON payments
    FOR EACH ROW EXECUTE FUNCTION log_changes_function();