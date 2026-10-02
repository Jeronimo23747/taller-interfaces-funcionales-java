package co.edu.unihumboldt.funcional.lab4_promociones;

import co.edu.unihumboldt.funcional.modelo.Catalogo;
import co.edu.unihumboldt.funcional.modelo.Producto;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.function.ToDoubleBiFunction;

/**
 * Ejecutable del Laboratorio 4: Proyecto integrador - Motor de promociones del catálogo.
 * Demuestra:
 * 1. Modelado de promociones comerciales usando Predicate y DoubleUnaryOperator.
 * 2. Uso de Supplier como fallback ("Sin promoción").
 * 3. Selección funcional de la mejor opción con BinaryOperator.minBy y Comparator.
 * 4. Procesamiento de salida con BiConsumer y acumulación de ahorros con ToDoubleBiFunction.
 */
public class Laboratorio4Main {

    public static void main(String[] args) {
        // Asegurar formato numérico consistente con separador de miles '.'
        Locale localeCo = Locale.of("es", "CO");

        System.out.println("============================================================");
        System.out.println("LABORATORIO 4 · Proyecto integrador: Motor de promociones");
        System.out.println("============================================================");

        // 1. Definición de las tres promociones solicitadas
        List<Promocion> promociones = List.of(
            new Promocion("Tecno 10 %", p -> p.categoria().equals("tecnología"), AjustePrecio.porcentaje(10)),
            new Promocion("Bono $50.000", p -> p.precio() >= 200_000, AjustePrecio.montoFijo(50_000)),
            new Promocion("Liquidación", p -> p.stock() > 100, AjustePrecio.porcentaje(30))
        );

        // 2. Opción neutra "Sin promoción" mediante Supplier
        Supplier<Promocion> sinPromocion = () ->
            new Promocion("Sin promoción", p -> true, AjustePrecio.sinAjuste());

        // 3. BiConsumer para formateo de salida en consola
        BiConsumer<Producto, Promocion> imprimir = (p, promo) ->
            System.out.printf(localeCo, "%-17s %-14s $%,11.0f → $%,11.0f%n",
                p.nombre(), promo.nombre(), p.precio(), promo.precioCon(p));

        // 4. ToDoubleBiFunction para calcular el ahorro unitario sin autoboxing
        ToDoubleBiFunction<Producto, Promocion> ahorro = (p, promo) ->
            p.precio() - promo.precioCon(p);

        System.out.println("\n--- SALIDA ESPERADA DE LA GUÍA ---");
        double ahorroTotal = 0.0;

        for (Producto p : Catalogo.muestra()) {
            if (!p.disponible()) {
                continue; // Cuaderno tiene stock 0, se descarta
            }

            // Seleccionar la promoción más ventajosa para el cliente (menor precio final)
            BinaryOperator<Promocion> masConveniente = BinaryOperator.minBy(
                Comparator.comparingDouble(promo -> promo.precioCon(p))
            );

            Promocion mejor = sinPromocion.get();
            for (Promocion promo : promociones) {
                mejor = masConveniente.apply(mejor, promo);
            }

            imprimir.accept(p, mejor);
            ahorroTotal += ahorro.applyAsDouble(p, mejor);
        }

        System.out.printf(localeCo, "Ahorro total para el cliente: $%,.0f%n", ahorroTotal);

        System.out.println("\n--- OBSERVACIÓN DE DISEÑO ---");
        System.out.println("El código que calcula precios y evalúa ahorros es completamente desacoplado:");
        System.out.println("No conoce ninguna promoción en concreto. Agregar nuevas campañas promocionales");
        System.out.println("equivale a agregar nuevos elementos a la lista de promociones, sin tocar el motor.");
    }
}
