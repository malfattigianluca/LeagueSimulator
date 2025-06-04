package TP4;

import TP4.Excepciones.TorneoException;
import TP4.Modelo.GestorEquipos;
import TP4.Modelo.GestorJugadores;
import TP4.Modelo.SimuladorLiga;
import TP4.Modelo.Equipo;
import TP4.Modelo.Torneo;

import java.util.IllegalFormatConversionException;
import java.util.Scanner;
import java.util.List;

public class App {
    public static void main(String[] args) throws TorneoException {
        GestorEquipos gestorequipo = new GestorEquipos();
        GestorJugadores gestorjugador = new GestorJugadores(gestorequipo);
        gestorequipo.cargarEquiposPorLiga("./files/teams.txt");
        gestorjugador.cargarJugadores("./files/players.txt");
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("\n=== SISTEMA DE GESTION DE TORNEOS FUTBOLISTICOS ===");
            System.out.println("1. Simular liga");
            System.out.println("2. Crear tu propio torneo");
            System.out.println("3. Ver ligas");
            System.out.println("4. Ver equipos");
            System.out.println("5. Ver jugadores");
            System.out.println("6. Buscar equipos");
            System.out.println("7. Buscar jugadores");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opcion: ");

            int opcion;
            try {
                opcion = scanner.nextInt();
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Entrada inválida. Por favor, ingrese un número.");
                scanner.nextLine();
                continue;
            }

            switch (opcion) {
                case 1:
                    System.out.println("\n=== Simular Liga ===");
                    System.out.println("Ligas disponibles:");
                    for (String liga : gestorequipo.getEquiposPorLiga().keySet()) {
                        System.out.println("- " + liga);
                    }
                    System.out.print("\nIngrese el nombre de la liga a simular: ");
                    String ligaSeleccionada = scanner.nextLine();

                    if (gestorequipo.getEquiposPorLiga().containsKey(ligaSeleccionada)) {
                        SimuladorLiga simulador = new SimuladorLiga(ligaSeleccionada);
                        List<Equipo> equiposLiga = gestorequipo.getEquiposPorLiga().get(ligaSeleccionada);

                        // Agregar equipos al simulador
                        for (Equipo equipo : equiposLiga) {
                            simulador.agregarEquipo(equipo);
                        }

                        // Simular jornadas
                        System.out.print("Ingrese el número de jornadas a simular: ");
                        try {
                            int numJornadas = Integer.parseInt(scanner.nextLine());
                            for (int i = 0; i < numJornadas; i++) {
                                System.out.println("\nSimulando jornada " + (i + 1));
                                simulador.simularJornada();
                                simulador.mostrarPartidos();
                            }
                            simulador.mostrarTabla();
                        } catch (NumberFormatException e) {
                            System.out.println("Número de jornadas inválido");
                        } catch (TorneoException e) {
                            System.out.println("Error al simular: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Liga no encontrada");
                    }
                    break;
                case 2:
                    System.out.println("\n=== Crear Torneo Personalizado ===");
                    System.out.print("Ingrese el nombre del torneo: ");
                    String nombreTorneo = scanner.nextLine();

                    System.out.println("\nSeleccione el tipo de torneo:");
                    System.out.println("1. Liga (todos contra todos)");
                    System.out.println("2. Eliminación directa");
                    System.out.print("Opción: ");

                    try {
                        int tipoTorneo = Integer.parseInt(scanner.nextLine());
                        boolean eliminacionDirecta = tipoTorneo == 2;

                        Torneo torneo = new Torneo(nombreTorneo, eliminacionDirecta);

                        // Mostrar equipos disponibles
                        System.out.println("\nEquipos disponibles:");
                        List<Equipo> todosEquipos = gestorequipo.getEquipos();
                        for (int i = 0; i < todosEquipos.size(); i++) {
                            System.out.println((i + 1) + ". " + todosEquipos.get(i).getNombre());
                        }

                        // Seleccionar equipos
                        System.out.println(
                                "\nSeleccione los equipos que participarán (ingrese los números separados por comas):");
                        String seleccion = scanner.nextLine();
                        String[] indices = seleccion.split(",");

                        for (String indice : indices) {
                            try {
                                int idx = Integer.parseInt(indice.trim()) - 1;
                                if (idx >= 0 && idx < todosEquipos.size()) {
                                    torneo.agregarEquipo(todosEquipos.get(idx));
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Número inválido: " + indice);
                            }
                        }

                        // Simular torneo
                        try {
                            torneo.simularTorneo();
                            torneo.mostrarPartidos();
                            torneo.mostrarTablaPosiciones();
                        } catch (TorneoException e) {
                            System.out.println("Error al simular torneo: " + e.getMessage());
                        }

                    } catch (NumberFormatException e) {
                        System.out.println("Opción inválida");
                    }
                    break;
                case 3:
                    System.out.println("--- Ver ligas ---");
                    System.out.println("1. Premier League");
                    System.out.println("2. La Liga");
                    System.out.println("3. Liga Argentina de Futbol");
                    System.out.println("4. Serie A");
                    System.out.println("5. Ligue 1 de Francia");
                    System.out.println("0. Volver");
                    System.out.print("Seleccione una liga: ");
                    try {
                        int opcionLiga = scanner.nextInt();
                        scanner.nextLine();
                        switch (opcionLiga) {
                            case 1:
                                gestorequipo.mostrarEquiposPorLiga("Premier League");
                                break;
                            case 2:
                                gestorequipo.mostrarEquiposPorLiga("LaLiga");
                                break;
                            case 3:
                                gestorequipo.mostrarEquiposPorLiga("Liga Argentina de Futbol");
                                break;
                            case 4:
                                gestorequipo.mostrarEquiposPorLiga("Serie A");
                                break;
                            case 5:
                                gestorequipo.mostrarEquiposPorLiga("Ligue 1");
                                break;
                            case 0:
                                System.out.println("Volviendo al menú principal...");
                                break;
                            default:
                                System.out.println("Opción inválida. Intente nuevamente.");
                        }
                    } catch (Exception e) {
                        System.out.println("Entrada inválida. Intente nuevamente.");
                        scanner.nextLine();
                    }
                    break;
                case 4:
                    System.out.println("--- Ver equipos ---");
                    gestorequipo.listarEquipos();
                    break;
                case 5:
                    System.out.println("--- Ver jugadores ---");
                    gestorjugador.mostrarJugadores();
                    break;
                case 6:
                    System.out.println("--- Buscar equipos ---");
                    System.out.println("1. Por nombre");
                    System.out.println("2. Por liga");
                    System.out.println("0. Volver");
                    System.out.print("Seleccione una opción: ");
                    try {
                        int opcionBuscarEquipo = scanner.nextInt();
                        scanner.nextLine();
                    } catch (Exception e) {
                        System.out.println("Entrada inválida. Intente nuevamente.");
                        scanner.nextLine();
                    }
                    break;
                case 7:
                    System.out.println("--- Buscar jugadores ---");
                    System.out.println("1. Por nombre");
                    System.out.println("2. Por equipo");
                    System.out.println("0. Volver");
                    System.out.print("Seleccione una opción: ");
                    try {
                        int opcionBuscarJugador = scanner.nextInt();
                        scanner.nextLine();
                        switch (opcionBuscarJugador) {
                            case 1: // Buscar jugador por nombre
                                try {
                                    System.out.println("Ingrese el nombre o apellido de un jugador: ");
                                    String jugadorNombre = scanner.nextLine();
                                    if (gestorjugador.existeJugador(jugadorNombre)) {
                                        gestorjugador.buscarJugadorPorNombre(jugadorNombre);
                                    } else {
                                        System.out.println(
                                                "El jugador '" + jugadorNombre + "' no se encuentra en el sistema.");
                                    }
                                } catch (NullPointerException e) {
                                    System.out.println(
                                            "Error: Un valor no está inicializado (posiblemente en los datos del equipo o jugadores).");
                                    e.printStackTrace();
                                } catch (IllegalFormatConversionException e) {
                                    System.out.println("Error: Formato incorrecto en la salida de datos.");
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    System.out.println("Error inesperado: " + e.getMessage());
                                    e.printStackTrace();
                                }
                                break;
                            case 2: // Buscar jugador por equipo
                                try {
                                    System.out.println("Ingrese el nombre del club para buscar jugadores: ");
                                    String jugadorClub = scanner.nextLine();
                                    if (gestorequipo.existeEquipo(jugadorClub)) {
                                        gestorequipo.mostrarEquipo(jugadorClub);
                                    } else {
                                        System.out.println(
                                                "El club '" + jugadorClub + "' no se encuentra en el sistema.");
                                    }
                                } catch (NullPointerException e) {
                                    System.out.println(
                                            "Error: Un valor no está inicializado (posiblemente en los datos del equipo o jugadores).");
                                    e.printStackTrace();
                                } catch (IllegalFormatConversionException e) {
                                    System.out.println("Error: Formato incorrecto en la salida de datos.");
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    System.out.println("Error inesperado: " + e.getMessage());
                                    e.printStackTrace();
                                }
                                break;
                            case 3:
                                break;
                        }
                    } catch (Exception e) {
                        System.out.println("Entrada inválida. Intente nuevamente.");
                        scanner.nextLine();
                    }
                    break;
                case 0:
                    salir = true;
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
        scanner.close();
    }
}