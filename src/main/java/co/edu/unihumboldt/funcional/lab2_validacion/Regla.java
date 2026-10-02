package co.edu.unihumboldt.funcional.lab2_validacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Laboratorio 2: Motor de validación componible.
 * Interfaz funcional genérica para reglas de validación.
 * Permite componer reglas mediante conjunción (y), disyunción (o),
 * proyección sobre campos y ejecución condicional.
 *
 * @param <T> Tipo del dato a validar.
 */
@FunctionalInterface
public interface Regla<T> {

    /**
     * Valida el valor dado.
     *
     * @param valor Instancia a evaluar.
     * @return Lista vacía si el valor es válido; lista con mensajes de error si no.
     */
    List<String> validar(T valor);

    /**
     * Compone esta regla con otra mediante conjunción lógica (AND).
     * Ejecuta ambas reglas y combina sus listas de errores.
     * Utiliza el comodín PECS (? super T) para aceptar validadores de supertipos.
     *
     * @param otra Regla adicional a evaluar.
     * @return Regla compuesta que acumula los errores de ambas.
     */
    default Regla<T> y(Regla<? super T> otra) {
        Objects.requireNonNull(otra, "La regla a encadenar no puede ser nula");
        return valor -> {
            List<String> errores = new ArrayList<>(validar(valor));
            errores.addAll(otra.validar(valor));
            return errores;
        };
    }

    /**
     * Reto 2: Compone esta regla con otra mediante disyunción lógica (OR).
     * Si cualquiera de las dos reglas es válida (lista vacía), la validación tiene éxito.
     * Si ambas fallan, se reportan los errores combinados.
     *
     * @param otra Regla alternativa.
     * @return Regla compuesta disyuntiva.
     */
    default Regla<T> o(Regla<? super T> otra) {
        Objects.requireNonNull(otra, "La regla alternativa no puede ser nula");
        return valor -> {
            List<String> errores1 = validar(valor);
            if (errores1.isEmpty()) {
                return List.of();
            }
            List<String> errores2 = otra.validar(valor);
            if (errores2.isEmpty()) {
                return List.of();
            }
            List<String> combinados = new ArrayList<>(errores1);
            combinados.addAll(errores2);
            return combinados;
        };
    }

    /**
     * Reto 2: Condiciona la aplicación de la regla.
     * Aplica esta regla únicamente si el predicado condición se cumple para el valor.
     * Si la condición no se cumple, se considera válido (retorna lista vacía).
     *
     * @param condicion Predicado que determina si la regla debe aplicarse.
     * @return Regla condicionada.
     */
    default Regla<T> cuando(Predicate<? super T> condicion) {
        Objects.requireNonNull(condicion, "La condición no puede ser nula");
        return valor -> condicion.test(valor) ? validar(valor) : List.of();
    }

    /**
     * Fábrica de reglas basada en un {@link Predicate}.
     *
     * @param <T>       Tipo del valor a evaluar.
     * @param condicion Condición que debe cumplirse para ser válido.
     * @param mensaje   Mensaje de error si la condición no se cumple.
     * @return Regla que valida la condición dada.
     */
    static <T> Regla<T> exigir(Predicate<? super T> condicion, String mensaje) {
        Objects.requireNonNull(condicion, "La condición no puede ser nula");
        Objects.requireNonNull(mensaje, "El mensaje de error no puede ser nulo");
        return valor -> condicion.test(valor) ? List.of() : List.of(mensaje);
    }

    /**
     * Fábrica de reglas para proyectar una regla sobre un campo de un objeto complejo.
     *
     * @param <T>       Tipo del objeto contenedor.
     * @param <C>       Tipo del campo a extraer y validar.
     * @param extractor Función extractora que obtiene el campo a partir de T.
     * @param regla     Regla que se aplicará sobre el campo C.
     * @return Regla aplicable sobre T.
     */
    static <T, C> Regla<T> campo(Function<? super T, ? extends C> extractor, Regla<? super C> regla) {
        Objects.requireNonNull(extractor, "La función extractora no puede ser nula");
        Objects.requireNonNull(regla, "La regla del campo no puede ser nula");
        return valor -> regla.validar(extractor.apply(valor));
    }
}
