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
