package TP4.Modelo;

import TP4.Excepciones.TorneoException;
import java.util.*;

public class TorneoMixto extends Torneo {
  private List<List<Equipo>> grupos;
  private int equiposQuePasanPorGrupo;
  private List<Equipo> equiposClasificados;
  private NodoPartido raiz;
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
    for (Equipo equipo : equiposClasificados) {
      agregarEquipo(equipo);
    }
    Collections.shuffle(equiposClasificados);

    Queue<NodoPartido> cola = new LinkedList<>();
    for (int i = 0; i < equiposClasificados.size(); i += 2) {
      Equipo local = equiposClasificados.get(i);
      Equipo visitante = (i + 1 < equiposClasificados.size()) ? equiposClasificados.get(i + 1)
              : new Equipo(-1, "BYE", 1500, "Libre", "bye.png", "N/A");
      simularPartido(local, visitante);
      Partido partido = getPartidos().get(getPartidos().size() - 1);
      Equipo ganador = partido.getGanadorConDesempate();

      NodoPartido nodo = new NodoPartido(partido);
      nodo.setGanador(ganador);
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

    raiz = cola.poll();
    imprimirLlavesEliminacion();
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
    System.out.println("\n=== Llave del Torneo (Formato Árbol) ===\n");
    imprimirLlaveRecursiva(raizEliminacion, 0);
    if (raizEliminacion != null && raizEliminacion.getPartido() != null && raizEliminacion.getPartido().getGanador() != null) {
      System.out.println("\n\uD83C\uDFC6 ¡" + raizEliminacion.getPartido().getGanador().getNombre() + " es el campeón del torneo!");
    }
  }

  private void imprimirLlaveRecursiva(NodoPartido nodo, int nivel) {
    if (nodo == null || nodo.getPartido() == null) return;

    String indent = " ".repeat(nivel * 4);
    Partido p = nodo.getPartido();

    String local = p.getLocal() != null ? abreviar(p.getLocal().getNombre()) : "BYE";
    String visitante = p.getVisitante() != null ? abreviar(p.getVisitante().getNombre()) : "BYE";
    String ganador = p.getGanador() != null ? abreviar(p.getGanador().getNombre()) : "¿?";

    System.out.println(indent + local + " vs " + visitante + " → 🏅 " + ganador);

    imprimirLlaveRecursiva(nodo.getIzquierdo(), nivel + 1);
    imprimirLlaveRecursiva(nodo.getDerecho(), nivel + 1);
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
