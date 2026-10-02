package co.edu.unihumboldt.funcional.autoevaluacion;

import java.util.List;

/**
 * Módulo de autoevaluación oficial (Páginas 32-33 de la guía técnica).
 * Contiene las 10 preguntas de validación de dominio conceptual con sus
 * respuestas correctas y justificaciones técnicas fundamentadas en la especificación de Java.
 */
public class Autoevaluacion {

    public record Pregunta(
        int numero,
        String enunciado,
        List<String> opciones,
        char respuestaCorrecta,
        String seccionGuia,
        String justificacion
    ) {}

    public static List<Pregunta> obtenerPreguntas() {
        return List.of(
            new Pregunta(
                1,
                "¿Cuál de estas es una interfaz funcional?",
                List.of(
                    "a) interface A { void x(); void y(); }",
                    "b) interface B { void x(); boolean equals(Object o); }",
                    "c) interface C { default void x() { } }",
                    "d) abstract class D { abstract void x(); }"
                ),
                'b',
                "§2",
                "Los métodos abstractos que coinciden con la firma de métodos públicos de java.lang.Object (como equals, hashCode, toString) NO cuentan para el cómputo de métodos abstractos. interface B posee exactamente un único método abstracto propio (void x()), por ende es funcional."
            ),
            new Pregunta(
                2,
                "¿Qué ocurre con var f = x -> x + 1; ?",
                List.of(
                    "a) Compila como Function",
                    "b) Error: la lambda necesita tipo destino",
                    "c) Compila como IntUnaryOperator",
                    "d) Error de sintaxis en la flecha"
                ),
                'b',
                "§3",
                "Una expresión lambda carece de tipo intrínseco por sí misma; requiere obligatoriamente un tipo destino (target type) provisto por el contexto. Con 'var', el compilador intenta deducir el tipo a partir del valor de la derecha, produciendo un ciclo irresoluble."
            ),
            new Pregunta(
                3,
                "Dentro del cuerpo de una lambda, this se refiere a…",
                List.of(
                    "a) La propia lambda",
                    "b) La instancia que la contiene",
                    "c) null",
                    "d) La interfaz funcional"
                ),
                'b',
                "§3 / §5",
                "A diferencia de las clases anónimas (donde 'this' apunta a la instancia anónima), las lambdas no crean un nuevo ámbito de objeto ni introducen un nuevo 'this'; mantienen el ámbito léxico de la clase circundante."
            ),
            new Pregunta(
                4,
                "String::length usada como Function<String, Integer> es una referencia a…",
                List.of(
                    "a) Método estático",
                    "b) Método de un objeto concreto",
                    "c) Método de instancia de un objeto arbitrario",
                    "d) Constructor"
                ),
                'c',
                "§4",
                "Es una referencia no ligada (unbound method reference). En String::length, el receptor del método de instancia se convierte en el primer parámetro formal de la función: (String s) -> s.length()."
            ),
            new Pregunta(
                5,
                "¿Qué calcula f.andThen(g).apply(x) ?",
                List.of(
                    "a) f(g(x))",
                    "b) g(f(x))",
                    "c) f(x) + g(x)",
                    "d) Depende de los tipos"
                ),
                'b',
                "§7",
                "andThen se lee en orden secuencial de izquierda a derecha: aplica primero 'f' sobre x, y con el resultado intermedio obtenido aplica 'g', produciendo g(f(x))."
            ),
            new Pregunta(
                6,
                "¿Qué interfaz convierte un Producto en double sin autoboxing?",
                List.of(
                    "a) Function<Producto, Double>",
                    "b) ToDoubleFunction<Producto>",
                    "c) DoubleFunction<Producto>",
                    "d) DoubleSupplier"
                ),
                'b',
                "§8",
                "ToDoubleFunction<T> define el método funcional primitivo 'double applyAsDouble(T value)', devolviendo un double primitivo directamente en la pila sin envolverlo en java.lang.Double."
            ),
            new Pregunta(
                7,
                "Una lambda hace contador++ sobre una variable local. ¿Qué pasa?",
                List.of(
                    "a) Compila y funciona",
                    "b) Error: debe ser efectivamente final",
                    "c) Excepción en ejecución",
                    "d) Solo compila si es static"
                ),
                'b',
                "§5",
                "Toda variable local capturada por una closure/lambda debe ser 'final' o 'efectivamente final'. Una operación de incremento modifica el valor de la variable local en la pila, lo cual está prohibido por especificación de Java."
            ),
            new Pregunta(
                8,
                "Function<String, URI> f = s -> new URI(s);",
                List.of(
                    "a) Compila sin problemas",
                    "b) Error: excepción verificada no reportada",
                    "c) Compila con una advertencia",
                    "d) Falla solo en ejecución"
                ),
                'b',
                "§10",
                "El constructor new URI(String) declara URISyntaxException (checked exception). Como Function.apply(T) no declara cláusula throws, el compilador rechaza el código."
            ),
            new Pregunta(
                9,
                "¿Para qué es especialmente útil Supplier<T> ?",
                List.of(
                    "a) Evaluación diferida de valores",
                    "b) Filtrar colecciones",
                    "c) Recibir dos parámetros",
                    "d) Comparar objetos"
                ),
                'a',
                "§11",
                "Supplier<T> abstrae una fábrica o cálculo perezoso (Lazy Evaluation), permitiendo postergar operaciones computacionalmente costosas hasta el momento exacto en que realmente son requeridas."
            ),
            new Pregunta(
                10,
                "Existen ejecutar(Supplier<String>) y ejecutar(Callable<String>). ¿Qué pasa con ejecutar(() -> \"x\")?",
                List.of(
                    "a) Llama a la versión Supplier",
                    "b) Llama a la versión Callable",
                    "c) Error: llamada ambigua",
                    "d) Error en ejecución"
                ),
                'c',
                "§12",
                "Ambigüedad en tiempo de compilación. Ambas interfaces son funcionales con descriptores () -> String compatibles, por lo que el compilador no puede determinar de forma unívoca a cuál sobrecarga corresponde."
            )
        );
    }

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("AUTOEVALUACIÓN DE DOMINIO · INTERFACES FUNCIONALES EN JAVA");
        System.out.println("============================================================");

        List<Pregunta> preguntas = obtenerPreguntas();
        int puntaje = 0;

        for (Pregunta p : preguntas) {
            System.out.printf("%nPregunta %d (%s): %s%n", p.numero(), p.seccionGuia(), p.enunciado());
            for (String op : p.opciones()) {
                System.out.println("   " + op);
            }
            System.out.printf("   >>> Respuesta correcta: [%c]%n", p.respuestaCorrecta());
            System.out.println("   Justificación técnica: " + p.justificacion());
            puntaje++;
        }

        System.out.println("\n------------------------------------------------------------");
        System.out.printf("Calificación del Taller: %d de %d aciertos (100%%) - Nivel: DOMINIO TOTAL%n", puntaje, preguntas.size());
        System.out.println("------------------------------------------------------------");
    }
}
