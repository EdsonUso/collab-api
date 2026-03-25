-- V2: Especialização de usuários (many-to-many)

CREATE TABLE specializations (
    id SMALLINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    slug VARCHAR(50) NOT NULL,
    description VARCHAR (255) NULL,
    icon VARCHAR (50) NULL,
    display_order SMALLINT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,

    PRIMARY KEY (id),
    UNIQUE KEY uk_specialization_slug (slug)
) ENGINE= InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_specializations (
                                      user_id             BIGINT      NOT NULL,
                                      specialization_id   SMALLINT    NOT NULL,
                                      is_primary          BOOLEAN     NOT NULL DEFAULT FALSE,    -- Especialização principal (destaque no perfil)
                                      assigned_at         TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                      PRIMARY KEY (user_id, specialization_id),
                                      INDEX idx_user_spec_spec (specialization_id),
                                      INDEX idx_user_spec_primary (user_id, is_primary),

                                      CONSTRAINT fk_user_spec_user FOREIGN KEY (user_id)
                                          REFERENCES users(id) ON DELETE CASCADE,
                                      CONSTRAINT fk_user_spec_spec FOREIGN KEY (specialization_id)
                                          REFERENCES specializations(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO specializations (name, slug, description, icon, display_order) VALUES
                                                                               ('Programador',      'programmer',     'Desenvolvimento de software e sistemas',          'code',            1),
                                                                               ('Designer',         'designer',       'Design gráfico, UI/UX e identidade visual',       'palette',         2),
                                                                               ('Artista',          'artist',         'Artes visuais, ilustração e arte digital',         'brush',           3),
                                                                               ('Músico',           'musician',       'Composição, produção e performance musical',       'music',           4),
                                                                               ('Escritor',         'writer',         'Escrita criativa, técnica e copywriting',           'pen-tool',        5),
                                                                               ('Fotógrafo',        'photographer',   'Fotografia artística, comercial e editorial',      'camera',          6),
                                                                               ('Videomaker',       'videomaker',     'Produção audiovisual e edição de vídeo',           'video',           7),
                                                                               ('Game Developer',   'game-dev',       'Desenvolvimento de jogos e game design',           'gamepad-2',       8),
                                                                               ('Data Scientist',   'data-scientist', 'Ciência de dados, ML e análise estatística',       'bar-chart-3',     9),
                                                                               ('DevOps',           'devops',         'Infraestrutura, CI/CD e cloud',                    'server',         10),
                                                                               ('Product Manager',  'product-mgr',    'Gestão de produto e estratégia',                   'layout-dashboard',11),
                                                                               ('Streamer',         'streamer',       'Live streaming e criação de conteúdo ao vivo',     'radio',          12);

CREATE TABLE squads (
                        id           BIGINT         NOT NULL AUTO_INCREMENT,
                        public_id    VARCHAR(36)    NOT NULL,
                        name         VARCHAR(100)   NOT NULL,
                        slug         VARCHAR(100)   NOT NULL,
                        description  TEXT           NULL,
                        avatar_url   VARCHAR(500)   NULL,
                        owner_id     BIGINT         NOT NULL,
                        active       BOOLEAN        NOT NULL DEFAULT TRUE,
                        created_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                        PRIMARY KEY (id),
                        UNIQUE INDEX uq_squads_public_id (public_id),
                        UNIQUE INDEX uq_squads_slug (slug),
                        CONSTRAINT fk_squads_owner FOREIGN KEY (owner_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE squad_members (
                               squad_id   BIGINT  NOT NULL,
                               user_id    BIGINT  NOT NULL,
                               role       ENUM('OWNER','ADMIN','MEMBER') NOT NULL DEFAULT 'MEMBER',
                               joined_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                               PRIMARY KEY (squad_id, user_id),
                               CONSTRAINT fk_sm_squad FOREIGN KEY (squad_id) REFERENCES squads(id) ON DELETE CASCADE,
                               CONSTRAINT fk_sm_user  FOREIGN KEY (user_id)  REFERENCES users(id)  ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE games (
                       id           BIGINT         NOT NULL AUTO_INCREMENT,
                       public_id    VARCHAR(36)    NOT NULL,
                       title        VARCHAR(200)   NOT NULL,
                       slug         VARCHAR(200)   NOT NULL,
                       description  TEXT           NULL,
                       cover_url    VARCHAR(500)   NULL,
                       squad_id     BIGINT         NULL,
                       creator_id   BIGINT         NOT NULL,
                       status       ENUM('IN_DEVELOPMENT','RELEASED','PAUSED','ABANDONED') NOT NULL DEFAULT 'IN_DEVELOPMENT',
                       active       BOOLEAN        NOT NULL DEFAULT TRUE,
                       created_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at   TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       PRIMARY KEY (id),
                       UNIQUE INDEX uq_project_public_id (public_id),
                       UNIQUE INDEX uq_project_slug (slug),
                       CONSTRAINT fk_project_squad   FOREIGN KEY (squad_id)   REFERENCES squads(id) ON DELETE SET NULL,
                       CONSTRAINT fk_project_creator FOREIGN KEY (creator_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE follows (
                         id               BIGINT      NOT NULL AUTO_INCREMENT,
                         follower_id      BIGINT      NOT NULL,
                         followable_type  ENUM('USER','SQUAD','GAME') NOT NULL,
                         followable_id    BIGINT      NOT NULL,
                         created_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

                         PRIMARY KEY (id),
                         UNIQUE INDEX uq_follows_unique (follower_id, followable_type, followable_id),
                         INDEX idx_follows_target (followable_type, followable_id),
                         CONSTRAINT fk_follows_follower FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;