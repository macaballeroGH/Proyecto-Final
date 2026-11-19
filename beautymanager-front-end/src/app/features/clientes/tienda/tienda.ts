import { ChangeDetectorRef, Component, OnInit, computed, signal } from '@angular/core';

import { HttpErrorResponse } from '@angular/common/http';
import { MatIcon } from '@angular/material/icon';
import { finalize, forkJoin } from 'rxjs';

import { ProductoService } from '../../../core/services/producto';
import { CarritoService } from '../../../core/services/carrito';
import { CompraService } from '../../../core/services/compra';

import { ObtenerProducto } from '../../../core/models/obtener-producto';
import { ObtenerCarrito } from '../../../core/models/obtener-carrito';
import { CarritoItem } from '../../../core/models/carrito-item';
import { ObtenerCompra } from '../../../core/models/obtener-compra';

import { EstadoProducto } from '../../../core/enums/estadoProducto';

type OrdenProductos = 'nombre' | 'precioAsc' | 'precioDesc';
@Component({
  selector: 'app-tienda-cliente',
  imports: [MatIcon],
  templateUrl: './tienda.html',
  styleUrl: './tienda.css',
})
export class TiendaCliente implements OnInit {

  readonly pagosHabilitados = true;

  readonly EstadoProducto = EstadoProducto;

  private readonly claveStorageBase =
    'beautymanager-compra-pendiente';

  //========================
  //Productos
  //========================
  productos = signal<ObtenerProducto[]>([]);

  cargandoProductos = signal(false);

  errorProductos = signal('');

  //========================
  //Carrito
  //========================
  carrito = signal<ObtenerCarrito | null>(null);

  cargandoCarrito = signal(false);

  procesandoCarrito = signal(false);

  productoEnProceso = signal<number | null>(null);

  carritoAbierto = signal(false);

  //========================
  //Compra
  //========================
  confirmacionAbierta = signal(false);

  preparandoCompra = signal(false);

  procesandoCompra = signal(false);

  compraRealizada = signal<ObtenerCompra | null>(null);

  clavePendiente = signal<string | null>(null);

  //========================
  //Mensajes
  //========================
  mensaje = signal('');

  errorOperacion = signal('');

  //========================
  //Filtros
  //========================
  busqueda = signal('');

  ordenProductos = signal<OrdenProductos>('nombre');

  //========================
  //Cantidades seleccionadas
  //========================
  cantidades = signal<Record<number, number>>({});

  //========================
  //Formato moneda
  //========================
  private readonly formatoMoneda = new Intl.NumberFormat(
    'es-AR',
    {
      style: 'currency',
      currency: 'ARS'
    }
  );

  constructor(
    private productoService: ProductoService,
    private carritoService: CarritoService,
    private compraService: CompraService,
    private cdr: ChangeDetectorRef
  ) {}

  //========================
  //Inicializar
  //========================
  ngOnInit(): void {

    this.recuperarCompraPendiente();

    this.cargarProductos();

    this.cargarCarrito();
  }

  //========================
  //Productos visibles
  //========================
  productosVisibles = computed(() => {

    const texto = this.busqueda()
      .trim()
      .toLowerCase();

    const productos = this.productos().filter(producto => {

      const visible =
        producto.estadoProducto !== EstadoProducto.INACTIVO;

      const coincideBusqueda =
        producto.nombre.toLowerCase().includes(texto) ||
        (producto.descripcion ?? '')
          .toLowerCase()
          .includes(texto);

      return visible && coincideBusqueda;
    });

    switch(this.ordenProductos()) {

      case 'precioAsc':

        return productos.sort(
          (a, b) => a.precio - b.precio
        );

      case 'precioDesc':

        return productos.sort(
          (a, b) => b.precio - a.precio
        );

      default:

        return productos.sort(
          (a, b) => a.nombre.localeCompare(b.nombre)
        );
    }
  });

