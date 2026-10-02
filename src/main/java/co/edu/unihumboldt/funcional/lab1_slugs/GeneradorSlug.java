package co.edu.unihumboldt.funcional.lab1_slugs;

import java.text.Normalizer;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Laboratorio 1: Generador de slugs por composición.
 * Convierte títulos o nombres de productos en identificadores amigables para URL (slugs),
 * componiendo funciones puras pequeñas con {@link java.util.function.UnaryOperator}.
 */
public final class GeneradorSlug {

    private GeneradorSlug() {
        // Clase utilitaria
    }

    /** 1. Recortar espacios al inicio y final (referencia a método) */
    public static final UnaryOperator<String> RECORTAR = String::strip;

    /** 2. Convertir texto a minúsculas (referencia a método) */
    public static final UnaryOperator<String> MINUSCULAS = String::toLowerCase;

    /** 3. Quitar tildes y diacríticos usando descomposición canónica NFD */
    public static final UnaryOperator<String> SIN_TILDES =
        s -> Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");

    /** 4. Eliminar todo lo que no sea letra minúscula, dígito, espacio o guion */
    public static final UnaryOperator<String> SOLO_VALIDOS =
        s -> s.replaceAll("[^a-z0-9\\s-]", "");

    /** 5. Reemplazar secuencias de espacios o guiones continuos por un único guion */
    public static final UnaryOperator<String> GUIONES =
        s -> s.strip().replaceAll("[\\s-]+", "-");

    /**
     * Construye una tubería de transformación encadenando una lista de funciones con andThen,
     * partiendo de {@link Function#identity()} como elemento neutro.
     * Utiliza comodines PECS (? extends Function&lt;String, String&gt;) para máxima flexibilidad.
     *
     * @param pasos Lista ordenada de pasos de transformación a aplicar.
     * @return Función compuesta resultante.
     */
    public static Function<String, String> tuberia(List<? extends Function<String, String>> pasos) {
        Objects.requireNonNull(pasos, "La lista de pasos no puede ser nula");
        Function<String, String> resultado = Function.identity();
        for (Function<String, String> paso : pasos) {
            resultado = resultado.andThen(paso);
        }
        return resultado;
    }

    /**
     * Genera la tubería estándar con los 5 pasos en el orden canónico.
     *
     * @return Función que transforma cualquier texto en un slug válido.
     */
    public static Function<String, String> tuberiaEstandar() {
        return tuberia(List.of(RECORTAR, MINUSCULAS, SIN_TILDES, SOLO_VALIDOS, GUIONES));
    }

    /**
     * Genera un slug a partir de una cadena dada aplicando la tubería estándar.
     *
     * @param texto Cadena original.
     * @return Slug generado.
     */
    public static String generar(String texto) {
        if (texto == null) {
            return "";
        }
        return tuberiaEstandar().apply(texto);
    }
}
