package com.edwin.citas.mapper;

import com.edwin.citas.dto.CitaRequest;
import com.edwin.citas.dto.CitaResponse;
import com.edwin.citas.entity.Cita;
import com.edwin.commons.dto.medicos.DatosMedicoResponse;
import com.edwin.commons.dto.medicos.MedicoResponse;
import com.edwin.commons.dto.pacientes.DatosPacienteResponse;
import com.edwin.commons.dto.pacientes.PacienteResponse;
import com.edwin.commons.mapper.CommonMapper;
import org.springframework.stereotype.Component;

@Component
public class CitaMapper implements CommonMapper<CitaRequest, CitaResponse, Cita> {
    @Override
    public Cita requestAEntidad(CitaRequest request) {
        return request == null
                ? null
                : Cita.crear(
                        request.idPaciente(),
                        request.idMedico(),
                        request.fechaCita(),
                        request.sintomas());
    }

    @Override
    public CitaResponse entidadAResponse(Cita entidad) {
        return entidad == null
                ? null
                : new CitaResponse(
                        entidad.getId(),
                        null,
                        null,
                        entidad.getFechaCita(),
                        entidad.getSintomas(),
                        entidad.getEstadoCita().getDescripcion()
        );
    }

    public CitaResponse entidadAResponse(Cita entidad, PacienteResponse paciente, MedicoResponse medico) {
        return entidad == null
                ? null
                : new CitaResponse(
                entidad.getId(),
                pacienteResponseADatosPaciente(paciente),
                medicoResponseADatos(medico),
                entidad.getFechaCita(),
                entidad.getSintomas(),
                entidad.getEstadoCita().getDescripcion()
        );
    }
    private DatosPacienteResponse pacienteResponseADatosPaciente(PacienteResponse paciente){
        return paciente == null
                ? null
                : new DatosPacienteResponse(
                        paciente.nombre(),
                paciente.numExpediente(),
                paciente.edad() + " años",
                paciente.peso() + " kg.",
                paciente.estatura() + " m.",
                String.join(Math.round(paciente.imc() * 100.0) / 100.0 + "",
                        clasificacionIMC(paciente.imc())),
                paciente.telefono()
        );
    }

    private String clasificacionIMC(double imc){
        if (imc < 18.5) return "Bajo peso";
        if (imc < 25) return "Peso normal";
        if (imc < 30) return "Sobrepeso";
        if (imc < 35) return "Obesidad grado I";
        if (imc < 40) return "Obesidad grado II";
        return "Obesidad grado III";
    }

    private DatosMedicoResponse medicoResponseADatos(MedicoResponse medico){
        return medico == null
                ? null
                : new DatosMedicoResponse(
                medico.nombre(),
                medico.cedulaProfesional(),
                medico.especialidad());
    }
}
