package TP4.Modelo;

import TP4.Excepciones.TorneoException;
import java.util.*;

public class TorneoMixto extends Torneo {
  private List<List<Equipo>> grupos;
  private int equiposQuePasanPorGrupo;
  private List<Equipo> equiposClasificados;
  protected NodoPartido raizEliminacion;

  public TorneoMixto(String nombre, int numGrupos, int equiposQuePasanPorGrupo) {
    super(nombre, true);
    this.grupos = new ArrayList<>();
    for (int i = 0; i < numGrupos; i++) {
      grupos.add(new ArrayList<>());
    }
    this.equiposQuePasanPorGrupo = equiposQuePasanPorGrupo;
    this.equiposClasificados = new ArrayList<>();
  }

  public void distribuirEquiposEnGrupos() throws TorneoException {
    if (getEquipos().size() < grupos.size()) {
      throw new TorneoException("No hay suficientes equipos para llenar los grupos");
    }

    List<Equipo> equiposMezclados = new ArrayList<>(getEquipos());
    Collections.shuffle(equiposMezclados);

    int grupoActual = 0;
    for (Equipo equipo : equiposMezclados) {
      grupos.get(grupoActual).add(equipo);
      grupoActual = (grupoActual + 1) % grupos.size();
    }
  }

  public void simularFaseGrupos() throws TorneoException {
    for (List<Equipo> grupo : grupos) {
      for (int i = 0; i < grupo.size(); i++) {
        for (int j = i + 1; j < grupo.size(); j++) {
          simularPartido(grupo.get(i), grupo.get(j));
        }
      }

      grupo.sort((a, b) -> {
        int puntosA = getPuntos(a);
        int puntosB = getPuntos(b);
        if (puntosA != puntosB) {
          return puntosB - puntosA;
        }
        int difGolesA = getDiferenciaGoles(a);
        int difGolesB = getDiferenciaGoles(b);
        return difGolesB - difGolesA;
      });

      for (int i = 0; i < Math.min(equiposQuePasanPorGrupo, grupo.size()); i++) {
        equiposClasificados.add(grupo.get(i));
      }
    }
  }

  public void simularFaseEliminacion() throws TorneoException {
    if (equiposClasificados.size() < 2) {
      throw new TorneoException("No hay suficientes equipos clasificados para la fase de eliminación");
    }
    for (Equipo equipo : equiposClasificados) agregarEquipo(equipo);
    Collections.shuffle(equiposClasificados);

    Queue<NodoPartido> cola = new LinkedList<>();
    for (int i = 0; i < equiposClasificados.size(); i += 2) {
      Equipo local = equiposClasificados.get(i);
      Equipo visitante = (i + 1 < equiposClasificados.size()) ? equiposClasificados.get(i + 1)
              : new Equipo(-1, "BYE", 1500, "Libre", "bye.png", "N/A");
      simularPartido(local, visitante);
      Partido partido = getPartidos().get(getPartidos().size() - 1);

      NodoPartido nodo = new NodoPartido(partido);
      nodo.setGanador(partido.getGanadorConDesempate());
      cola.offer(nodo);
    }

    while (cola.size() > 1) {
      NodoPartido nodo1 = cola.poll();
      NodoPartido nodo2 = cola.poll();

      Partido nuevo = new Partido(nodo1.getGanador(), nodo2.getGanador());
      simularPartido(nuevo.getLocal(), nuevo.getVisitante());
      nuevo = getPartidos().get(getPartidos().size() - 1);

      NodoPartido padre = new NodoPartido(nuevo);
      padre.setIzquierdo(nodo1);
      padre.setDerecho(nodo2);
      padre.setGanador(nuevo.getGanadorConDesempate());
      cola.offer(padre);
    }

    raizEliminacion = cola.poll();
  }

