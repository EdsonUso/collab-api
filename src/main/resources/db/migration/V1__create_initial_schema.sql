-- ═══════════════════════════════════════════════════════════
-- V1: Schema inicial — Users + Refresh Tokens
-- ═══════════════════════════════════════════════════════════

CREATE TABLE users (
                       id              BIGINT          NOT NULL AUTO_INCREMENT,
                       public_id       CHAR(36)        NOT NULL,
                       email           VARCHAR(255)    NOT NULL,
                       password_hash   VARCHAR(255)    NULL,
                       display_name    VARCHAR(100)    NOT NULL,
                       avatar_url      VARCHAR(500)    NULL,
                       bio             VARCHAR(500)    NULL,
                       auth_provider   ENUM('LOCAL','GOOGLE','GITHUB') NOT NULL DEFAULT 'LOCAL',
                       provider_id     VARCHAR(255)    NULL,
                       email_verified  BOOLEAN         NOT NULL DEFAULT FALSE,
                       role            ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
                       active          BOOLEAN         NOT NULL DEFAULT TRUE,
                       created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       PRIMARY KEY (id),
                       UNIQUE KEY uk_users_public_id (public_id),
                       UNIQUE KEY uk_users_email (email),
                       UNIQUE KEY uk_users_provider (auth_provider, provider_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE refresh_tokens (
                                id              BIGINT          NOT NULL AUTO_INCREMENT,
                                token_hash      VARCHAR(255)    NOT NULL,
                                user_id         BIGINT          NOT NULL,
                                expires_at      TIMESTAMP       NOT NULL,
                                revoked         BOOLEAN         NOT NULL DEFAULT FALSE,
                                created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                PRIMARY KEY (id),
                                UNIQUE KEY uk_refresh_token_hash (token_hash),
                                INDEX idx_refresh_user (user_id),
                                INDEX idx_refresh_expires (expires_at),

                                CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;