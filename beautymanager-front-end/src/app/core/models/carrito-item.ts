export interface CarritoItem {

    idCarritoItem: number;
    idProducto: number;
    nombreProducto: string;
    descripcionProducto: string | null;
    precioProducto: number;
    precioUnitario: number;
    cantidad: number;
    subtotal: number;
}