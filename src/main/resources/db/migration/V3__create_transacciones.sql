CREATE TABLE transacciones (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id   BIGINT         NOT NULL,
    categoria_id BIGINT         NOT NULL,
    monto        DECIMAL(15,2)  NOT NULL,
    tipo         VARCHAR(20)    NOT NULL COMMENT 'INGRESO | GASTO',
    descripcion  VARCHAR(255),
    fecha        DATE           NOT NULL,
    created_at   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tx_usuario   FOREIGN KEY (usuario_id)   REFERENCES usuarios(id)   ON DELETE CASCADE,
    CONSTRAINT fk_tx_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    INDEX idx_tx_usuario_fecha (usuario_id, fecha)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

