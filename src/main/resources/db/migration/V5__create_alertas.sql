CREATE TABLE alertas (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id  BIGINT       NOT NULL,
    tipo        VARCHAR(50)  NOT NULL COMMENT 'PRESUPUESTO_EXCEDIDO | CUOTA_PROXIMA',
    mensaje     VARCHAR(500) NOT NULL,
    leida       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_alerta_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_alerta_usuario (usuario_id, leida)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

