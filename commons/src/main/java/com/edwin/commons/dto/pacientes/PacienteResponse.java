package com.edwin.commons.dto.pacientes;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informacion de un oaciente", example = "1")
public record PacienteResponse(

        @Schema(description = "ID del paciente", example = "1")
        Long id,

        @Schema(description = "Nombre completo del paciente", example = "Edwin Blancas Lopez")
        String nombre,

        @Schema(description = "Edad del paciente", example = "24")
        Short edad,

        @Schema(description = "Peso del paciente", example = "90")
        Double peso,

        @Schema(description = "Estatura del paciente", example = "1.76")
        Double estatura,

        @Schema(description = "IMC del pacienteo", example = "29.05")
        Double imc,

        @Schema(description = "Correo electronico del paciente", example = "edwin.blancas@example.com")
        String email,

        @Schema(description = "Numero de expediente del paciente", example = "5X5X7X2X7X2X0X4X")
        String numExpediente,

        @Schema(description = "Telefono del paciente", example = "7861584455")
        String telefono,

        @Schema(description = "Direccion del paciente", example = "Av. Vicente Guerrero 40")
        String direccion
) {
}
