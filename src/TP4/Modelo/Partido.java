package TP4.Modelo;

import TP4.Excepciones.TorneoException;
import TP4.Util.Validador;

import java.util.*;

/**
 * Representa un partido de fútbol entre dos equipos: local y visitante.
 * Contiene información sobre el resultado del encuentro, incluyendo los goles anotados,
 * los goleadores, asistentes, y los jugadores que recibieron tarjetas.
 *
 * Esta clase permite registrar el desarrollo completo de un partido y sirve como base
 * para estadísticas individuales y de equipos en torneos o simulaciones.
 *
 * Los jugadores que participan en eventos (goles, asistencias, tarjetas) se almacenan
 * por separado según el tipo de acción y el equipo al que pertenecen.
 *
 * Se asume que un partido no puede tener más de un resultado asociado (no hay desempates).
 *
 * @author
 */
public class Partido {
  // Equipo que juega como local en el partido
  private Equipo local;

  // Equipo que juega como visitante en el partido
  private Equipo visitante;

  // Goles anotados por el equipo local
  private int golesLocal;

  // Goles anotados por el equipo visitante
  private int golesVisitante;

  // Lista de jugadores del equipo local que anotaron goles
  private List<Jugador> goleadoresLocal;

  // Lista de jugadores del equipo visitante que anotaron goles
  private List<Jugador> goleadoresVisitante;

  // Lista de jugadores del equipo local que realizaron asistencias
  private List<Jugador> asistentesLocal;

  // Lista de jugadores del equipo visitante que realizaron asistencias
  private List<Jugador> asistentesVisitante;

  // Lista de jugadores que recibieron tarjeta amarilla (de ambos equipos)
  private List<Jugador> jugadoresAmarillas;

  // Lista de jugadores que recibieron tarjeta roja (de ambos equipos)
  private List<Jugador> jugadoresRojas;

  // Equipo ganador del partido
  private Equipo ganador;



  /**
   * Crea un nuevo partido entre dos equipos, registrando el resultado y generando estadísticas de jugadores.
   *
   * Este constructor:
   * - Valida que los equipos no sean nulos.
   * - Valida los goles anotados por cada equipo.
   * - Inicializa las listas de eventos (goles, asistencias, tarjetas).
   * - Actualiza las estadísticas de los equipos (goles a favor/en contra, puntos, etc.).
   * - Simula estadísticas individuales de los jugadores (goleadores, asistentes, tarjetas).
   *
   * @param local          Equipo que juega como local.
   * @param visitante      Equipo que juega como visitante.
   * @param golesLocal     Goles marcados por el equipo local.
   * @param golesVisitante Goles marcados por el equipo visitante.
   * @throws TorneoException Si los equipos son nulos o los goles tienen valores inválidos.
   */
  public Partido(Equipo local, Equipo visitante, int golesLocal, int golesVisitante) throws TorneoException {
    // Validaciones de entrada
    if (local == null || visitante == null) {
      throw new TorneoException("Los equipos no pueden ser nulos");
    }
    Validador.validarGoles(golesLocal);
    Validador.validarGoles(golesVisitante);

    // Asignación de atributos principales
    this.local = local;
    this.visitante = visitante;
    this.golesLocal = golesLocal;
    this.golesVisitante = golesVisitante;
    this.ganador = determinarGanador(); // Determina el ganador al crear el partido

    // Inicialización de listas de eventos por jugador
    this.goleadoresLocal = new ArrayList<>();
    this.goleadoresVisitante = new ArrayList<>();
    this.asistentesLocal = new ArrayList<>();
    this.asistentesVisitante = new ArrayList<>();
    this.jugadoresAmarillas = new ArrayList<>();
    this.jugadoresRojas = new ArrayList<>();

    // Actualiza las estadísticas del equipo (partidos jugados, puntos, goles, etc.)
    local.registrarPartido(golesLocal, golesVisitante);
    visitante.registrarPartido(golesVisitante, golesLocal);

    // Genera automáticamente estadísticas de jugadores (simulación)
    simularEstadisticasJugadores();
  }

  public Partido(Equipo local, Equipo visitante) throws TorneoException {
    this(local, visitante, 0, 0); // Crea un partido inicializado en 0 a 0
  }

  private int generarPoisson(int elo) {
    double lambda = Math.max(1.0, elo / 800.0); // Ajustá esto según tu sistema
    double L = Math.exp(-lambda);
    int k = 0;
    double p = 1.0;
    do {
      k++;
      p *= Math.random();
    } while (p > L);
    return k - 1;
  }

  public void simular() {
    // Simular el partido usando tu lógica avanzada si está accesible desde acá
    // Como no podés acceder directamente a `Torneo.simularPartido(...)`, simulá acá con lo mínimo:
    this.golesLocal = generarPoisson(local.getElo());
    this.golesVisitante = generarPoisson(visitante.getElo());

    if (golesLocal > golesVisitante) {
      this.ganador = local;
    } else if (golesVisitante > golesLocal) {
      this.ganador = visitante;
    } else {
      this.ganador = getGanadorConDesempate();
    }
  }

