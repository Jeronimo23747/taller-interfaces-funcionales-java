package co.edu.unihumboldt.funcional.lab1_slugs;

import java.util.List;
import java.util.function.Function;

/**
 * Ejecutable del Laboratorio 1: Generador de slugs por composición.
 * Demuestra:
 * 1. Salida idéntica a la guía técnica con los casos de prueba requeridos.
 * 2. Análisis del impacto al alterar el orden de los pasos (Punto 4 de la guía).
 */
public class Laboratorio1Main {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("LABORATORIO 1 · Generador de slugs por composición");
        System.out.println("============================================================");

        Function<String, String> slug = GeneradorSlug.tuberiaEstandar();

        List<String> casosPrueba = List.of(
            " Silla Ergonómica ",
            "Audífonos Bluetooth 5.3",
            "¡Oferta! Portátil i7 -- 16GB"
        );

        System.out.println("\n--- SALIDA ESPERADA DE LA GUÍA ---");
        for (String nombre : casosPrueba) {
            System.out.printf("%-32s → %s%n", "\"" + nombre + "\"", slug.apply(nombre));
        }

        System.out.println("\n--- ANÁLISIS DE REORDENAMIENTO DE PASOS (PUNTO 4) ---");
        // Experimento: Cambiar el orden de SOLO_VALIDOS y SIN_TILDES
        // Si ejecutamos SOLO_VALIDOS antes de SIN_TILDES:
        // Las letras acentuadas (como 'ó', 'á') aún tienen su tilde acoplada y no coinciden con [a-z0-9\s-],
        // por lo que son eliminadas por completo en lugar de normalizarse a letras latinas ('o', 'a').
        Function<String, String> tuberiaAlterada = GeneradorSlug.tuberia(List.of(
            GeneradorSlug.RECORTAR,
            GeneradorSlug.MINUSCULAS,
            GeneradorSlug.SOLO_VALIDOS, // Se ejecuta ANTES de quitar tildes
            GeneradorSlug.SIN_TILDES,
            GeneradorSlug.GUIONES
        ));

        System.out.println("Comparación con tubería alterada (SOLO_VALIDOS antes de SIN_TILDES):");
        for (String nombre : casosPrueba) {
            String original = slug.apply(nombre);
            String alterado = tuberiaAlterada.apply(nombre);
            System.out.printf("Entrada:  \"%s\"%n", nombre);
            System.out.printf("  Orden correcto:  %s%n", original);
            System.out.printf("  Orden alterado:  %s (¡Pérdida de caracteres acentuados!)%n%n", alterado);
        }

        System.out.println("Conclusión: Las operaciones no son conmutativas (f.andThen(g) != g.andThen(f)).");
        System.out.println("Normalizar tildes primero es indispensable para preservar las vocales base.");
    }
}