  //========================
  //Cantidad total en carrito
  //========================
  cantidadEnCarrito = computed(() => {

    return (this.carrito()?.items ?? [])
      .reduce(
        (total, item) => total + item.cantidad,
        0
      );
  });

  //========================
  //Total del carrito
  //========================
  totalCarrito = computed(() => {

    return this.carrito()?.total ?? 0;
  });

  //========================
  //Total con precios vigentes
  //========================
  totalActualizadoCarrito = computed(() => {

    return (this.carrito()?.items ?? [])
      .reduce(
        (total, item) =>
          total + item.precioProducto * item.cantidad,
        0
      );
  });

  //========================
  //Detectar cambios de precio
  //========================
  hayCambiosPrecio = computed(() => {

    return (this.carrito()?.items ?? [])
      .some(item =>
        item.precioProducto !== item.precioUnitario
      );
  });

  //========================
  //Detectar problemas de stock
  //========================
  hayProblemasStock = computed(() => {

    return (this.carrito()?.items ?? [])
      .some(item => {

        const producto = this.obtenerProductoCarrito(item);

        return !producto ||
          producto.estadoProducto !== EstadoProducto.ACTIVO ||
          producto.stock < item.cantidad ||
          producto.precio <= 0;
      });
  });

  //========================
  //Comprobar si puede pagar
  //========================
  puedeConfirmarCompra = computed(() => {

    const carritoActual = this.carrito();

    if(!this.pagosHabilitados ||
       !carritoActual ||
       carritoActual.items.length === 0 ||
       this.cargandoProductos() ||
       this.cargandoCarrito() ||
       this.procesandoCarrito() ||
       this.preparandoCompra() ||
       this.procesandoCompra() ||
       this.clavePendiente() !== null ||
       this.hayCambiosPrecio() ||
       this.hayProblemasStock()) {

      return false;
    }

    return carritoActual.items.every(item => {

      const producto = this.obtenerProductoCarrito(item);

      return producto !== undefined &&
        item.cantidad > 0 &&
        item.precioUnitario > 0 &&
        item.precioUnitario === producto.precio;
    });
  });

  //========================
  //Cargar productos
  //========================
  cargarProductos(): void {

    this.cargandoProductos.set(true);

    this.errorProductos.set('');

    this.productoService.listarProductos()
      .pipe(
        finalize(() => {

          this.cargandoProductos.set(false);

          this.cdr.markForCheck();
        })
      )
      .subscribe({

        next: productos => {

          this.productos.set(productos);

          this.cdr.detectChanges();
        },

        error: () => {

          this.productos.set([]);

          this.errorProductos.set(
            'No se pudieron cargar los productos.'
          );

          this.cdr.detectChanges();
        }
      });
  }

  //========================
  //Cargar carrito
  //========================
  cargarCarrito(): void {

    this.cargandoCarrito.set(true);

    this.carritoService.obtenerMiCarrito()
      .pipe(
        finalize(() => {

          this.cargandoCarrito.set(false);

          this.cdr.markForCheck();
        })
      )
      .subscribe({

        next: carrito => {

          this.carrito.set(carrito);

          this.cdr.detectChanges();
        },

        error: error => {

          this.carrito.set(null);

          this.errorOperacion.set(
            this.obtenerMensajeError(
              error,
              'No se pudo cargar el carrito.'
            )
          );

          this.cdr.detectChanges();
        }
      });
  }

  //========================
  //Actualizar tienda
  //========================
  actualizarTienda(): void {

    if(this.procesandoCarrito() ||
       this.procesandoCompra() ||
       this.preparandoCompra() ||
       this.cargandoCarrito() ||
       this.cargandoProductos()) {

      return;
    }

    this.mensaje.set('');

    this.errorOperacion.set('');

    this.cargarProductos();

    this.cargarCarrito();
  }

  //========================
  //Buscar productos
  //========================
  buscarProductos(texto: string): void {

    this.busqueda.set(texto);
  }

