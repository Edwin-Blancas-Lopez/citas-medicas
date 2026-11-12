package com.edwin.commons.dto.medicos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos necesarios para registrar o actualizar a un medico")
public record MedicoRequest(
        @Schema(description = "Nombre del medico", example = "Christian")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        String nombre,

        @Schema(description = "Apellido paterno del medico", example = "Garcia")
        @NotBlank(message = "El apellido paterno es requerido")
        @Size(min = 1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
        String apellidoPaterno,

        @Schema(description = "Apellido materno del medico", example = "Lopez")
        @NotBlank(message = "El apellido materno es requerido")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre 1 y 50 caracteres")
        String apellidoMaterno,

        @Schema(description = "Edad del medico. Debe estar entre 18 y 100 años.", example = "35")
        @NotNull(message = "La edad es requerida")
        @Min(value = 18, message = "La edad minima es de 18 años")
        @Max(value = 100, message = "La edad maxima es de 100 años")
        Short edad,

        @Schema(description = "Correo electronico del medico", example = "christian.garcia@example.com")
        @NotBlank(message = "El email es requerido")
        @Size(min = 1, max = 100, message = "El email debe tener entre 1 y 100 caracteres")
        @Email(message = "El email debe tener el formato correcto(correo@dominio")
        String email,

        @Schema(description = "El numero Telefonico de contacto. Debe contener exactamente 10 digitos", example = "2221234567", pattern = "^[0-9]{10}$")
        @Pattern(regexp = "^[0-9]{10}$", message = "El telefono debe contener solo 10 digitos numericos")
        @NotBlank(message = "El telefono es requerido")
        String telefono,

        @Schema(description = "Cedula rpofesional del medico. Debe contener exactamente 12 caracteres.", example = "123456789012", minLength = 12, maxLength = 12)
        @NotBlank(message = "La cedula profesional es requerida")
        @Size(min = 12, max = 12, message = "La cedula profesional debe tener exactamente 12 caracteres")
        String cedulaProfesional,

        @Schema(description = "Identificador de la especialidad medica asociada al medico", example = "5", minimum = "1")
        @NotNull(message = "El id de la eespecialidad es requerido")
        @Positive(message = "El id de la especialidad debe ser positivo")
        Long idEspecialidad
) {
}
