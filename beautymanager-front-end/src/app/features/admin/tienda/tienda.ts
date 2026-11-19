import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { ObtenerProducto } from '../../../core/models/obtener-producto';
import { EstadoProducto } from '../../../core/enums/estadoProducto';
import { ProductoService } from '../../../core/services/producto';
import { finalize } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { ActualizarProducto } from '../../../core/models/actualizar-producto';
import { CrearProducto } from '../../../core/models/crear-producto';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-tienda',
  standalone: true,
  imports: [FormsModule, MatIconModule, CurrencyPipe],
  templateUrl: './tienda.html',
  styleUrl: './tienda.css',
})
export class TiendaAdmin implements OnInit {

  productos: ObtenerProducto[] = [];

  productosFiltrados: ObtenerProducto[] = [];

  readonly EstadoProducto = EstadoProducto;

  cargando = false;

  guardando = false;

  eliminando = false;

  actualizandoFoto = false;

  gestionandoStock = false;

  busqueda = '';

  filtroEstado: 'TODOS' | EstadoProducto = 'TODOS';

  mostrarFormulario = false;

  modoEdicion = false;

  productoSeleccionado: ObtenerProducto | null = null;

  nombre = '';

  descripcion = '';

  precio: number | null = null;

  stock: number | null = null;

  estado = EstadoProducto.ACTIVO;

  fotoSeleccionada: File | null = null;

  vistaPreviaFoto: string | null = null;

  mostrarGestionStock = false;

  cantidadStock: number | null = null;

  productoStockSeleccionado: ObtenerProducto | null = null;

  productoEliminar: ObtenerProducto | null = null;

  mostrarConfirmacionEliminar = false;

  mensajeExito = '';

  mensajeError = '';

  constructor(private productoService: ProductoService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.cargarProductos();
  }

  cargarProductos(): void {

    this.cargando = true;

    this.limpiarMensajes();

    this.productoService.listarProductos().pipe(finalize(() => {

      this.cargando = false;

      this.cdr.detectChanges();
    })).subscribe({

      next: (productos: ObtenerProducto[]) => {

        this.productos = productos;

        this.aplicarFiltros();

        this.cdr.detectChanges();
      },

      error: (error: HttpErrorResponse) => {

        console.error('Error al cargar los productos:', error);

        this.mensajeError = 'No se pudieron cargar los productos.';

        this.cdr.detectChanges();
      }
    });
  }

  aplicarFiltros(): void {

    let resultado = [...this.productos];

    const busqueda = this.busqueda.trim().toLowerCase();

    if (busqueda) {
      resultado = resultado.filter(producto => producto.nombre.toLowerCase().includes(busqueda));
    }

    if (this.filtroEstado !== 'TODOS') {
      resultado = resultado.filter(producto => producto.estadoProducto === this.filtroEstado);
    }

    this.productosFiltrados = resultado;

    this.cdr.detectChanges();
  }

  cambiarFiltro(estado: 'TODOS' | EstadoProducto): void {

    this.filtroEstado = estado;

    this.aplicarFiltros();
  }

  get totalProductos(): number {
    return this.productos.length;
  }

  get productosActivos(): number {
    return this.productos.filter(producto => producto.estadoProducto === EstadoProducto.ACTIVO).length;
  }

  get productosSinStock(): number {
    return this.productos.filter(producto => producto.estadoProducto === EstadoProducto.SIN_STOCK).length;
  }

  abrirFormularioCrear(): void {

    this.limpiarFormulario();

    this.modoEdicion = false;

    this.productoSeleccionado = null;

    this.mostrarFormulario = true;
  }

  abrirFormularioEditar(producto: ObtenerProducto): void {

    this.limpiarFormulario();

    this.modoEdicion = true;

    this.productoSeleccionado = producto;

    this.nombre = producto.nombre;

    this.descripcion = producto.descripcion ?? '';

    this.precio = producto.precio;

    this.stock = producto.stock;

    this.estado = producto.estadoProducto;

    this.vistaPreviaFoto = producto.foto;

    this.mostrarFormulario = true;
  }

