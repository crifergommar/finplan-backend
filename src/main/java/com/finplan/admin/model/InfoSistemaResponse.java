package com.finplan.admin.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InfoSistemaResponse {

    private String version;
    private String uptime;
    private String timestamp;
    private String estado;
}