  //========================
  //Ordenar productos
  //========================
  cambiarOrden(orden: string): void {

    if(orden === 'nombre' ||
       orden === 'precioAsc' ||
       orden === 'precioDesc') {

      this.ordenProductos.set(orden);
    }
  }

  //========================
  //Formatear precio
  //========================
  formatearPrecio(precio: number): string {

    return this.formatoMoneda.format(precio);
  }

  //========================
  //Obtener producto del carrito
  //========================
  obtenerProductoCarrito(
    item: CarritoItem
  ): ObtenerProducto | undefined {

    return this.productos()
      .find(producto =>
        producto.idProducto === item.idProducto
      );
  }

  //========================
  //Precio vigente
  //========================
  precioVigente(item: CarritoItem): number {

    return item.precioProducto;
  }

  //========================
  //Detectar precio modificado
  //========================
  precioModificado(item: CarritoItem): boolean {

    return item.precioProducto !== item.precioUnitario;
  }

  //========================
  //Cantidad existente en carrito
  //========================
  cantidadProductoEnCarrito(
    idProducto: number
  ): number {

    return this.carrito()?.items
      .find(item => item.idProducto === idProducto)
      ?.cantidad ?? 0;
  }

  //========================
  //Stock disponible para agregar
  //========================
  stockDisponibleParaAgregar(
    producto: ObtenerProducto
  ): number {

    const cantidadActual =
      this.cantidadProductoEnCarrito(
        producto.idProducto
      );

    return Math.max(
      0,
      producto.stock - cantidadActual
    );
  }

  //========================
  //Puede agregar producto
  //========================
  puedeAgregar(producto: ObtenerProducto): boolean {

    return producto.estadoProducto === EstadoProducto.ACTIVO &&
      producto.precio > 0 &&
      this.stockDisponibleParaAgregar(producto) > 0 &&
      this.carrito() !== null &&
      !this.cargandoCarrito() &&
      !this.cargandoProductos() &&
      !this.procesandoCarrito() &&
      !this.preparandoCompra() &&
      !this.procesandoCompra() &&
      this.clavePendiente() === null;
  }

  //========================
  //Cantidad seleccionada
  //========================
  cantidadSeleccionada(idProducto: number): number {

    return this.cantidades()[idProducto] ?? 1;
  }

  //========================
  //Modificar cantidad seleccionada
  //========================
  cambiarCantidad(
    producto: ObtenerProducto,
    diferencia: number
  ): void {

    if(!this.puedeAgregar(producto)) {
      return;
    }

    const cantidadActual =
      this.cantidadSeleccionada(
        producto.idProducto
      );

    const cantidadMaxima =
      this.stockDisponibleParaAgregar(producto);

    const cantidadNueva = Math.max(
      1,
      Math.min(
        cantidadMaxima,
        cantidadActual + diferencia
      )
    );

    this.cantidades.update(cantidades => ({
      ...cantidades,
      [producto.idProducto]: cantidadNueva
    }));
  }

  //========================
  //Agregar producto al carrito
  //========================
  agregarAlCarrito(producto: ObtenerProducto): void {

    this.mensaje.set('');

    this.errorOperacion.set('');

    if(!this.puedeAgregar(producto)) {

      this.errorOperacion.set(
        'El producto no está disponible.'
      );

      return;
    }

    const cantidad = this.cantidadSeleccionada(
      producto.idProducto
    );

    if(!Number.isInteger(cantidad) ||
       cantidad <= 0 ||
       cantidad > this.stockDisponibleParaAgregar(producto)) {

      this.errorOperacion.set(
        'La cantidad seleccionada no es válida.'
      );

      return;
    }

    this.procesandoCarrito.set(true);

    this.productoEnProceso.set(
      producto.idProducto
    );

    this.carritoService.agregarProducto({
      idProducto: producto.idProducto,
      cantidad
    })
    .pipe(
      finalize(() => {

        this.procesandoCarrito.set(false);

        this.productoEnProceso.set(null);

        this.cdr.markForCheck();
      })
    )
    .subscribe({

      next: carritoActualizado => {

        this.carrito.set(carritoActualizado);

        this.cantidades.update(cantidades => ({
          ...cantidades,
          [producto.idProducto]: 1
        }));

        this.mensaje.set(
          'Producto agregado al carrito correctamente.'
        );

        this.cdr.detectChanges();
      },

      error: error => {

        this.errorOperacion.set(
          this.obtenerMensajeError(
            error,
            'No se pudo agregar el producto al carrito.'
          )
        );

        this.cdr.detectChanges();
      }
    });
  }