  public void imprimirTablaDeGrupos() {
    for (int i = 0; i < grupos.size(); i++) {
      List<Equipo> grupo = grupos.get(i);

      grupo.sort((a, b) -> Integer.compare(getPuntos(b), getPuntos(a))); // Orden descendente

      System.out.println("\n=== Tabla de Posiciones - Grupo " + (i + 1) + " ===");
      System.out.printf("%-25s %5s %5s %5s %5s %5s %5s %5s\n", "Equipo", "Pts", "PJ", "PG", "PE", "PP", "GF", "GC");
      System.out.println("---------------------------------------------------------------");

      for (Equipo eq : grupo) {
        System.out.printf("%-25s %5d %5d %5d %5d %5d %5d %5d\n",
                eq.getNombre(),
                getPuntos(eq),
                eq.getPartidosJugados(),
                eq.getPartidosGanados(),
                eq.getPartidosEmpatados(),
                eq.getPartidosPerdidos(),
                eq.getGolesAFavor(),
                eq.getGolesEnContra());
      }
    }
  }


  private void imprimirLlavesDesdeRaiz(NodoPartido nodo, int nivel) {
    if (nodo == null) return;
    imprimirLlavesDesdeRaiz(nodo.getDerecho(), nivel + 1);
    System.out.println("  ".repeat(nivel) + nodo.getPartido().resumenPartidoConGanador());
    imprimirLlavesDesdeRaiz(nodo.getIzquierdo(), nivel + 1);
  }

  public void imprimirLlavesEliminacion() {
    if (raizEliminacion == null) {
      System.out.println("No hay partidos simulados aún.");
      return;
    }

    System.out.println("\n=== Llave del Torneo (Formato Árbol) ===\n");
    imprimirLlavesEstiloBracket(raizEliminacion, 0, true);

    Equipo campeon = raizEliminacion.getGanador();
    if (campeon != null) {
      System.out.println("\n🏆 Campeón: " + campeon.getNombre());
    } else {
      System.out.println("\n🏆 Campeón: Desconocido");
    }
  }


  private void imprimirLlavesEstiloBracket(NodoPartido nodo, int nivel, boolean izquierdo) {
    if (nodo == null) return;

    Partido p = nodo.getPartido();
    String indent = "        ".repeat(nivel); // 8 espacios por nivel (más sangría)
    String flecha = izquierdo ? "└── " : "┌── ";

    // Imprime rama derecha primero (arriba)
    imprimirLlavesEstiloBracket(nodo.getDerecho(), nivel + 1, false);

    // Más espacio entre fases (vertical)
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

    String local = (p != null && p.getLocal() != null) ? abreviar(p.getLocal().getNombre()) : "BYE";
    String visitante = (p != null && p.getVisitante() != null) ? abreviar(p.getVisitante().getNombre()) : "BYE";
    String ganador = (p != null && p.getGanador() != null) ? " → 🏅" + abreviar(p.getGanador().getNombre()) : "";

    System.out.println(indent + flecha + "[" + fase + "] " + local + " vs " + visitante + ganador);

    // Imprime rama izquierda después (abajo)
    imprimirLlavesEstiloBracket(nodo.getIzquierdo(), nivel + 1, true);

    // Más espacio después de una rama (opcional)
    if (nivel <= 2) System.out.println();
  }



  private String abreviar(String nombre) {
    return nombre.length() <= 10 ? nombre : nombre.substring(0, 10);
  }

  public void mostrarTablasGrupos() {
    for (int i = 0; i < grupos.size(); i++) {
      System.out.println("\n=== Grupo " + (i + 1) + " ===");
      System.out.printf("%-30s %-5s %-5s %-5s %-5s %-5s %-5s %-5s %-5s %-6s%n",
              "Equipo", "Pts", "PJ", "PG", "PE", "PP", "GF", "GC", "DG", "ELO");
      System.out.println("----------------------------------------------------------------------------------");

      for (Equipo equipo : grupos.get(i)) {
        System.out.printf("%-30s %-5d %-5d %-5d %-5d %-5d %-5d %-5d %-5d %-6d%n",
                equipo.getNombre(),
                getPuntos(equipo),
                equipo.getPartidosJugados(),
                equipo.getPartidosGanados(),
                equipo.getPartidosEmpatados(),
                equipo.getPartidosPerdidos(),
                equipo.getGolesAFavor(),
                equipo.getGolesEnContra(),
                equipo.getDiferenciaGoles(),
                equipo.getElo());
      }
    }
  }

