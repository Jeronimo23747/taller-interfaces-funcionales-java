package co.edu.unihumboldt.funcional.ejemplos;

import co.edu.unihumboldt.funcional.modelo.Catalogo;
import co.edu.unihumboldt.funcional.modelo.Producto;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Demostración didáctica de las Secciones 9, 11 y 12 de la guía:
 * Diseño y práctica profesional, funciones de orden superior, patrones de diseño clásicos
 * y funcionamiento interno (invokedynamic, LambdaMetafactory).
 */
public class PatronesDisenoEjemplo {

    // --- Listado 14: Interfaz de dominio TarifaEnvio ---
    @FunctionalInterface
    public interface TarifaEnvio {
        double calcular(double pesoKg, int distanciaKm);

        default TarifaEnvio conRecargo(double porcentaje) {
            return (peso, km) -> calcular(peso, km) * (1 + porcentaje / 100.0);
        }

        default TarifaEnvio conMinimo(double minimo) {
            return (peso, km) -> Math.max(minimo, calcular(peso, km));
        }

        static TarifaEnvio porPesoYDistancia(double porKg, double porKm) {
            return (peso, km) -> peso * porKg + km * porKm;
        }
    }

    // Listado 16: Fábricas de funciones
    public static Predicate<Producto> precioEntre(double minimo, double maximo) {
        return p -> p.precio() >= minimo && p.precio() <= maximo;
    }

    public static DoubleUnaryOperator descuento(double porcentaje) {
        return precio -> precio * (1 - porcentaje / 100.0);
    }

    // Listado 17: Evaluación diferida
    private static int evaluacionesCostosas = 0;

    private static String reporteCostoso() {
        evaluacionesCostosas++;
        return "inventario de " + Catalogo.muestra().size() + " productos";
    }

    private static void registrar(boolean activo, String mensaje) {
        if (activo) {
            System.out.println("[LOG] " + mensaje);
        }
    }

    private static void registrarDiferido(boolean activo, Supplier<String> mensaje) {
        if (activo) {
            System.out.println("[LOG] " + mensaje.get()); // Solo se evalúa si activo es true
        }
    }

    // Listado 18: Memoización
    public static <T, R> Function<T, R> memorizar(Function<T, R> funcion) {
        Map<T, R> cache = new HashMap<>();
        return entrada -> cache.computeIfAbsent(entrada, funcion);
    }

    private static int calculosCiudadReales = 0;

    private static long tarifaPorCiudad(String ciudad) {
        calculosCiudadReales++;
        return ciudad.length() * 1_000L;
    }

    public static void main(String[] args) {
        Locale localeCo = Locale.of("es", "CO");

        System.out.println("============================================================");
        System.out.println("SECCIÓN 9, 11 Y 12: PATRONES DE DISEÑO Y ARQUITECTURA");
        System.out.println("============================================================");

        // 1. Listado 14: Dominio propio componible (TarifaEnvio)
        System.out.println("\n--- 1. Interfaz de dominio componible (TarifaEnvio) ---");
        TarifaEnvio estandar = TarifaEnvio.porPesoYDistancia(2_000, 150);
        TarifaEnvio urgente = estandar.conRecargo(50).conMinimo(15_000);

        System.out.println("  kg   km    estándar     urgente");
        double[][] envios = { {1.5, 10}, {0.2, 5}, {8.0, 120} };
        for (double[] e : envios) {
            System.out.printf(localeCo, "%5.1f %5.0f $%,9.0f $%,9.0f%n",
                e[0], e[1],
                estandar.calcular(e[0], (int) e[1]),
                urgente.calcular(e[0], (int) e[1])
            );
        }

        // 2. Listado 16: Fábricas y currificación
        System.out.println("\n--- 2. Fábricas de funciones y currificación ---");
        List<String> enRango = new ArrayList<>();
        for (Producto p : Catalogo.muestra()) {
            if (precioEntre(50_000, 500_000).test(p)) {
                enRango.add(p.nombre());
            }
        }
        System.out.println("Productos entre $50.000 y $500.000: " + enRango);

        DoubleFunction<DoubleUnaryOperator> descuentoCurry = pct -> precio -> precio * (1 - pct / 100.0);
        DoubleUnaryOperator blackFriday = descuentoCurry.apply(40);
        System.out.printf(localeCo, "Descuento 15%%: $%,.0f | Black Friday 40%%: $%,.0f%n",
            descuento(15).applyAsDouble(200_000), blackFriday.applyAsDouble(200_000));

        // 3. Listado 17: Evaluación diferida con Supplier
        System.out.println("\n--- 3. Evaluación diferida (Lazy Evaluation) con Supplier ---");
        registrar(false, reporteCostoso());                  // Se evalúa inútilmente y se descarta
        registrarDiferido(false, PatronesDisenoEjemplo::reporteCostoso); // NO se evalúa
        registrarDiferido(true, PatronesDisenoEjemplo::reporteCostoso);  // Se evalúa porque se necesita
        System.out.println("Evaluaciones reales ejecutadas: " + evaluacionesCostosas + " (esperado: 2)");

        // 4. Listado 18: Memoización con Map
        System.out.println("\n--- 4. Memoización (Caching de funciones puras) ---");
        Function<String, Long> tarifaMemoizada = memorizar(PatronesDisenoEjemplo::tarifaPorCiudad);
        List<String> consultas = List.of("Bogotá", "Cali", "Bogotá", "Medellín", "Cali", "Bogotá");
        for (String c : consultas) {
            System.out.print(c + "=" + tarifaMemoizada.apply(c) + " ");
        }
        System.out.println();
        System.out.println("Cálculos reales efectuados: " + calculosCiudadReales + " de " + consultas.size() + " consultas");

        // 5. Listado 19: Patrones Comando y Observador
        System.out.println("\n--- 5. Patrones Clásicos: Observador y Comando funcionales ---");
        List<Consumer<String>> suscriptores = new ArrayList<>(); // Observadores
        suscriptores.add(ev -> System.out.println("  [correo] " + ev));
        suscriptores.add(ev -> System.out.println("  [app]    " + ev));
        Consumer<String> publicar = ev -> suscriptores.forEach(s -> s.accept(ev));

        List<Runnable> colaComandos = new ArrayList<>(); // Comandos
        colaComandos.add(() -> publicar.accept("Pedido 101 recibido"));
        colaComandos.add(() -> publicar.accept("Pedido 101 despachado"));
        System.out.println("Tareas encoladas: " + colaComandos.size() + " (aún no se ejecutan)");
        colaComandos.forEach(Runnable::run);

        // 6. Listado 20: Inspección interna de métodos sintéticos de javac
        System.out.println("\n--- 6. Bajo el capó: Métodos sintéticos y LambdaMetafactory ---");
        Supplier<String> saludo = () -> "hola";
        Function<Integer, Integer> doble = x -> x * 2;
        Supplier<String> vacio = String::new;

        List<String> sinteticos = new ArrayList<>();
        for (Method m : PatronesDisenoEjemplo.class.getDeclaredMethods()) {
            if (m.isSynthetic()) {
                sinteticos.add(m.getName());
            }
        }
        Collections.sort(sinteticos);
        System.out.println("Métodos sintéticos en la clase: " + sinteticos);
        System.out.println("¿Es clase anónima la lambda? " + saludo.getClass().isAnonymousClass());
        System.out.println("Evaluación: " + saludo.get() + " " + doble.apply(21) + " [" + vacio.get() + "]");
    }
}
