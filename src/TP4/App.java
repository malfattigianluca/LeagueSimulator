package TP4;

import TP4.Excepciones.TorneoException;
import TP4.Modelo.*;
import TP4.Util.Consola;

import java.util.*;

public class App {
    private static boolean salir = false;

    public static void main(String[] args) throws TorneoException {
        GestorEquipos gestorequipo = new GestorEquipos();
        GestorJugadores gestorjugador = new GestorJugadores(gestorequipo);
        gestorequipo.cargarEquiposPorLiga("./files/teams.txt");
        gestorjugador.cargarJugadores("./files/players.txt");

        try (Scanner scanner = new Scanner(System.in)) {
            ejecutarMenu(scanner, gestorequipo, gestorjugador);
        }
    }

    private static void ejecutarMenu(Scanner scanner, GestorEquipos gestorequipo, GestorJugadores gestorjugador) throws TorneoException {
        while (!salir) {
            mostrarMenu();
            try {
                MenuOpcion opcion = MenuOpcion.fromInt(leerEntero(scanner, "Seleccione una opción: "));
                switch (opcion) {
                    case SIMULAR_LIGA -> gestorequipo.simularLiga(scanner, gestorjugador);
                    case CREAR_TORNEO -> gestorequipo.crearTorneoPersonalizado(scanner);
                    case VER_LIGAS -> gestorequipo.verLigas(scanner);
                    case VER_EQUIPOS -> {
                        System.out.print("Ingrese el nombre de la liga: ");
                        String liga = scanner.nextLine();
                        gestorequipo.mostrarTodosLosEquipos(liga);
                    }
                    case VER_JUGADORES -> gestorjugador.mostrarJugadores();
                    case BUSCAR_EQUIPOS -> gestorequipo.buscarEquipos(scanner);
                    case BUSCAR_JUGADORES -> gestorjugador.buscarJugadores(scanner);
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



    private static void mostrarMenu() {
        Consola.mostrarMensaje("""
            \n=== SISTEMA DE GESTION DE TORNEOS FUTBOLISTICOS ===
            1. Simular liga
            2. Crear tu propio torneo
            3. Ver ligas
            4. Ver equipos
            5. Ver jugadores
            6. Buscar equipos
            7. Buscar jugadores
            0. Salir""");
    }

    private static String leerEntrada(Scanner scanner, String mensaje) {
        Consola.mostrarMensaje(mensaje);
        return scanner.nextLine();
    }

    private static int leerEntero(Scanner scanner, String mensaje) {
        while (true) {
            try {
                return Integer.parseInt(leerEntrada(scanner, mensaje));
            } catch (NumberFormatException e) {
                Consola.mostrarError("Por favor, ingrese un número válido.");
            }
        }
    }

    enum MenuOpcion {
        SIMULAR_LIGA(1), CREAR_TORNEO(2), VER_LIGAS(3), VER_EQUIPOS(4),
        VER_JUGADORES(5), BUSCAR_EQUIPOS(6), BUSCAR_JUGADORES(7), SALIR(0);

        private final int valor;

        MenuOpcion(int valor) {
            this.valor = valor;
        }

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
