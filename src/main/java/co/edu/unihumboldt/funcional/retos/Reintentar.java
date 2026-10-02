package co.edu.unihumboldt.funcional.retos;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Reto 4: Mecanismo de reintentos funcional (Retry Pattern) con {@link Supplier}.
 * Permite ejecutar operaciones con resiliencia ante fallos transitorios.
 */
public final class Reintentar {

    private Reintentar() {
        // Utilidad estática
    }

    /**
     * Ejecuta el proveedor suministrado, reintentando hasta un número máximo de veces
     * en caso de que lance alguna excepción.
     *
     * @param <T>       Tipo del valor generado por el proveedor.
     * @param proveedor Función abastecedora a ejecutar.
     * @param intentos  Cantidad máxima de intentos permitidos (debe ser mayor a 0).
     * @return El valor obtenido si alguno de los intentos tiene éxito.
     * @throws IllegalArgumentException Si intentos es menor o igual a 0.
     * @throws RuntimeException         Si se agotan todos los intentos sin éxito, conservando la causa original.
     */
    public static <T> T de(Supplier<T> proveedor, int intentos) {
        return de(proveedor, intentos, null);
    }

    /**
     * Versión avanzada que notifica cada intento fallido mediante un {@link Consumer}.
     *
     * @param <T>             Tipo del valor generado.
     * @param proveedor       Función abastecedora.
     * @param intentos        Cantidad máxima de intentos.
     * @param oyenteFallo     Consumidor que recibe la excepción producida en cada intento fallido (opcional).
     * @return El valor obtenido.
     */
    public static <T> T de(Supplier<T> proveedor, int intentos, Consumer<Exception> oyenteFallo) {
        Objects.requireNonNull(proveedor, "El proveedor no puede ser nulo");
        if (intentos <= 0) {
            throw new IllegalArgumentException("La cantidad de intentos debe ser mayor a 0 (recibido: " + intentos + ")");
        }

        Exception ultimoFallo = null;
        for (int i = 1; i <= intentos; i++) {
            try {
                return proveedor.get();
            } catch (Exception e) {
                ultimoFallo = e;
                if (oyenteFallo != null) {
                    oyenteFallo.accept(e);
                }
            }
        }

        throw new IllegalStateException("Operación fallida tras agotar " + intentos + " intentos consecutivos", ultimoFallo);
    }
}
