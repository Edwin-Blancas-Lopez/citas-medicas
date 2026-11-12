package com.edwin.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informacion de un medico")
public record MedicoResponse(
        @Schema(description = "Identificador unico del medico", example = "1")
        Long id,

        @Schema(description = "Nombre completo del medico", example = "Christian Garcia Lopez")
        String nombre,

        @Schema(description = "Edad del medico", example = "35")
        Short edad,

        @Schema(description = "Correo electronico del medico", example = "cristian.garcia@example.com")
        String email,

        @Schema(description = "Numero telefonico de contacto del medico", example = "2221234567")
        String telefono,

        @Schema(description = "Cedula profesional del medico", example = "123456789012")
        String cedulaProfesional,

        @Schema(description = "Nombre de la especialidad medico del medico", example = "Cardiologia")
        String especialidad,

        @Schema(description = "Descripcion de la disponibilidad del medico", example = "Disponible para atender pacientes")
        String disponibilidad,

        @Schema(description = "Identificador de la disponibilidad asociada al medico", example = "1")
        Long idDisponibilidad
        ) {
}


