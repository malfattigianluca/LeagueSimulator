package TP4.Modelo;

import TP4.Excepciones.TorneoException;
import java.util.*;


/**
 * Representa un torneo de fútbol, que puede jugarse en modalidad de liga o
 * eliminación directa.
 *
 * El torneo mantiene un registro de:
 * - Equipos participantes.
 * - Partidos disputados.
 * - Estadísticas por equipo (puntos, goles a favor/en contra).
 * - Cálculo de ranking ELO con un factor de impacto fijo.
 *
 * Se pueden simular diferentes tipos de torneos controlando la bandera
 * {@code eliminacionDirecta}.
 *
 * Esta clase permite agregar partidos, registrar resultados, y realizar un
 * seguimiento de la clasificación.
 *
 * @author
 */
public class Torneo {
  // Nombre del torneo (ej. "Champions League", "Torneo de Verano")
  private String nombre;

  // Lista de equipos participantes
  private List<Equipo> equipos;

  // Lista de todos los partidos disputados en el torneo
  private List<Partido> partidos;

  // Tabla de posiciones: puntos acumulados por equipo
  private Map<Equipo, Integer> puntos;

  // Registro de goles a favor por equipo
  private Map<Equipo, Integer> golesAFavor;

  // Registro de goles en contra por equipo
  private Map<Equipo, Integer> golesEnContra;

  // Bandera que indica si el torneo es de eliminación directa (true) o tipo liga
  // (false)
  private boolean eliminacionDirecta;

  // Constante que define el factor de impacto para actualización del ranking ELO
  private static final int K = 100;

  public NodoPartido raizEliminacion;

  /**
   * Crea un nuevo torneo con el nombre y modalidad especificados.
   *
   * @param nombre             Nombre del torneo.
   * @param eliminacionDirecta true si el torneo es de eliminación directa, false
   *                           si es tipo liga.
   */
  public Torneo(String nombre, boolean eliminacionDirecta) {
    this.nombre = nombre;
    this.equipos = new ArrayList<>();
    this.partidos = new ArrayList<>();
    this.puntos = new HashMap<>();
    this.golesAFavor = new HashMap<>();
    this.golesEnContra = new HashMap<>();
    this.eliminacionDirecta = eliminacionDirecta;
  }

  /**
   * Agrega un equipo al torneo si no fue agregado previamente.
   * Inicializa sus estadísticas en las tablas de puntos y goles.
   *
   * @param equipo Equipo a incorporar al torneo.
   */
  public void agregarEquipo(Equipo equipo) {
    if (!equipos.contains(equipo)) {
      equipos.add(equipo);
      puntos.put(equipo, 0);
      golesAFavor.put(equipo, 0);
      golesEnContra.put(equipo, 0);
    }
  }

  /**
   * Simula un partido entre dos equipos utilizando el sistema ELO para determinar
   * las probabilidades.
   * <p>
   * El metodo realiza:
   * - Cálculo de probabilidades de victoria basado en ELO.
   * - Generación aleatoria de goles en base a dichas probabilidades.
   * - Creación y registro del objeto {@link Partido}.
   * - Actualización de estadísticas del torneo (puntos, goles).
   * - Reajuste de ELO para ambos equipos según el resultado.
   *
   * @param local     Equipo que juega como local.
   * @param visitante Equipo que juega como visitante.
   * @throws TorneoException Si alguno de los equipos no pertenece al torneo.
   */
  public void simularPartido(Equipo local, Equipo visitante) throws TorneoException {
    if (local == null || visitante == null) {
      throw new TorneoException("Uno de los equipos es null");
    }
    if (!equipos.contains(local) || !equipos.contains(visitante)) {
      throw new TorneoException("Los equipos deben pertenecer al torneo");
    }

    // Probabilidad de victoria del local según diferencia de ELO
    double pa = 1.0 / (1.0 + Math.pow(10, (visitante.getElo() - local.getElo()) / 1000.0));
    double pb = 1.0 - pa;

    // Generación de goles según probabilidad
    Random random = new Random();
    int golesLocal = simularGoles(pa);
    int golesVisitante = simularGoles(pb);

    // Crear el partido y registrar
    Partido partido = new Partido(local, visitante, golesLocal, golesVisitante);
    partidos.add(partido);
    actualizarEstadisticas(local, visitante, golesLocal, golesVisitante);

    // Calcular resultado real
    double ra = golesLocal > golesVisitante ? 1 : (golesLocal == golesVisitante ? 0.5 : 0);
    double rb = 1 - ra;

    // Actualizar ELO
    int nuevoEloLocal = (int) Math.round(local.getElo() + K * (ra - pa));
    int nuevoEloVisitante = (int) Math.round(visitante.getElo() + K * (rb - pb));
    local.setElo(nuevoEloLocal);
    visitante.setElo(nuevoEloVisitante);
  }

