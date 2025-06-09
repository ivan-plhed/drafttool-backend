CREATE TABLE IF NOT EXISTS roles (
                                     id UUID,
                                     name VARCHAR(50),
                                     PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users (
                                     id UUID,
                                     username VARCHAR(50) UNIQUE,
                                     password VARCHAR(120),
                                     email VARCHAR(150),
                                     phone VARCHAR(50),
                                     enabled BOOLEAN,
                                     first_name VARCHAR(50),
                                     last_name VARCHAR(50),
                                     last_password_reset_date DATE,
                                     PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS users_roles (
                                           id_user UUID,
                                           id_role UUID,
                                           FOREIGN KEY (id_user) REFERENCES users (id),
                                           FOREIGN KEY (id_role) REFERENCES roles (id),
                                           PRIMARY KEY (id_user, id_role)
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
                                              token UUID,
                                              user_id UUID,
                                              expiry_date TIMESTAMP,
                                              created_date TIMESTAMP,
                                              PRIMARY KEY (token),
                                              FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS champions (
                                         id UUID,
                                         name VARCHAR(255) UNIQUE,
                                         title VARCHAR(255),
                                         role1 VARCHAR(50),
                                         role2 VARCHAR(50),
                                         PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS players (
                                       id UUID,
                                       name VARCHAR(255) UNIQUE,
                                       real_name VARCHAR(255),
                                       position VARCHAR(255),
                                       country VARCHAR(255),
                                       created_by UUID,
                                       image VARCHAR(2000),
                                       PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS teams (
                                     id UUID,
                                     name VARCHAR(255) UNIQUE,
                                     id_top UUID,
                                     id_jungle UUID,
                                     id_mid UUID,
                                     id_bot UUID,
                                     id_support UUID,
                                     created_by UUID,
                                     PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS players_champions (
                                                 id_player UUID,
                                                 id_champion UUID,
                                                 PRIMARY KEY (id_player, id_champion)
);

CREATE TABLE IF NOT EXISTS users_teams (
                                           id_user UUID,
                                           id_team UUID,
                                           PRIMARY KEY (id_user, id_team)
);

CREATE TABLE IF NOT EXISTS users_players (
                                             id_user UUID,
                                             id_player UUID,
                                             PRIMARY KEY (id_user, id_player)
);