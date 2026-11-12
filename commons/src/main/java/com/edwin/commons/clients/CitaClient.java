package com.edwin.commons.clients;

import com.edwin.commons.dto.medicos.MedicoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "citas")
public interface CitaClient {
    @GetMapping("/id-medico/{idMedico}/citas-asignadas")
    void medicoTieneCitasAsignadas(@PathVariable Long idMedico);

    @GetMapping("/id-paciente/{idPaciente}/citas-asignadas")
    void pacienteTieneCitasAsignadas(@PathVariable Long idPaciente);
}
