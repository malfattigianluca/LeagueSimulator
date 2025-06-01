package TP4;
import java.util.Scanner;


public class App {
    public static void main(String[] args) {
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
            int opcion = scanner.nextInt();
            scanner.nextLine(); // consumir salto

            switch (opcion) {
                case 1:
                    System.out.println("--- Simular liga ---");
                    System.out.println("1. Premier League");
                    System.out.println("2. La Liga");
                    System.out.println("3. Liga Argentina");
                    System.out.println("4. Serie A");
                    System.out.println("5. Ligue 1 de Francia");
                    System.out.println("0. Volver");
                    scanner.nextLine();
                    break;
                case 2:
                    System.out.println("--- Crear tu propio torneo ---");
                    System.out.println("1. Eliminacion directa");
                    System.out.println("2. Fase de grupos");
                    System.out.println("3. Mixto");
                    System.out.println("4. Tabla comun");
                    System.out.println("0. Volver");
                    scanner.nextLine();
                    break;
                case 3:
                    System.out.println("--- Ver ligas ---");
                    System.out.println("[Mostrar listado de ligas con opcion de seleccion]");
                    scanner.nextLine();
                    break;
                case 4:
                    System.out.println("--- Ver equipos ---");
                    System.out.println("[Mostrar listado de equipos]");
                    scanner.nextLine();
                    break;
                case 5:
                    System.out.println("--- Ver jugadores ---");
                    System.out.println("[Mostrar listado de jugadores]");
                    scanner.nextLine();
                    break;
                case 6:
                    System.out.println("--- Buscar equipos ---");
                    System.out.println("1. Por nombre");
                    System.out.println("2. Por liga");
                    System.out.println("0. Volver");
                    scanner.nextLine();
                    break;
                case 7:
                    System.out.println("--- Buscar jugadores ---");
                    System.out.println("1. Por nombre");
                    System.out.println("2. Por equipo");
                    System.out.println("0. Volver");
                    scanner.nextLine();
                    break;
                case 0:
                    salir = true;
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opcion invalida. Intente nuevamente.");
                }
            }
        scanner.close();
    }
}
