-- Solo crea categorías globales sin usuario_id
-- Primero agrega columna nullable
CREATE TABLE categorias (
                            id         BIGINT AUTO_INCREMENT PRIMARY KEY,
                            usuario_id BIGINT       NOT NULL,
                            nombre     VARCHAR(100) NOT NULL,
                            tipo       VARCHAR(20)  NOT NULL,
                            activa     BOOLEAN      NOT NULL DEFAULT TRUE,
                            CONSTRAINT fk_cat_usuario FOREIGN KEY (usuario_id)
                                REFERENCES usuarios(id) ON DELETE CASCADE,
                            INDEX idx_cat_usuario (usuario_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE presupuestos (
                              id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                              usuario_id  BIGINT       NOT NULL,
                              anio        SMALLINT     NOT NULL,
                              descripcion VARCHAR(255),
                              created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              CONSTRAINT uq_usuario_anio UNIQUE (usuario_id, anio),
                              CONSTRAINT fk_pres_usuario FOREIGN KEY (usuario_id)
                                  REFERENCES usuarios(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE presupuestos_mensuales (
                                        id              BIGINT          AUTO_INCREMENT PRIMARY KEY,
                                        presupuesto_id  BIGINT          NOT NULL,
                                        categoria_id    BIGINT          NOT NULL,
                                        mes             SMALLINT         NOT NULL,
                                        monto_planeado  DECIMAL(15,2)   NOT NULL,
                                        CONSTRAINT uq_pres_cat_mes UNIQUE (presupuesto_id, categoria_id, mes),
                                        CONSTRAINT fk_pm_presupuesto FOREIGN KEY (presupuesto_id)
                                            REFERENCES presupuestos(id) ON DELETE CASCADE,
                                        CONSTRAINT fk_pm_categoria FOREIGN KEY (categoria_id)
                                            REFERENCES categorias(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;