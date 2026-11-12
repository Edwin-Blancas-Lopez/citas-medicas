package com.edwin.medicos.service;

import com.edwin.commons.clients.CitaClient;
import com.edwin.commons.dto.medicos.MedicoRequest;
import com.edwin.commons.dto.medicos.MedicoResponse;
import com.edwin.commons.enums.DisponibilidadMedico;
import com.edwin.commons.enums.EspecialidadMedico;
import com.edwin.commons.enums.EstadoRegistro;
import com.edwin.commons.exceptions.ConflictoException;
import com.edwin.commons.exceptions.RecursoNoEncontradoException;
import com.edwin.medicos.entity.Medico;
import com.edwin.medicos.mapper.MedicoMapper;
import com.edwin.medicos.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MedicoServiceImpl implements MedicoService{

    private final MedicoRepository medicoRepository;
    private final MedicoMapper medicoMapper;
    private final CitaClient citaClient;

    @Override
    public List<MedicoResponse> listar() {
        log.info("Listando todos los medicos activos...");
        return medicoRepository.findByEstadoRegistro(EstadoRegistro.ACTIVO).stream()
                .map(medicoMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponse obtenerPorId(Long id) {
        return medicoMapper.entidadAResponse(obtenerMedicoActivoPorId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public MedicoResponse obtenerMedicoPorIdSinEstado(Long id) {
        log.info("Buscando medico sin estado con id {}", id);
        return medicoMapper.entidadAResponse(medicoRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Medico sin estado no encontrado con id: " + id)));
    }

    @Override
    public MedicoResponse registrar(MedicoRequest request) {
        log.info("Registrabdo nuevo medico: {}", request.nombre());

        validarDatosUnicos(request);

        Medico medico = medicoMapper.requestAEntidad(request);

        medico.actualizarEspecialidad(EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad()));

        medicoRepository.saveAndFlush(medico);

        log.info("Nuevo medico registrado: {}", medico.obtenerNombreCompleto());

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public MedicoResponse actualizar(MedicoRequest request, Long id) {

        Medico medico = obtenerMedicoActivoPorId(id);

        validarMedicoTieneCitasAgendadas(id);

        validarCambiosUnicos(request, id);

        log.info("Actualizando medico con id: {}", id);

        medico.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.edad(),
                request.email(),
                request.telefono(),
                request.cedulaProfesional(),
                EspecialidadMedico.obtenerEspecialidadPorCodigo(request.idEspecialidad()));

        medicoRepository.saveAndFlush(medico);

        log.info("Medico con id {} actualizado correctamente", id);

        return medicoMapper.entidadAResponse(medico);
    }

    @Override
    public void actualizarDisponibilidadMedico(Long idMedico, Long idDisponibilidad) {
        Medico medico = obtenerMedicoActivoPorId(idMedico);

        log.info("Actualizando disponibilidad del medico con id: {}", idMedico);

        DisponibilidadMedico nuevaDisponibilidad = DisponibilidadMedico.obtenerDisponibilidadPorCodigo(idDisponibilidad);

        if (DisponibilidadMedico.DISPONIBLE.equals(nuevaDisponibilidad)) {
            validarMedicoTieneCitasAgendadas(idMedico);
        }

        DisponibilidadMedico disponibilidadAnterior = medico.getDisponibilidad();

        medico.actualizarDisponibilidad(nuevaDisponibilidad);

        medicoRepository.saveAndFlush(medico);

        log.info("Disponibilidad del medico con id {} cambio de {} a {}", idMedico, disponibilidadAnterior, nuevaDisponibilidad);
    }

    @Override
    public void eliminar(Long id) {
        Medico medico = obtenerMedicoActivoPorId(id);

        log.info("Crucificando al medico con id: {}", id);

        validarMedicoTieneCitasAgendadas(id);

        medico.eliminar();

        medicoRepository.saveAndFlush(medico);

        log.info("Medico ha sido crucificado exitosamente");
    }

    private Medico obtenerMedicoActivoPorId(Long id){
        log.info("Buscando médico activo con id {}", id);

        return medicoRepository.findByIdAndEstadoRegistro(id, EstadoRegistro.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medico activo no encontrado con id: " + id));
    }

    private void validarDatosUnicos(MedicoRequest request){
        log.info("Validando email unico...");

        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistro(
                request.email().trim(), EstadoRegistro.ACTIVO))
            throw new ConflictoException("Ya existe un medico activo registrado con el email: " + request.email());

        log.info("Validando telefono unico...");

        if (medicoRepository.existsByTelefonoAndEstadoRegistro(
                request.telefono().trim(), EstadoRegistro.ACTIVO))
            throw new ConflictoException("Ya existe un medico activo con el tellefono: " + request.telefono());

        log.info("Validando cedula profesional unica...");

        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistro(
                request.cedulaProfesional().trim(), EstadoRegistro.ACTIVO))
            throw new ConflictoException("Ya existe un medico activo con la cedula profesional: " + request.cedulaProfesional());
    }

    private void validarCambiosUnicos(MedicoRequest request, Long id){
        log.info("Validando cambio en email unico...");

        if (medicoRepository.existsByEmailIgnoreCaseAndEstadoRegistroAndIdNot(
                request.email().trim(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException("Ya existe un medico activo registrado con el email: " + request.email());

        log.info("Validando cambio en telefono unico...");

        if (medicoRepository.existsByTelefonoAndEstadoRegistroAndIdNot(
                request.telefono().trim(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException("Ya existe un medico activo con el tellefono: " + request.telefono());

        log.info("Validando camibio en cedula profesional unica...");

        if (medicoRepository.existsByCedulaProfesionalIgnoreCaseAndEstadoRegistroAndIdNot(
                request.cedulaProfesional().trim(), EstadoRegistro.ACTIVO, id))
            throw new ConflictoException("Ya existe un medico activo registrado con la cedula profesional: " + request.cedulaProfesional());
    }

    private void validarMedicoTieneCitasAgendadas(Long id){
        log.info("Validando si el medico con id {} tiene citas agendadas en servicio remoto..." + id);

        citaClient.medicoTieneCitasAsignadas(id);
    }
}
