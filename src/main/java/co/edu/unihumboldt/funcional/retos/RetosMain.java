package co.edu.unihumboldt.funcional.retos;

import co.edu.unihumboldt.funcional.lab2_validacion.Regla;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Ejecutable interactivo de demostración de los 4 RETOS PARA PROFUNDIZAR de la guía técnica (Pág. 33).
 */
public class RetosMain {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("RETOS PARA PROFUNDIZAR (Página 33)");
        System.out.println("============================================================");

        // RETO 1: TriFunction.curry()
        System.out.println("\n--- RETO 1: Currificación con TriFunction.curry() ---");
        TriFunction<String, Integer, Double, String> formatoFactura =
            (cliente, unidades, precioUnitario) ->
                String.format("Cliente: %s | Total (%d unid. a $%.2f): $%.2f",
                    cliente, unidades, precioUnitario, unidades * precioUnitario);

        // Currificación: transforma (A, B, C) -> R en A -> (B -> (C -> R))
        Function<String, Function<Integer, Function<Double, String>>> facturaCurry = formatoFactura.curry();

        // Aplicación parcial en tres etapas independientes
        Function<Integer, Function<Double, String>> facturaParaAna = facturaCurry.apply("Ana María");
        Function<Double, String> facturaTresItems = facturaParaAna.apply(3);
        String resultadoFinal = facturaTresItems.apply(12500.0);
        System.out.println("Resultado aplicando funciones currificadas paso a paso:");
        System.out.println("  " + resultadoFinal);

        // Descurrying
        TriFunction<String, Integer, Double, String> reconstruida = TriFunction.descurry(facturaCurry);
        System.out.println("Reconstrucción (descurry):");
        System.out.println("  " + reconstruida.aplicar("Carlos", 2, 45000.0));

        // RETO 2: Regla.o() y Regla.cuando()
        System.out.println("\n--- RETO 2: Métodos .o() y .cuando() en Regla<T> ---");
        Regla<Integer> esPar = Regla.exigir(n -> n % 2 == 0, "no es número par");
        Regla<Integer> esMultiploDeCinco = Regla.exigir(n -> n % 5 == 0, "no es múltiplo de 5");

        // Disyunción: Aceptable si es par O si es múltiplo de 5
        Regla<Integer> parOMultiplo5 = esPar.o(esMultiploDeCinco);

        System.out.println("Validar 4 (par, no mult 5): " + parOMultiplo5.validar(4));     // Válido []
        System.out.println("Validar 15 (impar, mult 5): " + parOMultiplo5.validar(15));   // Válido []
        System.out.println("Validar 7 (impar y no mult 5): " + parOMultiplo5.validar(7)); // Inválido con ambos errores

        // Condicional: Solo exigir que un número sea mayor a 100 CUANDO sea positivo
        Regla<Integer> mayorA100SiPositivo = Regla.<Integer>exigir(n -> n > 100, "debe superar 100")
            .cuando(n -> n > 0);
        System.out.println("Validar -5 (no aplica condición): " + mayorA100SiPositivo.validar(-5)); // Válido []
        System.out.println("Validar 40 (aplica pero incumple): " + mayorA100SiPositivo.validar(40)); // Inválido
        System.out.println("Validar 150 (aplica y cumple): " + mayorA100SiPositivo.validar(150));   // Válido []

        // RETO 3: Memoización segura para hilos y análisis de recursión
        System.out.println("\n--- RETO 3: Memoización concurrente con ConcurrentHashMap ---");
        AtomicInteger conteoCalculos = new AtomicInteger(0);
        Function<Integer, Long> computoPesado = n -> {
            conteoCalculos.incrementAndGet();
            return (long) n * n * n;
        };

        Function<Integer, Long> computoMemorizado = MemoizadorConcurrente.memorizar(computoPesado);
        System.out.println("Cálculo 1 (n=5): " + computoMemorizado.apply(5));
        System.out.println("Cálculo 2 (n=5 desde caché): " + computoMemorizado.apply(5));
        System.out.println("Cálculo 3 (n=5 desde caché): " + computoMemorizado.apply(5));
        System.out.println("Total evaluaciones reales en función: " + conteoCalculos.get() + " (esperado: 1)");

        System.out.println("\n[ANÁLISIS TEÓRICO RECURSIÓN CON ConcurrentHashMap]");
        System.out.println("¿Qué ocurre si la función es recursiva (ej: fibonacci)?");
        System.out.println("ConcurrentHashMap.computeIfAbsent bloquea el nodo/bin de la clave en evaluación.");
        System.out.println("Si la función se invoca recursivamente sobre la misma clave o sobre otra clave que");
        System.out.println("cae en el mismo bucket hash, se produce un DEADLOCK o una excepción");
        System.out.println("IllegalStateException: Recursive update.");
        System.out.println("Para recursión, la caché debe consultar con get() antes de compute/put.");

        // RETO 4: Reintentos con Supplier
        System.out.println("\n--- RETO 4: Reintentar.de(Supplier<T>, int intentos) ---");
        AtomicInteger intentosServicio = new AtomicInteger(0);

        String respuestaRed = Reintentar.de(() -> {
            int intentoActual = intentosServicio.incrementAndGet();
            System.out.println("  Intentando conectar con microservicio (intento #" + intentoActual + ")...");
            if (intentoActual < 3) {
                throw new RuntimeException("Error de red: Timeout 504");
            }
            return "Datos recibidos exitosamente desde API";
        }, 5, error -> System.out.println("  [Aviso] Intento falló con mensaje: " + error.getMessage()));

        System.out.println("Resultado de Reintentar: " + respuestaRed);
        System.out.println("Total intentos requeridos: " + intentosServicio.get());
    }
}
