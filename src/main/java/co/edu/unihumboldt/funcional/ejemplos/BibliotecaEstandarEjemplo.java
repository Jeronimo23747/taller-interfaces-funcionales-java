package co.edu.unihumboldt.funcional.ejemplos;

import co.edu.unihumboldt.funcional.modelo.Catalogo;
import co.edu.unihumboldt.funcional.modelo.Producto;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntUnaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.UnaryOperator;

/**
 * Demostración didáctica de la Parte II de la guía: La biblioteca estándar (java.util.function).
 * Abarca:
 * - Listado 8: Las 9 interfaces centrales (Function, BiFunction, UnaryOperator, BinaryOperator,
 *   Predicate, BiPredicate, Consumer, BiConsumer, Supplier).
 * - Listado 10: Composición de funciones (andThen, compose, identity).
 * - Listado 11: Lógica booleana de predicados (and, or, negate, not, isEqual).
 * - Listado 12: Encadenamiento de efectos, selección de extremos y comparadores.
 * - Listado 13: Especializaciones primitivas para cero autoboxing.
 */
public class BibliotecaEstandarEjemplo {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("PARTE II: LA BIBLIOTECA ESTÁNDAR (java.util.function)");
        System.out.println("============================================================");

        List<Producto> catalogo = Catalogo.muestra();
        Producto mouse = catalogo.get(1);

        // --- 1. Listado 8: Las 9 interfaces centrales ---
        System.out.println("\n--- 1. Las 9 interfaces centrales ---");
        Function<Producto, String> etiqueta = p -> p.nombre() + " (" + p.categoria() + ")";
        BiFunction<Producto, Integer, Double> subtotal = (p, cant) -> p.precio() * cant;
        UnaryOperator<String> mayus = String::toUpperCase;
        BinaryOperator<Integer> sumar = Integer::sum;
        Predicate<Producto> barato = p -> p.precio() < 100_000;
        BiPredicate<Producto, String> esDe = (p, cat) -> p.categoria().equals(cat);
        Consumer<String> imprimir = texto -> System.out.println(" · " + texto);
        BiConsumer<String, Double> linea = (n, v) -> System.out.printf(" · %s: $%,.0f%n", n, v);
        Supplier<Producto> porDefecto = () -> new Producto("Genérico", "otros", 0, 0);

        System.out.println("Function:       " + etiqueta.apply(mouse));
        System.out.println("UnaryOperator:  " + mayus.apply("lambda"));
        System.out.println("BinaryOperator: " + sumar.apply(20, 22));
        System.out.println("Predicate:      " + barato.test(mouse));
        System.out.println("BiPredicate:    " + esDe.test(mouse, "tecnología"));
        System.out.println("Supplier:       " + porDefecto.get().nombre());
        System.out.println("Consumer y BiConsumer:");
        imprimir.accept("sin valor de retorno");
        linea.accept("3 × Mouse", subtotal.apply(mouse, 3));

        // --- 2. Listado 10: Composición (andThen, compose, identity) ---
        System.out.println("\n--- 2. Composición de funciones (andThen vs compose) ---");
        Function<Integer, Integer> bono = precio -> precio - 10_000;
        Function<Integer, Integer> iva = precio -> precio * 119 / 100;

        System.out.println("IVA y luego bono (andThen): " + iva.andThen(bono).apply(100_000)); // (100k * 1.19) - 10k = 109k
        System.out.println("Bono y luego IVA (compose): " + iva.compose(bono).apply(100_000)); // (100k - 10k) * 1.19 = 107.1k
        System.out.println("Identidad:                  " + Function.<Integer>identity().apply(100_000));

        // Tubería dinámica
        List<Function<Integer, Integer>> pasos = List.of(bono, iva, bono);
        Function<Integer, Integer> tuberia = Function.identity();
        for (Function<Integer, Integer> paso : pasos) {
            tuberia = tuberia.andThen(paso);
        }
        System.out.println("Tubería de 3 pasos (bono -> iva -> bono): " + tuberia.apply(100_000));

