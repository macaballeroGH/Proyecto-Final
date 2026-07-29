package com.example.beautymanager.Servicio;

import com.example.beautymanager.Modelo.DTO.RecuperacionPasswordResponseDTO;
import com.example.beautymanager.Modelo.DTO.ResponseDTO;
import com.example.beautymanager.Modelo.DTO.RestablecerPasswordDTO;
import com.example.beautymanager.Modelo.DTO.SolicitarRecuperacionPasswordDTO;

public interface RecuperacionPasswordService {

    RecuperacionPasswordResponseDTO solicitarRecuperacionPassword(SolicitarRecuperacionPasswordDTO solicitarRecuperacionPassword);

    ResponseDTO restablecerPassword(RestablecerPasswordDTO restablecerPassword);
}
