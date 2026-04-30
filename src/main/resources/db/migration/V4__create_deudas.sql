CREATE TABLE deudas (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id      BIGINT         NOT NULL,
    nombre          VARCHAR(150)   NOT NULL,
    monto_total     DECIMAL(15,2)  NOT NULL,
    num_cuotas      SMALLINT       NOT NULL,
    tasa_interes    DECIMAL(5,2)   DEFAULT 0.00,
    fecha_inicio    DATE           NOT NULL,
    activa          BOOLEAN        NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_deuda_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE cuotas_deuda (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    deuda_id    BIGINT         NOT NULL,
    numero      SMALLINT       NOT NULL,
    monto       DECIMAL(15,2)  NOT NULL,
    fecha_vcto  DATE           NOT NULL,
    pagada      BOOLEAN        NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_cuota_deuda FOREIGN KEY (deuda_id)
        REFERENCES deudas(id) ON DELETE CASCADE,
    INDEX idx_cuota_deuda (deuda_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE pagos_deuda (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    cuota_id    BIGINT         NOT NULL,
    monto       DECIMAL(15,2)  NOT NULL,
    fecha_pago  DATE           NOT NULL,
    CONSTRAINT fk_pago_cuota FOREIGN KEY (cuota_id)
        REFERENCES cuotas_deuda(id) ON DELETE CASCADE,
    INDEX idx_pago_cuota (cuota_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

