package co.edu.unihumboldt.funcional.lab3_calculadora;

import java.util.List;

/**
 * Ejecutable del Laboratorio 3: Calculadora con estrategias.
 * Demuestra:
 * 1. Evaluación de expresiones aritméticas usando un mapa de funciones DoubleBinaryOperator.
 * 2. Manejo controlado de errores sintácticos, operadores no registrados y división por cero.
 * 3. Reflexión profunda sobre OCP (Open-Closed Principle) comparando mapa de lambdas vs switch (Punto 5).
 */
public class Laboratorio3Main {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("LABORATORIO 3 · Calculadora con estrategias");
        System.out.println("============================================================");

        List<String> expresiones = List.of(
            "12 + 30",
            "2 ^ 10",
            "7 / 2",
            "9 max 4",
            "5 / 0",
            "3 % 2"
        );

        System.out.println("\n--- SALIDA ESPERADA DE LA GUÍA ---");
        for (String e : expresiones) {
            try {
                System.out.printf("%-8s = %s%n", e, CalculadoraEstrategias.evaluar(e));
            } catch (RuntimeException ex) {
                System.out.printf("%-8s → error: %s%n", e, ex.getMessage());
            }
        }
        System.out.println("Operadores disponibles: " + CalculadoraEstrategias.operadoresDisponibles());

        System.out.println("\n--- REFLEXIÓN TÉCNICA (PUNTO 5 DE LA GUÍA) ---");
        System.out.println("¿Qué tendrías que cambiar para agregar el operador '%'?");
        System.out.println("1. Con el Mapa de Estrategias:");
        System.out.println("   - Solo se invoca: CalculadoraEstrategias.registrarOperacion(\"%\", (a, b) -> a % b);");
        System.out.println("   - El método 'evaluar' NO se modifica en lo absoluto.");
        System.out.println("   - Se respeta al 100% el Principio Abierto/Cerrado (OCP: Abierto a extensión, cerrado a modificación).");
        System.out.println("   - Permite agregar operadores de forma dinámica en tiempo de ejecución o desde módulos externos.");
        System.out.println("\n2. Con una estructura basada en 'switch':");
        System.out.println("   - Habría que abrir la clase y editar el código fuente del método evaluar para agregar un 'case \"%\": ...'.");
        System.out.println("   - Viola OCP, incrementa el riesgo de introducir regresiones y requiere recompilar el núcleo.");
        System.out.println("   - No permite extensión dinámica por parte de consumidores de la biblioteca.");

        System.out.println("\n--- DEMOSTRACIÓN DE EXTENSIÓN SIN MODIFICAR EL CÓDIGO NÚCLEO ---");
        CalculadoraEstrategias.registrarOperacion("%", (a, b) -> a % b);
        System.out.println("Registrando '%' dinámicamente...");
        System.out.println("Operadores ahora: " + CalculadoraEstrategias.operadoresDisponibles());
        System.out.printf("%-8s = %s%n", "3 % 2", CalculadoraEstrategias.evaluar("3 % 2"));
    }
}
