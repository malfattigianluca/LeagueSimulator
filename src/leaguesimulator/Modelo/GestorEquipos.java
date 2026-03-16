package leaguesimulator.Modelo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import leaguesimulator.Excepciones.TorneoException;

/**
 * Clase GestorEquipos
 *
 * Esta clase se encarga de gestionar los equipos de fútbol agrupados por liga,
 * así como de asociar simuladores de ligas correspondientes a cada liga cargada.
 * También maneja la asignación de IDs únicos para nuevos equipos que se registren.
 */
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

        // Asigna un ID único y lo incrementa
        equipo.setId(idEquipo++);

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

        // Convierte el fragmento a minúsculas
        String fragmentoLower = fragmento.toLowerCase();

        // Recorre todas las listas de equipos por liga
        for (List<Equipo> lista : equiposPorLiga.values()) {
            for (Equipo equipo : lista) {
                // Compara el nombre del equipo ignorando mayúsculas
                if (equipo.getNombre().toLowerCase().contains(fragmentoLower)) {
                    resultados.add(equipo);
                }
            }
        }
        // Devuelve la lista de equipos encontrados
        return resultados;
    }

    /**
     * Interactúa con el usuario para buscar equipos por nombre y mostrar los resultados formateados.
     *
     * @param scanner Objeto Scanner para leer la entrada del usuario.
     */
    public void buscarEquipos(Scanner scanner) {
        System.out.print("Ingrese el fragmento a buscar en el nombre del equipo: ");

        // Lectura y normalización del fragmento
        String fragmento = scanner.nextLine().toLowerCase();

        // Busca equipos cuyo nombre contenga el fragmento, usando streams
        List<Equipo> resultados = equiposPorLiga.values().stream()
                .flatMap(List::stream)
                .filter(equipo -> equipo.getNombre().toLowerCase().contains(fragmento))
                .toList();

        // Muestra los resultados o un mensaje si no se encontraron coincidencias
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron equipos que coincidan.");
        } else {

            // Cuerpo de la tabla con alineación formateada
            for (Equipo e : resultados) {
                // Encabezado de la tabla
                System.out.printf("%-35s %-8s %-25s %-15s %-10s%n", "Liga", "Escudo", "Equipo", "País", "ELO");
                System.out.println("---------------------------------------------------------------------------------------");
                System.out.printf("%-35s %-8s %-25s %-15s %-10d%n",
                        e.getLiga(),
                        e.getEscudo(),
                        e.getNombre(),
                        e.getPais(),
                        e.getElo());
                        imprimirJugadores(e);
            }
        }

    }

    private void imprimirJugadores(Equipo equipo) {
        System.out.println("\nJugadores del equipo: " + equipo.getNombre());

        List<Jugador> jugadores = equipo.getJugadores().getVertices();

        List<Jugador> titulares = new ArrayList<>();
        List<Jugador> suplentes = new ArrayList<>();

        for (Jugador j : jugadores) {
            if (j.isTitular()) {
                titulares.add(j);
            } else {
                suplentes.add(j);
            }
        }

        // Se ordena segun niveles por posición de manera personalizada siguiendo el orden real de alineaciones
        Map<String, Double> ordenPosiciones = new HashMap<>();
        ordenPosiciones.put("portero", 0.001);
        ordenPosiciones.put("defensa central", 0.01);
        ordenPosiciones.put("lateral derecho", 0.02);
        ordenPosiciones.put("lateral izquierdo", 0.02);
        ordenPosiciones.put("pivote", 0.03);
        ordenPosiciones.put("mediocentro", 0.05);
        ordenPosiciones.put("interior derecho", 0.08);
        ordenPosiciones.put("interior izquierdo", 0.08);
        ordenPosiciones.put("mediocentro ofensivo", 0.10);
        ordenPosiciones.put("mediapunta", 0.13);
        ordenPosiciones.put("extremo derecho", 0.18);
        ordenPosiciones.put("extremo izquierdo", 0.18);
        ordenPosiciones.put("delantero centro", 0.35);

        Comparator<Jugador> comparadorPorPosicion = Comparator.comparingDouble(
                j -> ordenPosiciones.getOrDefault(j.getPosicion().toLowerCase(), Double.MAX_VALUE)
        );

        titulares.sort(comparadorPorPosicion);
        suplentes.sort(comparadorPorPosicion);

        // Titulares
        System.out.println("Titulares:");
        System.out.printf("%-5s %-25s %-25s%n", "N°", "Nombre", "Posición");
        for (Jugador j : titulares) {
            System.out.printf("%-5d %-25s %-25s%n",
                    j.getNumeroCamiseta(),
                    j.getNombre(),
                    j.getPosicion());
        }

        // Suplentes
        System.out.println("\nSuplentes:");
        System.out.printf("%-5s %-25s %-25s%n", "N°", "Nombre", "Posición");
        for (Jugador j : suplentes) {
            System.out.printf("%-5d %-25s %-25s%n",
                    j.getNumeroCamiseta(),
                    j.getNombre(),
                    j.getPosicion());
        }

        System.out.println("---------------------------------------------------------------------------------------\n");
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

    /**
     * Permite al usuario crear un torneo personalizado eligiendo una de tres modalidades:
     *   1. Fase de grupos (tipo liga, todos contra todos, ida y vuelta)
     *   2. Eliminación directa (formato bracket con árbol binario)
     *   3. Mixto (fase de grupos + eliminación directa)
     *
     * El metodo guía al usuario para:
     * - Ingresar un nombre para el torneo.
     * - Seleccionar la modalidad de competencia.
     * - Elegir los equipos participantes desde una lista de disponibles.
     * - Simular automáticamente el torneo (según la modalidad).
     * - Mostrar tablas, fechas o estadísticas según corresponda.
     *
     * Dependiendo del tipo de torneo, se utilizan instancias de:
     *   - SimuladorLiga: para torneos de liga (fase de grupos).
     *   - Torneo: para torneos de eliminación directa.
     *   - TorneoMixto: para torneos que combinan ambas modalidades.
     *
     * @param scanner objeto Scanner para leer la entrada del usuario.
     * @throws TorneoException si ocurre un error durante la simulación.
     */
    public void crearTorneoPersonalizado(Scanner scanner) throws TorneoException {
        // Instancia el gestor de jugadores asociado a este gestor de equipos
        GestorJugadores gestorJugadores = new GestorJugadores(this);

        // Solicita el nombre del torneo
        System.out.print("Ingrese el nombre del torneo personalizado: ");
        String nombre = scanner.nextLine();

        // Muestra el menú de modalidades de torneo disponibles
        System.out.println("""
        Seleccione la modalidad del torneo:
        1. Fase de grupos (todos contra todos - ida y vuelta)
        2. Eliminación directa
        3. Mixto (grupos + eliminación directa)
    """);

        int modalidad = -1;

        // Validación del ingreso: solo se permite 1, 2 o 3 como opción válida
        while (modalidad < 1 || modalidad > 3) {
            try {
                System.out.print("Opción: ");
                modalidad = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un número válido (1-3).");
            }
        }

        // Obtiene la lista de equipos disponibles en el sistema
        List<Equipo> disponibles = getEquipos();
        if (disponibles.isEmpty()) {
            System.out.println("No hay equipos disponibles para crear un torneo.");
            return;
        }

        // Llama al metodo para seleccionar equipos en base a la modalidad elegida
        List<Equipo> seleccionados = seleccionarEquipos(scanner, modalidad, disponibles);

        // A partir de aquí comienza el bloque que define el comportamiento para cada modalidad
        switch (modalidad) {

            // --------------------------------------
            // 1. FASE DE GRUPOS (SIMULADOR DE LIGA)
            // --------------------------------------
            case 1 -> {
                // Crea una nueva liga con el nombre indicado
                SimuladorLiga simulador = new SimuladorLiga(nombre);

                // Se reinician las estadísticas y se agregan los equipos seleccionados
                for (Equipo equipo : seleccionados) {
                    equipo.reiniciarEstadisticas();
                    simulador.agregarEquipo(equipo);
                }

                try {
                    // Genera el calendario de partidos (ida y vuelta)
                    simulador.generarCalendario();

                    // Simula cada jornada y la agrega a la lista de fechas jugadas
                    for (int i = 0; i < simulador.getTotalJornadas(); i++) {
                        List<Partido> jornada = simulador.simularJornada();
                        simulador.agregarJornada(jornada);
                    }

                    // Se guarda el simulador con el nombre del torneo para futuras consultas
                    simuladoresPorLiga.put(nombre, simulador);

                    // Informa al usuario y muestra la tabla de posiciones final
                    System.out.println("Torneo de liga '" + nombre + "' creado y simulado exitosamente.\n");
                    simulador.mostrarTabla();

                    // Menú de opciones post-simulación (ver fechas, estadísticas, etc.)
                    mostrarMenuPostSimulacion(scanner, simulador);

                } catch (TorneoException e) {
                    System.out.println("Error al generar el calendario: " + e.getMessage());
                }
            }

            // --------------------------------------
            // 2. ELIMINACIÓN DIRECTA
            // --------------------------------------
            case 2 -> {
                // Obtiene los equipos que aún no fueron seleccionados
                List<Equipo> equiposdisponibles = getEquipos().stream()
                        .filter(e -> !seleccionados.contains(e))
                        .toList();

                // Muestra equipos restantes disponibles para completar la llave
                for (int i = 0; i < equiposdisponibles.size(); i++) {
                    System.out.println((i + 1) + ". " + equiposdisponibles.get(i).getNombre());
                }

                // Asegura que la cantidad total de equipos sea una potencia de 2
                while (!esPotenciaDeDos(seleccionados.size())) {
                    System.out.println("\n❌ Necesitás ingresar una cantidad de equipos que sea potencia de 2 (2, 4, 8, 16, ...).");
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

                // Se crea el torneo y se reinician estadísticas
                Torneo torneo = new Torneo(nombre, true);
                for (Equipo equipo : seleccionados) {
                    equipo.reiniciarEstadisticas();
                    torneo.agregarEquipo(equipo);
                }

                // Simula el torneo en formato de eliminación directa (árbol binario)
                try {
                    torneo.simularTorneo();
                } catch (TorneoException e) {
                    System.out.println("Error al simular torneo: " + e.getMessage());
                    return;
                }

                simuladoresPorLiga.put(nombre, null); // No se guarda simulador de liga
                System.out.println("\nTorneo de eliminación directa '" + nombre + "' creado exitosamente.");
                mostrarMenuPostTorneo(scanner, torneo); // Muestra menú post torneo
            }

            // --------------------------------------
            // 3. TORNEO MIXTO (GRUPOS + ELIMINACIÓN)
            // --------------------------------------
            case 3 -> {
                int cantidadEquipos = seleccionados.size();
                int grupos = 1;

                // Se calcula la cantidad de grupos razonable (entre 4 y 6 equipos por grupo)
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

                // Se indica al usuario cómo se organizarán los grupos
                System.out.printf("Se usarán %d grupo(s) de %d equipos cada uno.%n", grupos, cantidadEquipos / grupos);

                int clasificados = 2; // Equipos que avanzan por grupo
                System.out.printf("Se clasificarán %d equipos por grupo a la fase eliminatoria.%n", clasificados);

                // Se construye el torneo mixto y se agregan los equipos
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

                // Imprime las llaves de la fase eliminatoria y muestra menú de estadísticas
                torneoMixto.imprimirLlavesEliminacion();
                mostrarMenuPostTorneoMixto(scanner, torneoMixto);
            }
        }
    }

    /**
     * Permite al usuario seleccionar equipos desde una lista de disponibles para formar parte de un torneo.
     * El metodo muestra los equipos numerados, y el usuario ingresa los números correspondientes.
     * La selección se valida de acuerdo a la modalidad del torneo:
     *
     * - Se requiere al menos 2 equipos para cualquier torneo.
     * - Para modalidades de tipo Liga (1) o Mixto (3), se requiere una cantidad PAR de equipos.
     * - No se permite seleccionar el mismo equipo dos veces.
     *
     * El proceso termina cuando el usuario ingresa 0 y la cantidad mínima (y par, si aplica) ha sido alcanzada.
     *
     * @param scanner objeto Scanner para capturar la entrada del usuario.
     * @param modalidad número que representa la modalidad del torneo (1 = Liga, 2 = Eliminación, 3 = Mixto).
     * @param disponibles lista de equipos que pueden ser seleccionados.
     * @return una lista de equipos seleccionados por el usuario.
     */
    /**
     * Permite al usuario seleccionar equipos desde una lista de disponibles para conformar un torneo.
     *
     * El usuario ingresa por consola los números correspondientes a los equipos. La selección se completa
     * cuando se ingresa 0, siempre y cuando se cumplan las condiciones mínimas:
     * - Al menos 2 equipos deben estar seleccionados.
     * - Para torneos de modalidad 1 (liga) o 3 (mixto), la cantidad de equipos debe ser par.
     *
     * @param scanner Objeto Scanner utilizado para leer la entrada del usuario.
     * @param modalidad Tipo de torneo (1: liga, 2: eliminación directa, 3: mixto).
     * @param disponibles Lista de equipos disponibles para seleccionar.
     * @return Una lista de equipos seleccionados por el usuario.
     */
    private List<Equipo> seleccionarEquipos(Scanner scanner, int modalidad, List<Equipo> disponibles) {
        List<Equipo> seleccionados = new ArrayList<>();

        // Muestra todos los equipos disponibles numerados
        System.out.printf("%n=== Equipos disponibles ===%n");
        for (int i = 0; i < disponibles.size(); i++) {
            System.out.printf("%3d. %s (%s)%n", i + 1, disponibles.get(i).getNombre(), disponibles.get(i).getLiga());
        }

        // Bucle de selección interactiva
        while (true) {
            System.out.printf("%nActualmente hay %d equipo(s) seleccionados.%n", seleccionados.size());
            System.out.print("Ingrese el número del equipo a agregar (0 para finalizar): ");

            try {
                int index = Integer.parseInt(scanner.nextLine()) - 1;

                // El usuario desea finalizar la selección
                if (index == -1) {
                    // Valida cantidad mínima
                    if (seleccionados.size() < 2) {
                        System.out.println("⚠️ Se necesitan al menos 2 equipos para un torneo.");
                        continue;
                    }

                    // Valida paridad para modalidad 1 o 3
                    if ((modalidad == 1 || modalidad == 3) && seleccionados.size() % 2 != 0) {
                        System.out.println("⚠️ Esta modalidad requiere una cantidad PAR de equipos.");
                        continue;
                    }

                    return seleccionados; // Selección válida, retorna
                }

                // Validaciones de rango y duplicados
                if (index < 0 || index >= disponibles.size()) {
                    System.out.println("Índice fuera de rango.");
                } else if (seleccionados.contains(disponibles.get(index))) {
                    System.out.println("Ese equipo ya fue seleccionado.");
                } else {
                    // Agrega el equipo seleccionado
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

    /**
     * Muestra un menú interactivo luego de la simulación de un torneo de eliminación directa.
     * Permite al usuario consultar diferentes estadísticas del torneo ya finalizado.
     *
     * Las opciones disponibles son:
     * <ul>
     *   <li><b>1 - Ver fechas jugadas:</b> muestra los partidos organizados por jornadas.</li>
     *   <li><b>2 - Ver top 10 goleadores:</b> muestra los 10 jugadores con más goles.</li>
     *   <li><b>3 - Ver top 10 asistentes:</b> muestra los 10 jugadores con más asistencias.</li>
     *   <li><b>4 - Ver jugadores con tarjetas:</b> muestra los jugadores que recibieron tarjetas.</li>
     *   <li><b>0 - Volver al menú principal:</b> finaliza este submenú y retorna al menú principal.</li>
     * </ul>
     *
     * El menú se mantiene activo hasta que el usuario seleccione la opción "0".
     *
     * @param scanner Objeto Scanner para leer la entrada del usuario.
     * @param torneo Objeto Torneo ya simulado desde el cual se obtienen los datos estadísticos.
     */
    private void mostrarMenuPostTorneo(Scanner scanner, Torneo torneo) {
        while (true) {
            // Título del menú contextual
            System.out.println("\n=== Estadísticas de la Liga " + torneo.getNombre() + " ===");
            System.out.println("1. Ver fechas jugadas");
            System.out.println("2. Ver top 10 goleadores");
            System.out.println("3. Ver top 10 asistentes");
            System.out.println("4. Ver jugadores con tarjetas");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine();

            // Procesamiento de opciones del usuario
            switch (opcion) {
                case "1" ->
                        torneo.mostrarPartidosTorneo(scanner); // Muestra los partidos por jornada
                case "2" ->
                        mostrarTopGoleadores(torneo.getPartidos()); // Lista top 10 goleadores
                case "3" ->
                        mostrarTopAsistencias(torneo.getPartidos()); // Lista top 10 asistentes
                case "4" ->
                        mostrarJugadoresConTarjetas(torneo.getPartidos()); // Lista jugadores con tarjetas
                case "0" -> {
                    return; // Finaliza el menú y retorna al menú principal
                }
                default ->
                        System.out.println("Opción inválida. Intente de nuevo."); // Validación de error
            }
        }
    }

    /**
     * Muestra al usuario un menú de estadísticas luego de la finalización de un torneo mixto
     * (fase de grupos + eliminación directa). Permite visualizar partidos jugados, goleadores,
     * asistentes y jugadores con tarjetas.
     *
     * El menú se mantiene activo hasta que el usuario seleccione la opción 0 para salir.
     *
     * @param scanner Objeto Scanner para leer la entrada del usuario por consola.
     * @param torneoMixto Torneo mixto ya simulado del cual se extraen las estadísticas a mostrar.
     */
    private void mostrarMenuPostTorneoMixto(Scanner scanner, TorneoMixto torneoMixto) {
        while (true) {
            // Muestra el título del menú con el nombre del torneo mixto
            System.out.println("\n=== Estadísticas de la Liga " + torneoMixto.getNombre() + " ===");

            // Imprime las opciones disponibles
            System.out.println("1. Ver fechas jugadas");
            System.out.println("2. Ver top 10 goleadores");
            System.out.println("3. Ver top 10 asistentes");
            System.out.println("4. Ver jugadores con tarjetas");
            System.out.println("0. Volver al menú principal");

            // Solicita al usuario que seleccione una opción
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine();

            // Procesa la opción ingresada por el usuario
            switch (opcion) {
                case "1" ->
                    // Muestra todos los partidos disputados por jornada
                        torneoMixto.mostrarPartidosTorneo(scanner);

                case "2" ->
                    // Muestra los 10 jugadores con más goles en el torneo
                        mostrarTopGoleadores(torneoMixto.getPartidos());

                case "3" ->
                    // Muestra los 10 jugadores con más asistencias en el torneo
                        mostrarTopAsistencias(torneoMixto.getPartidos());

                case "4" ->
                    // Muestra la lista de jugadores que recibieron tarjetas
                        mostrarJugadoresConTarjetas(torneoMixto.getPartidos());

                case "0" -> {
                    // Finaliza el menú y retorna al menú principal del sistema
                    return;
                }

                default ->
                    // Informa al usuario que la opción ingresada no es válida
                        System.out.println("Opción inválida. Intente de nuevo.");
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

    /**
     * Verifica si un número entero positivo es una potencia de dos.
     * Utiliza una operación bit a bit eficiente: un número n es potencia de dos
     * si solo tiene un bit en 1 en su representación binaria.
     * Ejemplos válidos: 1, 2, 4, 8, 16, 32, ...
     *
     * @param n El número a verificar.
     * @return true si n es potencia de dos, false en caso contrario.
     */
    private boolean esPotenciaDeDos(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }
}


