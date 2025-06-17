package TP4.Modelo;

import TP4.Excepciones.TorneoException;
import java.util.*;

/**
 * Representa un torneo que combina fase de grupos y eliminación directa.
 * Primero se juega una fase de grupos donde los equipos se dividen en grupos
 * y juegan todos contra todos. Luego, los mejores equipos de cada grupo
 * pasan a una fase de eliminación directa.
 */
public class TorneoMixto extends Torneo {
  private List<List<Equipo>> grupos;
  private int equiposQuePasanPorGrupo;
  private List<Equipo> equiposClasificados;

  /**
   * Crea un nuevo torneo mixto.
   *
   * @param nombre                  Nombre del torneo
   * @param numGrupos               Número de grupos en la fase de grupos
   * @param equiposQuePasanPorGrupo Cuántos equipos pasan de cada grupo
   */
  public TorneoMixto(String nombre, int numGrupos, int equiposQuePasanPorGrupo) {
    super(nombre, true); // Heredamos de Torneo con eliminación directa
    this.grupos = new ArrayList<>();
    for (int i = 0; i < numGrupos; i++) {
      grupos.add(new ArrayList<>());
    }
    this.equiposQuePasanPorGrupo = equiposQuePasanPorGrupo;
    this.equiposClasificados = new ArrayList<>();
  }

  /**
   * Distribuye los equipos en grupos de manera aleatoria.
   * Los equipos deben haber sido agregados previamente con agregarEquipo().
   */
  public void distribuirEquiposEnGrupos() throws TorneoException {
    if (getEquipos().size() < grupos.size()) {
      throw new TorneoException("No hay suficientes equipos para llenar los grupos");
    }

    // Mezclar los equipos aleatoriamente
    List<Equipo> equiposMezclados = new ArrayList<>(getEquipos());
    Collections.shuffle(equiposMezclados);

    // Distribuir equipos en grupos
    int grupoActual = 0;
    for (Equipo equipo : equiposMezclados) {
      grupos.get(grupoActual).add(equipo);
      grupoActual = (grupoActual + 1) % grupos.size();
    }
  }

  /**
   * Simula la fase de grupos del torneo.
   * Cada equipo juega contra todos los demás equipos de su grupo.
   */
  public void simularFaseGrupos() throws TorneoException {
    for (List<Equipo> grupo : grupos) {
      // Simular partidos dentro del grupo
      for (int i = 0; i < grupo.size(); i++) {
        for (int j = i + 1; j < grupo.size(); j++) {
          simularPartido(grupo.get(i), grupo.get(j));
        }
      }

      // Ordenar equipos del grupo por puntos y diferencia de goles
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

      // Agregar los mejores equipos a la lista de clasificados
      for (int i = 0; i < Math.min(equiposQuePasanPorGrupo, grupo.size()); i++) {
        equiposClasificados.add(grupo.get(i));
      }
    }
  }

  /**
   * Simula la fase de eliminación directa con los equipos clasificados.
   */
  public void simularFaseEliminacion() throws TorneoException {
    if (equiposClasificados.size() < 2) {
      throw new TorneoException("No hay suficientes equipos clasificados para la fase de eliminación");
    }

    // Mezclar los equipos clasificados
    Collections.shuffle(equiposClasificados);

    // Simular partidos de eliminación directa
    while (equiposClasificados.size() > 1) {
      List<Equipo> ganadores = new ArrayList<>();
      for (int i = 0; i < equiposClasificados.size(); i += 2) {
        if (i + 1 < equiposClasificados.size()) {
          Equipo local = equiposClasificados.get(i);
          Equipo visitante = equiposClasificados.get(i + 1);
          simularPartido(local, visitante);
          Partido ultimoPartido = getPartidos().get(getPartidos().size() - 1);
          Equipo ganador = ultimoPartido.getGanador();
          if (ganador == null) {
            // En caso de empate, avanza el equipo con mejor ELO
            ganador = local.getElo() > visitante.getElo() ? local : visitante;
          }
          ganadores.add(ganador);
        } else {
          // Si hay un número impar de equipos, uno pasa automáticamente
          ganadores.add(equiposClasificados.get(i));
        }
      }
      equiposClasificados = ganadores;
    }
  }

  /**
   * Muestra la tabla de posiciones de cada grupo.
   */
  public void mostrarTablasGrupos() {
    for (int i = 0; i < grupos.size(); i++) {
      System.out.println("\n=== Grupo " + (i + 1) + " ===");
      System.out.printf("%-25s %-8s %-8s %-8s %-8s %-8s%n",
          "Equipo", "PJ", "PG", "PE", "PP", "Pts");
      System.out.println("------------------------------------------------------------");

      for (Equipo equipo : grupos.get(i)) {
        System.out.printf("%-25s %-8d %-8d %-8d %-8d %-8d%n",
            equipo.getNombre(),
            equipo.getPartidosJugados(),
            equipo.getPartidosGanados(),
            equipo.getPartidosEmpatados(),
            equipo.getPartidosPerdidos(),
            getPuntos(equipo));
      }
    }
  }

  /**
   * Muestra los equipos clasificados a la fase de eliminación.
   */
  public void mostrarEquiposClasificados() {
    System.out.println("\n=== Equipos Clasificados ===");
    for (Equipo equipo : equiposClasificados) {
      System.out.println("- " + equipo.getNombre());
    }
  }

  @Override
  public void simularTorneo() throws TorneoException {
    // Distribuir equipos en grupos
    distribuirEquiposEnGrupos();

    // Simular fase de grupos
    System.out.println("\nSimulando fase de grupos...");
    simularFaseGrupos();
    mostrarTablasGrupos();
    mostrarEquiposClasificados();

    // Simular fase de eliminación
    System.out.println("\nSimulando fase de eliminación directa...");
    simularFaseEliminacion();

    // Mostrar campeón
    if (!equiposClasificados.isEmpty()) {
      System.out.println("\n¡" + equiposClasificados.get(0).getNombre() + " es el campeón!");
    }
  }

  private int getPuntos(Equipo equipo) {
    return equipo.getPartidosGanados() * 3 + equipo.getPartidosEmpatados();
  }

  private int getDiferenciaGoles(Equipo equipo) {
    return equipo.getGolesAFavor() - equipo.getGolesEnContra();
  }
}