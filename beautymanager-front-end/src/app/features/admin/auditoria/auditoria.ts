import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ObtenerAuditoria } from '../../../core/models/obtener-auditoria';
import { CambioAuditoria } from '../../../core/models/cambio-auditoria';
import { AuditoriaService } from '../../../core/services/auditoria';
import { FiltroAuditoria } from '../../../core/models/filtro-auditoria';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-auditoria',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, MatIcon],
  templateUrl: './auditoria.html',
  styleUrl: './auditoria.css',
})
export class AuditoriaAdmin implements OnInit {

  registros: ObtenerAuditoria[] = [];

  filtro: FiltroAuditoria = {
    idUsuario: null,
    accion: '',
    tablaAfectada: '',
    tipoEvento: '',
    resultado: '',
    fechaDesde: '',
    fechaHasta: ''
  };

  cargando = false;
  error = '';

  paginaActual = 0;
  tamanioPagina = 20;

  totalRegistros = 0;
  totalPaginas = 0;

  registroSeleccionado: ObtenerAuditoria | null = null;
  cambiosSeleccionados: CambioAuditoria[] = [];

  contextoSeleccionado: CambioAuditoria[] = [];

  constructor(
    private auditoriaService: AuditoriaService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarAuditorias();
  }

  // ======================================================
  // CARGAR REGISTROS
  // ======================================================

  cargarAuditorias(): void {

    if (this.cargando) {
      return;
    }

    if (
      this.filtro.fechaDesde &&
      this.filtro.fechaHasta &&
      this.filtro.fechaDesde > this.filtro.fechaHasta
    ) {

      this.error =
        'La fecha desde no puede ser posterior a la fecha hasta.';

      return;
    }

    this.cargando = true;
    this.error = '';

    this.auditoriaService
      .buscarAuditorias(
        this.filtro,
        this.paginaActual,
        this.tamanioPagina
      )
      .subscribe({

        next: (respuesta) => {

          this.registros = respuesta.content;

          this.totalRegistros = respuesta.totalElements;
          this.totalPaginas = respuesta.totalPages;

          this.paginaActual = respuesta.number;

          this.cargando = false;

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error al cargar la auditoría:',
            error
          );

          this.error =
            'No se pudieron obtener los registros de auditoría.';

          this.cargando = false;

          this.cdr.detectChanges();
        }
      });
  }

  // ======================================================
  // APLICAR FILTROS
  // ======================================================

  aplicarFiltros(): void {

    this.paginaActual = 0;

    this.cargarAuditorias();
  }

  // ======================================================
  // LIMPIAR FILTROS
  // ======================================================

  limpiarFiltros(): void {

    this.filtro = {
      idUsuario: null,
      accion: '',
      tablaAfectada: '',
      tipoEvento: '',
      resultado: '',
      fechaDesde: '',
      fechaHasta: ''
    };

    this.paginaActual = 0;

    this.cargarAuditorias();
  }

  // ======================================================
  // PAGINACIÓN
  // ======================================================

  cambiarPagina(pagina: number): void {

    if (
      this.cargando ||
      pagina < 0 ||
      pagina >= this.totalPaginas ||
      pagina === this.paginaActual
    ) {
      return;
    }

    this.paginaActual = pagina;

    this.cargarAuditorias();
  }

  get paginas(): number[] {

    const inicio = Math.max(
      0,
      Math.min(
        this.paginaActual - 2,
        this.totalPaginas - 5
      )
    );

    const final = Math.min(
      this.totalPaginas,
      inicio + 5
    );

    return Array.from(
      { length: Math.max(0, final - inicio) },
      (_, indice) => inicio + indice
    );
  }

  // ======================================================
  // NOMBRE DE LA PERSONA RESPONSABLE
  // ======================================================

  obtenerResponsable(
    registro: ObtenerAuditoria
  ): string {

    if (registro.origen === 'SISTEMA') {
      return 'Sistema automático';
    }

    const nombre = [
      registro.nombreUsuario,
      registro.apellidoUsuario
    ]
      .filter(Boolean)
      .join(' ')
      .trim();

    if (nombre) {
      return nombre;
    }

    if (registro.identificadorExterno) {
      return registro.identificadorExterno;
    }

    if (registro.emailUsuario) {
      return registro.emailUsuario;
    }

    return 'No identificado';
  }

  // ======================================================
  // FORMATEAR ACCIONES
  // ======================================================

  formatearAccion(accion: string): string {

    if (!accion) {
      return 'Sin acción';
    }

    return accion
      .replaceAll('_', ' ')
      .toLowerCase()
      .replace(/^./, letra => letra.toUpperCase());
  }

  // ======================================================
  // PARSEAR VALORES GUARDADOS
  // ======================================================

  private parsearValores(
    texto: string | null
  ): Record<string, unknown> {

    if (!texto) {
      return {};
    }

    try {

      const datos: unknown = JSON.parse(texto);

      if (
        datos !== null &&
        typeof datos === 'object' &&
        !Array.isArray(datos)
      ) {

        return datos as Record<string, unknown>;
      }

    } catch (error) {

      console.warn(
        'No se pudo interpretar el JSON de auditoría:',
        error
      );
    }

    return {};
  }

  // ======================================================
  // COMPARAR ANTES Y DESPUÉS
  // ======================================================

  private obtenerCambios(
    registro: ObtenerAuditoria
  ): CambioAuditoria[] {

    if (!registro.valoresAnteriores || !registro.valoresNuevos) {
      return [];
    }

    const anteriores = this.parsearValores(
      registro.valoresAnteriores
    );

    const nuevos = this.parsearValores(
      registro.valoresNuevos
    );

    const campos = new Set([
      ...Object.keys(anteriores),
      ...Object.keys(nuevos)
    ]);

    const cambios: CambioAuditoria[] = [];

    for (const campo of campos) {

      const anterior = anteriores[campo];
      const nuevo = nuevos[campo];

      if (
        JSON.stringify(anterior) ===
        JSON.stringify(nuevo)
      ) {
        continue;
      }

      cambios.push({
        campo,
        anterior,
        nuevo
      });
    }

    return cambios;
  }

  // ======================================================
  // FORMATEAR VALORES PARA MOSTRAR
  // ======================================================

  formatearValor(valor: unknown): string {

    if (valor === null || valor === undefined) {
      return '—';
    }

    if (typeof valor === 'boolean') {
      return valor ? 'Sí' : 'No';
    }

    if (typeof valor === 'object') {
      return JSON.stringify(valor);
    }

    return String(valor);
  }

  // ======================================================
  // VER DETALLE
  // ======================================================

  verDetalle(registro: ObtenerAuditoria): void {

    this.registroSeleccionado = registro;

    this.cambiosSeleccionados =
      this.obtenerCambios(registro);

    if (!registro.valoresAnteriores) {

    const metadatos = this.parsearValores(
      registro.valoresNuevos
    );

    this.contextoSeleccionado =
      Object.entries(metadatos).map(
        ([campo, nuevo]) => ({
          campo,
          anterior: null,
          nuevo
        })
      );

  } else {

    this.contextoSeleccionado = [];
  }
  }

  cerrarDetalle(): void {

    this.registroSeleccionado = null;
    this.cambiosSeleccionados = [];
    this.contextoSeleccionado = [];
  }
}
