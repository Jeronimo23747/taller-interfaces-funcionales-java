package co.edu.unihumboldt.funcional;

import co.edu.unihumboldt.funcional.lab1_slugs.GeneradorSlug;
import co.edu.unihumboldt.funcional.lab2_validacion.Regla;
import co.edu.unihumboldt.funcional.lab2_validacion.Registro;
import co.edu.unihumboldt.funcional.lab3_calculadora.CalculadoraEstrategias;
import co.edu.unihumboldt.funcional.lab4_promociones.AjustePrecio;
import co.edu.unihumboldt.funcional.lab4_promociones.Promocion;
import co.edu.unihumboldt.funcional.modelo.Catalogo;
import co.edu.unihumboldt.funcional.modelo.Producto;
import co.edu.unihumboldt.funcional.retos.MemoizadorConcurrente;
import co.edu.unihumboldt.funcional.retos.Reintentar;
import co.edu.unihumboldt.funcional.retos.TriFunction;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Suite integral de pruebas unitarias y de integración para el taller.
 * No requiere frameworks externos para ejecutarse, garantizando portabilidad absoluta
 * en cualquier entorno con Java 21+.
 */
public class PruebasUnitariasRunner {

    private static int pruebasExitosas = 0;
    private static int pruebasTotales = 0;

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("SUITE DE PRUEBAS AUTOMATIZADAS DEL TALLER");
        System.out.println("============================================================");

        probarLaboratorio1();
        probarLaboratorio2();
        probarLaboratorio3();
        probarLaboratorio4();
        probarRetos();

        System.out.println("\n------------------------------------------------------------");
        System.out.printf("RESULTADO FINAL: %d de %d pruebas superadas (%.1f%%)%n",
            pruebasExitosas, pruebasTotales, (pruebasExitosas * 100.0 / pruebasTotales));
        System.out.println("------------------------------------------------------------");