        // --- 3. Listado 11: Predicate booleano y cortocircuito ---
        System.out.println("\n--- 3. Composición lógica con Predicate ---");
        Predicate<Producto> disponible = Producto::disponible;
        Predicate<Producto> tecnologia = p -> p.categoria().equals("tecnología");
        Predicate<Producto> caro = p -> p.precio() >= 500_000;

        List<String> nomAnd = catalogo.stream().filter(tecnologia.and(disponible)).map(Producto::nombre).toList();
        List<String> nomOr = catalogo.stream().filter(caro.or(disponible.negate())).map(Producto::nombre).toList();
        List<String> nomNot = catalogo.stream().filter(Predicate.not(tecnologia)).map(Producto::nombre).toList();

        System.out.println("and (tecnología Y disponible): " + nomAnd);
        System.out.println("or  (caro O no disponible):     " + nomOr);
        System.out.println("not (NO tecnología):            " + nomNot);

        Predicate<String> esMouse = Predicate.isEqual("Mouse");
        System.out.println("isEqual(\"Mouse\"): 'Mouse'=" + esMouse.test("Mouse") + ", 'mouse'=" + esMouse.test("mouse"));

        // --- 4. Listado 12: Consumer, BinaryOperator y Comparator ---
        System.out.println("\n--- 4. Consumer, BinaryOperator y Comparator ---");
        List<String> bitacora = new ArrayList<>();
        Consumer<Producto> registrar = p -> bitacora.add(p.nombre());
        Consumer<Producto> alertar = p -> {
            if (p.stock() < 5) {
                System.out.println("  Stock bajo: " + p.nombre());
            }
        };

        System.out.println("Alertas de inventario con consumer encadenado:");
        catalogo.forEach(registrar.andThen(alertar));
        System.out.println("Registrados en bitácora: " + bitacora.size());

        Comparator<Producto> porPrecio = Comparator.comparingDouble(Producto::precio);
        BinaryOperator<Producto> masBarato = BinaryOperator.minBy(porPrecio);
        BinaryOperator<Producto> masCaro = BinaryOperator.maxBy(porPrecio);
        Producto min = catalogo.get(0), max = catalogo.get(0);
        for (Producto p : catalogo) {
            min = masBarato.apply(min, p);
            max = masCaro.apply(max, p);
        }
        System.out.println("Extremos en catálogo: " + min.nombre() + " / " + max.nombre());

        // Orden multinivel
        Comparator<Producto> orden = Comparator.comparing(Producto::categoria)
            .thenComparing(Producto::precio, Comparator.reverseOrder());
        List<Producto> ordenados = new ArrayList<>(catalogo);
        ordenados.sort(orden);
        System.out.println("Ordenados por categoría ascendente y precio descendente:");
        ordenados.forEach(p -> System.out.printf("  %-11s %-16s $%,10.0f%n", p.categoria(), p.nombre(), p.precio()));

        // --- 5. Listado 13: Especializaciones primitivas ---
        System.out.println("\n--- 5. Especializaciones primitivas (Cero sobrecosto de boxing) ---");
        ToDoubleFunction<Producto> valorEnInventario = p -> p.precio() * p.stock();
        IntPredicate esPar = n -> n % 2 == 0;
        IntUnaryOperator cuadrado = n -> n * n;
        IntBinaryOperator maximo = Math::max;

        double totalInventario = 0;
        for (Producto p : catalogo) {
            totalInventario += valorEnInventario.applyAsDouble(p);
        }
        System.out.printf("Valor total del inventario: $%,.0f%n", totalInventario);
        System.out.println("Operaciones primitivas: esPar(42)=" + esPar.test(42)
            + ", cuadrado(12)=" + cuadrado.applyAsInt(12)
            + ", maximo(7,3)=" + maximo.applyAsInt(7, 3));
    }
}
