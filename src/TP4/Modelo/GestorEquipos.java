package TP4.Modelo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import TP4.Excepciones.TorneoException;

public class GestorEquipos {

    // Mapa que asocia el nombre de una liga con una lista de sus equipos
    private Map<String, List<Equipo>> equiposPorLiga;

    // Mapa que asocia el nombre de una liga con su simulador correspondiente
    private Map<String, SimuladorLiga> simuladoresPorLiga;

    // Contador autoincremental para asignar ID únicos a los equipos
    private int idEquipo;

    /**
     * Constructor de la clase GestorEquipos.
     * Inicializa los mapas y el contador de IDs.
     */
    public GestorEquipos() {
        this.equiposPorLiga = new HashMap<>();
        this.idEquipo = 1;
        this.simuladoresPorLiga = new HashMap<>();

    }

    /**
     * Carga los equipos desde un archivo de texto plano y los organiza por liga.
     * El archivo debe tener un formato donde cada línea representa un equipo,
     * con los datos separados por punto y coma (;).
     *
     * Formato esperado por línea:
     * liga;...;nombre;pais;escudo;elo
     *
     * @param rutaArchivo Ruta del archivo que contiene los datos de los equipos.
     */
    public void cargarEquiposPorLiga(String rutaArchivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;

            // Lee el archivo línea por línea
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(";");

                // Verifica que la línea tenga al menos los datos mínimos esperados
                if (datos.length >= 2) {
                    String liga = datos[0];
                    String nombre = datos[2];
                    String pais = datos[3];
                    String escudo = datos[4];

                    // Convierte el valor de elo a entero
                    int elo = (int) Double.parseDouble(datos[5].trim());

                    // Crea una nueva instancia de Equipo con los datos leídos
                    Equipo equipo = new Equipo(idEquipo, nombre, elo, liga, escudo, pais);
                    equipo.setId(idEquipo++); // Asigna y luego incrementa el ID

                    // Agrega el equipo a la lista correspondiente dentro del mapa por liga
                    equiposPorLiga.computeIfAbsent(liga, k -> new ArrayList<>()).add(equipo);
                }
            }
            System.out.println("---Archivo cargado correctamente---");
        } catch (IOException e) {

            // Manejo de errores de lectura del archivo
            System.err.println("Error al leer equipos: " + e.getMessage());
        } catch (TorneoException e) {

            // Reenvío de excepción específica de lógica del torneo
            throw new RuntimeException(e);
        }
    }

    /**
     * Agrega un equipo al sistema, asignándole un ID único y ubicándolo según su
     * liga.
     *
     * @param equipo El equipo a agregar.
     */
    public void agregarEquipo(Equipo equipo) {
        String liga = equipo.getLiga();

        equipo.setId(idEquipo++); // Asigna un ID único y lo incrementa

        // Agrega el equipo a la lista correspondiente en el mapa, creando la lista si
        // no existe
        equiposPorLiga.computeIfAbsent(liga, k -> new ArrayList<>()).add(equipo);
    }

    /**
     * Busca equipos cuyo nombre contenga un fragmento de texto (insensible a
     * mayúsculas).
     *
     * @param fragmento Fragmento del nombre del equipo a buscar.
     * @return Lista de equipos que contienen el fragmento en su nombre.
     */
    public List<Equipo> buscarEquipoPorNombre(String fragmento) {
        List<Equipo> resultados = new ArrayList<>();
        String fragmentoLower = fragmento.toLowerCase(); // Convierte el fragmento a minúsculas

        // Recorre todas las listas de equipos por liga
        for (List<Equipo> lista : equiposPorLiga.values()) {
            for (Equipo equipo : lista) {
                // Compara el nombre del equipo ignorando mayúsculas
                if (equipo.getNombre().toLowerCase().contains(fragmentoLower)) {
                    resultados.add(equipo);
                }
            }
        }

        return resultados;
    }

    /**
     * Interactúa con el usuario para buscar equipos por nombre y mostrar los
     * resultados formateados.
     *
     * @param scanner Objeto Scanner para leer la entrada del usuario.
     */
    public void buscarEquipos(Scanner scanner) {
        System.out.println("\n=== BÚSQUEDA DE EQUIPOS ===");
        System.out.println("1. Buscar por nombre");
        System.out.println("2. Buscar por liga");
        System.out.println("0. Volver");

        int opcion = -1;
        while (opcion < 0 || opcion > 2) {
            try {
                System.out.print("\nOpción: ");
                opcion = Integer.parseInt(scanner.nextLine());
                if (opcion < 0 || opcion > 2) {
                    System.out.println("Por favor ingrese una opción válida (0-2).");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un número válido.");
            }
        }

        if (opcion == 0) {
            return;
        }

        System.out.print("\nIngrese el término de búsqueda: ");
        String busqueda = scanner.nextLine().trim().toLowerCase();

        List<Equipo> resultados = new ArrayList<>();
        for (Equipo equipo : getEquipos()) {
            if (opcion == 1 && equipo.getNombre().toLowerCase().contains(busqueda)) {
                resultados.add(equipo);
            } else if (opcion == 2 && equipo.getLiga().toLowerCase().contains(busqueda)) {
                resultados.add(equipo);
            }
        }

        if (resultados.isEmpty()) {
            System.out.println("\nNo se encontraron equipos que coincidan con la búsqueda.");
            return;
        }

        System.out.println("\n=== Resultados de la búsqueda ===");
        System.out.printf("%-30s %-20s %-10s%n", "Equipo", "Liga", "ELO");
        System.out.println("------------------------------------------------------------");
        for (Equipo equipo : resultados) {
            System.out.printf("%-30s %-20s %-10d%n",
                    equipo.getNombre(),
                    equipo.getLiga(),
                    equipo.getElo());
        }
    }

    /**
     * Muestra por consola todos los equipos cargados, filtrando por liga si se
     * especifica.
     * Imprime los datos formateados en columnas: Liga, Código (escudo), Nombre del
     * equipo, País, ELO.
     *
     * @param liga Nombre de la liga a filtrar. Si es null o vacío, muestra todos
     *             los equipos de todas las ligas.
     */
    public void mostrarTodosLosEquipos(String liga) {
        System.out.println("\n=== EQUIPOS DE " + liga.toUpperCase() + " ===");
        System.out.printf("%-30s %-10s%n", "Equipo", "ELO");
        System.out.println("----------------------------------------");

        List<Equipo> equiposLiga = new ArrayList<>();
        for (Equipo equipo : getEquipos()) {
            if (equipo.getLiga().equalsIgnoreCase(liga)) {
                equiposLiga.add(equipo);
            }
        }

        if (equiposLiga.isEmpty()) {
            System.out.println("No hay equipos registrados en esta liga.");
            return;
        }

        // Ordenar por ELO
        equiposLiga.sort((e1, e2) -> Integer.compare(e2.getElo(), e1.getElo()));

        for (Equipo equipo : equiposLiga) {
            System.out.printf("%-30s %-10d%n",
                    equipo.getNombre(),
                    equipo.getElo());
        }
    }

    /**
     * Muestra un menú con todas las ligas disponibles y permite al usuario
     * seleccionar una opción.
     * Incluye una opción adicional para mostrar todos los equipos sin filtrar por
     * liga.
     *
     * @param scanner Scanner utilizado para leer la entrada del usuario desde
     *                consola.
     * @return El nombre de la liga seleccionada, o "TODAS" si el usuario elige ver
     *         todas las ligas.
     *         Devuelve null si no hay ligas disponibles.
     */
    public String seleccionarLigaConMenu(Scanner scanner) {
        // Obtener la lista de ligas disponibles a partir de las claves del mapa
        List<String> ligas = new ArrayList<>(equiposPorLiga.keySet());

        if (ligas.isEmpty()) {
            System.out.println("No hay ligas disponibles.");
            return null; // No se puede seleccionar nada si no hay ligas
        }

        // Mostrar menú de selección
        System.out.println("Seleccione una liga:");
        for (int i = 0; i < ligas.size(); i++) {
            System.out.println((i + 1) + ". " + ligas.get(i)); // Opciones numeradas desde 1
        }
        System.out.println("0. Todas las ligas"); // Opción extra para ver todos los equipos

        int opcion;
        while (true) {
            System.out.print("Ingrese el número de la liga: ");
            try {
                opcion = Integer.parseInt(scanner.nextLine());
                // Validar que la opción esté dentro del rango permitido
                if (opcion >= 0 && opcion <= ligas.size())
                    break;
                else
                    System.out.println("Número fuera de rango.");
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida. Ingrese un número.");
            }
        }

        // Si elige 0, se interpretará como solicitud de mostrar todas las ligas
        if (opcion == 0)
            return "TODAS";
        // Si elige una liga específica, devolver su nombre
        return ligas.get(opcion - 1);
    }

    /**
     * Muestra al usuario las ligas disponibles y permite seleccionar una para ver
     * sus equipos.
     * Si el usuario ingresa 0, se mostrarán los equipos de todas las ligas.
     *
     * @param scanner Objeto Scanner para leer la entrada del usuario por consola.
     */
    public void verLigas(Scanner scanner) {
        // Verifica si hay ligas cargadas
        if (equiposPorLiga.isEmpty()) {
            System.out.println("No hay ligas cargadas.");
            return;
        }

        // Muestra la lista de ligas disponibles
        System.out.println("Ligas disponibles:");
        List<String> ligas = new ArrayList<>(equiposPorLiga.keySet());

        for (int i = 0; i < ligas.size(); i++) {
            System.out.println((i + 1) + ". " + ligas.get(i));
        }

        // Solicita al usuario que seleccione una liga
        System.out.print("Seleccione una liga (ingrese el número, 0 para mostrar todas): ");
        try {
            int opcion = Integer.parseInt(scanner.nextLine());
            if (opcion == 0) {

                // Mostrar todos los equipos de todas las ligas
                mostrarTodosLosEquipos(null);
            } else if (opcion < 1 || opcion > ligas.size()) {
                System.out.println("Opción inválida. Por favor, seleccione un número entre 0 y " + ligas.size() + ".");
            } else {

                // Muestra solo los equipos de la liga seleccionada
                String ligaSeleccionada = ligas.get(opcion - 1);
                mostrarTodosLosEquipos(ligaSeleccionada);
            }
        } catch (NumberFormatException e) {
            // Manejo de error si el usuario ingresa algo que no sea un número
            System.out.println("Entrada inválida. Por favor, ingrese un número.");
        }
    }

    /**
     * Simula una liga con los equipos disponibles.
     *
     * @param scanner         Scanner para entrada de usuario.
     * @param gestorJugadores Gestor de jugadores para mostrar estadísticas.
     */
    public void simularLiga(Scanner scanner, GestorJugadores gestorJugadores) {
        System.out.println("\n=== SIMULACIÓN DE LIGA ===");

        // Obtener la liga seleccionada
        String ligaSeleccionada = seleccionarLigaConMenu(scanner);
        if (ligaSeleccionada == null) {
            return;
        }

        // Obtener equipos de la liga seleccionada
        List<Equipo> equiposLiga = equiposPorLiga.get(ligaSeleccionada);
        if (equiposLiga == null || equiposLiga.isEmpty()) {
            System.out.println("No hay equipos disponibles en la liga seleccionada.");
            return;
        }

        // Crear y simular la liga
        Torneo liga = new Torneo(ligaSeleccionada, false);
        for (Equipo equipo : equiposLiga) {
            liga.agregarEquipo(equipo);
        }

        try {
            System.out.println("\nSimulando liga...");
            liga.simularTorneo();
            System.out.println("¡Liga simulada con éxito!");

            // Mostrar resultados
            System.out.println("\n=== Tabla de Posiciones ===");
            liga.mostrarTablaPosiciones();

            System.out.println("\n=== Partidos Jugados ===");
            liga.mostrarPartidos();

            // Mostrar estadísticas de jugadores
            if (gestorJugadores != null) {
                System.out.println("\n=== Estadísticas de Jugadores ===");
                for (Equipo equipo : equiposLiga) {
                    gestorJugadores.mostrarEstadisticasJugadores(equipo.getNombre());
                }
            }

        } catch (TorneoException e) {
            System.out.println("Error al simular la liga: " + e.getMessage());
        }
    }

    /**
     * Crea y simula un torneo personalizado.
     *
     * @param scanner Scanner para entrada de usuario.
     */
    public void crearTorneoPersonalizado(Scanner scanner) {
        System.out.println("\n=== CREACIÓN DE TORNEO PERSONALIZADO ===");
        System.out.print("Ingrese el nombre del torneo: ");
        String nombre = scanner.nextLine().trim();

        if (nombre.isEmpty()) {
            System.out.println("El nombre del torneo no puede estar vacío.");
            return;
        }

        // Verificar si ya existe un torneo con ese nombre
        if (simuladoresPorLiga.containsKey(nombre)) {
            System.out.println("Ya existe un torneo con ese nombre.");
            return;
        }

        System.out.println("\nSeleccione la modalidad del torneo:");
        System.out.println("1. Eliminación directa");
        System.out.println("2. Fase de grupos (todos contra todos)");
        System.out.println("3. Mixto (grupos + eliminación)");
        System.out.println("0. Cancelar");

        int modalidad = -1;
        while (modalidad < 0 || modalidad > 3) {
            try {
                System.out.print("\nOpción: ");
                modalidad = Integer.parseInt(scanner.nextLine());
                if (modalidad < 0 || modalidad > 3) {
                    System.out.println("Por favor ingrese una opción válida (0-3).");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un número válido.");
            }
        }

        if (modalidad == 0) {
            System.out.println("Creación de torneo cancelada.");
            return;
        }

        // Obtener cantidad de equipos
        int cantidad = -1;
        while (cantidad < 2) {
            try {
                System.out.print("\n¿Cuántos equipos desea agregar? (mínimo 2): ");
                cantidad = Integer.parseInt(scanner.nextLine());
                if (cantidad < 2) {
                    System.out.println("Se necesitan al menos 2 equipos para un torneo.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingrese un número válido.");
            }
        }

        // Verificar disponibilidad de equipos
        List<Equipo> disponibles = getEquipos();
        if (disponibles.size() < cantidad) {
            System.out.println("No hay suficientes equipos disponibles. Solo hay " + disponibles.size() + " equipos.");
            return;
        }

        // Mostrar equipos disponibles
        System.out.println("\n=== Equipos disponibles ===");
        System.out.printf("%-4s %-30s %-20s %-10s%n", "N°", "Equipo", "Liga", "ELO");
        System.out.println("------------------------------------------------------------");
        for (int i = 0; i < disponibles.size(); i++) {
            Equipo e = disponibles.get(i);
            System.out.printf("%-4d %-30s %-20s %-10d%n",
                    i + 1,
                    e.getNombre(),
                    e.getLiga(),
                    e.getElo());
        }

        // Selección de equipos
        System.out.print("\nIngrese los números de los equipos separados por comas (ej: 1,3,5): ");
        String[] indices = scanner.nextLine().split(",");

        if (indices.length != cantidad) {
            System.out.println("La cantidad de equipos seleccionados no coincide con la cantidad especificada.");
            return;
        }

        // Crear el torneo según la modalidad
        Torneo torneo;
        if (modalidad == 1) {
            torneo = new Torneo(nombre, true); // Eliminación directa
        } else if (modalidad == 2) {
            torneo = new Torneo(nombre, false); // Liga
        } else {
            // Torneo mixto
            int numGrupos = -1;
            while (numGrupos < 2) {
                try {
                    System.out.print("\n¿Cuántos grupos desea crear? (mínimo 2): ");
                    numGrupos = Integer.parseInt(scanner.nextLine());
                    if (numGrupos < 2) {
                        System.out.println("Se necesitan al menos 2 grupos.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Por favor ingrese un número válido.");
                }
            }

            int equiposPorGrupo = cantidad / numGrupos;
            int equiposQuePasan = -1;
            while (equiposQuePasan < 1 || equiposQuePasan >= equiposPorGrupo) {
                try {
                    System.out.print("¿Cuántos equipos pasan de cada grupo? (1-" + (equiposPorGrupo - 1) + "): ");
                    equiposQuePasan = Integer.parseInt(scanner.nextLine());
                    if (equiposQuePasan < 1 || equiposQuePasan >= equiposPorGrupo) {
                        System.out.println("El número debe estar entre 1 y " + (equiposPorGrupo - 1));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Por favor ingrese un número válido.");
                }
            }

            torneo = new TorneoMixto(nombre, numGrupos, equiposQuePasan);
        }

        // Agregar equipos seleccionados
        Set<Integer> usados = new HashSet<>();
        for (String idxStr : indices) {
            try {
                int idx = Integer.parseInt(idxStr.trim()) - 1;
                if (idx < 0 || idx >= disponibles.size()) {
                    System.out.println("Índice inválido: " + (idx + 1));
                    return;
                }
                if (usados.contains(idx)) {
                    System.out.println("Equipo duplicado: " + disponibles.get(idx).getNombre());
                    return;
                }
                torneo.agregarEquipo(disponibles.get(idx));
                usados.add(idx);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido: " + idxStr);
                return;
            }
        }

        // Simular el torneo
        try {
            System.out.println("\nSimulando torneo '" + nombre + "'...");
            torneo.simularTorneo();
            System.out.println("¡Torneo simulado con éxito!");

            // Mostrar resultados
            System.out.println("\n=== Resultados del Torneo ===");
            if (torneo instanceof TorneoMixto) {
                ((TorneoMixto) torneo).mostrarTablasGrupos();
                ((TorneoMixto) torneo).mostrarEquiposClasificados();
            } else {
                torneo.mostrarTablaPosiciones();
            }
            torneo.mostrarPartidos();

        } catch (TorneoException e) {
            System.out.println("Error al simular el torneo: " + e.getMessage());
        }
    }

    /**
     * Muestra un menú interactivo post-simulación con estadísticas detalladas
     * sobre la liga simulada: fechas jugadas, goleadores, asistencias y tarjetas.
     *
     * @param scanner   Objeto Scanner para leer la entrada del usuario.
     * @param simulador Simulador de la liga previamente ejecutado.
     */
    private void mostrarMenuPostSimulacion(Scanner scanner, SimuladorLiga simulador) {
        while (true) {
            // Título del menú
            System.out.println("\n=== Estadísticas de la Liga " + simulador.getNombre() + " ===");
            System.out.println("1. Ver fechas jugadas");
            System.out.println("2. Ver top 10 goleadores");
            System.out.println("3. Ver top 10 asistentes");
            System.out.println("4. Ver jugadores con tarjetas");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1" -> simulador.mostrarPartidosJugados(scanner); // Muestra los partidos jugados por jornada
                case "2" -> mostrarTopGoleadores(simulador.getPartidos()); // Lista los 10 máximos goleadores
                case "3" -> mostrarTopAsistencias(simulador.getPartidos()); // Lista los 10 máximos asistentes
                case "4" -> mostrarJugadoresConTarjetas(simulador.getPartidos()); // Lista jugadores con tarjetas
                case "0" -> {
                    return; // Sale del menú y vuelve al menú principal
                }
                default -> System.out.println("Opción inválida. Intente de nuevo."); // Manejo de errores
            }
        }
    }

    /**
     * Muestra el Top 10 de goleadores de la liga, basado en la lista de partidos
     * simulados.
     * Se contabilizan todos los goles anotados por cada jugador a lo largo de la
     * liga.
     *
     * @param partidos Lista de partidos simulados en la liga.
     */
    public void mostrarTopGoleadores(List<Partido> partidos) {
        Map<Jugador, Integer> goles = new HashMap<>();

        // Recorre todos los partidos y acumula goles por jugador
        for (Partido partido : partidos) {
            for (Jugador j : partido.getGoleadoresLocal()) {
                goles.put(j, goles.getOrDefault(j, 0) + 1);
            }
            for (Jugador j : partido.getGoleadoresVisitante()) {
                goles.put(j, goles.getOrDefault(j, 0) + 1);
            }
        }

        // Ordena de mayor a menor y limita a los 10 mejores
        List<Map.Entry<Jugador, Integer>> top = goles.entrySet().stream()
                .sorted(Map.Entry.<Jugador, Integer>comparingByValue().reversed())
                .limit(10)
                .toList();

        // Imprime el ranking
        System.out.println("\n=== Top 10 Goleadores ===");
        System.out.printf("%-4s %-25s %-10s%n", "N°", "Jugador", "Goles");

        int pos = 1;
        for (Map.Entry<Jugador, Integer> entry : top) {
            System.out.printf("%-4d %-25s %-10d%n", pos++, entry.getKey().getNombre(), entry.getValue());
        }
    }

    /**
     * Muestra el Top 10 de asistentes de la liga, contabilizando todas las
     * asistencias registradas.
     *
     * @param partidos Lista de partidos simulados en la liga.
     */
    public void mostrarTopAsistencias(List<Partido> partidos) {
        Map<Jugador, Integer> asistencias = new HashMap<>();

        // Acumula asistencias por jugador
        for (Partido partido : partidos) {
            for (Jugador j : partido.getAsistentesLocal()) {
                asistencias.put(j, asistencias.getOrDefault(j, 0) + 1);
            }
            for (Jugador j : partido.getAsistentesVisitante()) {
                asistencias.put(j, asistencias.getOrDefault(j, 0) + 1);
            }
        }

        // Ordena de mayor a menor y limita a los 10 mejores
        List<Map.Entry<Jugador, Integer>> top = asistencias.entrySet().stream()
                .sorted(Map.Entry.<Jugador, Integer>comparingByValue().reversed())
                .limit(10)
                .toList();

        // Imprime el ranking
        System.out.println("\n=== Top 10 Asistentes ===");
        System.out.printf("%-4s %-25s %-12s%n", "N°", "Jugador", "Asistencias");

        int pos = 1;
        for (Map.Entry<Jugador, Integer> entry : top) {
            System.out.printf("%-4d %-25s %-12d%n", pos++, entry.getKey().getNombre(), entry.getValue());
        }
    }

    /**
     * Muestra los 10 jugadores con más tarjetas, ordenando por cantidad de rojas y
     * luego amarillas.
     *
     * @param partidos Lista de partidos simulados en la liga.
     */
    public void mostrarJugadoresConTarjetas(List<Partido> partidos) {
        // Mapa que almacena el conteo de tarjetas: [0] = rojas, [1] = amarillas
        Map<Jugador, int[]> tarjetas = new HashMap<>();

        // Recorre los partidos y acumula tarjetas por jugador
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

        // Ordena primero por cantidad de rojas, luego por amarillas (ambos en orden
        // descendente)
        List<Map.Entry<Jugador, int[]>> top = tarjetas.entrySet().stream()
                .sorted((a, b) -> {
                    int cmp = Integer.compare(b.getValue()[0], a.getValue()[0]); // comparar rojas
                    return (cmp != 0) ? cmp : Integer.compare(b.getValue()[1], a.getValue()[1]); // comparar amarillas
                })
                .limit(10)
                .toList();

        // Imprime el ranking
        System.out.println("\n=== Jugadores con Tarjetas ===");
        System.out.printf("%-4s %-25s %-8s %-10s%n", "N°", "Jugador", "Rojas", "Amarillas");

        int pos = 1;
        for (Map.Entry<Jugador, int[]> entry : top) {
            int rojas = entry.getValue()[0];
            int amarillas = entry.getValue()[1];
            System.out.printf("%-4d %-25s %-8d %-10d%n", pos++, entry.getKey().getNombre(), rojas, amarillas);
        }
    }

    /**
     * Devuelve el mapa completo que asocia cada liga con su lista de equipos.
     *
     * @return Mapa con los nombres de ligas como claves y listas de equipos como
     *         valores.
     */
    public Map<String, List<Equipo>> getEquiposPorLiga() {
        return equiposPorLiga;
    }

    /**
     * Devuelve una lista plana con todos los equipos cargados, sin importar la liga
     * a la que pertenezcan.
     *
     * @return Lista que contiene todos los equipos de todas las ligas.
     */
    public List<Equipo> getEquipos() {
        List<Equipo> todos = new ArrayList<>();

        // Recorre cada lista de equipos agrupada por liga y agrega sus elementos a la
        // lista final
        for (List<Equipo> lista : equiposPorLiga.values()) {
            todos.addAll(lista);
        }

        return todos;
    }

    // public void mostrarEquiposPorNombreParcial(String fragmento) {
    // List<Equipo> resultados = buscarEquipoPorNombre(fragmento);
    //
    // if (resultados.isEmpty()) {
    // System.out.println("No se encontraron equipos que coincidan con: " +
    // fragmento);
    // return;
    // }
    //
    // System.out.printf("%-28s | %-10s | %-20s | %4s\n", "Liga", "Escudo",
    // "Equipo", "ELO");
    // System.out.println("----------------------------------------------------------------------------");
    // for (Equipo equipo : resultados) {
    // System.out.printf("%-28s | %-10s | %-20s | %4d\n",
    // equipo.getLiga(), equipo.getEscudo(), equipo.getNombre(), equipo.getElo());
    // }
    // }
    //
    // public List<Equipo> listarEquipos() {
    // List<Equipo> todos = new ArrayList<>();
    // for (List<Equipo> lista : equiposPorLiga.values()) {
    // todos.addAll(lista);
    // }
    // return todos;
    // }
    //
    // public void mostrarTodosLosEquipos() {
    // List<Equipo> equipos = listarEquipos();
    // if (equipos.isEmpty()) {
    // System.out.println("No hay equipos cargados.");
    // return;
    // }
    // System.out.println("\n=== Listado de Equipos ===");
    // System.out.println("\nLiga | Escudo | Equipo | ELO");
    // System.out.println("----------------------------------------------------------------------------");
    //
    // for (Equipo equipo : equipos) {
    // System.out.printf("%-28s | %-10s | %-20s | %4d\n",
    // equipo.getLiga(),
    // equipo.getEscudo(),
    // equipo.getNombre(),
    // equipo.getElo());
    // }
    // }
    //
    // public boolean existeEquipo(String nombre) {
    // return buscarEquipoPorNombre(nombre) != null;
    // }
    //
    // public void mostrarEquiposPorLiga(String nombreLiga) {
    // for (String liga : equiposPorLiga.keySet()) {
    // if (liga.equalsIgnoreCase(nombreLiga)) {
    // System.out.println("\nLiga: " + liga);
    // System.out.printf("%-5s %-20s %-30s %-10s\n", "ID", "Escudo", "Nombre",
    // "Elo");
    // System.out.println("---------------------------------------------------------------");
    // for (Equipo e : equiposPorLiga.get(liga)) {
    // System.out.printf("%-5d %-20s %-30s %-10d\n",
    // e.getId(), e.getEscudo(), e.getNombre(), e.getElo());
    // }
    // return;
    // }
    // }
    // System.out.println("No se encontraron equipos para la liga: " + nombreLiga);
    // }
    // public void mostrarEquipo(String nombreEquipo) {
    // for (String liga : equiposPorLiga.keySet()) {
    // for (Equipo equipo : equiposPorLiga.get(liga)) {
    // if (equipo.getNombre().equalsIgnoreCase(nombreEquipo)) {
    // equipo.mostrarJugadores();
    // return;
    // }
    // }
    // }
    // System.out.println("No se encontró el equipo: " + nombreEquipo);
    // }
    //
    // public List<Equipo> filtrarPorLiga(String liga) {
    // return equiposPorLiga.getOrDefault(liga, new ArrayList<>());
    // }
    //
    // public void asignarJugadorAEquipo(Jugador jugador) throws TorneoException {
    // Equipo equipoJugador = jugador.getEquipo();
    // if (equipoJugador != null) {
    // Equipo equipoRegistrado = (Equipo)
    // buscarEquipoPorNombre(equipoJugador.getNombre());
    // if (equipoRegistrado != null) {
    // equipoRegistrado.agregarJugador(jugador);
    // } else {
    // agregarEquipo(equipoJugador);
    // equipoJugador.agregarJugador(jugador);
    // }
    // }
    // }
    //
    // public boolean eliminarEquipo(String nombre) {
    // for (String liga : equiposPorLiga.keySet()) {
    // List<Equipo> lista = equiposPorLiga.get(liga);
    // Iterator<Equipo> it = lista.iterator();
    // while (it.hasNext()) {
    // Equipo e = it.next();
    // if (e.getNombre().equalsIgnoreCase(nombre)) {
    // it.remove();
    // return true;
    // }
    // }
    // }
    // return false;
    // }
}
