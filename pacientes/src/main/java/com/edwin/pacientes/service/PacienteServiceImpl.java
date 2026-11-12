package com.edwin.pacientes.service;

import com.edwin.commons.clients.CitaClient;
import com.edwin.commons.clients.PacienteClient;
import com.edwin.commons.dto.medicos.MedicoResponse;
import com.edwin.commons.dto.pacientes.PacienteRequest;
import com.edwin.commons.dto.pacientes.PacienteResponse;
import com.edwin.commons.enums.EstadoRegistro;
import com.edwin.commons.exceptions.ConflictoException;
import com.edwin.commons.exceptions.RecursoNoEncontradoException;
import com.edwin.pacientes.entity.Paciente;
import com.edwin.pacientes.mapper.PacienteMapper;
import com.edwin.pacientes.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PacienteServiceImpl implements PacienteService{
    private final PacienteRepository pacienteRepository;

    private final PacienteMapper pacienteMapper;

    private final CitaClient citaClient;
    private final PacienteClient pacienteClient;

    @Override
    @Transactional(readOnly = true)
    public List<PacienteResponse> listar() {
        log.info("Listando todos los pacientes activos...");
        return pacienteRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(pacienteMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PacienteResponse obtenerPacienteSinValidarEstado(Long id){
        log.info("Buscando paciente sin validar estado con id {}", id);
        return pacienteMapper.entidadAResponse(pacienteRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Paciente sin validar estado, no encontrado con id: " + id)));
    }

    @Override
    public PacienteResponse obtenerPorId(Long id) {
        return pacienteMapper.entidadAResponse(obtenerPacienteActivoPorId(id));
    }

    @Override
    public PacienteResponse registrar(PacienteRequest request) {
        log.info("Registrando nuevo paciente: {}", request.nombre());

        validarDatosUnicos(request);

        Paciente paciente = pacienteMapper.requestAEntidad(request);

        pacienteRepository.saveAndFlush(paciente);

        log.info("Nuevo paciente registrado: {}", paciente.obtenerNombreCompleto());

        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public PacienteResponse actualizar(PacienteRequest request, Long id) {

        Paciente paciente = obtenerPacienteActivoPorId(id);

        validarPacienteTieneCitasAgendadas(id);

        validarCambiosUnicos(request, id);

        log.info("Actualizando paciente con id: {}", id);

        paciente.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.peso(),
                request.estatura(),
                request.email(),
                request.telefono(),
                request.direccion());

        pacienteRepository.saveAndFlush(paciente);

        log.info("Paciente con id: {} actualizado correctamente", id);

        return pacienteMapper.entidadAResponse(paciente);
    }

    @Override
    public void eliminar(Long id) {
        Paciente paciente = obtenerPacienteActivoPorId(id);

        log.info("Crucificando al paciente con id: {}", id);

        validarPacienteTieneCitasAgendadas(id);

        paciente.eliminar();

        pacienteRepository.saveAndFlush(paciente);

        log.info("Paciente ha sido crucificado exitosamente");
    }

    private Paciente obtenerPacienteActivoPorId(Long id){
        log.info("Buscando paciente activo con id {}", id);

        return pacienteRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente activo no encontrado con id: " + id));
    }

    private void validarDatosUnicos(PacienteRequest request){
        log.info("Validando email unico...");

        if (pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email().trim(), EstadoRegistro.ACTIVO))
            throw new ConflictoException("Ya existe un paciente activo registrado con el email: " + request.email());

        log.info("Validando telefono unico...");

        if (pacienteRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono().trim(), EstadoRegistro.ACTIVO))
            throw new ConflictoException("Ya existe un paciente activo con el tellefono: " + request.telefono());

    }

    private void validarCambiosUnicos(PacienteRequest request, Long id){
        log.info("Validando cambio en email unico...");

        if (pacienteRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
                request.email().trim(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException("Ya existe un paciente activo registrado con el email: " + request.email());

        log.info("Validando cambio en telefono unico...");

        if (pacienteRepository.existsByTelefonoAndEstadoRegistroAndIdNot(
                request.telefono().trim(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException("Ya existe un paciente activo con el telefono: " + request.telefono());



    }

    private void validarPacienteTieneCitasAgendadas(Long id){
        log.info("Validando si el paciente con id {} tiene citas agendadas en servicio remoto..." + id);

        citaClient.pacienteTieneCitasAsignadas(id);
    }
}
