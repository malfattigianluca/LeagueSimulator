package TP4.Modelo;

import TP4.Excepciones.TorneoException;
import java.util.*;

public class Torneo {
  private String nombre;
  private List<Equipo> equipos;
  private List<Partido> partidos;
  private Map<Equipo, Integer> puntos;
  private Map<Equipo, Integer> golesAFavor;
  private Map<Equipo, Integer> golesEnContra;
  private boolean eliminacionDirecta;

  public Torneo(String nombre, boolean eliminacionDirecta) {
    this.nombre = nombre;
    this.equipos = new ArrayList<>();
    this.partidos = new ArrayList<>();
    this.puntos = new HashMap<>();
    this.golesAFavor = new HashMap<>();
    this.golesEnContra = new HashMap<>();
    this.eliminacionDirecta = eliminacionDirecta;
  }

  public void agregarEquipo(Equipo equipo) {
    if (!equipos.contains(equipo)) {
      equipos.add(equipo);
      puntos.put(equipo, 0);
      golesAFavor.put(equipo, 0);
      golesEnContra.put(equipo, 0);
    }
  }

  public void simularPartido(Equipo local, Equipo visitante) throws TorneoException {
    if (!equipos.contains(local) || !equipos.contains(visitante)) {
      throw new TorneoException("Los equipos deben pertenecer al torneo");
    }

    // Calcular probabilidades basadas en el ELO
    double probLocal = (double) local.getElo() / (local.getElo() + visitante.getElo());
    double probVisitante = 1 - probLocal;

    // Simular resultado
    Random random = new Random();
    int golesLocal = simularGoles(probLocal);
    int golesVisitante = simularGoles(probVisitante);

    // Crear y registrar partido
    Partido partido = new Partido(local, visitante, golesLocal, golesVisitante);
    partidos.add(partido);

    // Actualizar estadísticas
    actualizarEstadisticas(local, visitante, golesLocal, golesVisitante);
  }

  private int simularGoles(double probabilidad) {
    Random random = new Random();
    int goles = 0;
    while (random.nextDouble() < probabilidad && goles < 5) {
      goles++;
    }
    return goles;
  }

  private void actualizarEstadisticas(Equipo local, Equipo visitante, int golesLocal, int golesVisitante) {
    // Actualizar goles
    golesAFavor.put(local, golesAFavor.get(local) + golesLocal);
    golesEnContra.put(local, golesEnContra.get(local) + golesVisitante);
    golesAFavor.put(visitante, golesAFavor.get(visitante) + golesVisitante);
    golesEnContra.put(visitante, golesEnContra.get(visitante) + golesLocal);

    // Actualizar puntos
    if (golesLocal > golesVisitante) {
      puntos.put(local, puntos.get(local) + 3);
    } else if (golesLocal < golesVisitante) {
      puntos.put(visitante, puntos.get(visitante) + 3);
    } else {
      puntos.put(local, puntos.get(local) + 1);
      puntos.put(visitante, puntos.get(visitante) + 1);
    }
  }

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

  private void simularLiga() throws TorneoException {
    // Cada equipo juega contra todos los demás
    for (int i = 0; i < equipos.size(); i++) {
      for (int j = i + 1; j < equipos.size(); j++) {
        simularPartido(equipos.get(i), equipos.get(j));
      }
    }
  }

  private void simularEliminacionDirecta() throws TorneoException {
    List<Equipo> equiposDisponibles = new ArrayList<>(equipos);
    Collections.shuffle(equiposDisponibles);

    while (equiposDisponibles.size() > 1) {
      List<Equipo> ganadores = new ArrayList<>();

      // Simular partidos de la ronda actual
      for (int i = 0; i < equiposDisponibles.size(); i += 2) {
        if (i + 1 < equiposDisponibles.size()) {
          Equipo local = equiposDisponibles.get(i);
          Equipo visitante = equiposDisponibles.get(i + 1);

          simularPartido(local, visitante);
          Partido ultimoPartido = partidos.get(partidos.size() - 1);

          Equipo ganador = ultimoPartido.getGanador();
          if (ganador == null) {
            // En caso de empate, gana el equipo con mejor ELO
            ganador = local.getElo() > visitante.getElo() ? local : visitante;
          }
          ganadores.add(ganador);
        } else {
          // Equipo que pasa automáticamente a la siguiente ronda
          ganadores.add(equiposDisponibles.get(i));
        }
      }

      equiposDisponibles = ganadores;
    }
  }

  public void mostrarTabla() {
    System.out.println("\n=== Tabla de Posiciones: " + nombre + " ===");
    System.out.printf("%-30s %-5s %-5s %-5s %-5s %-5s %-5s%n",
        "Equipo", "PJ", "PG", "PE", "PP", "GF", "GC");
    System.out.println("------------------------------------------------------------");

    // Crear lista de equipos ordenada por puntos
    List<Equipo> equiposOrdenados = new ArrayList<>(equipos);
    equiposOrdenados.sort((e1, e2) -> {
      int puntos1 = puntos.get(e1);
      int puntos2 = puntos.get(e2);
      if (puntos1 != puntos2)
        return puntos2 - puntos1;

      int difGoles1 = golesAFavor.get(e1) - golesEnContra.get(e1);
      int difGoles2 = golesAFavor.get(e2) - golesEnContra.get(e2);
      if (difGoles1 != difGoles2)
        return difGoles2 - difGoles1;

      return golesAFavor.get(e2) - golesAFavor.get(e1);
    });

    for (Equipo equipo : equiposOrdenados) {
      int partidosJugados = equipo.getPartidosJugados();
      int partidosGanados = equipo.getPartidosGanados();
      int partidosEmpatados = equipo.getPartidosEmpatados();
      int partidosPerdidos = equipo.getPartidosPerdidos();
      int golesFavor = golesAFavor.get(equipo);
      int golesContra = golesEnContra.get(equipo);

      System.out.printf("%-30s %-5d %-5d %-5d %-5d %-5d %-5d%n",
          equipo.getNombre(),
          partidosJugados,
          partidosGanados,
          partidosEmpatados,
          partidosPerdidos,
          golesFavor,
          golesContra);
    }
  }

  public void mostrarPartidos() {
    System.out.println("\n=== Partidos Jugados ===");
    for (Partido partido : partidos) {
      System.out.println(partido);
    }
  }

  public String getNombre() {
    return nombre;
  }

  public List<Equipo> getEquipos() {
    return equipos;
  }

  public boolean isEliminacionDirecta() {
    return eliminacionDirecta;
  }
}