  /**
   * Simula estadísticas de jugadores para el partido actual.
   *
   * Este metodo utiliza generación aleatoria para:
   * - Asignar goleadores a partir de los goles registrados por cada equipo.
   * - Asignar asistentes con un 70% de probabilidad por gol.
   * - Asignar tarjetas amarillas (0 a 3 por equipo) y rojas (0 a 1 por equipo).
   *
   * Las estadísticas actualizan los objetos {@link Jugador} directamente
   * (goles, asistencias, amarillas, rojas) y también se registran en las listas del partido.
   *
   * @throws TorneoException Si ocurre un error en la modificación de los jugadores.
   */
  void simularEstadisticasJugadores() throws TorneoException {
    Random random = new Random();

    // Obtener jugadores disponibles de cada equipo
    List<Jugador> jugadoresLocal = local.getJugadores().getVertices();
    List<Jugador> jugadoresVisitante = visitante.getJugadores().getVertices();

    // 🔹 Simular goles: elegir jugadores aleatorios como goleadores
    for (int i = 0; i < golesLocal; i++) {
      if (!jugadoresLocal.isEmpty()) {
        Jugador goleador = jugadoresLocal.get(random.nextInt(jugadoresLocal.size()));
        goleador.registrarGol();
        goleadoresLocal.add(goleador);
      }
    }
    for (int i = 0; i < golesVisitante; i++) {
      if (!jugadoresVisitante.isEmpty()) {
        Jugador goleador = jugadoresVisitante.get(random.nextInt(jugadoresVisitante.size()));
        goleador.registrarGol();
        goleadoresVisitante.add(goleador);
      }
    }

    // 🔹 Simular asistencias (70% de probabilidad por gol)
    for (int i = 0; i < golesLocal; i++) {
      if (!jugadoresLocal.isEmpty() && random.nextDouble() < 0.7) {
        Jugador asistente = jugadoresLocal.get(random.nextInt(jugadoresLocal.size()));
        asistente.registrarAsistencia();
        asistentesLocal.add(asistente);
      }
    }
    for (int i = 0; i < golesVisitante; i++) {
      if (!jugadoresVisitante.isEmpty() && random.nextDouble() < 0.7) {
        Jugador asistente = jugadoresVisitante.get(random.nextInt(jugadoresVisitante.size()));
        asistente.registrarAsistencia();
        asistentesVisitante.add(asistente);
      }
    }

    // 🔹 Simular tarjetas amarillas (0 a 3 por equipo) y rojas (0 a 1 por equipo)
    int amarillasLocal = random.nextInt(4);   // 0 a 3
    int amarillasVisitante = random.nextInt(4);
    int rojasLocal = random.nextInt(2);       // 0 o 1
    int rojasVisitante = random.nextInt(2);

    for (int i = 0; i < amarillasLocal; i++) {
      if (!jugadoresLocal.isEmpty()) {
        Jugador jugador = jugadoresLocal.get(random.nextInt(jugadoresLocal.size()));
        jugador.registrarAmarilla();
        jugadoresAmarillas.add(jugador);
      }
    }
    for (int i = 0; i < amarillasVisitante; i++) {
      if (!jugadoresVisitante.isEmpty()) {
        Jugador jugador = jugadoresVisitante.get(random.nextInt(jugadoresVisitante.size()));
        jugador.registrarAmarilla();
        jugadoresAmarillas.add(jugador);
      }
    }
    for (int i = 0; i < rojasLocal; i++) {
      if (!jugadoresLocal.isEmpty()) {
        Jugador jugador = jugadoresLocal.get(random.nextInt(jugadoresLocal.size()));
        jugador.registrarRoja();
        jugadoresRojas.add(jugador);
      }
    }
    for (int i = 0; i < rojasVisitante; i++) {
      if (!jugadoresVisitante.isEmpty()) {
        Jugador jugador = jugadoresVisitante.get(random.nextInt(jugadoresVisitante.size()));
        jugador.registrarRoja();
        jugadoresRojas.add(jugador);
      }
    }
  }
    /**
     * Determina el ganador del partido basándose en los goles anotados.
     * Si el equipo local tiene más goles, es el ganador.
     * Si el equipo visitante tiene más goles, es el ganador.
     * Si ambos equipos tienen la misma cantidad de goles, no hay ganador (empate).
     *
     * @return El equipo ganador o null si hay empate.
     */
    private Equipo determinarGanador() {
      if (local == null || visitante == null) return null;
      if (golesLocal > golesVisitante) return local;
      if (golesVisitante > golesLocal) return visitante;
      return null; // Empate
    }

  public Equipo getGanadorConDesempate() {
    if (ganador != null) return ganador;
    // Desempate por ELO si hay empate y no se definió aún
    if (local != null && visitante != null) {
      return local.getElo() >= visitante.getElo() ? local : visitante;
    }
    return null;
  }

