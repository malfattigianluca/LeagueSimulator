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
     * Agrega un equipo al sistema, asignándole un ID único y ubicándolo según su liga.
     *
     * @param equipo El equipo a agregar.
     */
    public void agregarEquipo(Equipo equipo) {
        String liga = equipo.getLiga();

        equipo.setId(idEquipo++); // Asigna un ID único y lo incrementa

        // Agrega el equipo a la lista correspondiente en el mapa, creando la lista si no existe
        equiposPorLiga.computeIfAbsent(liga, k -> new ArrayList<>()).add(equipo);
    }



    /**
     * Busca equipos cuyo nombre contenga un fragmento de texto (insensible a mayúsculas).
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
     * Interactúa con el usuario para buscar equipos por nombre y mostrar los resultados formateados.
     *
     * @param scanner Objeto Scanner para leer la entrada del usuario.
     */
    public void buscarEquipos(Scanner scanner) {
        System.out.print("Ingrese el fragmento a buscar en el nombre del equipo: ");
        String fragmento = scanner.nextLine().toLowerCase(); // Lectura y normalización del fragmento

        // Busca equipos cuyo nombre contenga el fragmento, usando streams
        List<Equipo> resultados = equiposPorLiga.values().stream()
                .flatMap(List::stream)
                .filter(equipo -> equipo.getNombre().toLowerCase().contains(fragmento))
                .toList();

        // Muestra los resultados o un mensaje si no se encontraron coincidencias
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron equipos que coincidan.");
        } else {

            // Encabezado de la tabla
            System.out.printf("%-35s %-8s %-25s %-15s %-10s%n", "Liga", "Escudo", "Equipo", "País", "ELO");
            System.out.println("---------------------------------------------------------------------------------------");

            // Cuerpo de la tabla con alineación formateada
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

    /**
     * Muestra por consola todos los equipos cargados, filtrando por liga si se especifica.
     * Imprime los datos formateados en columnas: Liga, Código (escudo), Nombre del equipo, País, ELO.
     *
     * @param liga Nombre de la liga a filtrar. Si es null o vacío, muestra todos los equipos de todas las ligas.
     */
    public void mostrarTodosLosEquipos(String liga) {
        // Encabezado de tabla
        System.out.printf("%-35s %-8s %-25s %-15s %-10s%n", "Liga", "Cod", "Equipo", "País", "ELO");
        System.out.println("---------------------------------------------------------------------------------------");

        // Si se especifica una liga, filtrar por ella
        if (liga != null && !liga.trim().isEmpty()) {
            List<Equipo> equipos = equiposPorLiga.getOrDefault(liga, new ArrayList<>());
            if (equipos.isEmpty()) {
                System.out.println("No se encontraron equipos para la liga: " + liga);
                return;
            }
            for (Equipo equipo : equipos) {
                System.out.printf("%-35s %-8s %-25s %-15s %-10d%n",
                        equipo.getLiga(),
                        equipo.getEscudo(),
                        equipo.getNombre(),
                        equipo.getPais(),
                        equipo.getElo());
            }
        } else {
            // Si no se especifica liga, mostrar todos los equipos de todas las ligas
            for (Map.Entry<String, List<Equipo>> entry : equiposPorLiga.entrySet()) {
                for (Equipo equipo : entry.getValue()) {
                    System.out.printf("%-35s %-8s %-25s %-15s %-10d%n",
                            equipo.getLiga(),
                            equipo.getEscudo(),
                            equipo.getNombre(),
                            equipo.getPais(),
                            equipo.getElo());
                }
            }
        }
    }

    /**
     * Muestra un menú con todas las ligas disponibles y permite al usuario seleccionar una opción.
     * Incluye una opción adicional para mostrar todos los equipos sin filtrar por liga.
     *
     * @param scanner Scanner utilizado para leer la entrada del usuario desde consola.
     * @return El nombre de la liga seleccionada, o "TODAS" si el usuario elige ver todas las ligas.
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
                if (opcion >= 0 && opcion <= ligas.size()) break;
                else System.out.println("Número fuera de rango.");
            } catch (NumberFormatException e) {
                System.out.println("Entrada no válida. Ingrese un número.");
            }
        }

        // Si elige 0, se interpretará como solicitud de mostrar todas las ligas
        if (opcion == 0) return "TODAS";
        // Si elige una liga específica, devolver su nombre
        return ligas.get(opcion - 1);
    }


    /**
     * Muestra al usuario las ligas disponibles y permite seleccionar una para ver sus equipos.
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
     * Simula una liga seleccionada por el usuario, ejecutando todas sus jornadas.
     * Reinicia las estadísticas de los equipos, genera el calendario de partidos
     * y muestra la tabla final al finalizar la simulación.
     *
     * @param scanner Scanner utilizado para la entrada de datos del usuario.
     * @param gestorJugadores Referencia al gestor de jugadores (puede ser usado en el menú post-simulación).
     */
    public void simularLiga(Scanner scanner, GestorJugadores gestorJugadores) {
        // Verifica si hay ligas cargadas
        if (equiposPorLiga.isEmpty()) {
            System.out.println("No hay ligas disponibles para simular.");
            return;
        }

        // Muestra las ligas disponibles para que el usuario seleccione una
        System.out.println("Seleccione una liga para simular:");
        List<String> ligas = new ArrayList<>(equiposPorLiga.keySet());
        for (int i = 0; i < ligas.size(); i++) {
            System.out.println((i + 1) + ". " + ligas.get(i));
        }

        // Lee la opción ingresada por el usuario
        int opcion = Integer.parseInt(scanner.nextLine());
        if (opcion < 1 || opcion > ligas.size()) {
            System.out.println("Opción inválida.");
            return;
        }

        // Obtiene el nombre de la liga seleccionada y sus equipos
        String ligaSeleccionada = ligas.get(opcion - 1);
        List<Equipo> equipos = equiposPorLiga.get(ligaSeleccionada);

        // Crea un simulador para la liga seleccionada
        SimuladorLiga simulador = new SimuladorLiga(ligaSeleccionada);

        // Reinicia las estadísticas de cada equipo y los agrega al simulador
        for (Equipo equipo : equipos) {
            equipo.reiniciarEstadisticas();
            simulador.agregarEquipo(equipo);
        }

        // Intenta generar el calendario de partidos
        try {
            simulador.generarCalendario();
        } catch (TorneoException e) {
            System.err.println("No se pudo generar el calendario: " + e.getMessage());
            return;
        }

        // Informa cuántas jornadas tendrá la simulación
        int totalJornadas = simulador.getTotalJornadas();
        System.out.println("\nComenzando simulación de la liga " + ligaSeleccionada + " (" + totalJornadas + " jornadas)");

        // Ejecuta la simulación jornada por jornada
        for (int jornada = 1; jornada <= totalJornadas; jornada++) {
            try {
                List<Partido> jornadas = simulador.simularJornada(); // Simula los partidos de la jornada
                simulador.agregarJornada(jornadas); // Registra los partidos simulados
            } catch (TorneoException e) {
                System.err.println("Error en la jornada: " + e.getMessage());
            }
        }

        // Muestra la tabla de posiciones finalizada
        simulador.mostrarTabla();

        // Muestra menú interactivo con estadísticas y opciones post-simulación
        mostrarMenuPostSimulacion(scanner, simulador);
    }





    public void crearTorneoPersonalizado(Scanner scanner) throws TorneoException {
        GestorJugadores gestorJugadores = new GestorJugadores(this);
        System.out.print("Ingrese el nombre del torneo personalizado: ");
        String nombre = scanner.nextLine();

        System.out.println("""
        Seleccione la modalidad del torneo:
        1. Fase de grupos (todos contra todos - ida y vuelta)
        2. Eliminación directa
        3. Mixto (grupos + eliminación directa)
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

        List<Equipo> disponibles = getEquipos();
        if (disponibles.isEmpty()) {
            System.out.println("No hay equipos disponibles para crear un torneo.");
            return;
        }

        // 👇 Se reemplaza la lógica de selección por una sola línea
        List<Equipo> seleccionados = seleccionarEquipos(scanner, modalidad, disponibles);

        switch (modalidad) {
            case 1 -> { // Fase de grupos estilo liga
                SimuladorLiga simulador = new SimuladorLiga(nombre);

                for (Equipo equipo : seleccionados) {
                    equipo.reiniciarEstadisticas();
                    simulador.agregarEquipo(equipo);
                }

                try {
                    simulador.generarCalendario();

                    // Simular todas las jornadas ida y vuelta
                    for (int i = 0; i < simulador.getTotalJornadas(); i++) {
                        List<Partido> jornada = simulador.simularJornada();
                        simulador.agregarJornada(jornada);
                    }

                    simuladoresPorLiga.put(nombre, simulador);

                    System.out.println("✅ Torneo de liga '" + nombre + "' creado y simulado exitosamente.\n");

                    simulador.mostrarTabla();

                    // Menú post simulación (ver fechas, goleadores, asistencias, etc.)
                    mostrarMenuPostSimulacion(scanner, simulador);

                } catch (TorneoException e) {
                    System.out.println("❌ Error al generar el calendario: " + e.getMessage());
                }
            }


            case 2 -> { // Eliminación directa con árbol binario

                List<Equipo> equiposdisponibles = getEquipos().stream()
                        .filter(e -> !seleccionados.contains(e))
                        .toList();

                for (int i = 0; i < equiposdisponibles.size(); i++) {
                    System.out.println((i + 1) + ". " + equiposdisponibles.get(i).getNombre());
                }

                while (!esPotenciaDeDos(seleccionados.size())) {
                    System.out.println("\n⚠ Necesitás ingresar una cantidad de equipos que sea potencia de 2 (2, 4, 8, 16, ...).");
                    System.out.println("Actualmente hay " + seleccionados.size() + " equipo(s) seleccionados.");
                    System.out.println("Ingrese el número del equipo a agregar (0 para cancelar):");

                    try {
                        int opcion = Integer.parseInt(scanner.nextLine());

                        if (opcion == 0) {
                            System.out.println("Operación cancelada.");
                            return;
                        }

                        if (opcion >= 1 && opcion <= disponibles.size()) {
                            Equipo elegido = equiposdisponibles.get(opcion - 1);
                            seleccionados.add(elegido);
                            System.out.println("Equipo agregado: " + elegido.getNombre());
                        } else {
                            System.out.println("Número fuera de rango.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Entrada inválida. Ingrese un número.");
                    }
                }

                // Crear y simular el torneo
                Torneo torneo = new Torneo(nombre, true);
                for (Equipo equipo : seleccionados) {
                    equipo.reiniciarEstadisticas();
                    torneo.agregarEquipo(equipo);
                }

                try {
                    torneo.simularTorneo();
                } catch (TorneoException e) {
                    System.out.println("Error al simular torneo: " + e.getMessage());
                    return;
                }

                simuladoresPorLiga.put(nombre, null);
                System.out.println("\n✅ Torneo de eliminación directa '" + nombre + "' creado exitosamente.");
                mostrarMenuPostTorneo(scanner, torneo);
            }



            case 3 -> { // Mixto (grupos + eliminación directa)
                int cantidadEquipos = seleccionados.size();
                int grupos = 1;

                for (int g = 2; g <= cantidadEquipos / 2; g++) {
                    if (cantidadEquipos % g == 0) {
                        int equiposPorGrupo = cantidadEquipos / g;
                        if (equiposPorGrupo >= 4 && equiposPorGrupo <= 6) {
                            grupos = g;
                            break;
                        }
                    }
                }

                if (grupos == 1) {
                    System.out.println("No se pudo dividir en grupos razonables. Usando 2 grupos por defecto.");
                    grupos = 2;
                }

                System.out.printf("✅ Se usarán %d grupo(s) de %d equipos cada uno.%n", grupos, cantidadEquipos / grupos);

                int clasificados = 2;
                System.out.printf("✅ Se clasificarán %d equipos por grupo a la fase eliminatoria.%n", clasificados);

                TorneoMixto torneoMixto = new TorneoMixto(nombre, grupos, clasificados);
                for (Equipo equipo : seleccionados) {
                    equipo.reiniciarEstadisticas();
                    torneoMixto.agregarEquipo(equipo);
                }

                try {
                    torneoMixto.simularTorneo();
                } catch (TorneoException e) {
                    System.out.println("Error en torneo mixto: " + e.getMessage());
                    return;
                }

                torneoMixto.imprimirLlavesEliminacion();
                mostrarMenuPostTorneoMixto(scanner, torneoMixto);
            }
        }
    }


    private List<Equipo> seleccionarEquipos(Scanner scanner, int modalidad, List<Equipo> disponibles) {
        List<Equipo> seleccionados = new ArrayList<>();

        System.out.printf("%n=== Equipos disponibles ===%n");
        for (int i = 0; i < disponibles.size(); i++) {
            System.out.printf("%3d. %s (%s)%n", i + 1, disponibles.get(i).getNombre(), disponibles.get(i).getLiga());
        }
        while (true) {
            System.out.printf("%nActualmente hay %d equipo(s) seleccionados.%n", seleccionados.size());
            System.out.print("Ingrese el número del equipo a agregar (0 para finalizar): ");

            try {
                int index = Integer.parseInt(scanner.nextLine()) - 1;

                if (index == -1) {
                    if (seleccionados.size() < 2) {
                        System.out.println("⚠️ Se necesitan al menos 2 equipos para un torneo.");
                        continue;
                    }

                    if ((modalidad == 1 || modalidad == 3) && seleccionados.size() % 2 != 0) {
                        System.out.println("⚠️ Esta modalidad requiere una cantidad PAR de equipos.");
                        continue;
                    }

                    return seleccionados; // válido, sale del metodo
                }

                if (index < 0 || index >= disponibles.size()) {
                    System.out.println("Índice fuera de rango.");
                } else if (seleccionados.contains(disponibles.get(index))) {
                    System.out.println("Ese equipo ya fue seleccionado.");
                } else {
                    seleccionados.add(disponibles.get(index));
                    System.out.printf("✅ Equipo agregado: %s%n", disponibles.get(index).getNombre());
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida.");
            }
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
                case "1" -> simulador.mostrarPartidosJugados(scanner);  // Muestra los partidos jugados por jornada
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

    private void mostrarMenuPostTorneo(Scanner scanner, Torneo torneo) {
        while (true) {
            // Título del menú
            System.out.println("\n=== Estadísticas de la Liga " + torneo.getNombre() + " ===");
            System.out.println("1. Ver fechas jugadas");
            System.out.println("2. Ver top 10 goleadores");
            System.out.println("3. Ver top 10 asistentes");
            System.out.println("4. Ver jugadores con tarjetas");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1" -> torneo.mostrarPartidosTorneo(scanner);  // Muestra los partidos jugados por jornada
                case "2" -> mostrarTopGoleadores(torneo.getPartidos()); // Lista los 10 máximos goleadores
                case "3" -> mostrarTopAsistencias(torneo.getPartidos()); // Lista los 10 máximos asistentes
                case "4" -> mostrarJugadoresConTarjetas(torneo.getPartidos()); // Lista jugadores con tarjetas
                case "0" -> {
                    return; // Sale del menú y vuelve al menú principal
                }
                default -> System.out.println("Opción inválida. Intente de nuevo."); // Manejo de errores
            }
        }
    }

    private void mostrarMenuPostTorneoMixto(Scanner scanner, TorneoMixto torneoMixto) {
        while (true) {
            // Título del menú
            System.out.println("\n=== Estadísticas de la Liga " + torneoMixto.getNombre() + " ===");
            System.out.println("1. Ver fechas jugadas");
            System.out.println("2. Ver top 10 goleadores");
            System.out.println("3. Ver top 10 asistentes");
            System.out.println("4. Ver jugadores con tarjetas");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1" -> torneoMixto.mostrarPartidosTorneo(scanner);  // Muestra los partidos jugados por jornada
                case "2" -> mostrarTopGoleadores(torneoMixto.getPartidos()); // Lista los 10 máximos goleadores
                case "3" -> mostrarTopAsistencias(torneoMixto.getPartidos()); // Lista los 10 máximos asistentes
                case "4" -> mostrarJugadoresConTarjetas(torneoMixto.getPartidos()); // Lista jugadores con tarjetas
                case "0" -> {
                    return; // Sale del menú y vuelve al menú principal
                }
                default -> System.out.println("Opción inválida. Intente de nuevo."); // Manejo de errores
            }
        }
    }



    /**
     * Muestra el Top 10 de goleadores de la liga, basado en la lista de partidos simulados.
     * Se contabilizan todos los goles anotados por cada jugador a lo largo de la liga.
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
     * Muestra el Top 10 de asistentes de la liga, contabilizando todas las asistencias registradas.
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
     * Muestra los 10 jugadores con más tarjetas, ordenando por cantidad de rojas y luego amarillas.
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

        // Ordena primero por cantidad de rojas, luego por amarillas (ambos en orden descendente)
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
     * @return Mapa con los nombres de ligas como claves y listas de equipos como valores.
     */
    public Map<String, List<Equipo>> getEquiposPorLiga() {
        return equiposPorLiga;
    }

    /**
     * Devuelve una lista plana con todos los equipos cargados, sin importar la liga a la que pertenezcan.
     *
     * @return Lista que contiene todos los equipos de todas las ligas.
     */
    public List<Equipo> getEquipos() {
        List<Equipo> todos = new ArrayList<>();

        // Recorre cada lista de equipos agrupada por liga y agrega sus elementos a la lista final
        for (List<Equipo> lista : equiposPorLiga.values()) {
            todos.addAll(lista);
        }

        return todos;
    }

    private boolean esPotenciaDeDos(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }


//    public void mostrarEquiposPorNombreParcial(String fragmento) {
//        List<Equipo> resultados = buscarEquipoPorNombre(fragmento);
//
//        if (resultados.isEmpty()) {
//            System.out.println("No se encontraron equipos que coincidan con: " + fragmento);
//            return;
//        }
//
//        System.out.printf("%-28s | %-10s | %-20s | %4s\n", "Liga", "Escudo", "Equipo", "ELO");
//        System.out.println("----------------------------------------------------------------------------");
//        for (Equipo equipo : resultados) {
//            System.out.printf("%-28s | %-10s | %-20s | %4d\n",
//                    equipo.getLiga(), equipo.getEscudo(), equipo.getNombre(), equipo.getElo());
//        }
//    }
//
//    public List<Equipo> listarEquipos() {
//        List<Equipo> todos = new ArrayList<>();
//        for (List<Equipo> lista : equiposPorLiga.values()) {
//            todos.addAll(lista);
//        }
//        return todos;
//    }
//
//    public void mostrarTodosLosEquipos() {
//        List<Equipo> equipos = listarEquipos();
//        if (equipos.isEmpty()) {
//            System.out.println("No hay equipos cargados.");
//            return;
//        }
//        System.out.println("\n=== Listado de Equipos ===");
//        System.out.println("\nLiga                         |   Escudo   | Equipo                  | ELO");
//        System.out.println("----------------------------------------------------------------------------");
//
//        for (Equipo equipo : equipos) {
//            System.out.printf("%-28s | %-10s | %-20s | %4d\n",
//                    equipo.getLiga(),
//                    equipo.getEscudo(),
//                    equipo.getNombre(),
//                    equipo.getElo());
//        }
//    }
//
//    public boolean existeEquipo(String nombre) {
//        return buscarEquipoPorNombre(nombre) != null;
//    }
//
//    public void mostrarEquiposPorLiga(String nombreLiga) {
//        for (String liga : equiposPorLiga.keySet()) {
//            if (liga.equalsIgnoreCase(nombreLiga)) {
//                System.out.println("\nLiga: " + liga);
//                System.out.printf("%-5s %-20s %-30s %-10s\n", "ID", "Escudo", "Nombre", "Elo");
//                System.out.println("---------------------------------------------------------------");
//                for (Equipo e : equiposPorLiga.get(liga)) {
//                    System.out.printf("%-5d %-20s %-30s %-10d\n",
//                            e.getId(), e.getEscudo(), e.getNombre(), e.getElo());
//                }
//                return;
//            }
//        }
//        System.out.println("No se encontraron equipos para la liga: " + nombreLiga);
//    }
//    public void mostrarEquipo(String nombreEquipo) {
//        for (String liga : equiposPorLiga.keySet()) {
//            for (Equipo equipo : equiposPorLiga.get(liga)) {
//                if (equipo.getNombre().equalsIgnoreCase(nombreEquipo)) {
//                    equipo.mostrarJugadores();
//                    return;
//                }
//            }
//        }
//        System.out.println("No se encontró el equipo: " + nombreEquipo);
//    }
//
//    public List<Equipo> filtrarPorLiga(String liga) {
//        return equiposPorLiga.getOrDefault(liga, new ArrayList<>());
//    }
//
//    public void asignarJugadorAEquipo(Jugador jugador) throws TorneoException {
//        Equipo equipoJugador = jugador.getEquipo();
//        if (equipoJugador != null) {
//            Equipo equipoRegistrado = (Equipo) buscarEquipoPorNombre(equipoJugador.getNombre());
//            if (equipoRegistrado != null) {
//                equipoRegistrado.agregarJugador(jugador);
//            } else {
//                agregarEquipo(equipoJugador);
//                equipoJugador.agregarJugador(jugador);
//            }
//        }
//    }
//
//    public boolean eliminarEquipo(String nombre) {
//        for (String liga : equiposPorLiga.keySet()) {
//            List<Equipo> lista = equiposPorLiga.get(liga);
//            Iterator<Equipo> it = lista.iterator();
//            while (it.hasNext()) {
//                Equipo e = it.next();
//                if (e.getNombre().equalsIgnoreCase(nombre)) {
//                    it.remove();
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
}