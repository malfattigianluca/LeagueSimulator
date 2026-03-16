package leaguesimulator.Modelo;

import leaguesimulator.Excepciones.TorneoException;
import java.util.*;

/**
 * Simula una liga de fútbol estructurada por jornadas, con posibilidad de jugar en modalidad de ida o ida y vuelta.
 *
 * La clase administra:
 * - Lista de equipos participantes.
 * - Estadísticas acumuladas por equipo (goles a favor y en contra).
 * - Lista de partidos jugados.
 * - Jornadas (fechas) compuestas por varios partidos.
 * - Un calendario estructurado con enfrentamientos predefinidos.
 *
 * También permite avanzar jornada por jornada, reiniciar la simulación, y aplicar el sistema ELO
 * para ajustar la fuerza de los equipos después de cada partido.
 *
 * @author
 */
public class SimuladorLiga {
  // Nombre de la liga (ej. "LaLiga", "Serie A")
  private String nombre;

  // Lista de equipos participantes
  private List<Equipo> equipos;

  // Goles a favor por equipo acumulados durante la simulación
  private Map<Equipo, Integer> golesAFavor;

  // Goles en contra por equipo acumulados durante la simulación
  private Map<Equipo, Integer> golesEnContra;

  // Todos los partidos simulados (en orden cronológico)
  private List<Partido> partidos;

  // Lista de jornadas (fechas), cada una con su lista de partidos jugados
  private List<List<Partido>> jornadas;

  // Calendario teórico con todos los enfrentamientos programados
  private List<List<Enfrentamiento>> calendario;

  // Índice de la jornada actual (comienza en 0)
  private int jornadaActual;

  // Bandera para indicar si se juega solo ida (true) o ida y vuelta (false)
  private boolean soloIda;

  // Constante del sistema ELO: factor de impacto en la actualización del rating
  private static final int K = 100;


  /**
   * Crea una nueva simulación de liga con el nombre especificado.
   * Inicializa las estructuras necesarias para gestionar equipos, jornadas y calendario.
   *
   * @param nombre Nombre de la liga a simular.
   */
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


  /**
   * Agrega un equipo a la liga, si aún no fue incorporado.
   * <p>
   * También inicializa su registro de goles a favor y en contra.
   *
   * @param equipo Equipo a agregar a la liga.
   */
  public void agregarEquipo(Equipo equipo) {
    if (!equipos.contains(equipo)) {
      equipos.add(equipo);
      golesAFavor.put(equipo, 0);
      golesEnContra.put(equipo, 0);
    }
  }



  /**
   * Genera el calendario completo de la liga en formato ida y vuelta.
   *
   * - El calendario se organiza en jornadas (fechas).
   * - En cada jornada se generan enfrentamientos sin repetición.
   * - Se usa un algoritmo de rotación para garantizar que todos los equipos se enfrenten entre sí una vez por fase.
   *
   * Restricciones:
   * - Debe haber al menos 2 equipos.
   * - El número de equipos debe ser par para formar emparejamientos completos.
   *
   * El calendario generado se almacena en la estructura {@code calendario},
   * que contiene dos fases: ida (local vs visitante) y vuelta (visitante vs local).
   *
   * @throws TorneoException Si no hay suficientes equipos o el número no es par.
   */
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

    // 🔁 Generar calendario de ida
    List<List<Enfrentamiento>> ida = new ArrayList<>();
    for (int jornada = 0; jornada < numJornadasIda; jornada++) {
      List<Enfrentamiento> enfrentamientosJornada = new ArrayList<>();
      for (int i = 0; i < numEquipos / 2; i++) {
        Equipo local = equiposLista.get(i);
        Equipo visitante = equiposLista.get(numEquipos - 1 - i);
        enfrentamientosJornada.add(new Enfrentamiento(local, visitante));
      }
      ida.add(enfrentamientosJornada);
      // Rotación de los equipos (excepto el primero)
      Collections.rotate(equiposLista.subList(1, numEquipos), 1);
    }

    // 🔁 Generar calendario de vuelta (invirtiendo local y visitante)
    List<List<Enfrentamiento>> vuelta = new ArrayList<>();
    for (List<Enfrentamiento> jornada : ida) {
      List<Enfrentamiento> enfrentamientosVuelta = new ArrayList<>();
      for (Enfrentamiento enf : jornada) {
        enfrentamientosVuelta.add(new Enfrentamiento(enf.visitante, enf.local));
      }
      vuelta.add(enfrentamientosVuelta);
    }