  //========================
  //Cambiar cantidad en carrito
  //========================
  cambiarCantidadCarrito(
    item: CarritoItem,
    diferencia: number
  ): void {

    const cantidadNueva = item.cantidad + diferencia;

    if(cantidadNueva < 1) {
      return;
    }

    if(this.precioModificado(item)) {

      this.errorOperacion.set(
        'Primero aceptá el precio actualizado del producto.'
      );

      return;
    }

    this.actualizarCantidadCarrito(
      item,
      cantidadNueva,
      false
    );
  }

  //========================
  //Aceptar cambio de precio
  //========================
  aceptarCambioPrecio(item: CarritoItem): void {

    if(!this.precioModificado(item)) {
      return;
    }

    this.actualizarCantidadCarrito(
      item,
      item.cantidad,
      true
    );
  }

  //========================
  //Actualizar cantidad en backend
  //========================
  private actualizarCantidadCarrito(
    item: CarritoItem,
    cantidad: number,
    aceptandoPrecio: boolean
  ): void {

    this.mensaje.set('');

    this.errorOperacion.set('');

    if(this.procesandoCarrito() ||
       this.procesandoCompra() ||
       this.preparandoCompra() ||
       this.cargandoCarrito() ||
       this.clavePendiente() !== null) {

      return;
    }

    const producto = this.obtenerProductoCarrito(item);

    if(!producto ||
       producto.estadoProducto !== EstadoProducto.ACTIVO) {

      this.errorOperacion.set(
        'Este producto ya no está disponible.'
      );

      return;
    }

    if(!Number.isInteger(cantidad) ||
       cantidad <= 0 ||
       cantidad > producto.stock) {

      this.errorOperacion.set(
        'La cantidad supera el stock disponible.'
      );

      return;
    }

    this.procesandoCarrito.set(true);

    this.productoEnProceso.set(item.idProducto);

    this.carritoService.actualizarCantidad({
      idProducto: item.idProducto,
      cantidad
    })
    .pipe(
      finalize(() => {

        this.procesandoCarrito.set(false);

        this.productoEnProceso.set(null);

        this.cdr.markForCheck();
      })
    )
    .subscribe({

      next: carritoActualizado => {

        this.carrito.set(carritoActualizado);

        this.mensaje.set(
          aceptandoPrecio
            ? 'Precio actualizado correctamente.'
            : 'Cantidad actualizada correctamente.'
        );

        this.cdr.detectChanges();
      },

      error: error => {

        this.errorOperacion.set(
          this.obtenerMensajeError(
            error,
            'No se pudo actualizar el carrito.'
          )
        );

        this.cdr.detectChanges();
      }
    });
  }

  //========================
  //Eliminar producto del carrito
  //========================
  eliminarDelCarrito(item: CarritoItem): void {

    this.mensaje.set('');

    this.errorOperacion.set('');

    if(this.procesandoCarrito() ||
       this.procesandoCompra() ||
       this.preparandoCompra() ||
       this.cargandoCarrito() ||
       this.clavePendiente() !== null) {

      return;
    }

    this.procesandoCarrito.set(true);

    this.productoEnProceso.set(item.idProducto);

    this.carritoService.eliminarProducto(item.idProducto)
      .pipe(
        finalize(() => {

          this.procesandoCarrito.set(false);

          this.productoEnProceso.set(null);

          this.cdr.markForCheck();
        })
      )
      .subscribe({

        next: carritoActualizado => {

          this.carrito.set(carritoActualizado);

          this.mensaje.set(
            'Producto eliminado del carrito.'
          );

          this.cdr.detectChanges();
        },

        error: error => {

          this.errorOperacion.set(
            this.obtenerMensajeError(
              error,
              'No se pudo eliminar el producto.'
            )
          );

          this.cdr.detectChanges();
        }
      });
  }

