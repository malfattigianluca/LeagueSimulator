package TP4.Modelo;

import TP4.Excepciones.TorneoException;
import java.util.*;

public class SimuladorLiga {
  private String nombre;
  private List<Equipo> equipos;
  private Map<Equipo, Integer> golesAFavor;
  private Map<Equipo, Integer> golesEnContra;
  private List<Partido> partidos;
  private List<List<Partido>> jornadas;
  private List<List<Enfrentamiento>> calendario;
  private int jornadaActual;
  private boolean soloIda;
  private static final int K = 100;

  public SimuladorLiga(String nombre) {
    this.nombre = nombre;
    this.equipos = new ArrayList<>();
    this.golesAFavor = new HashMap<>();
    this.golesEnContra = new HashMap<>();
    this.partidos = new ArrayList<>();
    this.jornadas = new ArrayList<>();
    this.calendario = new ArrayList<>();
    this.jornadaActual = 0;
  }

  public void agregarEquipo(Equipo equipo) {
    if (!equipos.contains(equipo)) {
      equipos.add(equipo);
      golesAFavor.put(equipo, 0);
      golesEnContra.put(equipo, 0);
    }
  }

  public void generarCalendario() throws TorneoException {
    if (equipos.size() < 2) {
      throw new TorneoException("Se necesitan al menos 2 equipos para generar un calendario");
    }
    if (equipos.size() % 2 != 0) {
      throw new TorneoException("El número de equipos debe ser par para generar un calendario");
    }

    calendario.clear();
    int numEquipos = equipos.size();
    int numJornadasIda = numEquipos - 1;
    List<Equipo> equiposLista = new ArrayList<>(equipos);

    // Calendario de ida
    List<List<Enfrentamiento>> ida = new ArrayList<>();
    for (int jornada = 0; jornada < numJornadasIda; jornada++) {
      List<Enfrentamiento> enfrentamientosJornada = new ArrayList<>();
      for (int i = 0; i < numEquipos / 2; i++) {
        Equipo local = equiposLista.get(i);
        Equipo visitante = equiposLista.get(numEquipos - 1 - i);
        enfrentamientosJornada.add(new Enfrentamiento(local, visitante));
      }
      ida.add(enfrentamientosJornada);
      Collections.rotate(equiposLista.subList(1, numEquipos), 1);
    }

    // Calendario de vuelta (invirtiendo local/visitante)
    List<List<Enfrentamiento>> vuelta = new ArrayList<>();
    for (List<Enfrentamiento> jornada : ida) {
      List<Enfrentamiento> enfrentamientosVuelta = new ArrayList<>();
      for (Enfrentamiento enf : jornada) {
        enfrentamientosVuelta.add(new Enfrentamiento(enf.visitante, enf.local));
      }
      vuelta.add(enfrentamientosVuelta);
    }

    calendario.addAll(ida);
    calendario.addAll(vuelta); // total 38 jornadas
  }


