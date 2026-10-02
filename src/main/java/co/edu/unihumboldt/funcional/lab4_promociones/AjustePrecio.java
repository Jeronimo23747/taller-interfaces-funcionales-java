package co.edu.unihumboldt.funcional.lab4_promociones;

import java.util.function.DoubleUnaryOperator;

/**
 * Fábricas de funciones de ajuste de precio para promociones,
 * implementadas con la especialización primitiva {@link DoubleUnaryOperator}
 * para evitar sobrecostos por autoboxing.
 */
public final class AjustePrecio {

    private AjustePrecio() {
        // Utilidad estática
    }

    /**
     * Crea un operador de descuento porcentual.
     *
     * @param pct Porcentaje de descuento (ej: 10 para 10%).
     * @return Operador funcional que aplica el descuento.
     */
    public static DoubleUnaryOperator porcentaje(double pct) {
        return precio -> precio * (1.0 - pct / 100.0);
    }

    /**
     * Crea un operador de descuento por monto fijo, garantizando que el precio nunca baje de cero.
     *
     * @param monto Monto fijo a descontar en la misma divisa del producto.
     * @return Operador funcional que descuenta el monto protegiendo contra valores negativos.
     */
    public static DoubleUnaryOperator montoFijo(double monto) {
        return precio -> Math.max(0.0, precio - monto);
    }

    /**
     * Operador identidad: no aplica ninguna variación sobre el precio.
     *
     * @return Operador identidad de double.
     */
    public static DoubleUnaryOperator sinAjuste() {
        return DoubleUnaryOperator.identity();
    }
}
