create procedure register (
    r_fname VARCHAR,
    r_lname VARCHAR,
    r_phone VARCHAR,
    r_email VARCHAR,
    OUT p_status TEXT
)
language plpgsql
as $$
BEGIN
    if exists (select 1 from guests where email = r_email) THEN
        p_status := '1' -- 1 means failure
        RETURN;
    END IF;

    INSERT INTO guests(first_name, last_name, phone, email)
    VALUES (r_fname, r_lname, r_phone, r_email);

    p_status := '0'; --0 means success
END;
$$;