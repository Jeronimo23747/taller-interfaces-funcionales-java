package co.edu.unihumboldt.funcional.lab2_validacion;

import java.util.List;

/**
 * Ejecutable del Laboratorio 2: Motor de validación componible.
 * Demuestra:
 * 1. Composición de reglas para validación de formularios con errores acumulados.
 * 2. Validación de los tres registros de prueba de la guía.
 * 3. Demostración práctica de los métodos agregados para el Reto 2 (.o() y .cuando()).
 */
public class Laboratorio2Main {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("LABORATORIO 2 · Motor de validación componible");
        System.out.println("============================================================");

        // 1. Reglas para el texto del usuario
        Regla<String> usuarioTexto = Regla.<String>exigir(s -> !s.isBlank(), "usuario: obligatorio")
            .y(Regla.exigir(s -> s.length() >= 4, "usuario: mínimo 4 caracteres"));

        // 2. Proyectar regla del usuario al objeto Registro
        Regla<Registro> usuarioValido = Regla.campo(Registro::usuario, usuarioTexto);

        // 3. Regla para correo con formato válido
        Regla<Registro> correoValido = Regla.exigir(
            r -> r.correo().matches("[^@\\s]+@[^@\\s]+\\.[a-z]{2,}"),
            "correo: formato inválido"
        );

        // 4. Regla para edad mínima de 14 años
        Regla<Registro> edadValida = Regla.exigir(
            r -> r.edad() >= 14,
            "edad: mínimo 14 años"
        );

        // 5. Composición total de reglas (AND)
        Regla<Registro> todas = usuarioValido.y(correoValido).y(edadValida);

        // 6. Casos de prueba de la guía
        List<Registro> registros = List.of(
            new Registro("camila_r", "camila@correo.co", 19),
            new Registro("", "sin-arroba", 12),
            new Registro("ana", "ana@correo", 15)
        );

        System.out.println("\n--- SALIDA ESPERADA DE LA GUÍA ---");
        for (Registro r : registros) {
            List<String> errores = todas.validar(r);
            String usuario = r.usuario().isEmpty() ? "(vacío)" : r.usuario();
            System.out.printf("%-9s %-10s %s%n",
                errores.isEmpty() ? "VÁLIDO" : "INVÁLIDO",
                usuario,
                errores
            );
        }

        System.out.println("\n--- DEMOSTRACIÓN DEL RETO 2 (.o() y .cuando()) ---");
        // Regla disyuntiva: un usuario es aceptado si tiene al menos 18 años O si su correo es institucional (.edu.co)
        Regla<Registro> mayorEdad = Regla.exigir(r -> r.edad() >= 18, "debe ser mayor de edad");
        Regla<Registro> esEstudiante = Regla.exigir(r -> r.correo().endsWith(".edu.co"), "debe ser correo institucional");
        Regla<Registro> beneficioEspecial = mayorEdad.o(esEstudiante);

        // Regla condicional: si el correo es de dominio .edu.co, exigir que el usuario tenga longitud >= 6
        Regla<Registro> reglaCondicionada = Regla.campo(
            Registro::usuario,
            Regla.<String>exigir(u -> u.length() >= 6, "usuario institucional debe tener al menos 6 caracteres")
        ).cuando(r -> r.correo().endsWith(".edu.co"));

        Registro estudianteJoven = new Registro("juan", "juan@unihumboldt.edu.co", 16);
        System.out.println("Validando estudiante con beneficio especial (OR): " + beneficioEspecial.validar(estudianteJoven));
        System.out.println("Validando regla condicional (CUANDO institucional): " + reglaCondicionada.validar(estudianteJoven));
    }
}
