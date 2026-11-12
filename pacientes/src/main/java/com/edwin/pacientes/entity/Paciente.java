package com.edwin.pacientes.entity;

import com.edwin.commons.enums.EstadoRegistro;
import com.edwin.commons.exceptions.ConflictoException;
import com.edwin.commons.exceptions.DatoInvalidoException;
import com.edwin.commons.utils.StringCustomUtils;
import com.edwin.commons.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "PACIENTES")
@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter
public class Paciente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PACIENTE")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", nullable = false, length = 50)
    private String apellidoMaterno;

    @Column(name = "EDAD", nullable = false)
    private Short edad;

    @Column(name = "PESO", nullable = false)
    private Double peso;

    @Column(name = "ESTATURA", nullable = false)
    private Double estatura;

    @Column(name = "IMC", nullable = false)
    private Double imc;

    @Column(name = "EMAIL", nullable = false, length = 100)
    private String email;

    @Column(name = "NUM_EXPEDIENTE", nullable = false, length = 20)
    private String numeroExpediente;

    @Column(name = "TELEFONO", length = 10, nullable = false)
    private String telefono;

    @Column(name = "DIRECCION", nullable = false, length = 150)
    private String direccion;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_REGISTRO", nullable = false)
    private EstadoRegistro estadoRegistro;

    /////////////////////////////////////////////////////////////////////////////////

    public String obtenerNombreCompleto(){
        return String.join(" ",
                this.getNombre(),
                this.getApellidoPaterno(),
                this.getApellidoMaterno()
        );
    }

    private void validarNoEliminado(){
        if (this.estadoRegistro == EstadoRegistro.ELIMINADO)
            throw new ConflictoException("El paciente ya esta eliminado");

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad, Double peso, Double estatura, String email, String telefono, String direccion) {
        StringCustomUtils.validarTamanio(nombre, 1, 50, "El nombre es requerido y debe tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoPaterno, 1, 50, "El apellido paterno es requerido y debe tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoMaterno, 1, 50, "El apellido materno es requerido y debe tener entre 1 y 50 caracteres");
        ValoresNumericosUtils.validarRengoShort(edad, (short)1, (short)100, "La edad es requerida y tiene que tener entre 1 y 100 años");
        ValoresNumericosUtils.validarRangoDouble(peso, 0.1, 200, "El peso es requerido y tiene que pesar entre 0.1 y 200 kg.");
        ValoresNumericosUtils.validarRangoDouble(estatura, 1.0, 2.0, "La estatura es requerida y tiene que medir entre 1.00 y 2.00 metros");
        StringCustomUtils.validarTamanio(email, 1, 100, "El email es requerido y debe tener entre 1 y 100 caracteres");
        StringCustomUtils.validarTamanio(telefono, 10, 10, "El telefono es requerido y debe tener entre 10 digitos(0-9)");
        StringCustomUtils.validarTamanio(direccion, 1, 150, "La direccion es requerida y debe contener entre 1 y 150 caracteres");
    }

    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad, Double peso, Double estatura, String email, String telefono, String direccion){

        validarNoEliminado();

        validarDatos(nombre, apellidoPaterno, apellidoMaterno, edad, peso, estatura, email, telefono, direccion);

        if(!(peso.equals(this.peso)) || !(estatura.equals(this.estatura))){
            this.edad = edad;
            this.peso = peso;
            this.imc = calcularIMC(peso, estatura);
        }


        if (!(telefono.equals(this.telefono))){
            this.telefono = telefono.trim();
            this.numeroExpediente = generarNumeroExpediente(telefono);
        }

        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
        this.estatura = estatura;
        this.email = email.trim().toLowerCase();
        this.direccion = direccion.trim();
    }

    public void eliminar(){
        validarNoEliminado();
        this.estadoRegistro = EstadoRegistro.ELIMINADO;
    }


    public static Double calcularIMC(Double peso, Double estatura){
        return (peso / (estatura * estatura));
    }

    public static String generarNumeroExpediente(String telefono){
        return String.join("X", telefono.split("")) + "X";
    }

    public static Paciente crear(String nombre, String apellidoPaterno, String apellidoMaterno, Short edad, Double peso, Double estatura, String email, String telefono, String direccion){
        validarDatos(nombre, apellidoPaterno, apellidoMaterno, edad, peso, estatura, email, telefono, direccion);


        return Paciente.builder()
                .nombre(nombre.trim())
                .apellidoPaterno(apellidoPaterno.trim())
                .apellidoMaterno(apellidoMaterno.trim())
                .edad(edad)
                .peso(peso)
                .estatura(estatura)
                .imc(calcularIMC(peso, estatura))
                .email(email.trim().toLowerCase())
                .numeroExpediente(generarNumeroExpediente(telefono))
                .telefono(telefono.trim())
                .direccion(direccion.trim())
                .estadoRegistro(EstadoRegistro.ACTIVO)
                .build();
    }
}
