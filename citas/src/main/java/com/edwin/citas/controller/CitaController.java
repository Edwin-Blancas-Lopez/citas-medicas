package com.edwin.citas.controller;

import com.edwin.citas.dto.CitaRequest;
import com.edwin.citas.dto.CitaResponse;
import com.edwin.citas.service.CitaService;
import com.edwin.commons.controller.CrudController;
import com.edwin.commons.docs.ProblemaDoc;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "API Citas", description = "Metodos para la geestion de citas")
public class CitaController extends CrudController<CitaRequest, CitaResponse, CitaService> {
    public CitaController(CitaService service) {
        super(service);
    }

    @Operation(summary = "Actualizar estado de la cita",
            description = "Actualiza el estado de una cita utilizando el identificador de la otra y el identificador del nuevo estado")
    @ApiResponse(responseCode = "204", description = "Estado de la cita actualizado")
    @ApiResponse(responseCode = "409", description = "El estado de la cita no puede " +
            "actualizarse por regla de negocio",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDoc.class)))

    @PatchMapping("/{idCita}/estado/{idEstado}")
    public ResponseEntity<Void> actualizarEstadoCita(
            @PathVariable @Positive(message = "El idCita debe ser positivo") Long idCita,
            @PathVariable @Positive(message = "El idEstado debe ser positivo") Long idEstado
    ) {
        service.actualizarEstadoCita(idCita, idEstado);
        return ResponseEntity.noContent().build();
    }

    @ApiResponse(responseCode = "204", description = "Medico no tiene citas asignadas")
    @ApiResponse(responseCode = "409", description = "Medico tiene citas asignadas y no se puede " +
            "modificar ni eliminar",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDoc.class)))
    @GetMapping("/id-medico/{idMedico}/citas-asignadas")
    public ResponseEntity<Void> medicoTieneCitasAsignadas(
            @Parameter(description = "Id del medico", example = "1")
            @PathVariable @Positive(message = "El idMedico debe ser positivo")Long idMedico){
        service.medicoTieneCitasAsignadas(idMedico);
        return ResponseEntity.noContent().build();
    }

    @ApiResponse(responseCode = "204", description = "Paciente no tiene citas asignadas")
    @ApiResponse(responseCode = "409", description = "Paciente tiene citas asignadas y no se puede " +
            "modificar ni eliminar",
            content = @Content(mediaType = "application/problem+json",
                    schema = @Schema(implementation = ProblemaDoc.class)))
    @GetMapping("/id-paciente/{idPaciente}/citas-asignadas")
    public ResponseEntity<Void> pacienteTieneCitasAsignadas(
            @Parameter(description = "Id del paciente", example = "1")
            @PathVariable @Positive(message = "El idPaciente debe ser positivo")Long idPaciente){
        service.pacienteTieneCitasAsignadas(idPaciente);
        return ResponseEntity.noContent().build();
    }
}