  //========================
  //Abrir carrito
  //========================
  abrirCarrito(): void {

    if(this.procesandoCarrito() ||
       this.procesandoCompra()) {
      return;
    }

    this.carritoAbierto.set(true);

    this.cargarCarrito();
  }

  //========================
  //Cerrar carrito
  //========================
  cerrarCarrito(): void {

    if(this.procesandoCarrito() ||
       this.procesandoCompra() ||
       this.preparandoCompra()) {
      return;
    }

    this.carritoAbierto.set(false);
  }

  //========================
  //Preparar confirmación de compra
  //========================
  abrirConfirmacionCompra(): void {

    this.errorOperacion.set('');

    if(!this.pagosHabilitados) {

      this.errorOperacion.set(
        'Las compras simuladas no están habilitadas.'
      );

      return;
    }

    if(!this.puedeConfirmarCompra() ||
       this.confirmacionAbierta()) {

      return;
    }

    this.preparandoCompra.set(true);

    //========================
    //Consultar datos actualizados
    //========================
    forkJoin({
      productos: this.productoService.listarProductos(),
      carrito: this.carritoService.obtenerMiCarrito()
    })
    .pipe(
      finalize(() => {

        this.preparandoCompra.set(false);

        this.cdr.markForCheck();
      })
    )
    .subscribe({

      next: resultado => {

        this.productos.set(resultado.productos);

        this.carrito.set(resultado.carrito);

        if(resultado.carrito.items.length === 0) {

          this.errorOperacion.set(
            'El carrito está vacío.'
          );

        }
        else if(this.hayCambiosPrecio()) {

          this.errorOperacion.set(
            'Algunos precios cambiaron. Revisalos y ' +
            'aceptalos antes de comprar.'
          );

        }
        else if(this.hayProblemasStock()) {

          this.errorOperacion.set(
            'Algunos productos no tienen stock suficiente.'
          );

        }
        else {

          this.confirmacionAbierta.set(true);
        }

        this.cdr.detectChanges();
      },

      error: error => {

        this.errorOperacion.set(
          this.obtenerMensajeError(
            error,
            'No se pudo preparar la compra.'
          )
        );

        this.cdr.detectChanges();
      }
    });
  }

  //========================
  //Cerrar confirmación
  //========================
  cerrarConfirmacionCompra(): void {

    if(this.procesandoCompra()) {
      return;
    }

    this.confirmacionAbierta.set(false);
  }

  //========================
  //Confirmar compra
  //========================
  confirmarCompra(): void {

    this.errorOperacion.set('');

    if(!this.confirmacionAbierta() ||
       !this.puedeConfirmarCompra()) {

      return;
    }

    //========================
    //Clave única de la compra
    //========================
    const clave = crypto.randomUUID();

    //========================
    //Guardar clave antes del envío
    //========================
    try {

      sessionStorage.setItem(
        this.obtenerClaveStorage(),
        clave
      );

    } catch {

      this.errorOperacion.set(
        'No se pudo guardar la identificación de la compra.'
      );

      return;
    }

    this.clavePendiente.set(clave);

    this.enviarCompra(clave);
  }

  //========================
  //Reintentar una compra pendiente
  //========================
  reintentarCompraPendiente(): void {

    if(!this.pagosHabilitados ||
       this.procesandoCompra() ||
       this.procesandoCarrito() ||
       this.preparandoCompra()) {

      return;
    }

    const clave = this.clavePendiente();

    if(!clave) {
      return;
    }

    //========================
    //Reutilizar la misma clave
    //========================
    this.enviarCompra(clave);
  }

