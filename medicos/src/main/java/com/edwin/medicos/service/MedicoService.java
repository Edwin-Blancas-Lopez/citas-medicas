package com.edwin.medicos.service;

import com.edwin.commons.dto.medicos.MedicoRequest;
import com.edwin.commons.dto.medicos.MedicoResponse;
import com.edwin.commons.service.CrudService;

public interface MedicoService extends CrudService<MedicoRequest, MedicoResponse> {
    MedicoResponse obtenerMedicoPorIdSinEstado(Long id);

    void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad);
}
