INSERT INTO roles (id, name)
VALUES ('3d7f1c1a-55ef-4d8b-8b4b-f76e1a9050b8', 'SUPER_ADMIN'),
       ('f0e3fabc-1c62-4f27-8b7d-9148a309147a', 'ADMIN'),
       ('9a6e3c9d-c6c4-44cf-a8b1-370b86ac6b6d', 'USER')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users (id, username, password, first_name, last_name, email, phone, enabled, last_password_reset_date)
VALUES ('642e1a9e-38b6-4191-b0ff-2b69a3a9f5f3', 'drafttool', '$2a$10$udA7KSLol8HYA5Ntt98e8OrTtR49vXRMozVwrnudDjJLNCo3RSyFW', 'drafttool', '', 'u1@gmail.com', '+34 636598871', true, '01-01-2016')
ON CONFLICT (id) DO NOTHING;

INSERT INTO users_roles (id_user, id_role)
VALUES ('642e1a9e-38b6-4191-b0ff-2b69a3a9f5f3', '3d7f1c1a-55ef-4d8b-8b4b-f76e1a9050b8'),
       ('642e1a9e-38b6-4191-b0ff-2b69a3a9f5f3', 'f0e3fabc-1c62-4f27-8b7d-9148a309147a'),
       ('642e1a9e-38b6-4191-b0ff-2b69a3a9f5f3', '9a6e3c9d-c6c4-44cf-a8b1-370b86ac6b6d')
ON CONFLICT (id_user, id_role) DO NOTHING;