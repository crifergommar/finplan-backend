package com.finplan.alerta.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AlertaResponse {

    private Long id;
    private String tipo;
    private String mensaje;
    private boolean leida;

    @JsonProperty("createdAt")
    private Instant createdAt;
}