  private NodoPartido construirArbolEliminacion(List<Equipo> equipos) throws TorneoException {
    if (equipos.size() == 1) return new NodoPartido(null);

    List<NodoPartido> nodos = new ArrayList<>();
    List<Equipo> ganadores = new ArrayList<>();

    for (int i = 0; i < equipos.size(); i += 2) {
      Equipo e1 = equipos.get(i);
      Equipo e2 = equipos.get(i + 1);
      Partido partido;
      NodoPartido nodo;

      if (e1.getNombre().equals("BYE")) {
        nodo = new NodoPartido(null);
        nodo.setGanador(e2);
      } else if (e2.getNombre().equals("BYE")) {
        nodo = new NodoPartido(null);
        nodo.setGanador(e1);
      } else {
        partido = new Partido(e1, e2);
        nodo = new NodoPartido(partido);
        nodo.setGanador(partido.getGanadorConDesempate());
      }
      nodos.add(nodo);
      ganadores.add(nodo.getGanador());
    }

    if (ganadores.size() == 1) return nodos.get(0);

    return construirArbolEliminacion(ganadores);
  }

  @Override
  public void mostrarPartidosTorneo(Scanner scanner) {
    if (grupos == null || grupos.isEmpty()) {
      System.out.println("No hay grupos cargados.");
      return;
    }

    // === FASE DE GRUPOS ===
    for (int g = 0; g < grupos.size(); g++) {
      List<Equipo> grupo = grupos.get(g);
      List<Partido> partidosGrupo = filtrarPartidosPorGrupo(grupo);
      List<List<Partido>> jornadas = generarJornadasGrupo(grupo, partidosGrupo);

      if (jornadas.isEmpty()) continue;

      ListIterator<List<Partido>> iterador = jornadas.listIterator();
      int nroJornada = 0;

      System.out.println("\n=== Fase de Grupos - Grupo " + (g + 1) + " ===");

      if (iterador.hasNext()) {
        List<Partido> primera = iterador.next();
        nroJornada++;
        mostrarJornadaFormatoLiga(primera, nroJornada);
      }

      while (true) {
        System.out.println("\nIngrese N para siguiente, P para anterior, 0 para salir del grupo:");
        String entrada = scanner.nextLine().trim().toUpperCase();

        switch (entrada) {
          case "N" -> {
            if (iterador.hasNext()) {
              List<Partido> siguiente = iterador.next();
              nroJornada++;
              mostrarJornadaFormatoLiga(siguiente, nroJornada);
            } else {
              System.out.println("No hay más jornadas en este grupo.");
            }
          }
          case "P" -> {
            if (iterador.hasPrevious()) {
              iterador.previous();
              if (iterador.hasPrevious()) {
                List<Partido> anterior = iterador.previous();
                nroJornada--;
                mostrarJornadaFormatoLiga(anterior, nroJornada);
                iterador.next();
              } else {
                System.out.println("Ya estás en la primera jornada.");
              }
            } else {
              System.out.println("Ya estás en la primera jornada.");
            }
          }
          case "0" -> {
            System.out.println("Saliendo del grupo " + (g + 1));
            break;
          }
          default -> System.out.println("Opción inválida.");
        }

        if (entrada.equals("0")) break;
      }
    }

    // === FASE ELIMINATORIA ===
    System.out.println("\n=== Fase Eliminatoria ===");

    // Filtrar partidos que NO pertenecen a fase de grupos
    Set<Partido> partidosGrupos = new HashSet<>();
    for (List<Equipo> grupo : grupos) {
      partidosGrupos.addAll(filtrarPartidosPorGrupo(grupo));
    }

    List<Partido> partidosEliminacion = getPartidos().stream()
            .filter(p -> !partidosGrupos.contains(p))
            .toList();

    if (partidosEliminacion.isEmpty()) {
      System.out.println("No hay partidos de eliminación directa registrados.");
      return;
    }

    // Agrupar en "jornadas" de 4 partidos
    List<List<Partido>> jornadasEliminacion = new ArrayList<>();
    int cantidadPorJornada = 4;
    for (int i = 0; i < partidosEliminacion.size(); i += cantidadPorJornada) {
      int fin = Math.min(i + cantidadPorJornada, partidosEliminacion.size());
      jornadasEliminacion.add(partidosEliminacion.subList(i, fin));
    }

    ListIterator<List<Partido>> it = jornadasEliminacion.listIterator();
    int jornada = 0;

    if (it.hasNext()) {
      List<Partido> primera = it.next();
      jornada++;
      mostrarJornadaFormatoLiga(primera, jornada);
    }

    while (true) {
      System.out.println("\nIngrese N para siguiente, P para anterior, 0 para salir:");
      String entrada = scanner.nextLine().trim().toUpperCase();

      switch (entrada) {
        case "N" -> {
          if (it.hasNext()) {
            List<Partido> siguiente = it.next();
            jornada++;
            mostrarJornadaFormatoLiga(siguiente, jornada);
          } else {
            System.out.println("No hay más jornadas.");
          }
        }
        case "P" -> {
          if (it.hasPrevious()) {
            it.previous();
            if (it.hasPrevious()) {
              List<Partido> anterior = it.previous();
              jornada--;
              mostrarJornadaFormatoLiga(anterior, jornada);
              it.next();
            } else {
              System.out.println("Ya estás en la primera jornada.");
            }
          } else {
            System.out.println("Ya estás en la primera jornada.");
          }
        }
        case "0" -> {
          System.out.println("Saliendo del visor.");
          return;
        }
        default -> System.out.println("Opción inválida.");
      }
    }
  }

