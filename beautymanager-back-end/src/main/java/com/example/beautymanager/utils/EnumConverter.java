package com.example.beautymanager.utils;

import com.example.beautymanager.Modelo.Enums.*;
import com.example.beautymanager.exception.BusinessException;

/**
 * Utilidad para conversiones seguras de String a Enums
 * Evita IllegalArgumentException y proporciona manejo de errores robusto
 */
public class EnumConverter {

    /**
     * Convierte String seguro a EstadoTurnoEnums
     * @param estadoString valor del estado como string
     * @return EstadoTurnoEnums correspondiente
     * @throws BusinessException si el valor es inválido
     */
    public static EstadoTurnoEnums toEstadoTurnoEnums(String estadoString) {
        if (estadoString == null || estadoString.isBlank()) {
            throw new BusinessException("Estado de turno no puede ser nulo o vacío");
        }
        try {
            return EstadoTurnoEnums.valueOf(estadoString.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado de turno inválido: " + estadoString + 
                ". Valores válidos: " + getAllEstadoTurnoValues());
        }
    }

    /**
     * Convierte String seguro a EstadoPagoEnums
     * @param estadoString valor del estado como string
     * @return EstadoPagoEnums correspondiente
     * @throws BusinessException si el valor es inválido
     */
    public static EstadoPagoEnums toEstadoPagoEnums(String estadoString) {
        if (estadoString == null || estadoString.isBlank()) {
            throw new BusinessException("Estado de pago no puede ser nulo o vacío");
        }
        try {
            return EstadoPagoEnums.valueOf(estadoString.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado de pago inválido: " + estadoString + 
                ". Valores válidos: " + getAllEstadoPagoValues());
        }
    }

    /**
     * Convierte String seguro a EstadoCarritoEnums
     * @param estadoString valor del estado como string
     * @return EstadoCarritoEnums correspondiente
     * @throws BusinessException si el valor es inválido
     */
    public static EstadoCarritoEnums toEstadoCarritoEnums(String estadoString) {
        if (estadoString == null || estadoString.isBlank()) {
            throw new BusinessException("Estado de carrito no puede ser nulo o vacío");
        }
        try {
            return EstadoCarritoEnums.valueOf(estadoString.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado de carrito inválido: " + estadoString + 
                ". Valores válidos: " + getAllEstadoCarritoValues());
        }
    }

    /**
     * Convierte String seguro a EstadoMovimientoEnums
     * @param estadoString valor del estado como string
     * @return EstadoMovimientoEnums correspondiente
     * @throws BusinessException si el valor es inválido
     */
    public static EstadoMovimientoEnums toEstadoMovimientoEnums(String estadoString) {
        if (estadoString == null || estadoString.isBlank()) {
            throw new BusinessException("Estado de movimiento no puede ser nulo o vacío");
        }
        try {
            return EstadoMovimientoEnums.valueOf(estadoString.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Estado de movimiento inválido: " + estadoString + 
                ". Valores válidos: " + getAllEstadoMovimientoValues());
        }
    }

    /**
     * Convierte String seguro a MetodoPagoEnums
     * @param metodoString valor del método como string
     * @return MetodoPagoEnums correspondiente
     * @throws BusinessException si el valor es inválido
     */
    public static MetodoPagoEnums toMetodoPagoEnums(String metodoString) {
        if (metodoString == null || metodoString.isBlank()) {
            throw new BusinessException("Método de pago no puede ser nulo o vacío");
        }
        try {
            return MetodoPagoEnums.valueOf(metodoString.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Método de pago inválido: " + metodoString + 
                ". Valores válidos: " + getAllMetodoPagoValues());
        }
    }

    /**
     * Convierte String seguro a TipoMovimientoEnums
     * @param tipoString valor del tipo como string
     * @return TipoMovimientoEnums correspondiente
     * @throws BusinessException si el valor es inválido
     */
    public static TipoMovimientoEnums toTipoMovimientoEnums(String tipoString) {
        if (tipoString == null || tipoString.isBlank()) {
            throw new BusinessException("Tipo de movimiento no puede ser nulo o vacío");
        }
        try {
            return TipoMovimientoEnums.valueOf(tipoString.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Tipo de movimiento inválido: " + tipoString + 
                ". Valores válidos: " + getAllTipoMovimientoValues());
        }
    }

    /**
     * Convierte String seguro a MovimientoBilleteraEnums
     * @param movimientoString valor del movimiento como string
     * @return MovimientoBilleteraEnums correspondiente
     * @throws BusinessException si el valor es inválido
     */
    public static MovimientoBilleteraEnums toMovimientoBilleteraEnums(String movimientoString) {
        if (movimientoString == null || movimientoString.isBlank()) {
            throw new BusinessException("Movimiento de billetera no puede ser nulo o vacío");
        }
        try {
            return MovimientoBilleteraEnums.valueOf(movimientoString.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Movimiento de billetera inválido: " + movimientoString + 
                ". Valores válidos: " + getAllMovimientoBilleteraValues());
        }
    }

    // ========================= Métodos auxiliares =========================

    private static String getAllEstadoTurnoValues() {
        StringBuilder sb = new StringBuilder();
        for (EstadoTurnoEnums e : EstadoTurnoEnums.values()) {
            sb.append(e.name()).append(", ");
        }
        return sb.toString();
    }

    private static String getAllEstadoPagoValues() {
        StringBuilder sb = new StringBuilder();
        for (EstadoPagoEnums e : EstadoPagoEnums.values()) {
            sb.append(e.name()).append(", ");
        }
        return sb.toString();
    }

    private static String getAllEstadoCarritoValues() {
        StringBuilder sb = new StringBuilder();
        for (EstadoCarritoEnums e : EstadoCarritoEnums.values()) {
            sb.append(e.name()).append(", ");
        }
        return sb.toString();
    }

    private static String getAllEstadoMovimientoValues() {
        StringBuilder sb = new StringBuilder();
        for (EstadoMovimientoEnums e : EstadoMovimientoEnums.values()) {
            sb.append(e.name()).append(", ");
        }
        return sb.toString();
    }

    private static String getAllMetodoPagoValues() {
        StringBuilder sb = new StringBuilder();
        for (MetodoPagoEnums e : MetodoPagoEnums.values()) {
            sb.append(e.name()).append(", ");
        }
        return sb.toString();
    }

    private static String getAllTipoMovimientoValues() {
        StringBuilder sb = new StringBuilder();
        for (TipoMovimientoEnums e : TipoMovimientoEnums.values()) {
            sb.append(e.name()).append(", ");
        }
        return sb.toString();
    }

    private static String getAllMovimientoBilleteraValues() {
        StringBuilder sb = new StringBuilder();
        for (MovimientoBilleteraEnums e : MovimientoBilleteraEnums.values()) {
            sb.append(e.name()).append(", ");
        }
        return sb.toString();
    }
}