  /**
   * Simula la cantidad de goles que anota un equipo en un partido,
   * en función de una probabilidad dada.
   *
   * Utiliza un modelo probabilístico: por cada intento (límite de 5),
   * si un número aleatorio [0.0, 1.0) es menor a la probabilidad recibida,
   * se incrementa el contador de goles.
   *
   * Este metodo simula un comportamiento aleatorio acumulativo
   * controlado por una probabilidad, con un límite superior de 5 goles.
   *
   * @param probabilidad Valor entre 0.0 y 1.0 que representa la probabilidad de marcar un gol en un intento.
   * @return Número total de goles anotados (entre 0 y 5).
   */
  private int simularGoles(double probabilidad) {
    Random random = new Random();
    int goles = 0;

    // Mientras se supere el umbral de probabilidad y no se superen los 5 goles
    while (random.nextDouble() < probabilidad && goles < 5) {
      goles++; // Se considera un gol
    }

    return goles;
  }

  /**
   * Actualiza las estadísticas de goles y puntos para los equipos
   * involucrados en un partido una vez finalizado.
   *
   * Para cada equipo se actualizan:
   * - Goles a favor
   * - Goles en contra
   * - Puntos según el resultado (victoria = 3, empate = 1, derrota = 0)
   *
   * @param local Equipo local del partido.
   * @param visitante Equipo visitante del partido.
   * @param golesLocal Cantidad de goles anotados por el equipo local.
   * @param golesVisitante Cantidad de goles anotados por el equipo visitante.
   */
  private void actualizarEstadisticas(Equipo local, Equipo visitante, int golesLocal, int golesVisitante) {
    // Goles a favor y en contra para el equipo local
    golesAFavor.put(local, golesAFavor.get(local) + golesLocal);
    golesEnContra.put(local, golesEnContra.get(local) + golesVisitante);

    // Goles a favor y en contra para el visitante
    golesAFavor.put(visitante, golesAFavor.get(visitante) + golesVisitante);
    golesEnContra.put(visitante, golesEnContra.get(visitante) + golesLocal);

    // Asignación de puntos según el resultado
    if (golesLocal > golesVisitante)
      puntos.put(local, puntos.get(local) + 3); // Gana el local
    else if (golesLocal < golesVisitante)
      puntos.put(visitante, puntos.get(visitante) + 3); // Gana el visitante
    else {
      // Empate
      puntos.put(local, puntos.get(local) + 1);
      puntos.put(visitante, puntos.get(visitante) + 1);
    }
  }

  /**
   * Devuelve una versión abreviada de un nombre si supera los 25 caracteres.
   * Agrega "…" al final como indicativo de truncado.
   *
   * @param nombre Cadena original a abreviar.
   * @return Nombre abreviado (máximo 25 caracteres), o el original si no excede el límite.
   */
  private String abreviar(String nombre) {
    return nombre.length() > 25 ? nombre.substring(0, 23) + "…" : nombre;
  }

