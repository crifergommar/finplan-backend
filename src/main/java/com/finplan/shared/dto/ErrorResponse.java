package com.finplan.shared.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ErrorResponse {

    private int status;
    private String error;
    private String mensaje;
    private String path;
    private String timestamp;

    public static ErrorResponse of(int status, String error,
                                   String mensaje, String path) {
        return ErrorResponse.builder()
                .status(status)
                .error(error)
                .mensaje(mensaje)
                .path(path)
                .timestamp(Instant.now().toString())
                .build();
    }
}