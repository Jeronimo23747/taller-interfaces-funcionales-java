package co.edu.unihumboldt.funcional.ejemplos;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Demostración didáctica de la Sección 10 de la guía: Excepciones verificadas y lambdas.
 * Las interfaces estándar de java.util.function no declaran 'throws'.
 * Aquí se implementa la solución profesional utilizada por bibliotecas como Vavr y Apache Commons:
 * Una interfaz funcional propia con tipo de excepción genérico y un adaptador que preserva la causa.
 */
public class ExcepcionesVerificadasEjemplo {

    /**
     * Interfaz funcional que admite el lanzamiento de excepciones verificadas.
     *
     * @param <T> Tipo de argumento de entrada.
     * @param <R> Tipo de retorno.
     * @param <E> Tipo de excepción verificada declarada.
     */
    @FunctionalInterface
    public interface FuncionConExcepcion<T, R, E extends Exception> {
        R aplicar(T t) throws E;
    }

    /**
     * Adaptador que transforma una {@link FuncionConExcepcion} en una {@link Function} estándar.
     * Buenas prácticas de diseño:
     * 1. Las excepciones no verificadas (RuntimeException) pasan intactas.
     * 2. Las excepciones verificadas se envuelven conservando SIEMPRE la causa original.
     * 3. Nunca se oculta el error con 'return null'.
     *
     * @param <T>     Tipo de entrada.
     * @param <R>     Tipo de retorno.
     * @param funcion Función susceptible a fallos verificados.
     * @return Function estándar de Java.
     */
    public static <T, R> Function<T, R> sinVerificar(FuncionConExcepcion<T, R, ?> funcion) {
        Objects.requireNonNull(funcion, "La función no puede ser nula");
        return entrada -> {
            try {
                return funcion.aplicar(entrada);
            } catch (RuntimeException e) {
                throw e; // Pasan sin alteración
            } catch (Exception e) {
                // Conserva la causa (e) y detalla la entrada infractora
                throw new IllegalArgumentException("'" + entrada + "' → " + e.getMessage(), e);
            }
        };
    }

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("SECCIÓN 10: MANEJO PROFESIONAL DE EXCEPCIONES VERIFICADAS");
        System.out.println("============================================================");

        // URI::new lanza URISyntaxException (checked exception).
        // Al adaptarla con 'sinVerificar', se convierte en una Function<String, URI> legítima.
        Function<String, URI> aUri = sinVerificar(URI::new);

        List<String> urisPrueba = List.of(
            "https://tienda.co/productos?id=42",
            "https://tienda .co" // URI inválida con espacio en la autoridad
        );

        for (String texto : urisPrueba) {
            try {
                URI uri = aUri.apply(texto);
                System.out.println("OK   host = " + uri.getHost());
            } catch (IllegalArgumentException e) {
                System.out.println("ERROR: " + e.getMessage());
                System.out.println("  Causa subyacente conservada: " + e.getCause().getClass().getSimpleName());
            }
        }
    }
}