  guardarProducto(): void {

    this.limpiarMensajes();

    if (!this.validarFormulario()) {

      this.cdr.detectChanges();

      return;
    }

    if (this.guardando) {
      return;
    }

    this.guardando = true;

    if (this.modoEdicion && this.productoSeleccionado) {

      const datosProducto: ActualizarProducto = {
        nombre: this.nombre.trim(),
        descripcion: this.descripcion.trim(),
        precio: this.precio!,
        stock: this.stock!,
        estado: this.estado
      };

      this.productoService.actualizarProducto(this.productoSeleccionado.idProducto, datosProducto).pipe(
        finalize(() => {

          this.guardando = false;

          this.cdr.detectChanges();
        })
      ).subscribe({

        next: (producto: ObtenerProducto) => {

          if (this.fotoSeleccionada) {

            this.subirFotoProducto(producto.idProducto);

          } else {

            this.finalizarGuardado(producto, 'El producto se actualizó correctamente.');
          }
        },

        error: (error: HttpErrorResponse) => {

          console.error('Error al actualizar el producto:', error);

          this.mensajeError = 'No se pudo actualizar el producto.';

          this.cdr.detectChanges();
        }
      });

    } else {

      const datosProducto: CrearProducto = {
        nombre: this.nombre.trim(),
        descripcion: this.descripcion.trim(),
        precio: this.precio!,
        stock: this.stock!,
        estado: this.estado
      };

      this.productoService.crearProducto(datosProducto).pipe(
        finalize(() => {

          this.guardando = false;

          this.cdr.detectChanges();
        })
      ).subscribe({

        next: (producto: ObtenerProducto) => {

          if (this.fotoSeleccionada) {

            this.subirFotoProducto(producto.idProducto);

          } else {

            this.finalizarGuardado(producto, 'El producto se creó correctamente.');
          }
        },

        error: (error: HttpErrorResponse) => {

          console.error('Error al crear el producto:', error);

          this.mensajeError = 'No se pudo crear el producto.';

          this.cdr.detectChanges();
        }
      });
    }
  }

  seleccionarFoto(event: Event): void {

    const input = event.target as HTMLInputElement;

    const archivo = input.files?.[0];

    if (!archivo) {
      return;
    }

    const extensionesPermitidas = ['image/jpeg', 'image/png', 'image/webp'];

    if (!extensionesPermitidas.includes(archivo.type)) {

      this.mensajeError = 'La fotografía debe estar en formato JPG, PNG o WEBP.';

      input.value = '';

      this.cdr.detectChanges();

      return;
    }

    if (archivo.size > 5 * 1024 * 1024) {

      this.mensajeError = 'La fotografía no puede superar los 5 MB.';

      input.value = '';

      this.cdr.detectChanges();

      return;
    }

    this.fotoSeleccionada = archivo;

    if (this.vistaPreviaFoto?.startsWith('blob:')) {

      URL.revokeObjectURL(this.vistaPreviaFoto);
    }

    this.vistaPreviaFoto = URL.createObjectURL(archivo);

    this.mensajeError = '';

    this.cdr.detectChanges();
  }

  private subirFotoProducto(idProducto: number): void {

    if (!this.fotoSeleccionada) {
      return;
    }

    this.actualizandoFoto = true;

    this.productoService.actualizarFotoProducto(idProducto, this.fotoSeleccionada).pipe(
      finalize(() => {

        this.actualizandoFoto = false;

        this.cdr.detectChanges();
      })
    ).subscribe({

      next: () => {

        this.cargarProductos();

        this.cerrarFormulario();

        this.mensajeExito = this.modoEdicion ? 'El producto se actualizó correctamente.' : 'El producto se creó correctamente.';

        this.cdr.detectChanges();
      },

      error: (error: HttpErrorResponse) => {

        console.error('Error al subir la fotografía:', error);

        this.mensajeError = 'El producto se guardó, pero no se pudo cargar la fotografía.';

        this.cargarProductos();

        this.cdr.detectChanges();
      }
    });
  }

  eliminarFoto(): void {

    if (!this.productoSeleccionado || !this.productoSeleccionado.foto) {
      return;
    }

    this.limpiarMensajes();

    this.actualizandoFoto = true;

    this.productoService.eliminarFotoProducto(this.productoSeleccionado.idProducto).pipe(
      finalize(() => {

        this.actualizandoFoto = false;

        this.cdr.detectChanges();
      })
    ).subscribe({

      next: () => {

        this.vistaPreviaFoto = null;

        this.fotoSeleccionada = null;

        this.cargarProductos();

        this.mensajeExito = 'La fotografía se eliminó correctamente.';

        this.cdr.detectChanges();
      },

      error: (error: HttpErrorResponse) => {

        console.error('Error al eliminar la fotografía:', error);

        this.mensajeError = 'No se pudo eliminar la fotografía.';

        this.cdr.detectChanges();
      }
    });
  }

