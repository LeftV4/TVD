create procedure register_user(
    r_email VARCHAR,
    r_role VARCHAR,
    r_pass VARCHAR,
    OUT p_status TEXT
)
language plpgsql
as $$
BEGIN
    if exists (select 1 from users where email = r_email) THEN
        p_status := '1' -- 1 means exists
        RETURN;
    END IF;

    INSERT INTO users(email, password, role)
    VALUES (r_email, r_pass, r_role);

    p_status := '0'; --0 means success
END;
$$;

create procedure register_info(
    r_fname VARCHAR,
    r_lname VARCHAR,
    r_phone VARCHAR,
    r_role VARCHAR
)
language plpgsql
as $$
    begin
    IF r_role = 'admin' THEN
        insert into admins(first_name, last_name, phone) values (r_fname, r_lname, r_phone);
        return;
    elsif r_role = 'staff' then
        insert into staff(first_name, last_name, phone) values (r_fname, r_lname, r_phone);
        return;
    else
        insert into guests(first_name, last_name, phone) values (r_fname, r_lname, r_phone);
        return;
    end if;
    END;
$$;

create procedure login (
    r_email VARCHAR,
    r_password VARCHAR,
    out p_status TEXT
)
language plpgsql
as $$
    BEGIN
        --check to see if user exists
        IF NOT EXISTS (SELECT 1 FROM users WHERE email = r_email) THEN
            p_status := '1' --failed
            RETURN;
        END IF;

        --check password
        IF EXISTS (SELECT 1 FROM users WHERE email = r_email AND password = r_password) THEN
            p_status := '0';--success
        ELSE
            p_status := '2'; -- wrong password
        END IF;
   END
$$