        if (pruebasExitosas != pruebasTotales) {
            System.err.println("¡ALERTA: Se detectaron fallos en las pruebas!");
            System.exit(1);
        } else {
            System.out.println("¡TODO EL CÓDIGO PASÓ LAS PRUEBAS CON ÉXITO!");
        }
    }

    private static void afimar(boolean condicion, String descripcion) {
        pruebasTotales++;
        if (condicion) {
            pruebasExitosas++;
            System.out.println("  [OK] " + descripcion);
        } else {
            System.err.println("  [FALLO] " + descripcion);
        }
    }

    private static void probarLaboratorio1() {
        System.out.println("\n>>> PRUEBAS LABORATORIO 1 (Generador de Slugs) <<<");
        afimar("silla-ergonomica".equals(GeneradorSlug.generar(" Silla Ergonómica ")),
            "Caso 1: Normalización de tildes y espacios extremos");

        afimar("audifonos-bluetooth-53".equals(GeneradorSlug.generar("Audífonos Bluetooth 5.3")),
            "Caso 2: Eliminación de puntos y caracteres no alfanuméricos");

        afimar("oferta-portatil-i7-16gb".equals(GeneradorSlug.generar("¡Oferta! Portátil i7 -- 16GB")),
            "Caso 3: Signos de admiración y guiones múltiples consecutivos");

        afimar("-texto-limpio-".equals(GeneradorSlug.generar("---texto---limpio---")),
            "Caso 4: Reducción de secuencias de guiones repetidos a un único guion");
    }

    private static void probarLaboratorio2() {
        System.out.println("\n>>> PRUEBAS LABORATORIO 2 (Motor de Validación) <<<");
        Regla<String> usuarioTexto = Regla.<String>exigir(s -> !s.isBlank(), "usuario: obligatorio")
            .y(Regla.exigir(s -> s.length() >= 4, "usuario: mínimo 4 caracteres"));
        Regla<Registro> usuarioValido = Regla.campo(Registro::usuario, usuarioTexto);
        Regla<Registro> correoValido = Regla.exigir(
            r -> r.correo().matches("[^@\\s]+@[^@\\s]+\\.[a-z]{2,}"), "correo: formato inválido");
        Regla<Registro> edadValida = Regla.exigir(r -> r.edad() >= 14, "edad: mínimo 14 años");
        Regla<Registro> motor = usuarioValido.y(correoValido).y(edadValida);

        Registro r1 = new Registro("camila_r", "camila@correo.co", 19);
        Registro r2 = new Registro("", "sin-arroba", 12);
        Registro r3 = new Registro("ana", "ana@correo", 15);

        afimar(motor.validar(r1).isEmpty(), "Registro 1 (camila_r) debe ser VÁLIDO");

        List<String> err2 = motor.validar(r2);
        afimar(err2.size() == 4, "Registro 2 vacío debe tener exactamente 4 errores");
        afimar(err2.contains("usuario: obligatorio") && err2.contains("edad: mínimo 14 años"),
            "Registro 2 contiene mensajes esperados");

        List<String> err3 = motor.validar(r3);
        afimar(err3.size() == 2, "Registro 3 (ana) debe tener exactamente 2 errores");
    }

    private static void probarLaboratorio3() {
        System.out.println("\n>>> PRUEBAS LABORATORIO 3 (Calculadora con Estrategias) <<<");
        afimar(CalculadoraEstrategias.evaluar("12 + 30") == 42.0, "Suma: 12 + 30 = 42.0");
        afimar(CalculadoraEstrategias.evaluar("2 ^ 10") == 1024.0, "Potencia: 2 ^ 10 = 1024.0");
        afimar(CalculadoraEstrategias.evaluar("7 / 2") == 3.5, "División: 7 / 2 = 3.5");
        afimar(CalculadoraEstrategias.evaluar("9 max 4") == 9.0, "Máximo: 9 max 4 = 9.0");

        boolean errorDivisionCero = false;
        try {
            CalculadoraEstrategias.evaluar("5 / 0");
        } catch (ArithmeticException e) {
            errorDivisionCero = "división por cero".equals(e.getMessage());
        }
        afimar(errorDivisionCero, "División por cero lanza ArithmeticException específica");

        boolean errorOperadorDesconocido = false;
        try {
            CalculadoraEstrategias.evaluar("3 % 2");
        } catch (IllegalArgumentException e) {
            errorOperadorDesconocido = e.getMessage().contains("operador desconocido");
        }
        afimar(errorOperadorDesconocido, "Operador no registrado lanza IllegalArgumentException");
    }

    private static void probarLaboratorio4() {
        System.out.println("\n>>> PRUEBAS LABORATORIO 4 (Motor de Promociones) <<<");
        Producto portatil = new Producto("Portátil", "tecnología", 3_200_000, 4);
        Producto mouse = new Producto("Mouse", "tecnología", 85_000, 25);
        Producto lapiz = new Producto("Lápiz", "papelería", 2_500, 120);

        Promocion tecno = new Promocion("Tecno 10 %", p -> p.categoria().equals("tecnología"), AjustePrecio.porcentaje(10));
        Promocion bono = new Promocion("Bono $50.000", p -> p.precio() >= 200_000, AjustePrecio.montoFijo(50_000));
        Promocion liq = new Promocion("Liquidación", p -> p.stock() > 100, AjustePrecio.porcentaje(30));
        Supplier<Promocion> sinPromo = () -> new Promocion("Sin promoción", p -> true, AjustePrecio.sinAjuste());

        List<Promocion> promos = List.of(tecno, bono, liq);

        BinaryOperator<Promocion> selector = BinaryOperator.minBy(Comparator.comparingDouble(pr -> pr.precioCon(portatil)));
        Promocion mejorPortatil = sinPromo.get();
        for (Promocion p : promos) mejorPortatil = selector.apply(mejorPortatil, p);
        afimar("Tecno 10 %".equals(mejorPortatil.nombre()), "Mejor promo para Portátil es 'Tecno 10 %'");
        afimar(mejorPortatil.precioCon(portatil) == 2_880_000.0, "Precio con promo Portátil = $2.880.000");

        BinaryOperator<Promocion> selectorLapiz = BinaryOperator.minBy(Comparator.comparingDouble(pr -> pr.precioCon(lapiz)));
        Promocion mejorLapiz = sinPromo.get();
        for (Promocion p : promos) mejorLapiz = selectorLapiz.apply(mejorLapiz, p);
        afimar("Liquidación".equals(mejorLapiz.nombre()), "Mejor promo para Lápiz es 'Liquidación'");
        afimar(mejorLapiz.precioCon(lapiz) == 1_750.0, "Precio con promo Lápiz = $1.750");
    }

    private static void probarRetos() {
        System.out.println("\n>>> PRUEBAS RETOS PARA PROFUNDIZAR <<<");
        // Reto 1: Currying
        TriFunction<Integer, Integer, Integer, Integer> sumarTres = (a, b, c) -> a + b + c;
        int resCurry = sumarTres.curry().apply(10).apply(20).apply(12);
        afimar(resCurry == 42, "Reto 1: TriFunction.curry() evalúa correctamente en 3 etapas");

        // Reto 2: Regla o() y cuando()
        Regla<String> terminaEnA = Regla.exigir(s -> s.endsWith("a"), "no termina en a");
        Regla<String> terminaEnO = Regla.exigir(s -> s.endsWith("o"), "no termina en o");
        Regla<String> terminaEnAoO = terminaEnA.o(terminaEnO);
        afimar(terminaEnAoO.validar("casa").isEmpty(), "Reto 2: Regla.o() pasa con primera opción");
        afimar(terminaEnAoO.validar("carro").isEmpty(), "Reto 2: Regla.o() pasa con segunda opción");
        afimar(terminaEnAoO.validar("luz").size() == 2, "Reto 2: Regla.o() reporta ambos si ambos fallan");

        // Reto 3: Memoizador concurrente
        AtomicInteger ejecuciones = new AtomicInteger(0);
        Function<String, Integer> longitud = MemoizadorConcurrente.memorizar(s -> {
            ejecuciones.incrementAndGet();
            return s.length();
        });
        longitud.apply("hola");
        longitud.apply("hola");
        longitud.apply("hola");
        afimar(ejecuciones.get() == 1, "Reto 3: MemoizadorConcurrente invoca la función una sola vez");

        // Reto 4: Reintentar
        AtomicInteger intentos = new AtomicInteger(0);
        String exitoAlTercero = Reintentar.de(() -> {
            if (intentos.incrementAndGet() < 3) {
                throw new IllegalStateException("Fallo transitorio");
            }
            return "OK";
        }, 3);
        afimar("OK".equals(exitoAlTercero) && intentos.get() == 3,
            "Reto 4: Reintentar reintenta y tiene éxito en el intento límite");
    }
}
