export interface DetalleCompra {

    idDetalleCompra: number;
    idProducto: number;
    nombreProducto: string;
    descripcionProducto: string | null;
    cantidad: number;
    precioUnitario: number;
    subtotal: number;
}