  /**
   * Inicia la simulación completa del torneo.
   *
   * - Verifica que haya al menos 2 equipos.
   * - Simula todos los partidos según el tipo de torneo:
   * - Eliminación directa: mediante eliminación por rondas.
   * - Liga: todos contra todos.
   *
   *
   * @throws TorneoException Si hay menos de 2 equipos.
   */
  public void simularTorneo() throws TorneoException {
    // Validación mínima: al menos dos equipos son necesarios
    if (equipos.size() < 2) {
      throw new TorneoException("Se necesitan al menos 2 equipos para simular un torneo");
    }

    if (eliminacionDirecta) {
      // Elimina la ventaja de orden cargado mezclando los equipos
      List<Equipo> disponibles = new ArrayList<>(equipos);
      Collections.shuffle(disponibles);

      // Simula la ronda completa y guarda la raíz del árbol
      this.raizEliminacion = simularRonda(disponibles);

      // Imprime visualmente el cuadro del torneo
      imprimirBracket();
    } else {
      // Modalidad de liga: todos contra todos una vez (round robin)
      for (int i = 0; i < equipos.size(); i++) {
        for (int j = i + 1; j < equipos.size(); j++) {
          simularPartido(equipos.get(i), equipos.get(j));
        }
      }
    }
  }

  /**
   * Simula un torneo en formato de liga donde cada equipo juega contra todos los
   * demás una vez.
   * Los partidos se generan por combinaciones únicas (no se repiten encuentros).
   *
   * @throws TorneoException Si ocurre un error durante la simulación de un
   *                         partido.
   */
  private void simularLiga() throws TorneoException {
    for (int i = 0; i < equipos.size(); i++) {
      for (int j = i + 1; j < equipos.size(); j++) {
        simularPartido(equipos.get(i), equipos.get(j));
      }
    }
  }

  /**
   * Simula un torneo por eliminación directa.
   *
   * - Los equipos son mezclados aleatoriamente para definir emparejamientos.
   * - En cada ronda se enfrentan por pares.
   * - El ganador de cada partido avanza a la siguiente ronda.
   * - En caso de empate, avanza el equipo con mayor ELO.
   * - Si hay un número impar de equipos en una ronda, uno pasa automáticamente.
   *
   * El proceso se repite hasta que queda un solo equipo: el campeón.
   *
   * @throws TorneoException Si ocurre un error durante la simulación de partidos.
   */
  private void simularEliminacionDirecta() throws TorneoException {
    List<Equipo> equiposDisponibles = new ArrayList<>(equipos);
    Collections.shuffle(equiposDisponibles);

    // Simula el torneo completo y devuelve la raíz del árbol con todos los partidos jugados
    this.raizEliminacion = simularRonda(equiposDisponibles);

    // Mostrar el cuadro del torneo (ya simulado)
    imprimirBracket();
  }



  /**
   * Imprime el cuadro completo del torneo en formato de eliminación directa ("bracket").
   * Si no se ha simulado el torneo aún (la raíz del árbol es null), informa al usuario.
   * Al finalizar, también muestra el campeón del torneo, si ya está definido.
   */
  public void imprimirBracket() {
    if (raizEliminacion == null) {
      System.out.println("No hay partidos simulados aún.");
      return;
    }

    System.out.println("\n=== Cuadro del Torneo (Formato Bracket) ===\n");

    // Inicia la impresión recursiva desde la raíz (nivel 0)
    imprimirBracketRecursivo(raizEliminacion, 0, true);

    // Muestra el campeón si está disponible
    Equipo campeon = raizEliminacion.getGanador();
    if (campeon != null) {
      System.out.println("\n🏆 Campeón: " + campeon.getNombre());
    } else {
      System.out.println("\n🏆 Campeón: Desconocido");
    }
  }

