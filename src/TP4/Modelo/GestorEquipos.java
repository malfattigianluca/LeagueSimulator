package TP4.Modelo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import TP4.Excepciones.TorneoException;

public class GestorEquipos {
    private Map<String, List<Equipo>> equiposPorLiga;
    private Map<String, SimuladorLiga> simuladoresPorLiga;
    private int idEquipo;

    public GestorEquipos() {
        this.equiposPorLiga = new HashMap<>();
        this.idEquipo = 1;
        this.simuladoresPorLiga = new HashMap<>();

    }

    public void cargarEquiposPorLiga(String rutaArchivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length >= 2) {
                    String liga = datos[0];
                    String nombre = datos[2];
                    String pais = datos[3];
                    String escudo = datos[4];
                    int elo = (int) Double.parseDouble(datos[5].trim());
                    Equipo equipo = new Equipo(idEquipo, nombre, elo, liga, escudo, pais);
                    equipo.setId(idEquipo++);
                    equiposPorLiga.computeIfAbsent(liga, k -> new ArrayList<>()).add(equipo);
                }
            }
            System.out.println("---Archivo cargado correctamente---");
        } catch (IOException e) {
            System.err.println("Error al leer equipos: " + e.getMessage());
        } catch (TorneoException e) {
            throw new RuntimeException(e);
        }
    }

    public void agregarEquipo(Equipo equipo) {
        String liga = equipo.getLiga();
        equipo.setId(idEquipo++); // Asignar ID
        equiposPorLiga.computeIfAbsent(liga, k -> new ArrayList<>()).add(equipo);
    }

    public List<Equipo> buscarEquipoPorNombre(String fragmento) {
        List<Equipo> resultados = new ArrayList<>();
        String fragmentoLower = fragmento.toLowerCase();

        for (List<Equipo> lista : equiposPorLiga.values()) {
            for (Equipo equipo : lista) {
                if (equipo.getNombre().toLowerCase().contains(fragmentoLower)) {
                    resultados.add(equipo);
                }
            }
        }

        return resultados;
    }

    public void buscarEquipos(Scanner scanner) {
        System.out.print("Ingrese el fragmento a buscar en el nombre del equipo: ");
        String fragmento = scanner.nextLine().toLowerCase();
        List<Equipo> resultados = equiposPorLiga.values().stream()
                .flatMap(List::stream)
                .filter(equipo -> equipo.getNombre().toLowerCase().contains(fragmento))
                .toList();

        if (resultados.isEmpty()) {
            System.out.println("No se encontraron equipos que coincidan.");
        } else {
            System.out.printf("%-35s %-8s %-25s %-15s %-10s%n", "Liga", "Escudo", "Equipo", "País", "ELO");
            System.out.println("---------------------------------------------------------------------------------------");
            for (Equipo e : resultados) {
                System.out.printf("%-35s %-8s %-25s %-15s %-10d%n",
                        e.getLiga(),
                        e.getEscudo(),
                        e.getNombre(),
                        e.getPais(),
                        e.getElo());
            }
        }
    }

    public void mostrarTodosLosEquipos(String liga) {
        System.out.printf("%-35s %-8s %-25s %-15s %-10s%n", "Liga", "Cod", "Equipo", "País", "ELO");
        System.out.println("---------------------------------------------------------------------------------------");

        // Si se especifica una liga, mostrar solo sus equipos; si no, mostrar todos
        if (liga != null && !liga.trim().isEmpty()) {
            List<Equipo> equipos = equiposPorLiga.getOrDefault(liga, new ArrayList<>());
            if (equipos.isEmpty()) {
                System.out.println("No se encontraron equipos para la liga: " + liga);
                return;
            }
            for (Equipo equipo : equipos) {
                System.out.printf("%-35s %-8s %-25s %-15s %-10d%n",
                        equipo.getLiga(), equipo.getEscudo(), equipo.getNombre(), equipo.getPais(), equipo.getElo());
            }
        } else {
            // Mostrar todos los equipos de todas las ligas
            for (Map.Entry<String, List<Equipo>> entry : equiposPorLiga.entrySet()) {
                for (Equipo equipo : entry.getValue()) {
                    System.out.printf("%-35s %-8s %-25s %-15s %-10d%n",
                            equipo.getLiga(), equipo.getEscudo(), equipo.getNombre(), equipo.getPais(), equipo.getElo());
                }
            }
        }
    }

    public void verLigas(Scanner scanner) {
        if (equiposPorLiga.isEmpty()) {
            System.out.println("No hay ligas cargadas.");
            return;
        }

        // Mostrar ligas disponibles
        System.out.println("Ligas disponibles:");
        List<String> ligas = new ArrayList<>(equiposPorLiga.keySet());

        for (int i = 0; i < ligas.size(); i++) {
            System.out.println((i + 1) + ". " + ligas.get(i));
        }

        // Solicitar selección de liga
        System.out.print("Seleccione una liga (ingrese el número, 0 para mostrar todas): ");
        try {
            int opcion = Integer.parseInt(scanner.nextLine());
            if (opcion == 0) {
                mostrarTodosLosEquipos(null); // Mostrar todas las ligas
            } else if (opcion < 1 || opcion > ligas.size()) {
                System.out.println("Opción inválida. Por favor, seleccione un número entre 0 y " + ligas.size() + ".");
            } else {
                String ligaSeleccionada = ligas.get(opcion - 1);
                mostrarTodosLosEquipos(ligaSeleccionada); // Mostrar equipos de la liga seleccionada
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor, ingrese un número.");
        }
    }

    public void simularLiga(Scanner scanner, GestorJugadores gestorJugadores) {

        if (equiposPorLiga.isEmpty()) {
            System.out.println("No hay ligas disponibles para simular.");
            return;
        }

        System.out.println("Seleccione una liga para simular:");
        List<String> ligas = new ArrayList<>(equiposPorLiga.keySet());
        for (int i = 0; i < ligas.size(); i++) {
            System.out.println((i + 1) + ". " + ligas.get(i));
        }

        int opcion = Integer.parseInt(scanner.nextLine());
        if (opcion < 1 || opcion > ligas.size()) {
            System.out.println("Opción inválida.");
            return;
        }

        String ligaSeleccionada = ligas.get(opcion - 1);
        List<Equipo> equipos = equiposPorLiga.get(ligaSeleccionada);

        SimuladorLiga simulador = new SimuladorLiga(ligaSeleccionada);

        for (Equipo equipo : equipos) {
            equipo.reiniciarEstadisticas();
            simulador.agregarEquipo(equipo);
        }

        try {
            simulador.generarCalendario();
        } catch (TorneoException e) {
            System.err.println("No se pudo generar el calendario: " + e.getMessage());
            return;
        }

        int totalJornadas = simulador.getTotalJornadas();
        System.out.println("\nComenzando simulación de la liga " + ligaSeleccionada + " (" + totalJornadas + " jornadas)");

        for (int jornada = 1; jornada <= totalJornadas; jornada++) {
            try {
                List<Partido> jornadas = simulador.simularJornada();
                simulador.agregarJornada(jornadas);
            } catch (TorneoException e) {
                System.err.println("Error en la jornada: " + e.getMessage());
            }
        }

        simulador.mostrarTabla();
        mostrarMenuPostSimulacion(scanner, simulador);
    }




    public void crearTorneoPersonalizado(Scanner scanner) {
        System.out.print("Ingrese el nombre del torneo personalizado: ");
        String nombre = scanner.nextLine();

        System.out.println("""
        Seleccione la modalidad del torneo:
        1. Eliminación directa
        2. Fase de grupos (todos contra todos)
        3. Mixto (grupos + eliminación)
        """);

        int modalidad = -1;
        while (modalidad < 1 || modalidad > 3) {
            try {
                System.out.print("Opción: ");
                modalidad = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido (1-3).");
            }
        }

        // Cantidad de equipos
        System.out.print("¿Cuántos equipos desea agregar? ");
        int cantidad = Integer.parseInt(scanner.nextLine());

        // Mostrar todos los equipos disponibles con índice
        List<Equipo> disponibles = getEquipos();
        if (disponibles.size() < cantidad) {
            System.out.println("No hay suficientes equipos disponibles para ese torneo.");
            return;
        }

        System.out.println("\n=== Equipos disponibles ===");
        for (int i = 0; i < disponibles.size(); i++) {
            System.out.printf("%d. %s (%s)%n", i + 1, disponibles.get(i).getNombre(), disponibles.get(i).getLiga());
        }

        System.out.print("\nIngrese los números de los equipos separados por comas (ej: 1,3,5): ");
        String[] indices = scanner.nextLine().split(",");

        if (indices.length != cantidad) {
            System.out.println("La cantidad ingresada no coincide con la cantidad esperada.");
            return;
        }

        SimuladorLiga simulador = new SimuladorLiga(nombre);
        Set<Integer> usados = new HashSet<>();

        for (String idxStr : indices) {
            try {
                int idx = Integer.parseInt(idxStr.trim()) - 1;
                if (idx < 0 || idx >= disponibles.size() || usados.contains(idx)) {
                    System.out.println("Índice inválido o duplicado: " + (idx + 1));
                    return;
                }
                simulador.agregarEquipo(disponibles.get(idx));
                usados.add(idx);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido: " + idxStr);
                return;
            }
        }

        simuladoresPorLiga.put(nombre, simulador);
        System.out.println("Torneo personalizado '" + nombre + "' creado con éxito.");
    }

    private void mostrarMenuPostSimulacion(Scanner scanner, SimuladorLiga simulador) {
        while (true) {
            System.out.println("\n=== Estadísticas de la Liga " + simulador.getNombre() + " ===");
            System.out.println("1. Ver fechas jugadas");
            System.out.println("2. Ver top 10 goleadores");
            System.out.println("3. Ver top 10 asistentes");
            System.out.println("4. Ver jugadores con tarjetas");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1" -> simulador.mostrarPartidosJugados(scanner);
                case "2" -> mostrarTopGoleadores(simulador.getPartidos());
                case "3" -> mostrarTopAsistencias(simulador.getPartidos());
                case "4" -> mostrarJugadoresConTarjetas(simulador.getPartidos());
                case "0" -> {
                    return;
                }
                default -> System.out.println("Opción inválida. Intente de nuevo.");
            }
        }
    }

