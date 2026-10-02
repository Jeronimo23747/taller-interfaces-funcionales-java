package co.edu.unihumboldt.funcional.retos;

import java.util.Objects;
import java.util.function.Function;

/**
 * Reto 1: Interfaz funcional genérica para funciones de tres argumentos.
 * Incluye composición fluida con comodines PECS y currificación (curry).
 *
 * @param <A> Tipo del primer parámetro.
 * @param <B> Tipo del segundo parámetro.
 * @param <C> Tipo del tercer parámetro.
 * @param <R> Tipo de retorno.
 */
@FunctionalInterface
public interface TriFunction<A, B, C, R> {

    /**
     * Aplica la función a los tres argumentos dados.
     *
     * @param a Primer argumento.
     * @param b Segundo argumento.
     * @param c Tercer argumento.
     * @return Resultado computado.
     */
    R aplicar(A a, B b, C c);

    /**
     * Compone esta función con una función posterior que transformará el resultado R en un valor V.
     * Implementa la regla PECS: ? super R (consume R), ? extends V (produce V).
     *
     * @param <V>     Tipo del valor resultante tras la transformación.
     * @param despues Función a ejecutar inmediatamente después con el resultado.
     * @return TriFunction compuesta.
     */
    default <V> TriFunction<A, B, C, V> luego(Function<? super R, ? extends V> despues) {
        Objects.requireNonNull(despues, "La función posterior no puede ser nula");
        return (a, b, c) -> despues.apply(aplicar(a, b, c));
    }

    /**
     * Reto 1: Currificación (Currying).
     * Transforma una función de 3 argumentos f(a, b, c) en una cadena de funciones unarias de un solo argumento:
     * a -> b -> c -> resultado.
     *
     * @return Función currificada de orden superior.
     */
    default Function<A, Function<B, Function<C, R>>> curry() {
        return a -> b -> c -> aplicar(a, b, c);
    }

    /**
     * Operación inversa a la currificación (Uncurrying).
     * Reconstruye una TriFunction a partir de una función currificada de tres niveles.
     *
     * @param <A>                 Tipo del primer argumento.
     * @param <B>                 Tipo del segundo argumento.
     * @param <C>                 Tipo del tercer argumento.
     * @param <R>                 Tipo del resultado.
     * @param funcionCurrificada  Función currificada de entrada.
     * @return TriFunction equivalente de 3 argumentos.
     */
    static <A, B, C, R> TriFunction<A, B, C, R> descurry(
        Function<A, Function<B, Function<C, R>>> funcionCurrificada
    ) {
        Objects.requireNonNull(funcionCurrificada, "La función currificada no puede ser nula");
        return (a, b, c) -> funcionCurrificada.apply(a).apply(b).apply(c);
    }
}
