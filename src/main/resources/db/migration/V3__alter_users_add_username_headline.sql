ALTER TABLE users
    ADD COLUMN username VARCHAR(30) NOT NULL DEFAULT '' AFTER email,
    ADD COLUMN headline VARCHAR(150) NULL AFTER username;

ALTER TABLE users
    ADD UNIQUE INDEX uq_users_username (username)