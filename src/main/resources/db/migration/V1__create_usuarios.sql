CREATE TABLE usuarios (
                          id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nombre     VARCHAR(100)  NOT NULL,
                          email      VARCHAR(150)  NOT NULL UNIQUE,
                          password   VARCHAR(255)  NOT NULL,
                          rol        VARCHAR(20)   NOT NULL DEFAULT 'USUARIO',
                          activo     BOOLEAN       NOT NULL DEFAULT TRUE,
                          created_at DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          INDEX idx_usuarios_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE refresh_tokens (
                                id               BIGINT AUTO_INCREMENT PRIMARY KEY,
                                token            VARCHAR(512) NOT NULL UNIQUE,
                                usuario_id       BIGINT       NOT NULL,
                                fecha_expiracion DATETIME     NOT NULL,
                                revocado         BOOLEAN      NOT NULL DEFAULT FALSE,
                                CONSTRAINT fk_rt_usuario FOREIGN KEY (usuario_id)
                                    REFERENCES usuarios(id) ON DELETE CASCADE,
                                INDEX idx_rt_token (token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;