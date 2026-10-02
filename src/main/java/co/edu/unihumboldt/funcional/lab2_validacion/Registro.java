package co.edu.unihumboldt.funcional.lab2_validacion;

/**
 * Record que representa el formulario de registro de un usuario a ser validado,
 * según el punto 5 del Laboratorio 2.
 *
 * @param usuario Nombre de usuario.
 * @param correo  Dirección de correo electrónico.
 * @param edad    Edad del usuario en años.
 */
public record Registro(String usuario, String correo, int edad) {
}
