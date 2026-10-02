package co.edu.unihumboldt.funcional.ejemplos;

import co.edu.unihumboldt.funcional.modelo.Catalogo;
import co.edu.unihumboldt.funcional.modelo.Producto;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Demostración didáctica de la Parte I de la guía: Fundamentos.
 * Abarca:
 * - Listado 2: Evolución histórica (Clase con nombre -> Anónima -> Lambda -> Referencia).
 * - Listado 3: @FunctionalInterface válida con métodos default, static, private y métodos de Object.
 * - Listado 4: Tipado por contexto/destino (Target Typing).
 * - Listado 5: Comportamiento de 'this' en lambdas vs clases anónimas.
 * - Listado 6: Los 4 tipos de referencias a métodos.
 * - Listado 7: Captura de variables (closures) y variables efectivamente finales.
 */
public class FundamentosEjemplo {

    private final String identificadorInstancia = "Instancia FundamentosEjemplo";
    private int contadorModificable = 0;

    // --- Listado 3: Interfaz funcional con métodos adicionales ---
    @FunctionalInterface
    public interface Transformador {
        String aplicar(String texto); // Único método abstracto (SAM)

        boolean equals(Object otro); // De Object: NO cuenta como método abstracto adicional

        default Transformador luego(Transformador siguiente) {
            return texto -> siguiente.aplicar(aplicar(texto));
        }

        static Transformador identidad() {
            return texto -> texto;
        }

        private static String recortar(String t) {
            return t.strip();
        }

        static Transformador limpiar() {
            return Transformador::recortar;
        }
    }

    // Listado 2: Contrato Criterio para filtrar
    @FunctionalInterface
    public interface Criterio {
        boolean cumple(Producto p);
    }

    public static List<String> filtrar(List<Producto> productos, Criterio criterio) {
        List<String> resultado = new ArrayList<>();
        for (Producto p : productos) {
            if (criterio.cumple(p)) {
                resultado.add(p.nombre());
            }
        }
        return resultado;
    }

    static class EnStock implements Criterio {
        @Override
        public boolean cumple(Producto p) {
            return p.disponible();
        }
    }

    public static double conIva(double precio) {
        return Math.round(precio * 1.19);
    }

    public void ejecutar() throws Exception {
        System.out.println("============================================================");
        System.out.println("PARTE I: FUNDAMENTOS DE INTERFACES FUNCIONALES");
        System.out.println("============================================================");

        List<Producto> catalogo = Catalogo.muestra();

        // 1. Listado 2: Cuatro formas del mismo comportamiento
        System.out.println("\n--- 1. Evolución histórica del comportamiento ---");
        System.out.println("A. Clase con nombre:   " + filtrar(catalogo, new EnStock()));
        System.out.println("B. Clase anónima:      " + filtrar(catalogo, new Criterio() {
            @Override
            public boolean cumple(Producto p) {
                return p.precio() < 100_000;
            }
        }));
        System.out.println("C. Lambda:             " + filtrar(catalogo, p -> p.categoria().equals("muebles")));
        System.out.println("D. Referencia a método: " + filtrar(catalogo, Producto::disponible));

        // 2. Listado 3: SAM con métodos default, static y de Object
        System.out.println("\n--- 2. Interfaz funcional enriquecida (@FunctionalInterface) ---");
        Transformador gritar = texto -> texto.toUpperCase() + "!";
        Transformador flujo = Transformador.limpiar().luego(gritar);
        System.out.println("Transformador compuesto: " + flujo.aplicar(" hola, lambdas "));
        System.out.println("Identidad:               " + Transformador.identidad().aplicar("sin cambios"));

        // 3. Listado 4: Tipado por destino
        System.out.println("\n--- 3. Tipado por contexto de destino (Target Typing) ---");
        Predicate<String> p = String::isEmpty;
        Function<String, Boolean> f = String::isEmpty;
        System.out.println("Misma lambda asignada a Predicate y Function: " + p.test("") + ", " + f.apply("x"));

        Callable<String> c = () -> "desde Callable";
        Supplier<String> su = () -> "desde Supplier";
        System.out.println(c.call() + " | " + su.get());

        Object o = (Runnable) () -> System.out.println("Cast explícito como tipo destino");
        ((Runnable) o).run();

        // 4. Listado 5: 'this' en lambda vs anónima
        System.out.println("\n--- 4. Ámbito léxico y significado de 'this' ---");
        Runnable anonima = new Runnable() {
            private final String identificadorInstancia = "Instancia Anónima";
            @Override
            public void run() {
                System.out.println("this en anónima → " + this.identificadorInstancia);
            }
        };
        Runnable lambda = () -> System.out.println("this en lambda → " + this.identificadorInstancia);
        anonima.run();
        lambda.run();

        // 5. Listado 6: Cuatro tipos de referencias a métodos
        System.out.println("\n--- 5. Los cuatro tipos de referencias a métodos ---");
        // 1. Estático
        DoubleUnaryOperator iva = FundamentosEjemplo::conIva;
        System.out.println("1. Estático (Clase::estatico):           " + iva.applyAsDouble(100_000));

        // 2. Objeto concreto
        String prefijo = "SKU-";
        Function<String, String> etiquetar = prefijo::concat;
        System.out.println("2. Objeto concreto (objeto::metodo):      " + etiquetar.apply("0042"));

        // 3. Objeto arbitrario
        Function<Producto, String> nombre = Producto::nombre;
        BiFunction<String, String, Boolean> empieza = String::startsWith;
        System.out.println("3. Objeto arbitrario (Clase::metodo):     " + nombre.apply(catalogo.get(0)) + " " + empieza.apply("Java", "Ja"));

        // 4. Constructor
        Supplier<List<String>> nuevaLista = ArrayList::new;
        Function<String, StringBuilder> constructorSB = StringBuilder::new;
        IntFunction<int[]> arreglo = int[]::new;
        List<String> l = nuevaLista.get();
        l.add("ok");
        System.out.println("4. Constructor (Clase::new):              " + l + " " + constructorSB.apply("abc").reverse() + " long:" + arreglo.apply(3).length);

        // 6. Listado 7: Captura de variables y closures
        System.out.println("\n--- 6. Captura de variables y closures ---");
        String mensajeCapturado = "Hola, Jerónimo"; // Efectivamente final
        Supplier<String> saludo = () -> mensajeCapturado + "!";
        System.out.println("Closure con variable local: " + saludo.get());

        IntSupplier contador = () -> ++this.contadorModificable; // Los campos sí pueden mutar
        contador.getAsInt();
        contador.getAsInt();
        System.out.println("Closure capturando this (campo mutado): " + contador.getAsInt());
    }

    public static void main(String[] args) throws Exception {
        new FundamentosEjemplo().ejecutar();
    }
}
