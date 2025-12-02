--liquibase formatted sql

--changeset damvih:1
CREATE TABLE users
(
    id       BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(128) NOT NULL,
    password VARCHAR(256) NOT NULL
);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);
