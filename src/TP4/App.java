package TP4;

import TP4.Excepciones.TorneoException;
import TP4.Modelo.*;
import TP4.Util.Consola;
import TP4.Modelo.GestorJugadores;

import java.util.*;

/**
 * Clase principal del sistema de gestión de torneos futbolísticos.
 * Contiene el método main que inicializa los gestores, carga datos
 * desde archivos y ejecuta el menú interactivo.
 */
public class App {
    // Bandera para controlar la salida del bucle principal del menú
    private static boolean salir = false;

    /**
     * Punto de entrada del programa.
     * Carga datos y ejecuta el menú principal.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     * @throws TorneoException si ocurre un error al cargar los equipos o al simular un torneo.
     */
    public static void main(String[] args) throws TorneoException {
        // Inicializa gestores de equipos y jugadores
        GestorEquipos gestorequipo = new GestorEquipos();
        GestorJugadores gestorjugador = new GestorJugadores(gestorequipo);

        // Carga archivos de equipos y jugadores
        gestorequipo.cargarEquiposPorLiga("./files/teams.txt");
        gestorjugador.cargarJugadores("./files/players.txt");

        // Ejecuta el menú interactivo
        try (Scanner scanner = new Scanner(System.in)) {
            ejecutarMenu(scanner, gestorequipo, gestorjugador);
        }
    }

    /**
     * Ejecuta el menú interactivo principal del sistema.
     * Permite al usuario acceder a las funcionalidades disponibles.
     *
     * @param scanner         Objeto Scanner para lectura desde consola.
     * @param gestorequipo    Gestor que maneja los equipos y ligas.
     * @param gestorjugador   Gestor que maneja los jugadores.
     * @throws TorneoException en caso de error en alguna simulación o creación de torneo.
     */
    private static void ejecutarMenu(Scanner scanner, GestorEquipos gestorequipo, GestorJugadores gestorjugador)
            throws TorneoException {
        while (!salir) {
            mostrarMenu();
            try {
                // Lee opción ingresada por el usuario
                MenuOpcion opcion = MenuOpcion.fromInt(leerEntero(scanner, "Seleccione una opción: "));

                // Ejecuta acción correspondiente según la opción
                switch (opcion) {
                    case SIMULAR_LIGA -> gestorequipo.simularLiga(scanner, gestorjugador);
                    case CREAR_TORNEO -> gestorequipo.crearTorneoPersonalizado(scanner);
                    case BUSCAR_EQUIPOS -> gestorequipo.buscarEquipos(scanner);
                    case BUSCAR_JUGADORES -> gestorjugador.buscarJugadores(scanner);
                    case VER_LIGAS -> gestorequipo.verLigas(scanner);
                    case VER_EQUIPOS -> {
                        String ligaSeleccionada = gestorequipo.seleccionarLigaConMenu(scanner);
                        if (ligaSeleccionada != null) {
                            gestorequipo.mostrarTodosLosEquipos(
                                    ligaSeleccionada.equals("TODAS") ? "" : ligaSeleccionada);
                        }
                    }
                    case VER_JUGADORES -> gestorjugador.mostrarJugadores();
                    case SALIR -> {
                        salir = true;
                        Consola.mostrarMensaje("Saliendo del sistema...");
                    }
                }
            } catch (IllegalArgumentException e) {
                Consola.mostrarError("Opción inválida. Intente nuevamente.");
            } catch (Exception e) {
                Consola.mostrarError("Error inesperado: " + e.getMessage());
            }
        }
    }

    /**
     * Muestra el menú principal del sistema por consola.
     */
    private static void mostrarMenu() {
        Consola.mostrarMensaje("""
                \n=== SISTEMA DE GESTIÓN DE TORNEOS FUTBOLÍSTICOS ===
                1. Simular liga
                2. Crear tu propio torneo
                3. Buscar equipos
                4. Buscar jugadores
                5. Ver ligas
                6. Ver equipos
                7. Ver jugadores
                0. Salir""");
    }

    /**
     * Solicita una línea de texto al usuario.
     *
     * @param scanner Objeto Scanner para lectura.
     * @param mensaje Mensaje que se muestra antes de leer.
     * @return Texto ingresado por el usuario.
     */
    private static String leerEntrada(Scanner scanner, String mensaje) {
        Consola.mostrarMensaje(mensaje);
        return scanner.nextLine();
    }

    /**
     * Solicita un número entero al usuario, validando la entrada.
     *
     * @param scanner Objeto Scanner para lectura.
     * @param mensaje Mensaje que se muestra antes de leer.
     * @return Número entero válido ingresado por el usuario.
     */
    private static int leerEntero(Scanner scanner, String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerEntrada(scanner, mensaje));
            } catch (NumberFormatException e) {
                Consola.mostrarError("Por favor, ingrese un número válido.");
            }
        }
    }

    /**
     * Enumeración de opciones del menú principal.
     * Cada opción está asociada a un número entero.
     */
    enum MenuOpcion {
        SIMULAR_LIGA(1),
        CREAR_TORNEO(2),
        BUSCAR_EQUIPOS(3),
        BUSCAR_JUGADORES(4),
        VER_LIGAS(5),
        VER_EQUIPOS(6),
        VER_JUGADORES(7),
        SALIR(0);

        private final int valor;

        MenuOpcion(int valor) {
            this.valor = valor;
        }

        /**
         * Devuelve la opción del menú correspondiente al número ingresado.
         *
         * @param valor Número de opción.
         * @return Instancia de MenuOpcion.
         * @throws IllegalArgumentException si el número no corresponde a ninguna opción válida.
         */
        public static MenuOpcion fromInt(int valor) {
            for (MenuOpcion opcion : values()) {
                if (opcion.valor == valor) {
                    return opcion;
                }
            }
            throw new IllegalArgumentException("Opción inválida: " + valor);
        }
    }
}
