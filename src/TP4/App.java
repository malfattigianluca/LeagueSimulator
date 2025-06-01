package TP4;

import TP4.Modelo.GestorEquipos;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        GestorEquipos gestor = new GestorEquipos();
        gestor.cargarEquiposPorLiga("files/teams.txt");
        gestor.listarEquipos();
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
                                gestor.mostrarEquiposPorLiga("Premier League");
                                break;
                            case 2:
                                gestor.mostrarEquiposPorLiga("LaLiga");
                                break;
                            case 3:
                                gestor.mostrarEquiposPorLiga("Liga Argentina de Futbol");
                                break;
                            case 4:
                                gestor.mostrarEquiposPorLiga("Serie A");
                                break;
                            case 5:
                                gestor.mostrarEquiposPorLiga("Ligue 1");
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
                    gestor.listarEquipos();
                    break;
                case 5:
                    System.out.println("--- Ver jugadores ---");
                    // gestorJugadores.mostrarJugadores();
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