//    private void mostrarTopJugadores(List<Partido> partidos, Comparator<Jugador> comparator, String criterio) {
//        Map<Jugador, Integer> mapa = new HashMap<>();
//
//        for (Partido p : partidos) {
//            List<Jugador> lista = switch (criterio) {
//                case "Goles" -> {
//                    List<Jugador> todos = new ArrayList<>(p.getGoleadoresLocal());
//                    todos.addAll(p.getGoleadoresVisitante());
//                    yield todos;
//                }
//                case "Asistencias" -> {
//                    List<Jugador> todos = new ArrayList<>(p.getAsistentesLocal());
//                    todos.addAll(p.getAsistentesVisitante());
//                    yield todos;
//                }
//                default -> Collections.emptyList();
//            };
//            for (Jugador j : lista) {
//                mapa.put(j, mapa.getOrDefault(j, 0) + 1);
//            }
//        }
//
//        List<Map.Entry<Jugador, Integer>> top = mapa.entrySet().stream()
//                .sorted(Map.Entry.<Jugador, Integer>comparingByValue().reversed())
//                .limit(10)
//                .toList();
//
//        System.out.println("\nTop 10 jugadores por " + criterio + ":");
//        for (Map.Entry<Jugador, Integer> entry : top) {
//            System.out.printf("%s - %d %s\n", entry.getKey().getNombre(), entry.getValue(), criterio.toLowerCase());
//        }
//    }


    public void mostrarTopGoleadores(List<Partido> partidos) {
        Map<Jugador, Integer> goles = new HashMap<>();

        for (Partido partido : partidos) {
            for (Jugador j : partido.getGoleadoresLocal()) {
                goles.put(j, goles.getOrDefault(j, 0) + 1);
            }
            for (Jugador j : partido.getGoleadoresVisitante()) {
                goles.put(j, goles.getOrDefault(j, 0) + 1);
            }
        }

        List<Map.Entry<Jugador, Integer>> top = goles.entrySet().stream()
                .sorted(Map.Entry.<Jugador, Integer>comparingByValue().reversed())
                .limit(10)
                .toList();

        System.out.println("\n=== Top 10 Goleadores ===");
        System.out.printf("%-4s %-25s %-10s%n", "N°", "Jugador", "Goles");

        int pos = 1;
        for (Map.Entry<Jugador, Integer> entry : top) {
            System.out.printf("%-4d %-25s %-10d%n", pos++, entry.getKey().getNombre(), entry.getValue());
        }
    }

    public void mostrarTopAsistencias(List<Partido> partidos) {
        Map<Jugador, Integer> asistencias = new HashMap<>();

        for (Partido partido : partidos) {
            for (Jugador j : partido.getAsistentesLocal()) {
                asistencias.put(j, asistencias.getOrDefault(j, 0) + 1);
            }
            for (Jugador j : partido.getAsistentesVisitante()) {
                asistencias.put(j, asistencias.getOrDefault(j, 0) + 1);
            }
        }

        List<Map.Entry<Jugador, Integer>> top = asistencias.entrySet().stream()
                .sorted(Map.Entry.<Jugador, Integer>comparingByValue().reversed())
                .limit(10)
                .toList();

        System.out.println("\n=== Top 10 Asistentes ===");
        System.out.printf("%-4s %-25s %-12s%n", "N°", "Jugador", "Asistencias");

        int pos = 1;
        for (Map.Entry<Jugador, Integer> entry : top) {
            System.out.printf("%-4d %-25s %-12d%n", pos++, entry.getKey().getNombre(), entry.getValue());
        }
    }


    public void mostrarJugadoresConTarjetas(List<Partido> partidos) {
        Map<Jugador, int[]> tarjetas = new HashMap<>(); // [0] = rojas, [1] = amarillas

        for (Partido partido : partidos) {
            for (Jugador j : partido.getJugadoresRojas()) {
                tarjetas.putIfAbsent(j, new int[2]);
                tarjetas.get(j)[0]++;
            }
            for (Jugador j : partido.getJugadoresAmarillas()) {
                tarjetas.putIfAbsent(j, new int[2]);
                tarjetas.get(j)[1]++;
            }
        }

        List<Map.Entry<Jugador, int[]>> top = tarjetas.entrySet().stream()
                .sorted((a, b) -> {
                    int cmp = Integer.compare(b.getValue()[0], a.getValue()[0]); // rojas desc
                    return (cmp != 0) ? cmp : Integer.compare(b.getValue()[1], a.getValue()[1]); // luego amarillas desc
                })
                .limit(10)
                .toList();

        System.out.println("\n=== Jugadores con Tarjetas ===");
        System.out.printf("%-4s %-25s %-8s %-10s%n", "N°", "Jugador", "Rojas", "Amarillas");

        int pos = 1;
        for (Map.Entry<Jugador, int[]> entry : top) {
            int rojas = entry.getValue()[0];
            int amarillas = entry.getValue()[1];
            System.out.printf("%-4d %-25s %-8d %-10d%n", pos++, entry.getKey().getNombre(), rojas, amarillas);
        }
    }


