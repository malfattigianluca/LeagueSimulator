package TP4.Modelo;

import TP4.Excepciones.TorneoException;
import java.util.*;

/**
 * Representa un torneo de fútbol, que puede jugarse en modalidad de liga o eliminación directa.
 *
 * El torneo mantiene un registro de:
 * - Equipos participantes.
 * - Partidos disputados.
 * - Estadísticas por equipo (puntos, goles a favor/en contra).
 * - Cálculo de ranking ELO con un factor de impacto fijo.
 *
 * Se pueden simular diferentes tipos de torneos controlando la bandera {@code eliminacionDirecta}.
 *
 * Esta clase permite agregar partidos, registrar resultados, y realizar un seguimiento de la clasificación.
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

  // Bandera que indica si el torneo es de eliminación directa (true) o tipo liga (false)
  private boolean eliminacionDirecta;

  // Constante que define el factor de impacto para actualización del ranking ELO
  private static final int K = 100;


  /**
   * Crea un nuevo torneo con el nombre y modalidad especificados.
   *
   * @param nombre             Nombre del torneo.
   * @param eliminacionDirecta true si el torneo es de eliminación directa, false si es tipo liga.
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
   * Simula un partido entre dos equipos utilizando el sistema ELO para determinar las probabilidades.
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
   * Simula la cantidad de goles anotados por un equipo en función de una probabilidad dada.
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
   * Actualiza las estadísticas acumuladas del torneo para ambos equipos luego de un partido.
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
    // Actualizar goles a favor y en contra
    golesAFavor.put(local, golesAFavor.get(local) + golesLocal);
    golesEnContra.put(local, golesEnContra.get(local) + golesVisitante);
    golesAFavor.put(visitante, golesAFavor.get(visitante) + golesVisitante);
    golesEnContra.put(visitante, golesEnContra.get(visitante) + golesLocal);

    // Actualizar puntos en la tabla
    if (golesLocal > golesVisitante) {
      puntos.put(local, puntos.get(local) + 3);
    } else if (golesLocal < golesVisitante) {
      puntos.put(visitante, puntos.get(visitante) + 3);
    } else {
      puntos.put(local, puntos.get(local) + 1);
      puntos.put(visitante, puntos.get(visitante) + 1);
    }
  }


  /**
   * Inicia la simulación completa del torneo.
   *
   * - Verifica que haya al menos 2 equipos.
   * - Simula todos los partidos según el tipo de torneo:
   *   - Eliminación directa: mediante eliminación por rondas.
   *   - Liga: todos contra todos.
   *
   * @throws TorneoException Si hay menos de 2 equipos.
   */
  public void simularTorneo() throws TorneoException {
    if (equipos.size() < 2) {
      throw new TorneoException("Se necesitan al menos 2 equipos para simular un torneo");
    }

    if (eliminacionDirecta) {
      simularEliminacionDirecta();
    } else {
      simularLiga();
    }
  }


  /**
   * Simula un torneo en formato de liga donde cada equipo juega contra todos los demás una vez.
   * Los partidos se generan por combinaciones únicas (no se repiten encuentros).
   *
   * @throws TorneoException Si ocurre un error durante la simulación de un partido.
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
    // Clonar y mezclar la lista de equipos para emparejamiento aleatorio
    List<Equipo> equiposDisponibles = new ArrayList<>(equipos);
    Collections.shuffle(equiposDisponibles);

    // Continuar hasta que solo quede un equipo (ganador final)
    while (equiposDisponibles.size() > 1) {
      List<Equipo> ganadores = new ArrayList<>();

      // Simular partidos de la ronda actual (emparejamientos de a dos)
      for (int i = 0; i < equiposDisponibles.size(); i += 2) {
        if (i + 1 < equiposDisponibles.size()) {
          Equipo local = equiposDisponibles.get(i);
          Equipo visitante = equiposDisponibles.get(i + 1);

          simularPartido(local, visitante);
          Partido ultimoPartido = partidos.get(partidos.size() - 1);

          Equipo ganador = ultimoPartido.getGanador();

          // Si empatan, avanza el de mayor ELO
          if (ganador == null) {
            ganador = local.getElo() > visitante.getElo() ? local : visitante;
          }

          ganadores.add(ganador);
        } else {
          // Número impar de equipos: uno pasa automáticamente
          ganadores.add(equiposDisponibles.get(i));
        }
      }

      // Actualizar lista para la siguiente ronda
      equiposDisponibles = ganadores;
    }
  }


  /**
   * Muestra en consola la tabla de posiciones del torneo.
   *
   * La tabla se ordena por:
   * 1. Puntos (de mayor a menor),
   * 2. Diferencia de gol (goles a favor - goles en contra),
   * 3. Goles a favor.
   *
   * Para cada equipo muestra:
   * - Partidos jugados (PJ)
   * - Partidos ganados (PG)
   * - Partidos empatados (PE)
   * - Partidos perdidos (PP)
   * - Goles a favor (GF)
   * - Goles en contra (GC)
   * - Puntos (Pts)
   * - ELO
   *
   * Esta tabla resume el rendimiento de cada equipo durante la simulación del torneo.
   */
  public void mostrarTablaPosiciones() {
    System.out.println("\n=== Tabla de Posiciones: " + nombre + " ===");
    System.out.printf("%-30s %-5s %-5s %-5s %-5s %-5s %-5s %-5s %-6s%n",
            "Equipo", "PJ", "PG", "PE", "PP", "GF", "GC", "Pts", "ELO");
    System.out.println("--------------------------------------------------------------------------");

    // Crear lista ordenada de equipos
    List<Equipo> equiposOrdenados = new ArrayList<>(equipos);
    Collections.sort(equiposOrdenados, (e1, e2) -> {
      int puntos1 = puntos.get(e1);
      int puntos2 = puntos.get(e2);

      if (puntos1 != puntos2) return puntos2 - puntos1;

      int difGoles1 = golesAFavor.get(e1) - golesEnContra.get(e1);
      int difGoles2 = golesAFavor.get(e2) - golesEnContra.get(e2);
      if (difGoles1 != difGoles2) return difGoles2 - difGoles1;

      return golesAFavor.get(e2) - golesAFavor.get(e1); // Tercer criterio: GF
    });

    // Mostrar fila por fila
    for (Equipo equipo : equiposOrdenados) {
      int pg = 0, pe = 0, pp = 0;

      // Calcular partidos ganados, empatados y perdidos
      for (Partido partido : partidos) {
        if (partido.getLocal() == equipo) {
          if (partido.getGolesLocal() > partido.getGolesVisitante()) pg++;
          else if (partido.getGolesLocal() == partido.getGolesVisitante()) pe++;
          else pp++;
        } else if (partido.getVisitante() == equipo) {
          if (partido.getGolesVisitante() > partido.getGolesLocal()) pg++;
          else if (partido.getGolesVisitante() == partido.getGolesLocal()) pe++;
          else pp++;
        }
      }

      int pj = pg + pe + pp;
      int puntosEquipo = puntos.get(equipo);
      int eloEquipo = equipo.getElo();

      System.out.printf("%-30s %-5d %-5d %-5d %-5d %-5d %-5d %-5d %-6d%n",
              equipo.getNombre(), pj, pg, pe, pp,
              golesAFavor.get(equipo), golesEnContra.get(equipo), puntosEquipo, eloEquipo);
    }
  }


  /**
   * Muestra por consola todos los partidos jugados en el torneo.
   *
   * Utiliza el método `toString()` de la clase {@link Partido} para imprimir
   * cada encuentro con detalle (resultado, goleadores, tarjetas, etc.).
   */
  public void mostrarPartidos() {
    System.out.println("\n=== Partidos Jugados ===");
    for (Partido partido : partidos) {
      System.out.println(partido);
    }
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
   * @return true si el torneo se juega en formato de eliminación directa; false si es tipo liga.
   */
  public boolean isEliminacionDirecta() {
    return eliminacionDirecta;
  }
}