    // Agregar ambas fases al calendario
    calendario.addAll(ida);
    calendario.addAll(vuelta); // Total de 2*(n−1) jornadas
  }


  /**
   * Simula la jornada actual según el calendario de enfrentamientos.
   *
   * Para cada partido:
   * - Calcula probabilidades ELO para ambos equipos.
   * - Ajusta parámetros para modelar ventaja local y desventaja visitante.
   * - Usa una distribución Poisson para simular goles.
   * - Crea un objeto {@link Partido} con estadísticas simuladas.
   * - Actualiza estadísticas del torneo y el ELO de los equipos.
   *
   * @return Lista de partidos jugados en la jornada simulada.
   * @throws TorneoException Si hay menos de 2 equipos o no quedan jornadas por simular.
   */
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

      // Calcular probabilidades ELO
      double pa = 1.0 / (1.0 + Math.pow(10, (visitante.getElo() - local.getElo()) / 2000.0));
      double pb = 1.0 - pa;

      // Ajuste para modelar ventaja local
      double lambdaTotal = 2.7;
      double ventajaLocal = 1.1;
      double desventajaVisitante = 0.9;
      double ajuste = lambdaTotal / (pa * ventajaLocal + pb * desventajaVisitante);

      double lambdaLocal = pa * ventajaLocal * ajuste;
      double lambdaVisitante = pb * desventajaVisitante * ajuste;

      // Introducir variación aleatoria
      lambdaLocal *= 0.9 + random.nextDouble() * 0.2;
      lambdaVisitante *= 0.9 + random.nextDouble() * 0.2;

      // Asegura que no hayan goleadas extremas
      lambdaLocal = Math.max(0.4, Math.min(lambdaLocal, 2.5));
      lambdaVisitante = Math.max(0.4, Math.min(lambdaVisitante, 2.5));

      // Simular goles
      int golesLocal = simularGoles(lambdaLocal);
      int golesVisitante = simularGoles(lambdaVisitante);

      // Crear y registrar el partido
      Partido partido = new Partido(local, visitante, golesLocal, golesVisitante);
      partidos.add(partido);
      partidosDeLaJornada.add(partido);

      actualizarEstadisticas(local, visitante, golesLocal, golesVisitante);

      // Actualizar ELO
      double ra = golesLocal > golesVisitante ? 1 : (golesLocal == golesVisitante ? 0.5 : 0);
      double rb = 1 - ra;
      int nuevoEloLocal = (int) Math.round(local.getElo() + K * (ra - pa));
      int nuevoEloVisitante = (int) Math.round(visitante.getElo() + K * (rb - pb));
      local.setElo(nuevoEloLocal);
      visitante.setElo(nuevoEloVisitante);
    }

    jornadaActual++;
    return partidosDeLaJornada;
  }


  /**
   * Agrega una jornada ya simulada (o manualmente definida) al registro histórico.
   *
   * @param jornada Lista de partidos que componen la jornada.
   */
  public void agregarJornada(List<Partido> jornada) {
    jornadas.add(jornada);
  }


  /**
   * Reinicia todas las estadísticas de los equipos en la liga.
   * No borra los partidos ni el calendario, solo restablece el estado de los equipos.
   */
  public void reiniciar() {
    for (Equipo equipo : equipos) {
      equipo.reiniciarEstadisticas();
    }
  }

  /**
   * Simula la cantidad de goles que anota un equipo utilizando una distribución de Poisson.
   *
   * El parámetro {@code lambda} representa la tasa promedio de goles esperados para el equipo.
   * Se utiliza el algoritmo clásico de Knuth para generar un número aleatorio con dicha distribución.
   *
   * @param lambda Valor medio de la distribución Poisson (esperanza de goles).
   * @return Número entero de goles simulados (≥ 0).
   */
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



  /**
   * Actualiza las estadísticas acumuladas de goles a favor y en contra para ambos equipos.
   *
   * Este metodo debe ser llamado después de cada partido simulado o cargado,
   * y se limita a actualizar los contadores de goles (no puntos ni clasificación).
   *
   * @param local          Equipo local.
   * @param visitante      Equipo visitante.
   * @param golesLocal     Goles anotados por el local.
   * @param golesVisitante Goles anotados por el visitante.
   * @throws TorneoException Si ocurre algún error en la actualización (no implementado pero declarado).
   */
  private void actualizarEstadisticas(Equipo local, Equipo visitante, int golesLocal, int golesVisitante) throws TorneoException {
    golesAFavor.put(local, golesAFavor.get(local) + golesLocal);
    golesEnContra.put(local, golesEnContra.get(local) + golesVisitante);
    golesAFavor.put(visitante, golesAFavor.get(visitante) + golesVisitante);
    golesEnContra.put(visitante, golesEnContra.get(visitante) + golesLocal);
  }


  /**
   * Muestra por consola la tabla de posiciones actual de la liga simulada.
   *
   * Ordena los equipos según los siguientes criterios, en orden de prioridad:
   * 1. Puntos totales (descendente).
   * 2. Diferencia de goles (descendente).
   * 3. Goles a favor (descendente).
   *
   * Para cada equipo, se imprime:
   * - Nombre del equipo
   * - Puntos
   * - Partidos jugados, ganados, empatados, perdidos
   * - Goles a favor (GF) y en contra (GC)
   * - Diferencia de goles (DG)
   * - Puntuación ELO actual
   */
  public void mostrarTabla() {
    // Título principal de la tabla
    System.out.println("\n=== Tabla de Posiciones: " + nombre + " ===");

    // Encabezado con nombres de columnas
    System.out.printf("%-30s %-5s %-5s %-5s %-5s %-5s %-5s %-5s %-5s %-6s%n",
            "Equipo", "Pts", "PJ", "PG", "PE", "PP", "GF", "GC", "DG", "ELO");

    // Línea separadora
    System.out.println("----------------------------------------------------------------------------------");

    // Copia de la lista de equipos para no modificar el original
    List<Equipo> equiposOrdenados = new ArrayList<>(equipos);

    // Ordenamiento personalizado según puntos, diferencia de goles y goles a favor
    equiposOrdenados.sort((e1, e2) -> {
      int puntos1 = e1.getPuntos();
      int puntos2 = e2.getPuntos();

      // Primero ordena por puntos (descendente)
      if (puntos1 != puntos2)
        return puntos2 - puntos1;

      // Luego por diferencia de goles (descendente)
      int difGoles1 = e1.getGolesAFavor() - e1.getGolesEnContra();
      int difGoles2 = e2.getGolesAFavor() - e2.getGolesEnContra();
      if (difGoles1 != difGoles2)
        return difGoles2 - difGoles1;

      // Por último, por goles a favor (descendente)
      return e2.getGolesAFavor() - e1.getGolesAFavor();
    });

    // Impresión de cada fila de equipo en la tabla
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

      // Impresión de los datos del equipo con formato alineado
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

  /**
   * Devuelve la cantidad total de jornadas que se han generado en el calendario del torneo.
   *
   * Esto incluye tanto las jornadas de ida como de vuelta (si aplica).
   *
   * @return Número total de jornadas en el calendario.
   */
  public int getTotalJornadas() {
    return calendario.size();
  }


  /**
   * Muestra por consola los partidos jugados jornada por jornada.
   *
   * Permite al usuario navegar interactivamente entre las distintas jornadas
   * usando las siguientes opciones:
   *
   * - "N": pasar a la jornada siguiente
   * - "P": volver a la jornada anterior
   * - "0": salir del menú de jornadas
   *
   * @param scanner el objeto Scanner utilizado para leer la entrada del usuario
   */
  public void mostrarPartidosJugados(Scanner scanner) {
    // Verifica si hay jornadas cargadas
    if (jornadas.isEmpty()) {
      System.out.println("No se han simulado jornadas aún.");
      return;
    }

    // Iterador bidireccional sobre las jornadas
    ListIterator<List<Partido>> iterador = jornadas.listIterator();
    int jornadaActual = 0;

    // Muestra la primera jornada automáticamente
    if (iterador.hasNext()) {
      List<Partido> primera = iterador.next();
      jornadaActual++;
      mostrarJornada(primera, jornadaActual); // Método auxiliar que imprime la jornada actual
    }

    // Bucle interactivo hasta que el usuario decida salir
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
            // Retrocede dos veces para mostrar correctamente la jornada anterior
            if (iterador.hasPrevious()) {
              iterador.previous(); // paso atrás desde el punto actual
              if (iterador.hasPrevious()) {
                List<Partido> anterior = iterador.previous(); // obtiene la jornada previa
                jornadaActual--;
                mostrarJornada(anterior, jornadaActual);
                iterador.next(); // reposiciona el iterador después de mostrar
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

  /**
   * Imprime por consola los resultados de los partidos de una jornada específica.
   *
   * @param jornada lista de partidos jugados en esa jornada
   * @param numero número de jornada (por ejemplo: 1, 2, 3, etc.)
   */
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


  /**
   * Representa un enfrentamiento entre dos equipos, usado en la generación del calendario.
   */
  private static class Enfrentamiento {
    Equipo local;
    Equipo visitante;

    /**
     * Constructor del enfrentamiento entre dos equipos.
     *
     * @param local el equipo que juega como local
     * @param visitante el equipo que juega como visitante
     */
    public Enfrentamiento(Equipo local, Equipo visitante) {
      this.local = local;
      this.visitante = visitante;
    }
  }

  /**
   * Devuelve el nombre de la liga simulada.
   *
   * @return el nombre de la liga
   */
  public String getNombre() {
    return nombre;
  }

  /**
   * Devuelve una lista con todos los partidos jugados hasta el momento en la liga.
   *
   * Se retorna una nueva lista para preservar la encapsulación y evitar
   * modificaciones externas sobre la lista interna de partidos.
   *
   * @return una nueva lista con los partidos jugados
   */
  public List<Partido> getPartidos() {
    return new ArrayList<>(partidos);
  }
}



