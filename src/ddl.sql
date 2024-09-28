CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       role_name VARCHAR(50) NOT NULL
);

INSERT INTO roles (role_name) VALUES ('USER'), ('ADMIN');

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    login VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    username VARCHAR(50) NOT NULL,
    role_id INTEGER NOT NULL,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);


DO $$
    DECLARE
        admin_role_id INTEGER;
    BEGIN
        SELECT id INTO admin_role_id FROM roles WHERE role_name = 'ADMIN';

        INSERT INTO users (login, password, username, role_id) VALUES ('admin', 'admin', 'Administrator', admin_role_id);
    END $$;