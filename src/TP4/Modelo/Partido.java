package TP4.Modelo;

import TP4.Excepciones.TorneoException;
import TP4.Util.Validador;

import java.util.*;

public class Partido {
  private Equipo local;
  private Equipo visitante;
  private int golesLocal;
  private int golesVisitante;
  private List<Jugador> goleadoresLocal;
  private List<Jugador> goleadoresVisitante;
  private List<Jugador> asistentesLocal;
  private List<Jugador> asistentesVisitante;
  private List<Jugador> jugadoresAmarillas;
  private List<Jugador> jugadoresRojas;

  public Partido(Equipo local, Equipo visitante, int golesLocal, int golesVisitante) throws TorneoException {
    if (local == null || visitante == null) {
      throw new TorneoException("Los equipos no pueden ser nulos");
    }
    Validador.validarGoles(golesLocal);
    Validador.validarGoles(golesVisitante);

    this.local = local;
    this.visitante = visitante;
    this.golesLocal = golesLocal;
    this.golesVisitante = golesVisitante;
    this.goleadoresLocal = new ArrayList<>();
    this.goleadoresVisitante = new ArrayList<>();
    this.asistentesLocal = new ArrayList<>();
    this.asistentesVisitante = new ArrayList<>();
    this.jugadoresAmarillas = new ArrayList<>();
    this.jugadoresRojas = new ArrayList<>();

    // Registrar el partido en los equipos
    local.registrarPartido(golesLocal, golesVisitante);
    visitante.registrarPartido(golesVisitante, golesLocal);

    // Simular estadísticas de jugadores
    simularEstadisticasJugadores();
  }

  private void simularEstadisticasJugadores() throws TorneoException {
    Random random = new Random();
    List<Jugador> jugadoresLocal = local.getJugadores().getVertices();
    List<Jugador> jugadoresVisitante = visitante.getJugadores().getVertices();

    // Asignar goleadores
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

    // Asignar asistencias (máximo una por gol, pero no siempre hay asistencias)
    for (int i = 0; i < golesLocal; i++) {
      if (!jugadoresLocal.isEmpty() && random.nextDouble() < 0.7) { // 70% de probabilidad de asistencia
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

    // Asignar tarjetas (0 a 3 amarillas por equipo, 0 a 1 roja por equipo)
    int amarillasLocal = random.nextInt(4);
    int amarillasVisitante = random.nextInt(4);
    int rojasLocal = random.nextInt(2);
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

  public List<Jugador> getGoleadoresLocal() {
    return new ArrayList<>(goleadoresLocal);
  }

  public List<Jugador> getGoleadoresVisitante() {
    return new ArrayList<>(goleadoresVisitante);
  }

  public List<Jugador> getAsistentesLocal() {
    return new ArrayList<>(asistentesLocal);
  }

  public List<Jugador> getAsistentesVisitante() {
    return new ArrayList<>(asistentesVisitante);
  }

  public List<Jugador> getJugadoresAmarillas() {
    return new ArrayList<>(jugadoresAmarillas);
  }

  public List<Jugador> getJugadoresRojas() {
    return new ArrayList<>(jugadoresRojas);
  }

  public Equipo getLocal() {
    return local;
  }

  public Equipo getVisitante() {
    return visitante;
  }

  public int getGolesLocal() {
    return golesLocal;
  }

  public int getGolesVisitante() {
    return golesVisitante;
  }

  public Equipo getGanador() {
    if (golesLocal > golesVisitante) {
      return local;
    } else if (golesVisitante > golesLocal) {
      return visitante;
    }
    return null; // Empate
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(String.format("%s %d - %d %s%n", local.getNombre(), golesLocal, golesVisitante, visitante.getNombre()));
    if (!goleadoresLocal.isEmpty() || !goleadoresVisitante.isEmpty()) {
      sb.append("Goles:\n");
      for (Jugador goleador : goleadoresLocal) {
        sb.append("  ").append(goleador.getNombre()).append(" (").append(local.getNombre()).append(")\n");
      }
      for (Jugador goleador : goleadoresVisitante) {
        sb.append("  ").append(goleador.getNombre()).append(" (").append(visitante.getNombre()).append(")\n");
      }
    }
    if (!asistentesLocal.isEmpty() || !asistentesVisitante.isEmpty()) {
      sb.append("Asistencias:\n");
      for (Jugador asistente : asistentesLocal) {
        sb.append("  ").append(asistente.getNombre()).append(" (").append(local.getNombre()).append(")\n");
      }
      for (Jugador asistente : asistentesVisitante) {
        sb.append("  ").append(asistente.getNombre()).append(" (").append(visitante.getNombre()).append(")\n");
      }
    }
    if (!jugadoresAmarillas.isEmpty()) {
      sb.append("Tarjetas Amarillas:\n");
      for (Jugador jugador : jugadoresAmarillas) {
        sb.append("  ").append(jugador.getNombre()).append(" (")
                .append(jugador.getEquipo().getNombre()).append(")\n");
      }
    }
    if (!jugadoresRojas.isEmpty()) {
      sb.append("Tarjetas Rojas:\n");
      for (Jugador jugador : jugadoresRojas) {
        sb.append("  ").append(jugador.getNombre()).append(" (")
                .append(jugador.getEquipo().getNombre()).append(")\n");
      }
    }
    return sb.toString();
  }
}