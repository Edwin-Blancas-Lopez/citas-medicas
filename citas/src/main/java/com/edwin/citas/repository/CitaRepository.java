package com.edwin.citas.repository;

import com.edwin.citas.entity.Cita;
import com.edwin.citas.enums.EstadoCita;
import com.edwin.commons.enums.EstadoRegistro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {
    List<Cita> findByEstadoRegistro(EstadoRegistro estadoRegistro);

    /*SELECT COUNT(*) FROM CITAS WHERE ID_MEDICO = ? AND
                    ESTADO_REGISTRO = ? AND ESTADO_CITA IN(?)*/


    boolean existsByIdMedicoAndEstadoRegistroAndEstadoCitaIn(
            Long idMedico, EstadoRegistro estadoRegistro, List<EstadoCita> estadoCitas
    );

    boolean existsByIdMedicoAndEstadoRegistroAndEstadoCitaInAndIdNot(
            Long idMedico, EstadoRegistro estadoRegistro, List<EstadoCita> estadoCitas, Long idCita
    );

    boolean existsByIdPacienteAndEstadoRegistroAndEstadoCitaIn(
            Long idPaciente, EstadoRegistro estadoRegistro, List<EstadoCita> estadoCitas
    );
}
