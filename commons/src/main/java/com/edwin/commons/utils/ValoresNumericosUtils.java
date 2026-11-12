package com.edwin.commons.utils;



import com.edwin.commons.exceptions.DatoInvalidoException;

import java.math.BigDecimal;

public class ValoresNumericosUtils {

    public static <N extends Number> void validarNumeroRequerido(N numero, String mensaje){
        if (numero == null)
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarNumeroPositivo(Integer numero, String mensaje){
        validarNumeroRequerido(numero, mensaje);

        if (numero <= 0)
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarBigDecilmalPositivo(BigDecimal numero, String mensaje){

        validarNumeroRequerido(numero, mensaje);

        if (numero.compareTo(BigDecimal.ZERO) <= 0)
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarLongPositivo(Long numero, String mensaje){

        validarNumeroRequerido(numero, mensaje);

        if (numero < 0)
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarRengoShort(Short numero, short min, short max, String mensaje){

        validarNumeroRequerido(numero, mensaje);

        if (numero < min || numero > max)
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarRangoDouble(Double numero, double min, double max, String mensaje){

        validarNumeroRequerido(numero, mensaje);

        if (numero < min || numero > max)
            throw new DatoInvalidoException(mensaje);
    }
}