  abrirGestionStock(producto: ObtenerProducto): void {

    this.productoStockSeleccionado = producto;

    this.cantidadStock = null;

    this.mostrarGestionStock = true;

    this.limpiarMensajes();
  }

  aumentarStock(): void {

    this.limpiarMensajes();

    if (!this.productoStockSeleccionado) {
      return;
    }

    if (this.cantidadStock === null || !Number.isInteger(this.cantidadStock) || this.cantidadStock <= 0) {

      this.mensajeError = 'Ingresá una cantidad entera mayor que cero.';

      this.cdr.detectChanges();

      return;
    }

    if (this.gestionandoStock) {
      return;
    }

    this.gestionandoStock = true;

    this.productoService.aumentarStock(this.productoStockSeleccionado.idProducto, this.cantidadStock).pipe(
      finalize(() => {

        this.gestionandoStock = false;

        this.cdr.detectChanges();
      })
    ).subscribe({

      next: () => {

        this.mostrarGestionStock = false;

        this.productoStockSeleccionado = null;

        this.cantidadStock = null;

        this.mensajeExito = 'El stock se actualizó correctamente.';

        this.cargarProductos();

        this.cdr.detectChanges();
      },

      error: (error: HttpErrorResponse) => {

        console.error('Error al aumentar el stock:', error);

        this.mensajeError = 'No se pudo actualizar el stock.';

        this.cdr.detectChanges();
      }
    });
  }

  confirmarEliminacion(producto: ObtenerProducto): void {

    this.productoEliminar = producto;

    this.mostrarConfirmacionEliminar = true;

    this.limpiarMensajes();
  }

  eliminarProducto(): void {

    if (!this.productoEliminar || this.eliminando) {
      return;
    }

    this.eliminando = true;

    this.productoService.eliminarProducto(this.productoEliminar.idProducto).pipe(
      finalize(() => {

        this.eliminando = false;

        this.cdr.detectChanges();
      })
    ).subscribe({

      next: () => {

        this.mostrarConfirmacionEliminar = false;

        this.productoEliminar = null;

        this.mensajeExito = 'El producto se eliminó correctamente.';

        this.cargarProductos();

        this.cdr.detectChanges();
      },

      error: (error: HttpErrorResponse) => {

        console.error('Error al eliminar el producto:', error);

        this.mensajeError = 'No se pudo eliminar el producto. Puede estar asociado a una compra.';

        this.cdr.detectChanges();
      }
    });
  }

  cerrarFormulario(): void {

    if (this.vistaPreviaFoto?.startsWith('blob:')) {

      URL.revokeObjectURL(this.vistaPreviaFoto);
    }

    this.mostrarFormulario = false;

    this.limpiarFormulario();
  }

  cerrarGestionStock(): void {

    this.mostrarGestionStock = false;

    this.productoStockSeleccionado = null;

    this.cantidadStock = null;
  }

  cancelarEliminacion(): void {

    this.mostrarConfirmacionEliminar = false;

    this.productoEliminar = null;
  }

  private validarFormulario(): boolean {

    if (!this.nombre.trim()) {

      this.mensajeError = 'Ingresá el nombre del producto.';

      return false;
    }

    if (this.precio === null || !Number.isFinite(this.precio) || this.precio <= 0) {

      this.mensajeError = 'Ingresá un precio válido mayor que cero.';

      return false;
    }

    if (this.stock === null || !Number.isInteger(this.stock) || this.stock < 0) {

      this.mensajeError = 'Ingresá un stock válido.';

      return false;
    }

    if (!this.estado) {

      this.mensajeError = 'Seleccioná un estado para el producto.';

      return false;
    }

    return true;
  }

  private finalizarGuardado(producto: ObtenerProducto, mensaje: string): void {

    this.cerrarFormulario();

    this.cargarProductos();

    this.mensajeExito = mensaje;

    this.cdr.detectChanges();
  }

  private limpiarFormulario(): void {

    this.nombre = '';

    this.descripcion = '';

    this.precio = null;

    this.stock = null;

    this.estado = EstadoProducto.ACTIVO;

    this.fotoSeleccionada = null;

    if (this.vistaPreviaFoto?.startsWith('blob:')) {

      URL.revokeObjectURL(this.vistaPreviaFoto);
    }

    this.vistaPreviaFoto = null;

    this.productoSeleccionado = null;
  }

  private limpiarMensajes(): void {

    this.mensajeExito = '';

    this.mensajeError = '';
  }
}
