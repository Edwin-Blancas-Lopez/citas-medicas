package com.edwin.pacientes.service;

import com.edwin.commons.dto.pacientes.PacienteRequest;
import com.edwin.commons.dto.pacientes.PacienteResponse;
import com.edwin.commons.service.CrudService;
import org.springframework.transaction.annotation.Transactional;

public interface PacienteService extends CrudService<PacienteRequest, PacienteResponse> {

    PacienteResponse obtenerPacienteSinValidarEstado(Long id);
}
