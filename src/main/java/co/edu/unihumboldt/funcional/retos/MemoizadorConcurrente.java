package co.edu.unihumboldt.funcional.retos;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * Reto 3: Memoización segura para concurrencia (Thread-Safe Memoization) con {@link ConcurrentHashMap}.
 *
 * <p><b>¿Qué ocurre si la función es recursiva?</b></p>
 * <p>
 * En Java, {@link ConcurrentHashMap#computeIfAbsent(Object, Function)} bloquea internamente
 * el segmento/bin correspondiente a la clave evaluada mientras se ejecuta la función de mapeo.
 * Si dicha función es recursiva:
 * </p>
 * <ol>
 *   <li><b>Recursión sobre la misma clave:</b> Se produce un bloqueo mutuo (deadlock) de hilo
 *       o una excepción en tiempo de ejecución ({@link IllegalStateException}: Recursive update).</li>
 *   <li><b>Recursión sobre claves distintas que colisionan en el mismo bin:</b> Si dos claves
 *       diferentes mapean a la misma cubeta hash interna, la llamada recursiva intentará
 *       adquirir el cerrojo del mismo bin que su llamada padre ya tiene bloqueado, generando un deadlock silencioso.</li>
 *   <li><b>Múltiples hilos calculando subárboles solapados:</b> Dos hilos pueden bloquear cubetas
 *       cruzadas y quedar en deadlock permanente (ABBA deadlock).</li>
 * </ol>
 * <p>
 * <b>Solución recomendada para funciones recursivas:</b> Separar la consulta de la inserción
 * (usando {@code get} explícito primero y {@code putIfAbsent} o sincronización a nivel de estructura externa),
 * o utilizar memoizadores específicos con referencias diferidas (trampolines o memoización explicita en vector/tabla).
 * </p>
 */
public final class MemoizadorConcurrente {

    private MemoizadorConcurrente() {
        // Utilidad estática
    }

    /**
     * Envuelve una función arbitraria en un envoltorio con caché segura para múltiples hilos.
     *
     * @param <T>     Tipo del argumento de entrada.
     * @param <R>     Tipo del valor calculado.
     * @param funcion Función pura a memoizar.
     * @return Función equivalente que reutiliza resultados previamente computados.
     */
    public static <T, R> Function<T, R> memorizar(Function<T, R> funcion) {
        Objects.requireNonNull(funcion, "La función no puede ser nula");
        ConcurrentMap<T, R> cache = new ConcurrentHashMap<>();
        return entrada -> cache.computeIfAbsent(entrada, funcion);
    }

    /**
     * Versión que permite inspeccionar la cantidad de elementos en caché (útil para auditoría y pruebas).
     *
     * @param <T>     Tipo de entrada.
     * @param <R>     Tipo de retorno.
     * @param funcion Función a memoizar.
     * @param cache   Mapa concurrente donde se almacenarán los resultados.
     * @return Función memoizada vinculada a la caché provista.
     */
    public static <T, R> Function<T, R> memorizarConCache(Function<T, R> funcion, ConcurrentMap<T, R> cache) {
        Objects.requireNonNull(funcion, "La función no puede ser nula");
        Objects.requireNonNull(cache, "La caché no puede ser nula");
        return entrada -> cache.computeIfAbsent(entrada, funcion);
    }
}
