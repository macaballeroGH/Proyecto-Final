package com.example.beautymanager.Servicio;

import java.math.BigDecimal;

import com.example.beautymanager.Modelo.DTO.CrearBilleteraDTO;
import com.example.beautymanager.Modelo.DTO.DebitarSaldoDTO;
import com.example.beautymanager.Modelo.DTO.ObtenerBilleteraDTO;
import com.example.beautymanager.Modelo.DTO.RecargarSaldoDTO;
import com.example.beautymanager.Modelo.Entidad.BilleteraEntity;

public interface BilleteraService {
    ObtenerBilleteraDTO toMap(BilleteraEntity billetera);

    ObtenerBilleteraDTO crearBilletera(CrearBilleteraDTO crearBilletera);

    BigDecimal consultarSaldo(Long idUsuario);

    ObtenerBilleteraDTO obtenerPorUsuario(Long idUsuario);

    ObtenerBilleteraDTO recargarSaldo(RecargarSaldoDTO recargarSaldo);

    ObtenerBilleteraDTO debitarSaldo(DebitarSaldoDTO debitarSaldo);
}