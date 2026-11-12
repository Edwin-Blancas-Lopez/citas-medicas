package com.edwin.citas.dto;

import com.edwin.commons.dto.medicos.DatosMedicoResponse;
import com.edwin.commons.dto.pacientes.DatosPacienteResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Informacion de una cita medica registrada")
public record CitaResponse(
        @Schema(description = "Identificador único de la cita", example = "1")
        Long id,

        @Schema(description = "Informacion del paciente asociado a la cita")
        DatosPacienteResponse paciente,

        @Schema(description = "Informacion del médico que atenderá la cita")
        DatosMedicoResponse medico,

        @Schema(description = "Fecha programada para la cita", example = "25/09/2026 10:30", type = "string", format = "date-time")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime fechaCita,

        @Schema(description = "Descripcion de los sintomas indicador por el paciente", example = "El paciente presenta solor de cabeza intenso y fiebre desde hace dos dias")
        String sintomas,

        @Schema(description = "Estadi actual de la cita", example = "Confirmda por el pacieente")
        String estadoCita
) {
}
