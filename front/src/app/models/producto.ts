export interface Producto {
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
  disponible: boolean;
  categoriaId: number;
  categoriaNombre: string;
  marcaId: number | null;
  marcaNombre: string | null;
}