  private List<List<Partido>> generarJornadasGrupo(List<Equipo> grupo, List<Partido> partidosGrupo) {
    List<List<Partido>> jornadas = new ArrayList<>();
    int totalEquipos = grupo.size();
    int totalJornadas = totalEquipos - 1;

    List<Equipo> rotables = new ArrayList<>(grupo);

    for (int j = 0; j < totalJornadas; j++) {
      List<Partido> jornada = new ArrayList<>();

      for (int i = 0; i < totalEquipos / 2; i++) {
        Equipo local = rotables.get(i);
        Equipo visitante = rotables.get(totalEquipos - 1 - i);

        // Buscar partido jugado entre estos dos equipos
        for (Partido p : partidosGrupo) {
          if ((p.getLocal().equals(local) && p.getVisitante().equals(visitante)) ||
                  (p.getLocal().equals(visitante) && p.getVisitante().equals(local))) {
            jornada.add(p);
            break;
          }
        }
      }

      jornadas.add(jornada);

      // Rotar todos menos el primero
      Collections.rotate(rotables.subList(1, totalEquipos), 1);
    }

    return jornadas;
  }



  private List<Partido> filtrarPartidosPorGrupo(List<Equipo> grupo) {
    return getPartidos().stream()
            .filter(p -> grupo.contains(p.getLocal()) && grupo.contains(p.getVisitante()))
            .toList();
  }


  public void mostrarEquiposClasificados() {
    System.out.println("\n=== Equipos Clasificados ===");
    for (Equipo equipo : equiposClasificados) {
      System.out.println("- " + equipo.getNombre());
    }
  }

  @Override
  public void simularTorneo() throws TorneoException {
    distribuirEquiposEnGrupos();
    System.out.println("\nSimulando fase de grupos...");
    simularFaseGrupos();
    mostrarTablasGrupos();
    mostrarEquiposClasificados();
    System.out.println("\nSimulando fase de eliminación directa...");
    simularFaseEliminacion();
  }

  private int getPuntos(Equipo equipo) {
    return equipo.getPartidosGanados() * 3 + equipo.getPartidosEmpatados();
  }

  private int getDiferenciaGoles(Equipo equipo) {
    return equipo.getGolesAFavor() - equipo.getGolesEnContra();
  }
}
