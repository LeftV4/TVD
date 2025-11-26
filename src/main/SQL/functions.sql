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