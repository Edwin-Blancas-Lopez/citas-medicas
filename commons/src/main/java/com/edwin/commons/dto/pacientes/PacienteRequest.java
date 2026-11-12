package com.edwin.commons.dto.pacientes;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos necesarios para registrar o actualizar a un paciente")
public record PacienteRequest(
        @Schema(description = "Nombre del paciente", example = "Edwin")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        String nombre,

        @Schema(description = "Apellido paterno del paciente", example = "Blancas")
        @NotBlank(message = "El apellido paterno es paciente")
        @Size(min = 1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
        String apellidoPaterno,

        @Schema(description = "Apellido materno del paciente", example = "Lopez")
        @NotBlank(message = "El apellido materno es requerido")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre 1 y 50 caracteres")
        String apellidoMaterno,

        @Schema(description = "Edad del Paciente. Debe estar entre 18 y 100 años.", example = "24 años")
        @NotNull(message = "La edad es requerida")
        @Min(value = 18, message = "La edad minima es de 18 años")
        @Max(value = 100, message = "La edad maxima es de 100 años")
        Short edad,

        @Schema(description = "Peso del Paciente. Debe estar entre 0.1 y 200 kg.", example = "90")
        @NotNull(message = "El peso es requerido")
        @DecimalMin(value = "0.1", message = "El peso minimo es de 0.1 kg")
        @DecimalMax(value = "200.0", message = "El peso maximo es de 200 kg")
        Double peso,

        @Schema(description = "Estatura del Paciente. Debe estar entre 1.0 y 2.0m", example = "1.76")
        @NotNull(message = "La estatura es requerido")
        @DecimalMin(value = "1.0", message = "La estatura minimo es de 1.0 m")
        @DecimalMax(value = "2.0", message = "La estatura maxima es de 2.0 m")
        Double estatura,

        @Schema(description = "Correo electronico del paciente", example = "edwin.blancas@example.com")
        @NotBlank(message = "El email es requerido")
        @Size(min = 1, max = 100, message = "El email debe tener entre 1 y 100 caracteres")
        @Email(message = "El email debe tener el formato correcto(correo@dominio")
        String email,


        @Schema(description = "El numero Telefonico de contacto. Debe contener exactamente 10 digitos", example = "7861548595", pattern = "^[0-9]{10}$")
        @Pattern(regexp = "^[0-9]{10}$", message = "El telefono debe contener solo 10 digitos numericos")
        @NotBlank(message = "El telefono es requerido")
        String telefono,

        @Schema(description = "Direccion del paciente", example = "Av Vicente Guerrero 40")
        @NotBlank(message = "La direccion del paciente es requerido")
        @Size(min = 1, max = 50, message = "La direccion del paciente debe tener entre 1 y 150 caracteres")
        String direccion
) {

}