  /**
   * Metodo recursivo que imprime el árbol binario de partidos del torneo en formato jerárquico.
   *
   * @param nodo      Nodo actual del árbol de eliminación.
   * @param nivel     Nivel de profundidad en el árbol (usado para indentación y etiqueta de fase).
   * @param izquierdo Indica si el nodo es hijo izquierdo (para usar símbolo de rama visual).
   */
  private void imprimirBracketRecursivo(NodoPartido nodo, int nivel, boolean izquierdo) {
    if (nodo == null) return;

    Partido p = nodo.getPartido();

    // Genera la indentación visual para el nivel actual (8 espacios por nivel)
    String indent = "        ".repeat(nivel);

    // Rama visual: └── para hijo izquierdo, ┌── para derecho
    String flecha = izquierdo ? "└── " : "┌── ";

    // Imprime primero el hijo derecho (se verá arriba en consola)
    imprimirBracketRecursivo(nodo.getDerecho(), nivel + 1, false);

    if (nivel > 0) System.out.println();

    // Determina la fase textual según el nivel
    String fase = switch (nivel) {
      case 0 -> "Final";
      case 1 -> "Semifinal";
      case 2 -> "Cuartos";
      case 3 -> "8vos";
      case 4 -> "16vos";
      case 5 -> "32vos";
      default -> "Ronda " + (nivel + 1);
    };

    // Construcción del texto de enfrentamiento (local vs visitante)
    String local = (p != null && p.getLocal() != null)
            ? String.format("%-22s", abreviar(p.getLocal().getNombre()))
            : "BYE                  ";

    String visitante = (p != null && p.getVisitante() != null)
            ? String.format("%-22s", abreviar(p.getVisitante().getNombre()))
            : "BYE                  ";

    // Indicación visual del ganador si ya fue definido
    String ganador = (p != null && p.getGanador() != null)
            ? "→ 🏅" + abreviar(p.getGanador().getNombre())
            : "";

    // Imprime la línea con el enfrentamiento en la consola
    System.out.println(indent + flecha + "[" + fase + "] " + local + " vs " + visitante + " " + ganador);

    // Imprime el hijo izquierdo (se verá abajo en consola)
    imprimirBracketRecursivo(nodo.getIzquierdo(), nivel + 1, true);

    // Salto de línea adicional para fases altas (mejora visual en la consola)
    if (nivel <= 2) System.out.println();
  }

  /**
   * Muestra los partidos jugados de un torneo en formato de jornadas visuales,
   * dividiendo los partidos en bloques de "jornadas" fijas para facilitar la navegación.
   * Utiliza un paginador interactivo que permite al usuario recorrer las fechas.
   *
   * @param scanner Scanner para leer la entrada del usuario desde consola.
   */
  public void mostrarPartidosTorneo(Scanner scanner) {
    // Si no hay partidos registrados aún, informa al usuario y sale
    if (partidos.isEmpty()) {
      System.out.println("No se han jugado partidos aún.");
      return;
    }

    // Divide los partidos en jornadas de 4 partidos por "fecha"
    List<List<Partido>> jornadas = new ArrayList<>();
    int cantidadPorJornada = 4;
    for (int i = 0; i < partidos.size(); i += cantidadPorJornada) {
      int fin = Math.min(i + cantidadPorJornada, partidos.size());
      jornadas.add(partidos.subList(i, fin));
    }

    // Se prepara un iterador para recorrer las jornadas visualmente
    ListIterator<List<Partido>> iterador = jornadas.listIterator();
    int jornadaActual = 0;

    // Muestra la primera jornada por defecto si existe
    if (iterador.hasNext()) {
      List<Partido> primera = iterador.next();
      jornadaActual++;
      mostrarJornadaFormatoLiga(primera, jornadaActual);
    }

    // Bucle interactivo para navegar por las jornadas
    while (true) {
      System.out.println("\nIngrese N para siguiente, P para anterior, 0 para salir:");
      String entrada = scanner.nextLine().trim().toUpperCase();

      switch (entrada) {
        case "N" -> {
          if (iterador.hasNext()) {
            List<Partido> siguiente = iterador.next();
            jornadaActual++;
            mostrarJornadaFormatoLiga(siguiente, jornadaActual);
          } else {
            System.out.println("No hay más jornadas siguientes.");
          }
        }

        case "P" -> {
          if (iterador.hasPrevious()) {
            iterador.previous(); // retroceder al actual
            if (iterador.hasPrevious()) {
              List<Partido> anterior = iterador.previous();
              jornadaActual--;
              mostrarJornadaFormatoLiga(anterior, jornadaActual);
              iterador.next(); // reposicionarse para futuros movimientos
            } else {
              System.out.println("Ya estás en la primera jornada.");
            }
          } else {
            System.out.println("Ya estás en la primera jornada.");
          }
        }

        case "0" -> {
          System.out.println("Saliendo del visor de jornadas.");
          return;
        }

        default -> System.out.println("Opción inválida.");
      }
    }
  }

