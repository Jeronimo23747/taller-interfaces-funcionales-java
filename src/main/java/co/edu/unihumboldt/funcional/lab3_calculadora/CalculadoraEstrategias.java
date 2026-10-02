package co.edu.unihumboldt.funcional.lab3_calculadora;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.DoubleBinaryOperator;

/**
 * Laboratorio 3: Calculadora con estrategias.
 * Reemplaza estructuras de control rígidas (como switch o cadenas de if-else)
 * por un catálogo dinámico y extensible de estrategias basadas en {@link DoubleBinaryOperator}.
 */
public final class CalculadoraEstrategias {

    private static final Map<String, DoubleBinaryOperator> OPERACIONES = new LinkedHashMap<>();

    static {
        // Inicialización de operaciones canónicas según la guía técnica
        OPERACIONES.put("+", Double::sum);
        OPERACIONES.put("-", (a, b) -> a - b);
        OPERACIONES.put("*", (a, b) -> a * b);
        OPERACIONES.put("/", (a, b) -> {
            if (b == 0.0) {
                throw new ArithmeticException("división por cero");
            }
            return a / b;
        });
        OPERACIONES.put("^", Math::pow);
        OPERACIONES.put("max", Math::max);
    }

    private CalculadoraEstrategias() {
        // Utilidad estática
    }

    /**
     * Evalúa una expresión aritmética en formato infijo simple (ej: "12 + 30").
     *
     * @param expresion Cadena con formato "a op b".
     * @return Resultado numérico de aplicar la operación.
     * @throws IllegalArgumentException Si el formato es incorrecto o el operador no está registrado.
     * @throws ArithmeticException      Si la operación genera una condición aritmética inválida (ej: div por cero).
     */
    public static double evaluar(String expresion) {
        Objects.requireNonNull(expresion, "La expresión no puede ser nula");
        String[] partes = expresion.strip().split("\\s+");
        if (partes.length != 3) {
            throw new IllegalArgumentException("formato esperado: a op b");
        }

        DoubleBinaryOperator operacion = OPERACIONES.get(partes[1]);
        if (operacion == null) {
            throw new IllegalArgumentException("operador desconocido: " + partes[1]);
        }

        double a = Double.parseDouble(partes[0]);
        double b = Double.parseDouble(partes[2]);

        return operacion.applyAsDouble(a, b);
    }

    /**
     * Permite extender la calculadora en tiempo de ejecución con nuevas operaciones,
     * cumpliendo a cabalidad con el Principio Abierto/Cerrado (OCP).
     *
     * @param simbolo  Símbolo o identificador del operador (ej: "%", "min").
     * @param operador Función binaria sobre valores primitivos double.
     */
    public static void registrarOperacion(String simbolo, DoubleBinaryOperator operador) {
        Objects.requireNonNull(simbolo, "El símbolo no puede ser nulo");
        Objects.requireNonNull(operador, "El operador no puede ser nulo");
        OPERACIONES.put(simbolo, operador);
    }

    /**
     * Retorna el conjunto inmutable de símbolos de operaciones disponibles.
     *
     * @return Conjunto con los nombres de operadores en el orden de registro.
     */
    public static Set<String> operadoresDisponibles() {
        return Collections.unmodifiableSet(OPERACIONES.keySet());
    }
}