  public List<Partido> simularJornada() throws TorneoException {
    if (equipos.size() < 2) {
      throw new TorneoException("Se necesitan al menos 2 equipos para simular una jornada");
    }
    if (jornadaActual >= calendario.size()) {
      throw new TorneoException("No hay más jornadas para simular");
    }

    List<Partido> partidosDeLaJornada = new ArrayList<>();
    List<Enfrentamiento> jornadaPredefinida = calendario.get(jornadaActual);
    Random random = new Random();

    for (Enfrentamiento enfrentamiento : jornadaPredefinida) {
      Equipo local = enfrentamiento.local;
      Equipo visitante = enfrentamiento.visitante;

      double pa = 1.0 / (1.0 + Math.pow(10, (visitante.getElo() - local.getElo()) / 1000.0));
      double pb = 1.0 - pa;

      double lambdaTotal = 2.0;
      double ventajaLocal = 1.1;
      double desventajaVisitante = 0.9;
      double ajuste = lambdaTotal / (pa * ventajaLocal + pb * desventajaVisitante);

      double lambdaLocal = pa * ventajaLocal * ajuste;
      double lambdaVisitante = pb * desventajaVisitante * ajuste;

      lambdaLocal *= 0.9 + random.nextDouble() * 0.2;
      lambdaVisitante *= 0.9 + random.nextDouble() * 0.2;

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

    jornadaActual++;
    return partidosDeLaJornada;
  }

  public void agregarJornada(List<Partido> jornada) {
    jornadas.add(jornada);
  }


  public void reiniciar() {
    for (Equipo equipo : equipos) {
      equipo.reiniciarEstadisticas();
    }
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

  private void actualizarEstadisticas(Equipo local, Equipo visitante, int golesLocal, int golesVisitante) throws TorneoException {
    golesAFavor.put(local, golesAFavor.get(local) + golesLocal);
    golesEnContra.put(local, golesEnContra.get(local) + golesVisitante);
    golesAFavor.put(visitante, golesAFavor.get(visitante) + golesVisitante);
    golesEnContra.put(visitante, golesEnContra.get(visitante) + golesLocal);
  }

  public void mostrarTabla() {
    System.out.println("\n=== Tabla de Posiciones: " + nombre + " ===");
    System.out.printf("%-30s %-5s %-5s %-5s %-5s %-5s %-5s %-5s %-5s %-6s%n",
            "Equipo", "Pts", "PJ", "PG", "PE", "PP", "GF", "GC", "DG", "ELO");
    System.out.println("----------------------------------------------------------------------------------");

    List<Equipo> equiposOrdenados = new ArrayList<>(equipos);
    equiposOrdenados.sort((e1, e2) -> {
      int puntos1 = e1.getPuntos();  // o puntos.get(e1) si no usás atributo en Equipo
      int puntos2 = e2.getPuntos();
      if (puntos1 != puntos2)
        return puntos2 - puntos1;

      int difGoles1 = e1.getGolesAFavor() - e1.getGolesEnContra();
      int difGoles2 = e2.getGolesAFavor() - e2.getGolesEnContra();
      if (difGoles1 != difGoles2)
        return difGoles2 - difGoles1;

      return e2.getGolesAFavor() - e1.getGolesAFavor();
    });

    for (Equipo equipo : equiposOrdenados) {
      int puntosEquipo = equipo.getPuntos();
      int partidosJugados = equipo.getPartidosJugados();
      int partidosGanados = equipo.getPartidosGanados();
      int partidosEmpatados = equipo.getPartidosEmpatados();
      int partidosPerdidos = equipo.getPartidosPerdidos();
      int golesFavor = equipo.getGolesAFavor();
      int golesContra = equipo.getGolesEnContra();
      int diferenciaGoles = golesFavor - golesContra;
      int eloEquipo = equipo.getElo();

      System.out.printf("%-30s %-5d %-5d %-5d %-5d %-5d %-5d %-5d %-5d %-6d%n",
              equipo.getNombre(),
              puntosEquipo,
              partidosJugados,
              partidosGanados,
              partidosEmpatados,
              partidosPerdidos,
              golesFavor,
              golesContra,
              diferenciaGoles,
              eloEquipo);
    }
  }

  public int getTotalJornadas() {
    return calendario.size();
  }

  public void mostrarPartidosJugados(Scanner scanner) {
    if (jornadas.isEmpty()) {
      System.out.println("No se han simulado jornadas aún.");
      return;
    }

    ListIterator<List<Partido>> iterador = jornadas.listIterator();
    int jornadaActual = 0;

    // Muestra la primera jornada por defecto
    if (iterador.hasNext()) {
      List<Partido> primera = iterador.next();
      jornadaActual++;
      mostrarJornada(primera, jornadaActual);
    }

    while (true) {
      System.out.println("\nIngrese N para siguiente, P para anterior, 0 para salir:");
      String entrada = scanner.nextLine().trim().toUpperCase();

      switch (entrada) {
        case "N" -> {
          if (iterador.hasNext()) {
            List<Partido> siguiente = iterador.next();
            jornadaActual++;
            mostrarJornada(siguiente, jornadaActual);
          } else {
            System.out.println("No hay más jornadas siguientes.");
          }
        }
        case "P" -> {
          if (iterador.hasPrevious()) {
            // Necesitamos retroceder dos veces para compensar la última llamada a next()
            if (iterador.hasPrevious()) {
              iterador.previous(); // retroceder al actual
              if (iterador.hasPrevious()) {
                List<Partido> anterior = iterador.previous();
                jornadaActual--;
                mostrarJornada(anterior, jornadaActual);
                iterador.next(); // reposicionarse
              } else {
                System.out.println("Ya estás en la primera jornada.");
              }
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

  private void mostrarJornada(List<Partido> jornada, int numero) {
    System.out.printf("\n=== Jornada %d ===\n", numero);
    System.out.printf("%-28s %3s - %-3s %-28s%n", "Equipo Local", "G", "G", "Equipo Visitante");
    System.out.println("---------------------------------------------------------------");
    for (Partido partido : jornada) {
      String local = partido.getLocal().getNombre();
      String visitante = partido.getVisitante().getNombre();
      int golesLocal = partido.getGolesLocal();
      int golesVisitante = partido.getGolesVisitante();

      System.out.printf("%-28s %3d - %-3d %-28s%n", local, golesLocal, golesVisitante, visitante);
    }
  }


  private static class Enfrentamiento {
    Equipo local;
    Equipo visitante;

    public Enfrentamiento(Equipo local, Equipo visitante) {
      this.local = local;
      this.visitante = visitante;
    }
  }

  public String getNombre() {
    return nombre;
  }

  public List<Partido> getPartidos() {
    return new ArrayList<>(partidos);
  }
}
