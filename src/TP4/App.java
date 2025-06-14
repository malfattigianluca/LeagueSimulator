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
                    case SIMULAR_LIGA -> simularLiga(scanner, gestorequipo, gestorjugador);
                    case CREAR_TORNEO -> crearTorneoPersonalizado(scanner, gestorequipo);
                    case VER_LIGAS -> verLigas(scanner, gestorequipo);
                    case VER_EQUIPOS -> verEquipos(gestorequipo);
                    case VER_JUGADORES -> verJugadores(gestorjugador);
                    case BUSCAR_EQUIPOS -> buscarEquipos(scanner, gestorequipo);
                    case BUSCAR_JUGADORES -> buscarJugadores(scanner, gestorjugador, gestorequipo);
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

    private static void simularLiga(Scanner scanner, GestorEquipos gestorequipo, GestorJugadores gestorjugador) {
        Consola.mostrarMensaje("\n=== Simular Liga ===");
        Consola.mostrarMensaje("Ligas disponibles:");
        List<String> ligas = new ArrayList<>(gestorequipo.getEquiposPorLiga().keySet());
        for (int i = 0; i < ligas.size(); i++) {
            Consola.mostrarMensaje((i + 1) + "- " + ligas.get(i));
        }

        int opcionLiga = leerEntero(scanner, "\nIngrese el número de la liga a simular: ");
        if (opcionLiga < 1 || opcionLiga > ligas.size()) {
            Consola.mostrarError("Número de liga inválido");
            return;
        }
        String ligaSeleccionada = ligas.get(opcionLiga - 1);

        if (!gestorequipo.getEquiposPorLiga().containsKey(ligaSeleccionada)) {
            Consola.mostrarError("Liga no encontrada");
            return;
        }

        SimuladorLiga simulador = new SimuladorLiga(ligaSeleccionada, true); // Simulate only ida
        List<Equipo> equiposLiga = gestorequipo.getEquiposPorLiga().get(ligaSeleccionada);
        equiposLiga.forEach(simulador::agregarEquipo);

        try {
            // Generar calendario de ida
            simulador.generarCalendario();
            int numJornadas = simulador.getTotalJornadas();
            List<List<Partido>> jornadas = new ArrayList<>();
            for (int i = 0; i < numJornadas; i++) {
                List<Partido> jornada = simulador.simularJornada();
                simulador.agregarJornada(jornada);
                jornadas.add(jornada);
            }
            // Mostrar la tabla de posiciones automáticamente después de la simulación
            simulador.mostrarTabla();
            // Mostrar menú para ver resultados
            mostrarMenuPostSimulacion(scanner, simulador, jornadas, gestorjugador);
        } catch (TorneoException e) {
            Consola.mostrarError("Error al simular: " + e.getMessage());
        }
    }

    private static void mostrarMenuPostSimulacion(Scanner scanner, SimuladorLiga simulador, List<List<Partido>> jornadas, GestorJugadores gestorjugador) {
        boolean volver = false;
        while (!volver) {
            Consola.mostrarMensaje("""
                \n=== Resultados de la Liga ===
                1. Ver tabla de posiciones
                2. Ver partidos
                3. Ver goleadores
                4. Ver asistencias
                5. Ver tarjetas amarillas y rojas
                0. Volver al menú principal""");
            int opcion = leerEntero(scanner, "Seleccione una opción: ");
            switch (opcion) {
                case 1 -> simulador.mostrarTabla();
                case 2 -> verPartidos(scanner, jornadas);
                case 3 -> verGoleadores(gestorjugador);
                case 4 -> verAsistencias(gestorjugador);
                case 5 -> verTarjetas(gestorjugador);
                case 0 -> volver = true;
                default -> Consola.mostrarError("Opción inválida. Intente nuevamente.");
            }
        }
    }

    private static void verPartidos(Scanner scanner, List<List<Partido>> jornadas) {
        if (jornadas.isEmpty()) {
            Consola.mostrarMensaje("No hay partidos para mostrar.");
            return;
        }

        int fechaActual = 1;
        boolean salir = false;
        while (!salir) {
            Consola.mostrarMensaje("\n--- Fecha " + fechaActual + " ---");
            for (Partido partido : jornadas.get(fechaActual - 1)) {
                Consola.mostrarMensaje(partido.toString());
            }
            Consola.mostrarMensaje("""
                \nOpciones:
                1. Fecha anterior
                2. Fecha siguiente
                0. Volver""");
            int opcion = leerEntero(scanner, "Seleccione una opción: ");
            switch (opcion) {
                case 1 -> {
                    if (fechaActual > 1) {
                        fechaActual--;
                    } else {
                        Consola.mostrarMensaje("Ya está en la primera fecha.");
                    }
                }
                case 2 -> {
                    if (fechaActual < jornadas.size()) {
                        fechaActual++;
                    } else {
                        Consola.mostrarMensaje("Ya está en la última fecha.");
                    }
                }
                case 0 -> salir = true;
                default -> Consola.mostrarError("Opción inválida.");
            }
        }
    }

    private static void verGoleadores(GestorJugadores gestorjugador) {
        Consola.mostrarMensaje("\n=== Tabla de Goleadores (Top 10) ===");
        List<Jugador> goleadores = gestorjugador.getJugadores().getVertices().stream()
                .sorted((j1, j2) -> Integer.compare(j2.getGoles(), j1.getGoles()))
                .limit(10)
                .toList();
        if (goleadores.isEmpty()) {
            Consola.mostrarMensaje("No hay jugadores con goles registrados.");
            return;
        }
        Consola.mostrarMensaje(String.format("%-30s %-20s %-10s", "Jugador", "Equipo", "Goles"));
        Consola.mostrarMensaje("--------------------------------------------------");
        for (Jugador jugador : goleadores) {
            Consola.mostrarMensaje(String.format("%-30s %-20s %-10d",
                    jugador.getNombre(),
                    jugador.getEquipo() != null ? jugador.getEquipo().getNombre() : "Sin equipo",
                    jugador.getGoles()));
        }
    }

    private static void verAsistencias(GestorJugadores gestorjugador) {
        Consola.mostrarMensaje("\n=== Tabla de Asistencias (Top 10) ===");
        List<Jugador> asistentes = gestorjugador.getJugadores().getVertices().stream()
                .filter(j -> j.getAsistencias() > 0)
                .sorted((j1, j2) -> Integer.compare(j2.getAsistencias(), j1.getAsistencias()))
                .limit(10)
                .toList();
        if (asistentes.isEmpty()) {
            Consola.mostrarMensaje("No hay jugadores con asistencias registradas.");
            return;
        }
        Consola.mostrarMensaje(String.format("%-30s %-20s %-10s", "Jugador", "Equipo", "Asistencias"));
        Consola.mostrarMensaje("--------------------------------------------------");
        for (Jugador jugador : asistentes) {
            Consola.mostrarMensaje(String.format("%-30s %-20s %-10d",
                    jugador.getNombre(),
                    jugador.getEquipo() != null ? jugador.getEquipo().getNombre() : "Sin equipo",
                    jugador.getAsistencias()));
        }
    }

    private static void verTarjetas(GestorJugadores gestorjugador) {
        Consola.mostrarMensaje("\n=== Tabla de Tarjetas (Top 10) ===");
        List<Jugador> jugadoresConTarjetas = gestorjugador.getJugadores().getVertices().stream()
                .filter(j -> j.getRojas() > 0 || j.getAmarillas() > 0)
                .sorted((j1, j2) -> {
                    int compareRojas = Integer.compare(j2.getRojas(), j1.getRojas());
                    return compareRojas != 0 ? compareRojas : Integer.compare(j2.getAmarillas(), j1.getAmarillas());
                })
                .limit(10)
                .toList();
        if (jugadoresConTarjetas.isEmpty()) {
            Consola.mostrarMensaje("No hay jugadores con tarjetas registradas.");
            return;
        }
        Consola.mostrarMensaje(String.format("%-30s %-20s %-10s %-10s", "Jugador", "Equipo", "Rojas", "Amarillas"));
        Consola.mostrarMensaje("------------------------------------------------------------");
        for (Jugador jugador : jugadoresConTarjetas) {
            Consola.mostrarMensaje(String.format("%-30s %-20s %-10d %-10d",
                    jugador.getNombre(),
                    jugador.getEquipo() != null ? jugador.getEquipo().getNombre() : "Sin equipo",
                    jugador.getRojas(),
                    jugador.getAmarillas()));
        }
    }

    private static void crearTorneoPersonalizado(Scanner scanner, GestorEquipos gestorequipo) {
        Consola.mostrarMensaje("\n=== Crear Torneo Personalizado ===");
        String nombreTorneo = leerEntrada(scanner, "Ingrese el nombre del torneo: ");
        Consola.mostrarMensaje("""
            Seleccione el tipo de torneo:
            1. Liga (todos contra todos)
            2. Eliminación directa
            Opción: """);

        try {
            int tipoTorneo = leerEntero(scanner, "");
            boolean eliminacionDirecta = tipoTorneo == 2;
            Torneo torneo = new Torneo(nombreTorneo, eliminacionDirecta);

            Consola.mostrarMensaje("\nEquipos disponibles:");
            List<Equipo> todosEquipos = gestorequipo.getEquipos();
            for (int i = 0; i < todosEquipos.size(); i++) {
                Consola.mostrarMensaje((i + 1) + ". " + todosEquipos.get(i).getNombre());
            }

            String seleccion = leerEntrada(scanner, "\nSeleccione los equipos que participarán (ingrese los números separados por comas):");
            String[] indices = seleccion.split(",");

            for (String indice : indices) {
                try {
                    int idx = Integer.parseInt(indice.trim()) - 1;
                    if (idx >= 0 && idx < todosEquipos.size()) {
                        torneo.agregarEquipo(todosEquipos.get(idx));
                    } else {
                        Consola.mostrarError("Índice inválido: " + indice);
                    }
                } catch (NumberFormatException e) {
                    Consola.mostrarError("Número inválido: " + indice);
                }
            }

            try {
                torneo.simularTorneo();
                torneo.mostrarPartidos();
                torneo.mostrarTablaPosiciones();
            } catch (TorneoException e) {
                Consola.mostrarError("Error al simular torneo: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            Consola.mostrarError("Opción inválida");
        }
    }

    private static void verLigas(Scanner scanner, GestorEquipos gestorequipo) {
        Consola.mostrarMensaje("""
            --- Ver ligas ---
            1. Premier League
            2. La Liga
            3. Liga Argentina de Futbol
            4. Serie A
            5. Ligue 1 de Francia
            0. Volver""");
        try {
            int opcionLiga = leerEntero(scanner, "Seleccione una liga: ");
            switch (opcionLiga) {
                case 1 -> gestorequipo.mostrarEquiposPorLiga("Premier League");
                case 2 -> gestorequipo.mostrarEquiposPorLiga("LaLiga");
                case 3 -> gestorequipo.mostrarEquiposPorLiga("Liga Argentina de Futbol");
                case 4 -> gestorequipo.mostrarEquiposPorLiga("Serie A");
                case 5 -> gestorequipo.mostrarEquiposPorLiga("Ligue 1");
                case 0 -> Consola.mostrarMensaje("Volviendo al menú principal...");
                default -> Consola.mostrarError("Opción inválida. Intente nuevamente.");
            }
        } catch (Exception e) {
            Consola.mostrarError("Entrada inválida. Intente nuevamente.");
        }
    }

    private static void verEquipos(GestorEquipos gestorequipo) {
        Consola.mostrarMensaje("--- Ver equipos ---");
        gestorequipo.listarEquipos();
    }

    private static void verJugadores(GestorJugadores gestorjugador) {
        Consola.mostrarMensaje("--- Ver jugadores ---");
        gestorjugador.mostrarJugadores();
    }

    private static void buscarEquipos(Scanner scanner, GestorEquipos gestorequipo) {
        Consola.mostrarMensaje("""
            --- Buscar equipos ---
            1. Por nombre
            2. Por liga
            0. Volver""");
        try {
            int opcion = leerEntero(scanner, "Seleccione una opción: ");
            switch (opcion) {
                case 1 -> {
                    String nombre = leerEntrada(scanner, "Ingrese el nombre del equipo: ");
                    gestorequipo.mostrarEquiposPorNombreParcial(nombre);
                }
                case 2 -> {
                    String liga = leerEntrada(scanner, "Ingrese el nombre de la liga: ");
                    if (ligaExiste(gestorequipo, liga)) {
                        gestorequipo.mostrarEquiposPorLiga(liga);
                    } else {
                        Consola.mostrarError("La liga '" + liga + "' no se encuentra en el sistema.");
                    }
                }
                case 0 -> Consola.mostrarMensaje("Volviendo al menú principal...");
                default -> Consola.mostrarError("Opción inválida. Intente nuevamente.");
            }
        } catch (Exception e) {
            Consola.mostrarError("Entrada inválida. Intente nuevamente.");
        }
    }

    private static void buscarJugadores(Scanner scanner, GestorJugadores gestorjugador, GestorEquipos gestorequipo) {
        Consola.mostrarMensaje("""
            --- Buscar jugadores ---
            1. Por nombre
            2. Por equipo
            0. Volver""");
        try {
            int opcion = leerEntero(scanner, "Seleccione una opción: ");
            switch (opcion) {
                case 1 -> {
                    String nombre = leerEntrada(scanner, "Ingrese parte del nombre o apellido del jugador: ");
                    gestorjugador.buscarYMostrarJugadoresPorNombreParcial(nombre);
                }
                case 2 -> {
                    String club = leerEntrada(scanner, "Ingrese el nombre del club para buscar jugadores: ");
                    gestorequipo.mostrarEquiposPorNombreParcial(club);
                }
                case 0 -> Consola.mostrarMensaje("Volviendo al menú principal...");
                default -> Consola.mostrarError("Opción inválida. Intente nuevamente.");
            }
        } catch (Exception e) {
            Consola.mostrarError("Entrada inválida. Intente nuevamente.");
        }
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

    private static boolean ligaExiste(GestorEquipos gestorequipo, String liga) {
        return gestorequipo.getEquiposPorLiga().containsKey(liga);
    }

    private static boolean equipoExiste(GestorEquipos gestorequipo, String equipo) {
        return gestorequipo.existeEquipo(equipo);
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