// Debes llamar a mostrarMenuPostSimulacion(scanner, simulador); justo después de mostrarTabla()


    public void mostrarEquiposPorNombreParcial(String fragmento) {
        List<Equipo> resultados = buscarEquipoPorNombre(fragmento);

        if (resultados.isEmpty()) {
            System.out.println("No se encontraron equipos que coincidan con: " + fragmento);
            return;
        }

        System.out.printf("%-28s | %-10s | %-20s | %4s\n", "Liga", "Escudo", "Equipo", "ELO");
        System.out.println("----------------------------------------------------------------------------");
        for (Equipo equipo : resultados) {
            System.out.printf("%-28s | %-10s | %-20s | %4d\n",
                    equipo.getLiga(), equipo.getEscudo(), equipo.getNombre(), equipo.getElo());
        }
    }

    public List<Equipo> listarEquipos() {
        List<Equipo> todos = new ArrayList<>();
        for (List<Equipo> lista : equiposPorLiga.values()) {
            todos.addAll(lista);
        }
        return todos;
    }

    public void mostrarTodosLosEquipos() {
        List<Equipo> equipos = listarEquipos();
        if (equipos.isEmpty()) {
            System.out.println("No hay equipos cargados.");
            return;
        }
        System.out.println("\n=== Listado de Equipos ===");
        System.out.println("\nLiga                         |   Escudo   | Equipo                  | ELO");
        System.out.println("----------------------------------------------------------------------------");

        for (Equipo equipo : equipos) {
            System.out.printf("%-28s | %-10s | %-20s | %4d\n",
                    equipo.getLiga(),
                    equipo.getEscudo(),
                    equipo.getNombre(),
                    equipo.getElo());
        }
    }

    public boolean existeEquipo(String nombre) {
        return buscarEquipoPorNombre(nombre) != null;
    }

    public void mostrarEquiposPorLiga(String nombreLiga) {
        for (String liga : equiposPorLiga.keySet()) {
            if (liga.equalsIgnoreCase(nombreLiga)) {
                System.out.println("\nLiga: " + liga);
                System.out.printf("%-5s %-20s %-30s %-10s\n", "ID", "Escudo", "Nombre", "Elo");
                System.out.println("---------------------------------------------------------------");
                for (Equipo e : equiposPorLiga.get(liga)) {
                    System.out.printf("%-5d %-20s %-30s %-10d\n",
                            e.getId(), e.getEscudo(), e.getNombre(), e.getElo());
                }
                return;
            }
        }
        System.out.println("No se encontraron equipos para la liga: " + nombreLiga);
    }

    public Map<String, List<Equipo>> getEquiposPorLiga() {
        return equiposPorLiga;
    }

    public List<Equipo> getEquipos() {
        List<Equipo> todos = new ArrayList<>();
        for (List<Equipo> lista : equiposPorLiga.values()) {
            todos.addAll(lista);
        }
        return todos;
    }

    public void mostrarEquipo(String nombreEquipo) {
        for (String liga : equiposPorLiga.keySet()) {
            for (Equipo equipo : equiposPorLiga.get(liga)) {
                if (equipo.getNombre().equalsIgnoreCase(nombreEquipo)) {
                    equipo.mostrarJugadores();
                    return;
                }
            }
        }
        System.out.println("No se encontró el equipo: " + nombreEquipo);
    }

    public List<Equipo> filtrarPorLiga(String liga) {
        return equiposPorLiga.getOrDefault(liga, new ArrayList<>());
    }

    public void asignarJugadorAEquipo(Jugador jugador) throws TorneoException {
        Equipo equipoJugador = jugador.getEquipo();
        if (equipoJugador != null) {
            Equipo equipoRegistrado = (Equipo) buscarEquipoPorNombre(equipoJugador.getNombre());
            if (equipoRegistrado != null) {
                equipoRegistrado.agregarJugador(jugador);
            } else {
                agregarEquipo(equipoJugador);
                equipoJugador.agregarJugador(jugador);
            }
        }
    }

    public boolean eliminarEquipo(String nombre) {
        for (String liga : equiposPorLiga.keySet()) {
            List<Equipo> lista = equiposPorLiga.get(liga);
            Iterator<Equipo> it = lista.iterator();
            while (it.hasNext()) {
                Equipo e = it.next();
                if (e.getNombre().equalsIgnoreCase(nombre)) {
                    it.remove();
                    return true;
                }
            }
        }
        return false;
    }
}
