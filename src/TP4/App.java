package TP4;

import TP4.Excepciones.TorneoException;
import TP4.Modelo.GestorEquipos;
import TP4.Modelo.GestorJugadores;

import java.util.Scanner;

public class App {
    public static void main(String[] args) throws TorneoException {
        GestorEquipos gestorequipo = new GestorEquipos();
        GestorJugadores gestorjugador = new GestorJugadores(gestorequipo);
        gestorequipo.cargarEquiposPorLiga("files/teams.txt");
        gestorjugador.cargarJugadores("files/players.txt");
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
                    System.out.println("--- Simular liga ---");
                    System.out.println("1. Premier League");
                    System.out.println("2. LaLiga");
                    System.out.println("3. Liga Argentina de Futbol");
                    System.out.println("4. Serie A");
                    System.out.println("5. Ligue 1");
                    System.out.println("0. Volver");
                    System.out.print("Seleccione una liga: ");
                    try {
                        int opcionSimular = scanner.nextInt();
                        scanner.nextLine();
                        switch (opcionSimular){
                            case 1:

                                break;
                            case 2:
                                break;
                            case 3:
                                break;
                            case 4:
                                break;
                            case 5:
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
                case 2:
                    System.out.println("--- Crear tu propio torneo ---");
                    System.out.println("1. Eliminacion directa");
                    System.out.println("2. Fase de grupos");
                    System.out.println("3. Mixto");
                    System.out.println("0. Volver");
                    System.out.print("Seleccione una opción: ");
                    try {
                        int opcionTorneo = scanner.nextInt();
                        scanner.nextLine();
                    } catch (Exception e) {
                        System.out.println("Entrada inválida. Intente nuevamente.");
                        scanner.nextLine();
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
                        scanner.nextLine();}
                    catch (Exception e) {
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
                        switch (opcionBuscarJugador){
                            case 1: //Buscar jugador por nombre
                                System.out.println("Ingrese el nombre o apellido de un jugador: ");
                                String jugadorNombre = scanner.toString();
                                gestorjugador.buscarJugadorPorNombre(jugadorNombre);
                                break;
                            case 2: //Buscar jugador por equipo
                                System.out.println("Ingrese el club para buscar jugadores: ");
                                String jugadorClub = scanner.toString();
                                gestorjugador.buscarJugadorPorNombre(jugadorClub);
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