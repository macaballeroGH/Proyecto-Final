package com.example.beautymanager.Modelo.DTO;

import java.util.List;

import lombok.Data;

@Data 
public class ObtenerEstadisticasDTO {

    private List<DatoEstadisticaDTO> ingresos;

    private List<DatoEstadisticaDTO> turnos;

    private List<RankingEstadisticasDTO> serviciosMasSolicitados;

    private List<RankingEstadisticasDTO> productosMasVendidos;

    private ComparacionEstadisticaDTO comparacionIngresos;

    private ComparacionEstadisticaDTO comparacionTurno;

    private List<ComparacionRankingDTO> comparacionServicios;

    private List<ComparacionRankingDTO> comparacionProductos;
}
