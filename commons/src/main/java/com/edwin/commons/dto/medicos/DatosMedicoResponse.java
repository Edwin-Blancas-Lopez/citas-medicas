package com.edwin.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de un medico asociado a una cita")
public record DatosMedicoResponse(
        @Schema(description = "Nombre completo del medico", example = "Christian Garcia Lopez")
        String nombre,

        @Schema(description = "Cedula profesional del medico", example = "123456789012")
        String cedulaProfesional,

        @Schema(description = "Nombre de la especialidad medica del medico", example = "Cardiologia")
        String especialidad

        ) {
}
