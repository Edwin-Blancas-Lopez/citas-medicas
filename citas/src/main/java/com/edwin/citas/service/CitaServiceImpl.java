package com.edwin.citas.service;

import com.edwin.citas.dto.CitaRequest;
import com.edwin.citas.dto.CitaResponse;
import com.edwin.citas.entity.Cita;
import com.edwin.citas.enums.EstadoCita;
import com.edwin.citas.mapper.CitaMapper;
import com.edwin.citas.repository.CitaRepository;
import com.edwin.commons.clients.MedicoClient;
import com.edwin.commons.clients.PacienteClient;
import com.edwin.commons.dto.medicos.MedicoResponse;
import com.edwin.commons.dto.pacientes.PacienteResponse;
import com.edwin.commons.enums.DisponibilidadMedico;
import com.edwin.commons.enums.EstadoRegistro;
import com.edwin.commons.exceptions.ConflictoException;
import com.edwin.commons.exceptions.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CitaServiceImpl implements CitaService{

    private final CitaRepository citaRepository;
    private final CitaMapper citaMapper;
    private final MedicoClient medicoClient;
    private final PacienteClient pacienteClient;

    private final List<EstadoCita> ESTADOS_ACTIVOS_INVALIDOS = List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA, EstadoCita.EN_CURSO);


    @Override
    @Transactional(readOnly = true)
    public List<CitaResponse> listar() {
        log.info("Listando todas las citas activas...");
        return citaRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(cita -> citaMapper.entidadAResponse(
                        cita,
                        obtenerPacienteSinEstado(cita.getIdPaciente()),
                        obtenerMedicoSinEstado(cita.getIdMedico())
                )).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CitaResponse obtenerPorId(Long id) {
        Cita cita = obtenerCitaOException(id);
        return citaMapper.entidadAResponse(cita,
                obtenerPacienteSinEstado(cita.getIdPaciente()),
                obtenerMedicoSinEstado(cita.getIdMedico()));
    }

    @Override
    public CitaResponse registrar(CitaRequest request) {
        log.info("Registrando nueva cita...");

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());

        validarMedicoActivoDisponible(medico);
        validarPacienteActivoSinCitasActivas(request.idPaciente());

        Cita cita = citaMapper.requestAEntidad(request);
        citaRepository.saveAndFlush(cita);

        log.info("Cita registrada");

        cambiarDisponibilidadMedicoSegunEstadoCita(medico.id(), cita.getEstadoCita());

        return citaMapper.entidadAResponse(
                cita,
                paciente,
                medico);
    }

    @Override
    public CitaResponse actualizar(CitaRequest request, Long id) {
        Cita cita = obtenerCitaOException(id);

        if (cita.getEstadoCita() != EstadoCita.PENDIENTE && cita.getEstadoCita() != EstadoCita.CONFIRMADA) {
            throw new ConflictoException("Solo se puede actualizar una cita en estado PENDIENTE o CONFIRMADA");
        }
        log.info("Actualizando cita con id: {}", id);

        MedicoResponse medico = obtenerMedicoActivo(request.idMedico());
        PacienteResponse paciente = obtenerPacienteActivo(request.idPaciente());

        if (!cita.getIdMedico().equals(request.idMedico())) {
            validarMedicoActivoDisponible(medico);
            actualizarDisponibilidadMedico(cita.getIdMedico(), DisponibilidadMedico.DISPONIBLE.getCodigo());
            cambiarDisponibilidadMedicoSegunEstadoCita(medico.id(), cita.getEstadoCita());
        }

        if (!cita.getIdPaciente().equals(request.idPaciente())) {
            validarPacienteActivoSinCitasActivas(request.idPaciente());
        }

        cita.actualizar(
                request.idPaciente(),
                request.idMedico(),
                request.fechaCita(),
                request.sintomas());

        citaRepository.saveAndFlush(cita);

        log.info("Cita actualizada");

        return citaMapper.entidadAResponse(
                cita,
                paciente,
                medico);
    }

    @Override
    public void actualizarEstadoCita(Long idCita, Long idEstadoCita) {
        Cita cita = obtenerCitaOException(idCita);
        EstadoCita nuevoEstado = EstadoCita.obtenerEstadoCitaPorCodigo(idEstadoCita);

        log.info("Actualizando estado de la cita con id: {} a {}", idCita, nuevoEstado);

        validarTransicionEstado(cita.getEstadoCita(), nuevoEstado);

        cita.actualizarEstadoCita(nuevoEstado);
        citaRepository.saveAndFlush(cita);

        log.info("Estado de la cita {} actualizado", idCita);

        cambiarDisponibilidadMedicoSegunEstadoCita(cita.getIdMedico(), cita.getEstadoCita());
    }

    @Override
    @Transactional(readOnly = true)
    public void medicoTieneCitasAsignadas(Long idMedico) {
        log.info("Validando si el medico tiene una cita activa con los estados: {}", ESTADOS_ACTIVOS_INVALIDOS);
        if (citaRepository.existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
                idMedico, EstadoRegistro.ACTIVO, ESTADOS_ACTIVOS_INVALIDOS))
            throw new ConflictoException("No se puede modificar o eliminar el medico ya que " +
                    "tiene citas en estado: " + ESTADOS_ACTIVOS_INVALIDOS);
    }

    @Override
    public void pacienteTieneCitasAsignadas(Long idPaciente) {
        validarPacienteActivoSinCitasActivas(idPaciente);
    }

    @Override
    public void eliminar(Long id) {
        Cita cita = obtenerCitaOException(id);

        if (cita.getEstadoCita() != EstadoCita.PENDIENTE &&
                cita.getEstadoCita() != EstadoCita.CANCELADA &&
                cita.getEstadoCita() != EstadoCita.FINALIZADA) {
            throw new ConflictoException("No se puede eliminar una cita que se encuentra en estado: " + cita.getEstadoCita());
        }

        log.info("Eliminando cita con id: {}", id);

        cita.eliminar();
        citaRepository.saveAndFlush(cita);

        log.info("Cita con id {} ha sido marrcada como eliminada", id);

        if (cita.getEstadoCita() == EstadoCita.PENDIENTE)
            actualizarDisponibilidadMedico(cita.getIdMedico(), DisponibilidadMedico.DISPONIBLE.getCodigo());
    }

    private Cita obtenerCitaOException(Long id){
        log.info("Buscando cita activa con id: {}", id);

        return citaRepository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException("Cita no encontrada con id: " + id));
    }


    //wiwin
    private MedicoResponse obtenerMedicoActivo(Long id){
        log.info("Buscando medico activo con id {} en el servicio remoto", id);
        return medicoClient.obtenerMedicoActivoPorId(id);
    }

    private PacienteResponse obtenerPacienteActivo(Long id){
        log.info("Buscando paciente activo con id {} en el servicio remoto", id);
        return pacienteClient.obtenerPacienteActivoPorId(id);
    }

    private MedicoResponse obtenerMedicoSinEstado(Long id){
        log.info("Buscando medico sin estado con id {} en el servicio remoto", id);
        return medicoClient.obtenerMedicoSinEstadoPorId(id);
    }

    private PacienteResponse obtenerPacienteSinEstado(Long id){
        log.info("Buscando paciente sin estado con id {} en el servicio remoto", id);

        return pacienteClient.obtenerPacienteSinEstadoPorId(id);
    }

    private void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad){
        log.info("Actualizando disponiblidad del medico en el servicio remoto");

        medicoClient.actualizarDisponibilidadMedico(idMedico,idDisponibilidad);

        log.info("Disponibilidad del medico actualizando en el servicio remoto");
    }

    private void validarMedicoActivoDisponible(MedicoResponse medico){
        log.info("Validando si el medico acivo esta disponible..");
        if(!DisponibilidadMedico.DISPONIBLE.getCodigo().equals(medico.idDisponibilidad()))
            throw new ConflictoException("El medico activo no esta disponible para consulta");
    }

    private void validarPacienteActivoSinCitasActivas(Long idPaciente){
        log.info("Validando si el paciente acivo no tiene citas..");

        if (citaRepository.existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
                idPaciente, EstadoRegistro.ACTIVO, ESTADOS_ACTIVOS_INVALIDOS)) {
            throw new ConflictoException("El paciente no se puede eliminar ya que cuenta con una cita en estado PENDIENTE, CONFIRMADA o EN_CURSO.");
        }
    }

    private void validarTransicionEstado(EstadoCita estadoActual, EstadoCita nuevoEstado) {
        boolean transicionValida = switch (estadoActual) {
            case PENDIENTE -> nuevoEstado == EstadoCita.CONFIRMADA || nuevoEstado == EstadoCita.CANCELADA;
            case CONFIRMADA -> nuevoEstado == EstadoCita.EN_CURSO || nuevoEstado == EstadoCita.CANCELADA;
            case EN_CURSO -> nuevoEstado == EstadoCita.FINALIZADA;
            case FINALIZADA, CANCELADA -> false;
        };

        if (!transicionValida) {
            throw new ConflictoException(
                    String.format("No se permite cambiar el estado de la cita de [%s] a [%s]", estadoActual, nuevoEstado)
            );
        }
    }

    private void cambiarDisponibilidadMedicoSegunEstadoCita(Long idMedico, EstadoCita estadoCita){
        switch (estadoCita){
            case PENDIENTE, CONFIRMADA ->
                    actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.NO_DISPONIBLE.getCodigo());

            case FINALIZADA, CANCELADA ->
                    actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.DISPONIBLE.getCodigo());

            case EN_CURSO ->
                    actualizarDisponibilidadMedico(idMedico, DisponibilidadMedico.EN_CONSULTA.getCodigo());
        }
    }
}