  public String resumenPartidoConGanador() {
    return local.getNombre() + " vs " + visitante.getNombre() + " → 🏅 " +
            (getGanadorConDesempate() != null ? getGanadorConDesempate().getNombre() : "¿?");
  }


  public Equipo getGanador() {
    return ganador;
  }

  public void setGanador(Equipo ganador) {
    this.ganador = ganador;
  }

  /**
   * @return Lista de jugadores del equipo local que anotaron goles en el partido.
   */
  public List<Jugador> getGoleadoresLocal() {
    return new ArrayList<>(goleadoresLocal);
  }

  /**
   * @return Lista de jugadores del equipo visitante que anotaron goles en el partido.
   */
  public List<Jugador> getGoleadoresVisitante() {
    return new ArrayList<>(goleadoresVisitante);
  }

  /**
   * @return Lista de jugadores del equipo local que realizaron asistencias.
   */
  public List<Jugador> getAsistentesLocal() {
    return new ArrayList<>(asistentesLocal);
  }

  /**
   * @return Lista de jugadores del equipo visitante que realizaron asistencias.
   */
  public List<Jugador> getAsistentesVisitante() {
    return new ArrayList<>(asistentesVisitante);
  }

  /**
   * @return Lista de jugadores (locales o visitantes) que recibieron tarjetas amarillas.
   */
  public List<Jugador> getJugadoresAmarillas() {
    return new ArrayList<>(jugadoresAmarillas);
  }

  /**
   * @return Lista de jugadores (locales o visitantes) que recibieron tarjetas rojas.
   */
  public List<Jugador> getJugadoresRojas() {
    return new ArrayList<>(jugadoresRojas);
  }

  /**
   * @return Equipo que jugó como local.
   */
  public Equipo getLocal() {
    return local;
  }

  /**
   * @return Equipo que jugó como visitante.
   */
  public Equipo getVisitante() {
    return visitante;
  }

  /**
   * @return Cantidad de goles anotados por el equipo local.
   */
  public int getGolesLocal() {
    return golesLocal;
  }

  /**
   * @return Cantidad de goles anotados por el equipo visitante.
   */
  public int getGolesVisitante() {
    return golesVisitante;
  }

  /**
   * Determina el equipo ganador del partido.
   *
   * @return El equipo con más goles, o null si el resultado fue un empate.
   */

//  public Equipo getGanadorConDesempate() {
//    if (golesLocal > golesVisitante) return local;
//    if (golesVisitante > golesLocal) return visitante;
//    return local.getElo() >= visitante.getElo() ? local : visitante;
//  }

  /**
   * Devuelve una representación detallada del partido en forma de texto.
   *
   * Incluye:
   * - Resultado (nombre de los equipos y goles).
   * - Lista de goleadores y equipo correspondiente.
   * - Lista de asistentes.
   * - Jugadores con tarjetas amarillas y rojas.
   *
   * Este metodo es útil para mostrar el resumen de un partido en consola o informes.
   *
   * @return Cadena con el detalle del partido, incluyendo eventos destacados.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    // Resultado principal
    sb.append(String.format("%s %d - %d %s%n",
            local.getNombre(), golesLocal, golesVisitante, visitante.getNombre()));

    // Goleadores
    if (!goleadoresLocal.isEmpty() || !goleadoresVisitante.isEmpty()) {
      sb.append("Goles:\n");
      for (Jugador goleador : goleadoresLocal) {
        sb.append("  ").append(goleador.getNombre())
                .append(" (").append(local.getNombre()).append(")\n");
      }
      for (Jugador goleador : goleadoresVisitante) {
        sb.append("  ").append(goleador.getNombre())
                .append(" (").append(visitante.getNombre()).append(")\n");
      }
    }

    // Asistentes
    if (!asistentesLocal.isEmpty() || !asistentesVisitante.isEmpty()) {
      sb.append("Asistencias:\n");
      for (Jugador asistente : asistentesLocal) {
        sb.append("  ").append(asistente.getNombre())
                .append(" (").append(local.getNombre()).append(")\n");
      }
      for (Jugador asistente : asistentesVisitante) {
        sb.append("  ").append(asistente.getNombre())
                .append(" (").append(visitante.getNombre()).append(")\n");
      }
    }

    // Amarillas
    if (!jugadoresAmarillas.isEmpty()) {
      sb.append("Tarjetas Amarillas:\n");
      for (Jugador jugador : jugadoresAmarillas) {
        sb.append("  ").append(jugador.getNombre())
                .append(" (").append(jugador.getEquipo().getNombre()).append(")\n");
      }
    }

    // Rojas
    if (!jugadoresRojas.isEmpty()) {
      sb.append("Tarjetas Rojas:\n");
      for (Jugador jugador : jugadoresRojas) {
        sb.append("  ").append(jugador.getNombre())
                .append(" (").append(jugador.getEquipo().getNombre()).append(")\n");
      }
    }

    return sb.toString();
  }
}