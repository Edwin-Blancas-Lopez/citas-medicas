package com.edwin.citas.service;

import com.edwin.citas.dto.CitaRequest;
import com.edwin.citas.dto.CitaResponse;
import com.edwin.commons.service.CrudService;

public interface CitaService extends CrudService<CitaRequest, CitaResponse> {
    void actualizarEstadoCita(Long idCita, Long idEstadoCita);

    void medicoTieneCitasAsignadas(Long idMedico);

    void pacienteTieneCitasAsignadas(Long idPaciente);
}
