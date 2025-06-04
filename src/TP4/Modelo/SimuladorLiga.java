package TP4.Modelo;

import TP4.Excepciones.TorneoException;
import java.util.*;

public class SimuladorLiga {
  private String nombre;
  private List<Equipo> equipos;
  private Map<Equipo, Integer> puntos;
  private Map<Equipo, Integer> golesAFavor;
  private Map<Equipo, Integer> golesEnContra;
  private List<Partido> partidos;
  private List<List<Partido>> jornadas;
  private static final int K = 100; // Factor de impacto en el ELO

  public SimuladorLiga(String nombre) {
    this.nombre = nombre;
    this.equipos = new ArrayList<>();
    this.puntos = new HashMap<>();
    this.golesAFavor = new HashMap<>();
    this.golesEnContra = new HashMap<>();
    this.partidos = new ArrayList<>();
    this.jornadas = new ArrayList<>();
  }

  public void agregarEquipo(Equipo equipo) {
    if (!equipos.contains(equipo)) {
      equipos.add(equipo);
      puntos.put(equipo, 0);
      golesAFavor.put(equipo, 0);
      golesEnContra.put(equipo, 0);
    }
  }

  public List<Partido> simularJornada() throws TorneoException {
    if (equipos.size() < 2) {
      throw new TorneoException("Se necesitan al menos 2 equipos para simular una jornada");
    }

    List<Equipo> equiposDisponibles = new ArrayList<>(equipos);
    Collections.shuffle(equiposDisponibles);

    List<Partido> partidosDeLaJornada = new ArrayList<>();

    while (equiposDisponibles.size() >= 2) {
      Equipo local = equiposDisponibles.remove(0);
      Equipo visitante = equiposDisponibles.remove(0);

      // Calcular probabilidades ELO
      double pa = 1.0 / (1.0 + Math.pow(10, (visitante.getElo() - local.getElo()) / 1000.0));
      double pb = 1.0 - pa;

      double lambdaTotal = 2.0;
      double ventajaLocal = 1.1;
      double desventajaVisitante = 0.9;

      double ajuste = lambdaTotal / (pa * ventajaLocal + pb * desventajaVisitante);
      double lambdaLocal = pa * ventajaLocal * ajuste;
      double lambdaVisitante = pb * desventajaVisitante * ajuste;

      lambdaLocal *= 0.9 + Math.random() * 0.2;
      lambdaVisitante *= 0.9 + Math.random() * 0.2;

      int golesLocal = simularGoles(lambdaLocal);
      int golesVisitante = simularGoles(lambdaVisitante);

      Partido partido = new Partido(local, visitante, golesLocal, golesVisitante);
      partidos.add(partido);
      partidosDeLaJornada.add(partido);

      actualizarEstadisticas(local, visitante, golesLocal, golesVisitante);

      double ra = golesLocal > golesVisitante ? 1 : (golesLocal == golesVisitante ? 0.5 : 0);
      double rb = golesVisitante > golesLocal ? 1 : (golesLocal == golesVisitante ? 0.5 : 0);
      int nuevoEloLocal = (int) Math.round(local.getElo() + K * (ra - pa));
      int nuevoEloVisitante = (int) Math.round(visitante.getElo() + K * (rb - pb));
      local.setElo(nuevoEloLocal);
      visitante.setElo(nuevoEloVisitante);
    }

    return partidosDeLaJornada;
  }

  public void agregarJornada(List<Partido> jornada) {
    jornadas.add(jornada);
  }

  private int simularGoles(double lambda) {
    Random random = new Random();
    double L = Math.exp(-lambda);
    int k = 0;
    double p = 1.0;

    do {
      k++;
      p *= random.nextDouble();
    } while (p > L);

    return k - 1;
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

  public void mostrarTabla() {
    System.out.println("\n=== Tabla de Posiciones: " + nombre + " ===");
    System.out.printf("%-30s %-5s %-5s %-5s %-5s %-5s %-5s %-5s %-6s%n",
        "Equipo", "Pts", "PJ", "PG", "PE", "PP", "GF", "GC", "ELO");
    System.out.println("--------------------------------------------------------------------------");

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
      int puntosEquipo = puntos.get(equipo);
      int eloEquipo = equipo.getElo();

      System.out.printf("%-30s %-5d %-5d %-5d %-5d %-5d %-5d %-5d %-6d%n",
          equipo.getNombre(),
          puntosEquipo,
          partidosJugados,
          partidosGanados,
          partidosEmpatados,
          partidosPerdidos,
          golesFavor,
          golesContra,
          eloEquipo);
    }
  }

  public void mostrarPartidos() {
    System.out.println("\n=== Partidos Jugados por Jornada ===");
    for (int i = 0; i < jornadas.size(); i++) {
      List<Partido> jornada = jornadas.get(i);
      System.out.println("\n--- Fecha " + (i + 1) + " ---");
      for (Partido partido : jornada) {
        System.out.println(partido);
      }
    }
  }


}