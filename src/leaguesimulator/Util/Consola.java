package leaguesimulator.Util;

/**
 * Clase de utilidad para mostrar mensajes en consola.
 */
public class Consola {
    /**
     * Muestra un mensaje en la consola.
     *
     * @param mensaje Mensaje a mostrar.
     */
    public static void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    /**
     * Muestra un mensaje de error en la consola.
     *
     * @param mensaje Mensaje de error a mostrar.
     */
    public static void mostrarError(String mensaje) {
        System.err.println("Error: " + mensaje);
    }
}



