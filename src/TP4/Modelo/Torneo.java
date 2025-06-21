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

  private NodoPartido raizEliminacion;

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

    if (!equipos.contains(local) || !equipos.contains(visitante)) {
      System.out.println("DEBUG - Equipos en torneo:");
      for (Equipo e : equipos) System.out.println(e.getNombre() + " - ID: " + e.getId());

      System.out.println("DEBUG - Local: " + local.getNombre() + " - ID: " + local.getId());
      System.out.println("DEBUG - Visitante: " + visitante.getNombre() + " - ID: " + visitante.getId());

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

  private void imprimirNodo(NodoPartido nodo, int nivel) {
    if (nodo == null) return;
    imprimirNodo(nodo.getIzquierdo(), nivel + 1);
    String esp = "  ".repeat(nivel);
    String local = abreviar(nodo.getPartido().getLocal().getNombre());
    String visitante = abreviar(nodo.getPartido().getVisitante().getNombre());
    String ganador = abreviar(nodo.getPartido().getGanador().getNombre());
    System.out.println(esp + "├─ " + local + " vs " + visitante + " → 🏅 " + ganador);
    imprimirNodo(nodo.getDerecho(), nivel + 1);
  }


  private String abreviar(String nombre) {
    return nombre.length() <= 6 ? nombre : nombre.substring(0, 6);
  }

  private String nombreFase(int ronda, int totalRondas) {
    int desdeFinal = totalRondas - ronda;
    return switch (desdeFinal) {
      case 0 -> "Campeón";
      case 1 -> "Final";
      case 2 -> "Semifinales";
      case 3 -> "Cuartos";
      case 4 -> "Octavos";
      case 5 -> "16avos";
      case 6 -> "32avos";
      case 7 -> "64avos";
      default -> "";
    };
  }



  /**
   * Inicia la simulación completa del torneo.
   *
   * - Verifica que haya al menos 2 equipos.
   * - Simula todos los partidos según el tipo de torneo:
   * - Eliminación directa: mediante eliminación por rondas.
   * - Liga: todos contra todos.
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

    // Simular y construir el árbol binario
    this.raizEliminacion = construirArbolEliminacion(equiposDisponibles);
    imprimirBracket();
  }

  public void imprimirBracket() {
    if (raizEliminacion == null) {
      System.out.println("No hay partidos simulados aún.");
      return;
    }

    System.out.println("\n=== Cuadro del Torneo (Formato Bracket) ===\n");
    imprimirBracketRecursivo(raizEliminacion, 0, true);
    System.out.println("\n🏆 Campeón: " + raizEliminacion.getPartido().getGanador().getNombre());
  }

  private void imprimirBracketRecursivo(NodoPartido nodo, int nivel, boolean izquierdo) {
    if (nodo == null || nodo.getPartido() == null) return;

    Partido p = nodo.getPartido();
    String indent = "    ".repeat(nivel);
    String flecha = izquierdo ? "└── " : "┌── ";

    imprimirBracketRecursivo(nodo.getDerecho(), nivel + 1, false);

    String local = p.getLocal() != null ? p.getLocal().getNombre() : "BYE";
    String visitante = p.getVisitante() != null ? p.getVisitante().getNombre() : "BYE";
    String ganador = p.getGanador() != null ? " 🏅" + p.getGanador().getNombre() : "";

    System.out.println(indent + flecha + local + " vs " + visitante + ganador);

    imprimirBracketRecursivo(nodo.getIzquierdo(), nivel + 1, true);
  }


  private NodoPartido construirArbolEliminacion(List<Equipo> equipos) throws TorneoException {
    if (equipos.size() == 1) {
      // Nodo hoja con el único equipo restante
      NodoPartido nodo = new NodoPartido(null);
      nodo.setGanador(equipos.get(0));
      return nodo;
    }

    List<NodoPartido> nodosRonda = new ArrayList<>();

    for (int i = 0; i < equipos.size(); i += 2) {
      Equipo e1 = equipos.get(i);
      Equipo e2 = (i + 1 < equipos.size()) ? equipos.get(i + 1) : null;

      NodoPartido nodo;

      if (e2 == null || e1.getNombre().equals("BYE")) {
        nodo = new NodoPartido(null);
        nodo.setGanador(e2);
      } else if (e2.getNombre().equals("BYE")) {
        nodo = new NodoPartido(null);
        nodo.setGanador(e1);
      } else {
        Partido partido = new Partido(e1, e2);
        nodo = new NodoPartido(partido);
        nodo.setGanador(partido.getGanadorConDesempate());
      }

      nodosRonda.add(nodo);
    }

    // Obtener ganadores para la siguiente ronda
    List<Equipo> ganadores = new ArrayList<>();
    for (NodoPartido nodo : nodosRonda) {
      if (nodo.getGanador() != null) {
        ganadores.add(nodo.getGanador());
      } else {
        throw new TorneoException("Error: nodo sin ganador válido.");
      }
    }

    // Construir recursivamente el siguiente nivel
    NodoPartido siguienteNivel = construirArbolEliminacion(ganadores);

    // Si hay exactamente un nodo, es la raíz del árbol
    if (nodosRonda.size() == 1) return nodosRonda.get(0);

    // Combinar nodos de esta ronda como hijos del nodo raíz final
    for (int i = 0; i < nodosRonda.size(); i += 2) {
      NodoPartido padre = new NodoPartido(null);
      padre.setIzquierdo(nodosRonda.get(i));
      if (i + 1 < nodosRonda.size()) {
        padre.setDerecho(nodosRonda.get(i + 1));
      }
      padre.setGanador(siguienteNivel.getGanador()); // marcador simbólico
      siguienteNivel = padre;
    }

    return siguienteNivel;
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

    ListIterator<Partido> iterador = partidos.listIterator();
    int numeroPartido = 0;

    // Mostrar el primer partido
    if (iterador.hasNext()) {
      Partido primero = iterador.next();
      numeroPartido++;
      System.out.println("\n=== Partido " + numeroPartido + " ===");
      System.out.println(primero);
    }

    while (true) {
      System.out.println("\nIngrese N para siguiente, P para anterior, 0 para salir:");
      String entrada = scanner.nextLine().trim().toUpperCase();

      switch (entrada) {
        case "N" -> {
          if (iterador.hasNext()) {
            Partido siguiente = iterador.next();
            numeroPartido++;
            System.out.println("\n=== Partido " + numeroPartido + " ===");
            System.out.println(siguiente);
          } else {
            System.out.println("No hay más partidos siguientes.");
          }
        }
        case "P" -> {
          if (iterador.hasPrevious()) {
            // retroceder al partido actual y mostrar el anterior
            if (iterador.hasPrevious()) {
              iterador.previous(); // posicionar al actual
              Partido anterior = iterador.previous();
              numeroPartido--;
              System.out.println("\n=== Partido " + numeroPartido + " ===");
              System.out.println(anterior);
              iterador.next(); // reposicionar en el correcto
            } else {
              System.out.println("Ya estás en el primer partido.");
            }
          } else {
            System.out.println("Ya estás en el primer partido.");
          }
        }
        case "0" -> {
          System.out.println("Saliendo del visor de partidos.");
          return;
        }
        default -> System.out.println("Opción inválida.");
      }
    }
  }






//  public void imprimirLlavesEliminacion() {
//    System.out.println("\n=== Llave del Torneo (Formato Árbol) ===\n");
//    imprimirLlaveRecursiva(raizEliminacion, 0);
//    if (raizEliminacion != null && raizEliminacion.getPartido() != null && raizEliminacion.getPartido().getGanador() != null) {
//      System.out.println("\n\uD83C\uDFC6 \u00a1" + raizEliminacion.getPartido().getGanador().getNombre() + " es el campeón del torneo!");
//    }
//  }
//
//  private void imprimirLlaveRecursiva(NodoPartido nodo, int nivel) {
//    if (nodo == null || nodo.getPartido() == null) return;
//
//    String indent = " ".repeat(nivel * 4);
//    Partido p = nodo.getPartido();
//
//    String local = p.getLocal() != null ? abreviar(p.getLocal().getNombre()) : "BYE";
//    String visitante = p.getVisitante() != null ? abreviar(p.getVisitante().getNombre()) : "BYE";
//    String ganador = p.getGanador() != null ? abreviar(p.getGanador().getNombre()) : "¿?";
//
//    System.out.println(indent + local + " vs " + visitante + " → 🏅 " + ganador);
//
//    imprimirLlaveRecursiva(nodo.getIzquierdo(), nivel + 1);
//    imprimirLlaveRecursiva(nodo.getDerecho(), nivel + 1);
//  }


  private NodoPartido simularRonda(List<Equipo> equipos) throws TorneoException {
    if (equipos.size() == 1) return null;

    if (equipos.size() == 2) {
      Equipo local = equipos.get(0);
      Equipo visitante = equipos.get(1);
      simularPartido(local, visitante);
      Partido partido = partidos.get(partidos.size() - 1);
      return new NodoPartido(partido);
    }

    List<Equipo> ganadores = new ArrayList<>();
    List<NodoPartido> nodos = new ArrayList<>();

    for (int i = 0; i < equipos.size(); i += 2) {
      if (i + 1 < equipos.size()) {
        Equipo local = equipos.get(i);
        Equipo visitante = equipos.get(i + 1);
        simularPartido(local, visitante);
        Partido partido = partidos.get(partidos.size() - 1);

        Equipo ganador = partido.getGanador();
        if (ganador == null) {
          ganador = local.getElo() > visitante.getElo() ? local : visitante;
        }
        ganadores.add(ganador);
        NodoPartido nodo = new NodoPartido(partido);
        nodos.add(nodo);
      } else {
        ganadores.add(equipos.get(i));
      }
    }

    List<NodoPartido> nuevaRonda = new ArrayList<>();
    for (int i = 0; i < nodos.size(); i += 2) {
      if (i + 1 < nodos.size()) {
        Equipo local = ganadores.get(i);
        Equipo visitante = ganadores.get(i + 1);
        simularPartido(local, visitante);
        Partido partido = partidos.get(partidos.size() - 1);
        Equipo ganador = partido.getGanador();
        if (ganador == null) {
          ganador = local.getElo() > visitante.getElo() ? local : visitante;
        }
        ganadores.set(i / 2, ganador);
        NodoPartido padre = new NodoPartido(partido);
        padre.setIzquierdo(nodos.get(i));
        padre.setDerecho(nodos.get(i + 1));
        nuevaRonda.add(padre);
      }
    }

    return nuevaRonda.size() == 1 ? nuevaRonda.get(0) : simularRonda(ganadores);
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
   * Esta tabla resume el rendimiento de cada equipo durante la simulación del
   * torneo.
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

      if (puntos1 != puntos2)
        return puntos2 - puntos1;

      int difGoles1 = golesAFavor.get(e1) - golesEnContra.get(e1);
      int difGoles2 = golesAFavor.get(e2) - golesEnContra.get(e2);
      if (difGoles1 != difGoles2)
        return difGoles2 - difGoles1;

      return golesAFavor.get(e2) - golesAFavor.get(e1); // Tercer criterio: GF
    });

    // Mostrar fila por fila
    for (Equipo equipo : equiposOrdenados) {
      int pg = 0, pe = 0, pp = 0;

      // Calcular partidos ganados, empatados y perdidos
      for (Partido partido : partidos) {
        if (partido.getLocal() == equipo) {
          if (partido.getGolesLocal() > partido.getGolesVisitante())
            pg++;
          else if (partido.getGolesLocal() == partido.getGolesVisitante())
            pe++;
          else
            pp++;
        } else if (partido.getVisitante() == equipo) {
          if (partido.getGolesVisitante() > partido.getGolesLocal())
            pg++;
          else if (partido.getGolesVisitante() == partido.getGolesLocal())
            pe++;
          else
            pp++;
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