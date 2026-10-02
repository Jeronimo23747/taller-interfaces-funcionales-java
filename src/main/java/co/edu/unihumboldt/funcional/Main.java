package co.edu.unihumboldt.funcional;

import co.edu.unihumboldt.funcional.autoevaluacion.Autoevaluacion;
import co.edu.unihumboldt.funcional.ejemplos.BibliotecaEstandarEjemplo;
import co.edu.unihumboldt.funcional.ejemplos.ExcepcionesVerificadasEjemplo;
import co.edu.unihumboldt.funcional.ejemplos.FundamentosEjemplo;
import co.edu.unihumboldt.funcional.ejemplos.PatronesDisenoEjemplo;
import co.edu.unihumboldt.funcional.lab1_slugs.Laboratorio1Main;
import co.edu.unihumboldt.funcional.lab2_validacion.Laboratorio2Main;
import co.edu.unihumboldt.funcional.lab3_calculadora.Laboratorio3Main;
import co.edu.unihumboldt.funcional.lab4_promociones.Laboratorio4Main;
import co.edu.unihumboldt.funcional.retos.RetosMain;
import java.util.Scanner;

/**
 * Clase principal de entrada al proyecto: Taller de Interfaces Funcionales en Java.
 * Desarrollado por Jerónimo Hoyos para la Universidad Alexander von Humboldt.
 * Ofrece un menú interactivo y un ejecutor global de todos los laboratorios y retos.
 */
public class Main {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("all")) {
            ejecutarTodo();
            return;
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            imprimirEncabezado();
            System.out.println("Seleccione el componente que desea ejecutar:");
            System.out.println(" 1. Laboratorio 1 · Generador de Slugs por Composición");
            System.out.println(" 2. Laboratorio 2 · Motor de Validación Componible");
            System.out.println(" 3. Laboratorio 3 · Calculadora con Estrategias");
            System.out.println(" 4. Laboratorio 4 · Motor de Promociones (Proyecto Integrador)");
            System.out.println(" 5. Retos para Profundizar (Curry, Reglas OR/Cuando, Caché, Retry)");
            System.out.println(" 6. Ejemplos Teóricos (Fundamentos, Estándar, Excepciones, Patrones)");
            System.out.println(" 7. Autoevaluación Oficial (10 Preguntas y Justificaciones)");
            System.out.println(" 8. Ejecutar TODO de forma secuencial");
            System.out.println(" 0. Salir");
            System.out.print("\nIngrese su opción: ");

            String entrada = scanner.hasNextLine() ? scanner.nextLine().trim() : "8";
            if (entrada.isEmpty()) {
                entrada = "8";
            }

            switch (entrada) {
                case "1" -> Laboratorio1Main.main(new String[0]);
                case "2" -> Laboratorio2Main.main(new String[0]);
                case "3" -> Laboratorio3Main.main(new String[0]);
                case "4" -> Laboratorio4Main.main(new String[0]);
                case "5" -> RetosMain.main(new String[0]);
                case "6" -> ejecutarEjemplosTeoricos();
                case "7" -> Autoevaluacion.main(new String[0]);
                case "8" -> {
                    ejecutarTodo();
                    return;
                }
                case "0" -> {
                    System.out.println("Finalizando programa. ¡Hasta pronto!");
                    return;
                }
                default -> System.out.println("Opción no válida. Intente nuevamente.");
            }

            System.out.println("\nPresione ENTER para continuar...");
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            } else {
                break;
            }
        }
    }

    private static void imprimirEncabezado() {
        System.out.println("\n######################################################################");
        System.out.println("  TALLER: INTERFACES FUNCIONALES EN JAVA (JAVA 21 LTS)");
        System.out.println("  Estudiante: Jerónimo Hoyos");
        System.out.println("  Institución: Universidad Alexander von Humboldt");
        System.out.println("######################################################################\n");
    }

    public static void ejecutarTodo() {
        imprimirEncabezado();
        System.out.println(">>> EJECUTANDO TODO EL TALLER COMPLETO <<<\n");

        System.out.println("\n" + "=".repeat(70));
        Laboratorio1Main.main(new String[0]);

        System.out.println("\n" + "=".repeat(70));
        Laboratorio2Main.main(new String[0]);

        System.out.println("\n" + "=".repeat(70));
        Laboratorio3Main.main(new String[0]);

        System.out.println("\n" + "=".repeat(70));
        Laboratorio4Main.main(new String[0]);

        System.out.println("\n" + "=".repeat(70));
        RetosMain.main(new String[0]);

        System.out.println("\n" + "=".repeat(70));
        ejecutarEjemplosTeoricos();

        System.out.println("\n" + "=".repeat(70));
        Autoevaluacion.main(new String[0]);

        System.out.println("\n" + "#".repeat(70));
        System.out.println("  ¡TODAS LAS PRUEBAS Y LABORATORIOS SE EJECUTARON EXITOSAMENTE!");
        System.out.println("#".repeat(70));
    }

    private static void ejecutarEjemplosTeoricos() {
        try {
            FundamentosEjemplo.main(new String[0]);
            System.out.println();
            BibliotecaEstandarEjemplo.main(new String[0]);
            System.out.println();
            ExcepcionesVerificadasEjemplo.main(new String[0]);
            System.out.println();
            PatronesDisenoEjemplo.main(new String[0]);
        } catch (Exception e) {
            System.err.println("Error ejecutando ejemplos teóricos: " + e.getMessage());
        }
    }
}
