package co.edu.unihumboldt.funcional.modelo;

/**
 * Modelo de datos inmutable compartido por los ejemplos y laboratorios de la guía.
 * Representa un producto en el catálogo de la tienda.
 *
 * @param nombre    Nombre comercial del producto.
 * @param categoria Categoría a la que pertenece (tecnología, papelería, muebles, etc.).
 * @param precio    Precio unitario en pesos colombianos.
 * @param stock     Cantidad de unidades disponibles en inventario.
 */
public record Producto(String nombre, String categoria, double precio, int stock) {

    /**
     * Determina si el producto está disponible para la venta (stock mayor a cero).
     *
     * @return true si hay stock disponible; false en caso contrario.
     */
    public boolean disponible() {
        return stock > 0;
    }
}