  /**
   * Muestra en consola los partidos de una jornada específica con formato estilo liga.
   * Imprime nombre de los equipos y resultado del partido en una tabla alineada.
   *
   * @param jornada Lista de partidos que conforman una jornada.
   * @param numero Número de la jornada a mostrar (con fines visuales).
   */
  protected void mostrarJornadaFormatoLiga(List<Partido> jornada, int numero) {
    // Encabezado con número de jornada
    System.out.printf("\n=== Jornada %d ===\n", numero);
    System.out.printf("%-28s %3s - %-3s %-28s%n", "Equipo Local", "G", "G", "Equipo Visitante");
    System.out.println("---------------------------------------------------------------");

    // Itera por cada partido de la jornada
    for (Partido partido : jornada) {
      // Obtiene nombres de los equipos (si es un BYE, lo indica)
      String local = partido.getLocal() != null ? partido.getLocal().getNombre() : "BYE";
      String visitante = partido.getVisitante() != null ? partido.getVisitante().getNombre() : "BYE";

      // Obtiene los goles convertidos
      int golesLocal = partido.getGolesLocal();
      int golesVisitante = partido.getGolesVisitante();

      // Imprime la línea formateada del partido
      System.out.printf("%-28s %3d - %-3d %-28s%n", local, golesLocal, golesVisitante, visitante);
    }
  }

  /**
   * Simula recursivamente las rondas de un torneo de eliminación directa.
   * Genera un árbol binario de partidos (NodoPartido) que representa el progreso
   * del torneo desde los equipos iniciales hasta el campeón.
   *
   * Cada ronda empareja los equipos en duelos, simula los partidos y crea nodos
   * con los ganadores. En caso de cantidad impar, un equipo avanza automáticamente.
   *
   * @param equipos Lista de equipos participantes.
   * @return NodoPartido raíz del árbol de eliminación (el partido final).
   * @throws TorneoException Si ocurre un error al simular algún partido.
   */
  private NodoPartido simularRonda(List<Equipo> equipos) throws TorneoException {
    // Caso base: sólo queda un equipo → se lo considera ganador directo
    if (equipos.size() == 1) {
      NodoPartido nodo = new NodoPartido(null);
      nodo.setGanador(equipos.get(0));
      return nodo;
    }

    List<NodoPartido> nodosRonda = new ArrayList<>();

    // Fase de emparejamiento de equipos
    for (int i = 0; i < equipos.size(); i += 2) {
      Equipo e1 = equipos.get(i);
      Equipo e2 = (i + 1 < equipos.size()) ? equipos.get(i + 1) : null;

      if (e2 == null) {
        // Si hay un número impar, e1 pasa de ronda sin jugar
        NodoPartido nodo = new NodoPartido(null);
        nodo.setGanador(e1);
        nodosRonda.add(nodo);
      } else {
        // Simular partido entre e1 y e2
        simularPartido(e1, e2);
        Partido p = partidos.get(partidos.size() - 1);

        // Si no hubo ganador (empate), decidir por ELO
        Equipo ganador = p.getGanador();
        if (ganador == null)
          ganador = e1.getElo() >= e2.getElo() ? e1 : e2;

        NodoPartido nodo = new NodoPartido(p);
        nodo.setGanador(ganador);
        nodosRonda.add(nodo);
      }
    }

    // Caso base: ya quedó un único nodo (ganador del torneo)
    if (nodosRonda.size() == 1) return nodosRonda.get(0);

    List<NodoPartido> siguienteNivel = new ArrayList<>();

    // Emparejar nodos de la ronda actual para construir la siguiente
    for (int i = 0; i < nodosRonda.size(); i += 2) {
      NodoPartido izquierdo = nodosRonda.get(i);
      NodoPartido derecho = (i + 1 < nodosRonda.size()) ? nodosRonda.get(i + 1) : null;

      if (derecho == null) {
        // Si queda un nodo sin par, pasa directamente
        siguienteNivel.add(izquierdo);
      } else {
        Equipo local = izquierdo.getGanador();
        Equipo visitante = derecho.getGanador();

        if (local == null || visitante == null) {
          // Uno de los equipos no válido → pasa el otro
          NodoPartido pasoLibre = new NodoPartido(null);
          pasoLibre.setGanador(local != null ? local : visitante);
          pasoLibre.setIzquierdo(izquierdo);
          pasoLibre.setDerecho(derecho);
          siguienteNivel.add(pasoLibre);
          continue;
        }

        // Simula el partido entre ganadores de nodos previos
        simularPartido(local, visitante);
        Partido partido = partidos.get(partidos.size() - 1);
        Equipo ganador = partido.getGanador();
        if (ganador == null)
          ganador = local.getElo() >= visitante.getElo() ? local : visitante;

        // Crea nodo padre que conecta los nodos previos
        NodoPartido padre = new NodoPartido(partido);
        padre.setGanador(ganador);
        padre.setIzquierdo(izquierdo);
        padre.setDerecho(derecho);
        siguienteNivel.add(padre);
      }
    }

    // Llamada recursiva con los ganadores de esta ronda
    return simularRondaDesdeNodos(siguienteNivel);
  }

