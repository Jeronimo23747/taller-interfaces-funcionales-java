package co.edu.unihumboldt.funcional.lab4_promociones;

import co.edu.unihumboldt.funcional.modelo.Producto;
import java.util.Objects;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Predicate;

/**
 * Laboratorio 4: Proyecto integrador - Motor de promociones.
 * Modela una regla de promoción comercial compuesta por un criterio de elegibilidad (Predicate)
 * y una función de ajuste de precio (DoubleUnaryOperator).
 *
 * @param nombre  Nombre descriptivo de la promoción.
 * @param aplicaA Predicado funcional que evalúa si el producto es elegible.
 * @param ajuste  Operador primitivo sobre double que transforma el precio original.
 */
public record Promocion(
    String nombre,
    Predicate<Producto> aplicaA,
    DoubleUnaryOperator ajuste
) {
    public Promocion {
        Objects.requireNonNull(nombre, "El nombre de la promoción no puede ser nulo");
        Objects.requireNonNull(aplicaA, "El predicado de aplicación no puede ser nulo");
        Objects.requireNonNull(ajuste, "El operador de ajuste no puede ser nulo");
    }

    /**
     * Calcula el precio final del producto tras aplicar la promoción, si corresponde.
     * Si el producto no cumple el criterio, devuelve su precio original sin modificaciones.
     *
     * @param p Producto a cotizar.
     * @return Precio con descuento aplicado o precio original.
     */
    public double precioCon(Producto p) {
        Objects.requireNonNull(p, "El producto no puede ser nulo");
        return aplicaA.test(p) ? ajuste.applyAsDouble(p.precio()) : p.precio();
    }
}