  //========================
  //Enviar compra al backend
  //========================
  private enviarCompra(clave: string): void {

    this.procesandoCompra.set(true);

    this.errorOperacion.set('');

    this.compraService.realizarCompra({
      claveIdempotencia: clave
    })
    .pipe(
      finalize(() => {

        this.procesandoCompra.set(false);

        this.cdr.markForCheck();
      })
    )
    .subscribe({

      next: compra => {

        //========================
        //Compra confirmada
        //========================
        this.compraRealizada.set(compra);

        this.limpiarCompraPendiente();

        this.confirmacionAbierta.set(false);

        this.carritoAbierto.set(false);

        this.mensaje.set(
          '¡Compra realizada correctamente!'
        );

        this.cargarProductos();

        this.cargarCarrito();

        this.cdr.detectChanges();
      },

      error: error => {

        const mensaje = this.obtenerMensajeError(
          error,
          'No se pudo confirmar la compra.'
        );

        //========================
        //Resultado incierto
        //Conservar clave para reintentar
        //========================
        if(this.resultadoCompraIncierto(error)) {

          this.errorOperacion.set(
            mensaje +
            ' No sabemos si la compra se registró. ' +
            'Podés verificarla usando la misma clave.'
          );

        }
        else {

          //========================
          //Compra rechazada
          //Permitir corregir el carrito
          //========================
          this.limpiarCompraPendiente();

          this.confirmacionAbierta.set(false);

          this.errorOperacion.set(mensaje);

          this.cargarProductos();

          this.cargarCarrito();
        }

        this.cdr.detectChanges();
      }
    });
  }

  //========================
  //Comprobar resultado incierto
  //========================
  private resultadoCompraIncierto(error: unknown): boolean {

    if(!(error instanceof HttpErrorResponse)) {
      return true;
    }

    return error.status === 0 ||
      error.status === 408 ||
      error.status === 409 ||
      error.status >= 500;
  }

  //========================
  //Clave asociada a la usuaria
  //========================
  private obtenerClaveStorage(): string {

    try {

      const sesion = localStorage.getItem('usuario');

      if(sesion) {

        const usuario = JSON.parse(sesion);

        if(usuario?.idUsuario != null) {

          return this.claveStorageBase +
            '-' + usuario.idUsuario;
        }
      }

    } catch {

      //Utilizar clave general de la sesión.
    }

    return this.claveStorageBase;
  }

  //========================
  //Recuperar compra pendiente
  //========================
  private recuperarCompraPendiente(): void {

    try {

      const clave = sessionStorage.getItem(
        this.obtenerClaveStorage()
      );

      if(clave) {

        this.clavePendiente.set(clave);
      }

    } catch {

      this.errorOperacion.set(
        'No se pudo recuperar una posible compra pendiente.'
      );
    }
  }

  //========================
  //Limpiar compra confirmada o rechazada
  //========================
  private limpiarCompraPendiente(): void {

    this.clavePendiente.set(null);

    try {

      sessionStorage.removeItem(
        this.obtenerClaveStorage()
      );

    } catch {

      //La aplicación continuará con la sesión actual.
    }
  }

  //========================
  //Cerrar comprobante
  //========================
  cerrarComprobante(): void {

    this.compraRealizada.set(null);
  }

  //========================
  //Obtener mensaje de error
  //========================
  private obtenerMensajeError(
    error: unknown,
    mensajeAlternativo: string
  ): string {

    if(error instanceof HttpErrorResponse) {

      const mensaje =
        error.error?.message ??
        error.error?.detail;

      if(typeof mensaje === 'string' &&
         mensaje.trim().length > 0) {

        return mensaje;
      }

      if(typeof error.error === 'string' &&
         error.error.trim().length > 0) {

        return error.error;
      }
    }

    return mensajeAlternativo;
  }
}