  /**
   * Metodo recursivo que construye el árbol de eliminación directa a partir de una lista de nodos
   * que representan los partidos ganados en la ronda anterior.
   *
   * Este metodo es una continuación del proceso iniciado por `simularRonda`, donde se van agrupando
   * pares de nodos para generar nuevos partidos entre los ganadores hasta obtener un único nodo raíz.
   *
   * @param nodos Lista de nodos que representan partidos/jugadores ganadores de la ronda anterior.
   * @return NodoPartido raíz del nuevo nivel o del árbol final del torneo.
   * @throws TorneoException Si ocurre un error al simular un partido.
   */
  private NodoPartido simularRondaDesdeNodos(List<NodoPartido> nodos) throws TorneoException {
    // Caso base: solo queda un nodo → es el campeón
    if (nodos.size() == 1) return nodos.get(0);

    List<NodoPartido> siguienteNivel = new ArrayList<>();

    // Empareja los nodos de la ronda actual de a pares
    for (int i = 0; i < nodos.size(); i += 2) {
      NodoPartido izquierdo = nodos.get(i);
      NodoPartido derecho = (i + 1 < nodos.size()) ? nodos.get(i + 1) : null;

      if (derecho == null) {
        // Nodo sin par → avanza automáticamente
        siguienteNivel.add(izquierdo);
      } else {
        Equipo local = izquierdo.getGanador();
        Equipo visitante = derecho.getGanador();

        if (local == null || visitante == null) {
          // Algún equipo es nulo → pasa el válido
          NodoPartido pasoLibre = new NodoPartido(null);
          pasoLibre.setGanador(local != null ? local : visitante);
          pasoLibre.setIzquierdo(izquierdo);
          pasoLibre.setDerecho(derecho);
          siguienteNivel.add(pasoLibre);
          continue;
        }

        // Simula el partido entre ganadores previos
        simularPartido(local, visitante);
        Partido partido = partidos.get(partidos.size() - 1);

        // En caso de empate, se elige el de mayor ELO
        Equipo ganador = partido.getGanador();
        if (ganador == null)
          ganador = local.getElo() >= visitante.getElo() ? local : visitante;

        // Se crea el nodo padre que unifica a ambos
        NodoPartido padre = new NodoPartido(partido);
        padre.setGanador(ganador);
        padre.setIzquierdo(izquierdo);
        padre.setDerecho(derecho);
        siguienteNivel.add(padre);
      }
    }

    // Llamada recursiva con los nodos del siguiente nivel
    return simularRondaDesdeNodos(siguienteNivel);
  }

  /**
   * @return El nombre del torneo.
   */
  public String getNombre() {
    return nombre;
  }

  /**
   * @return Lista de equipos que participan en el torneo.
   */
  public List<Equipo> getEquipos() {
    return equipos;
  }

  /**
   * @return true si el torneo se juega en formato de eliminación directa; false
   *         si es tipo liga.
   */
  public boolean isEliminacionDirecta() {
    return eliminacionDirecta;
  }

  /**
   * Devuelve la lista de partidos jugados en el torneo.
   *
   * @return Lista de partidos.
   */
  public List<Partido> getPartidos() {
    return partidos;
  }
}