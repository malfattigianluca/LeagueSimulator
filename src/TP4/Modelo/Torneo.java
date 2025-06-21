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
   * Simula la cantidad de goles anotados por un equipo en función de una
   * probabilidad dada.
   * Usa una probabilidad acumulativa con un máximo de 5 goles por equipo.
   *
   * @param probabilidad Probabilidad base de anotar (derivada del ELO).
   * @return Número de goles simulados (entre 0 y 5).
   */
  private int simularGoles(double probabilidad) {
    Random random = new Random();
    int goles = 0;
    while (random.nextDouble() < probabilidad && goles < 5) {
      goles++;
    }
    return goles;
  }



  /**
   * Actualiza las estadísticas acumuladas del torneo para ambos equipos luego de
   * un partido.
   *
   * - Suma los goles a favor y en contra.
   * - Asigna puntos según el resultado (3 por victoria, 1 por empate).
   *
   * @param local          Equipo local.
   * @param visitante      Equipo visitante.
   * @param golesLocal     Goles anotados por el local.
   * @param golesVisitante Goles anotados por el visitante.
   */
  private void actualizarEstadisticas(Equipo local, Equipo visitante, int golesLocal, int golesVisitante) {
    golesAFavor.put(local, golesAFavor.get(local) + golesLocal);
    golesEnContra.put(local, golesEnContra.get(local) + golesVisitante);
    golesAFavor.put(visitante, golesAFavor.get(visitante) + golesVisitante);
    golesEnContra.put(visitante, golesEnContra.get(visitante) + golesLocal);

    if (golesLocal > golesVisitante) puntos.put(local, puntos.get(local) + 3);
    else if (golesLocal < golesVisitante) puntos.put(visitante, puntos.get(visitante) + 3);
    else {
      puntos.put(local, puntos.get(local) + 1);
      puntos.put(visitante, puntos.get(visitante) + 1);
    }
  }

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
    if (equipos.size() < 2) {
      throw new TorneoException("Se necesitan al menos 2 equipos para simular un torneo");
    }

    if (eliminacionDirecta) {
      List<Equipo> disponibles = new ArrayList<>(equipos);
      Collections.shuffle(disponibles);
      this.raizEliminacion = simularRonda(disponibles);
      imprimirBracket();
    } else {
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



  public void imprimirBracket() {
    if (raizEliminacion == null) {
      System.out.println("No hay partidos simulados aún.");
      return;
    }

    System.out.println("\n=== Cuadro del Torneo (Formato Bracket) ===\n");
    imprimirBracketRecursivo(raizEliminacion, 0, true);

    Equipo campeon = raizEliminacion.getGanador();
    if (campeon != null) {
      System.out.println("\n🏆 Campeón: " + campeon.getNombre());
    } else {
      System.out.println("\n🏆 Campeón: Desconocido");
    }
  }

  private void imprimirBracketRecursivo(NodoPartido nodo, int nivel, boolean izquierdo) {
    if (nodo == null) return;

    Partido p = nodo.getPartido();
    String indent = "        ".repeat(nivel); // 8 espacios por nivel
    String flecha = izquierdo ? "└── " : "┌── ";

    imprimirBracketRecursivo(nodo.getDerecho(), nivel + 1, false);

    if (nivel > 0) System.out.println();

    String fase = switch (nivel) {
      case 0 -> "Final";
      case 1 -> "Semifinal";
      case 2 -> "Cuartos";
      case 3 -> "8vos";
      case 4 -> "16vos";
      case 5 -> "32vos";
      default -> "Ronda " + (nivel + 1);
    };

    String local = (p != null && p.getLocal() != null) ? String.format("%-22s", abreviar(p.getLocal().getNombre())) : "BYE                  ";
    String visitante = (p != null && p.getVisitante() != null) ? String.format("%-22s", abreviar(p.getVisitante().getNombre())) : "BYE                  ";
    String ganador = (p != null && p.getGanador() != null) ? "→ 🏅" + abreviar(p.getGanador().getNombre()) : "";

    System.out.println(indent + flecha + "[" + fase + "] " + local + " vs " + visitante + " " + ganador);

    imprimirBracketRecursivo(nodo.getIzquierdo(), nivel + 1, true);

    if (nivel <= 2) System.out.println();
  }



  /**
   * Muestra por consola los partidos jugados uno por uno en orden.
   *
   * Permite al usuario navegar interactivamente entre los distintos partidos:
   * - "N": pasar al siguiente partido
   * - "P": volver al anterior
   * - "0": salir del visor
   *
   * @param scanner el objeto Scanner utilizado para leer la entrada del usuario
   */
  public void mostrarPartidosTorneo(Scanner scanner) {
    if (partidos.isEmpty()) {
      System.out.println("No se han jugado partidos aún.");
      return;
    }

    List<List<Partido>> jornadas = new ArrayList<>();

    int cantidadPorJornada = 4; // cantidad visual por "fecha"
    for (int i = 0; i < partidos.size(); i += cantidadPorJornada) {
      int fin = Math.min(i + cantidadPorJornada, partidos.size());
      jornadas.add(partidos.subList(i, fin));
    }

    ListIterator<List<Partido>> iterador = jornadas.listIterator();
    int jornadaActual = 0;

    if (iterador.hasNext()) {
      List<Partido> primera = iterador.next();
      jornadaActual++;
      mostrarJornadaFormatoLiga(primera, jornadaActual);
    }

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
            iterador.previous();
            if (iterador.hasPrevious()) {
              List<Partido> anterior = iterador.previous();
              jornadaActual--;
              mostrarJornadaFormatoLiga(anterior, jornadaActual);
              iterador.next(); // reposicionarse
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



  protected void mostrarJornadaFormatoLiga(List<Partido> jornada, int numero) {
    System.out.printf("\n=== Jornada %d ===\n", numero);
    System.out.printf("%-28s %3s - %-3s %-28s%n", "Equipo Local", "G", "G", "Equipo Visitante");
    System.out.println("---------------------------------------------------------------");

    for (Partido partido : jornada) {
      String local = partido.getLocal() != null ? partido.getLocal().getNombre() : "BYE";
      String visitante = partido.getVisitante() != null ? partido.getVisitante().getNombre() : "BYE";
      int golesLocal = partido.getGolesLocal();
      int golesVisitante = partido.getGolesVisitante();

      System.out.printf("%-28s %3d - %-3d %-28s%n", local, golesLocal, golesVisitante, visitante);
    }
  }




  private NodoPartido simularRonda(List<Equipo> equipos) throws TorneoException {
    if (equipos.size() == 1) {
      NodoPartido nodo = new NodoPartido(null);
      nodo.setGanador(equipos.get(0));
      return nodo;
    }

    List<NodoPartido> nodosRonda = new ArrayList<>();

    for (int i = 0; i < equipos.size(); i += 2) {
      Equipo e1 = equipos.get(i);
      Equipo e2 = (i + 1 < equipos.size()) ? equipos.get(i + 1) : null;

      if (e2 == null) {
        NodoPartido nodo = new NodoPartido(null);
        nodo.setGanador(e1);
        nodosRonda.add(nodo);
      } else {
        simularPartido(e1, e2);
        Partido p = partidos.get(partidos.size() - 1);
        Equipo ganador = p.getGanador();
        if (ganador == null) ganador = e1.getElo() >= e2.getElo() ? e1 : e2;
        NodoPartido nodo = new NodoPartido(p);
        nodo.setGanador(ganador);
        nodosRonda.add(nodo);
      }
    }

    if (nodosRonda.size() == 1) return nodosRonda.get(0);

    List<NodoPartido> siguienteNivel = new ArrayList<>();

    for (int i = 0; i < nodosRonda.size(); i += 2) {
      NodoPartido izquierdo = nodosRonda.get(i);
      NodoPartido derecho = (i + 1 < nodosRonda.size()) ? nodosRonda.get(i + 1) : null;

      if (derecho == null) {
        siguienteNivel.add(izquierdo);
      } else {
        Equipo local = izquierdo.getGanador();
        Equipo visitante = derecho.getGanador();
        if (local == null || visitante == null) {
          NodoPartido pasoLibre = new NodoPartido(null);
          pasoLibre.setGanador(local != null ? local : visitante);
          pasoLibre.setIzquierdo(izquierdo);
          pasoLibre.setDerecho(derecho);
          siguienteNivel.add(pasoLibre);
          continue;
        }
        simularPartido(local, visitante);
        Partido partido = partidos.get(partidos.size() - 1);
        Equipo ganador = partido.getGanador();
        if (ganador == null) ganador = local.getElo() >= visitante.getElo() ? local : visitante;
        NodoPartido padre = new NodoPartido(partido);
        padre.setGanador(ganador);
        padre.setIzquierdo(izquierdo);
        padre.setDerecho(derecho);
        siguienteNivel.add(padre);
      }
    }

    return simularRondaDesdeNodos(siguienteNivel);
  }

  private NodoPartido simularRondaDesdeNodos(List<NodoPartido> nodos) throws TorneoException {
    if (nodos.size() == 1) return nodos.get(0);

    List<NodoPartido> siguienteNivel = new ArrayList<>();

    for (int i = 0; i < nodos.size(); i += 2) {
      NodoPartido izquierdo = nodos.get(i);
      NodoPartido derecho = (i + 1 < nodos.size()) ? nodos.get(i + 1) : null;

      if (derecho == null) {
        siguienteNivel.add(izquierdo);
      } else {
        Equipo local = izquierdo.getGanador();
        Equipo visitante = derecho.getGanador();
        if (local == null || visitante == null) {
          NodoPartido pasoLibre = new NodoPartido(null);
          pasoLibre.setGanador(local != null ? local : visitante);
          pasoLibre.setIzquierdo(izquierdo);
          pasoLibre.setDerecho(derecho);
          siguienteNivel.add(pasoLibre);
          continue;
        }
        simularPartido(local, visitante);
        Partido partido = partidos.get(partidos.size() - 1);
        Equipo ganador = partido.getGanador();
        if (ganador == null) ganador = local.getElo() >= visitante.getElo() ? local : visitante;
        NodoPartido padre = new NodoPartido(partido);
        padre.setGanador(ganador);
        padre.setIzquierdo(izquierdo);
        padre.setDerecho(derecho);
        siguienteNivel.add(padre);
      }
    }

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