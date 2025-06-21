package TP4.Modelo;

import TP4.Excepciones.TorneoException;
import java.util.*;

/**
 * Clase TorneoMixto
 *
 * Representa un torneo de fútbol mixto compuesto por:
 * - Una fase de grupos (todos contra todos dentro de cada grupo)
 * - Una fase final de eliminación directa con los equipos clasificados
 *
 * Extiende de la clase base `Torneo` e incluye lógica para manejar grupos
 * y nodos del árbol de eliminación directa.
 */
public class TorneoMixto extends Torneo {
  // Lista de grupos, cada uno contiene una lista de equipos
  private List<List<Equipo>> grupos;

  // Cantidad de equipos que clasifican por grupo a la fase eliminatoria
  private int equiposQuePasanPorGrupo;

  // Lista final de equipos clasificados para la eliminación directa
  private List<Equipo> equiposClasificados;

  // Raíz del árbol binario de partidos en la fase de eliminación
  protected NodoPartido raizEliminacion;


  /**
   * Constructor del torneo mixto.
   *
   * @param nombre Nombre del torneo.
   * @param numGrupos Cantidad de grupos para la fase inicial.
   * @param equiposQuePasanPorGrupo Número de equipos que avanzan por grupo a la siguiente fase.
   */
  public TorneoMixto(String nombre, int numGrupos, int equiposQuePasanPorGrupo) {
    super(nombre, true); // Activa modo eliminación para la segunda fase
    this.grupos = new ArrayList<>();
    for (int i = 0; i < numGrupos; i++) {
      grupos.add(new ArrayList<>()); // Inicializa cada grupo como una lista vacía
    }
    this.equiposQuePasanPorGrupo = equiposQuePasanPorGrupo;
    this.equiposClasificados = new ArrayList<>();
  }

  /**
   * Distribuye de forma aleatoria los equipos participantes en los distintos grupos del torneo.
   *
   * @throws TorneoException Si la cantidad de equipos es menor a la cantidad de grupos definidos.
   */
  public void distribuirEquiposEnGrupos() throws TorneoException {
    // Verifica que haya al menos un equipo por grupo
    if (getEquipos().size() < grupos.size()) {
      throw new TorneoException("No hay suficientes equipos para llenar los grupos");
    }

    // Copia y mezcla aleatoriamente la lista de equipos
    List<Equipo> equiposMezclados = new ArrayList<>(getEquipos());
    Collections.shuffle(equiposMezclados);

    // Asigna los equipos a los grupos en forma circular (round-robin)
    int grupoActual = 0;
    for (Equipo equipo : equiposMezclados) {
      grupos.get(grupoActual).add(equipo); // Agrega al grupo actual
      grupoActual = (grupoActual + 1) % grupos.size(); // Avanza al siguiente grupo
    }
  }

  /**
   * Simula todos los partidos dentro de cada grupo (todos contra todos),
   * ordena los equipos según puntos y diferencia de goles,
   * y clasifica a los mejores por grupo para la siguiente fase.
   *
   * @throws TorneoException Si ocurre un error durante la simulación de un partido.
   */
  public void simularFaseGrupos() throws TorneoException {
    // Recorre cada grupo de equipos
    for (List<Equipo> grupo : grupos) {

      // Simula todos los partidos posibles dentro del grupo (round-robin)
      for (int i = 0; i < grupo.size(); i++) {
        for (int j = i + 1; j < grupo.size(); j++) {
          simularPartido(grupo.get(i), grupo.get(j));
        }
      }

      // Ordena el grupo por:
      // 1. Puntos (mayor primero)
      // 2. Diferencia de goles (mayor primero)
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

      // Clasifica los mejores equipos del grupo según el cupo definido
      for (int i = 0; i < Math.min(equiposQuePasanPorGrupo, grupo.size()); i++) {
        equiposClasificados.add(grupo.get(i));
      }
    }
  }

  /**
   * Simula la fase de eliminación directa a partir de los equipos clasificados de la fase de grupos.
   * Construye un árbol binario de partidos hasta determinar al campeón.
   *
   * @throws TorneoException Si hay menos de 2 equipos clasificados.
   */
  public void simularFaseEliminacion() throws TorneoException {
    // Validación mínima: se requieren al menos 2 equipos para iniciar la fase
    if (equiposClasificados.size() < 2) {
      throw new TorneoException("No hay suficientes equipos clasificados para la fase de eliminación");
    }

    // Agrega los equipos clasificados al torneo base
    for (Equipo equipo : equiposClasificados) {
      agregarEquipo(equipo);
    }

    // Mezcla aleatoriamente el orden de los equipos para emparejamientos aleatorios
    Collections.shuffle(equiposClasificados);

    // Cola para almacenar nodos de partidos (bracket de eliminación)
    Queue<NodoPartido> cola = new LinkedList<>();

    // Crea los partidos de la primera ronda (octavos, cuartos, etc.)
    for (int i = 0; i < equiposClasificados.size(); i += 2) {
      Equipo local = equiposClasificados.get(i);
      // Si hay número impar, se asigna un equipo "BYE" que pierde automáticamente
      Equipo visitante = (i + 1 < equiposClasificados.size())
              ? equiposClasificados.get(i + 1)
              : new Equipo(-1, "BYE", 1500, "Libre", "bye.png", "N/A");

      // Simula el partido entre los dos equipos
      simularPartido(local, visitante);

      // Toma el partido recién creado desde la lista general
      Partido partido = getPartidos().get(getPartidos().size() - 1);

      // Crea un nodo de árbol binario con el partido
      NodoPartido nodo = new NodoPartido(partido);
      // Establece el ganador del nodo (con criterio de desempate)
      nodo.setGanador(partido.getGanadorConDesempate());

      // Agrega el nodo a la cola
      cola.offer(nodo);
    }

    // Proceso iterativo: toma dos nodos, crea un nuevo partido entre sus ganadores,
    // y vuelve a encolar el nodo padre (ronda superior del árbol)
    while (cola.size() > 1) {
      NodoPartido nodo1 = cola.poll();
      NodoPartido nodo2 = cola.poll();

      // Crea y simula el partido entre los ganadores de los nodos previos
      Partido nuevo = new Partido(nodo1.getGanador(), nodo2.getGanador());
      simularPartido(nuevo.getLocal(), nuevo.getVisitante());
      nuevo = getPartidos().get(getPartidos().size() - 1);

      // Construye el nodo padre y enlaza los hijos
      NodoPartido padre = new NodoPartido(nuevo);
      padre.setIzquierdo(nodo1);
      padre.setDerecho(nodo2);
      padre.setGanador(nuevo.getGanadorConDesempate());

      // Agrega el nodo padre a la cola para siguientes rondas
      cola.offer(padre);
    }

    // El único nodo restante es la raíz del árbol, que contiene al campeón
    raizEliminacion = cola.poll();
  }

  /**
   * Imprime en consola la llave de eliminación directa del torneo, en formato de árbol (bracket).
   * Utiliza una representación recursiva indentada de cada ronda, desde la final hacia rondas anteriores.
   * Muestra también el nombre del equipo campeón.
   */
  public void imprimirLlavesEliminacion() {
    // Si no se ha simulado el torneo aún, no hay raíz de árbol
    if (raizEliminacion == null) {
      System.out.println("No hay partidos simulados aún.");
      return;
    }

    System.out.println("\n=== Llave del Torneo (Formato Árbol) ===\n");

    // Llama al metodo recursivo para imprimir desde la raíz del árbol
    imprimirLlavesEstiloBracket(raizEliminacion, 0, true);

    // Muestra al campeón del torneo, si se definió correctamente
    Equipo campeon = raizEliminacion.getGanador();
    if (campeon != null) {
      System.out.println("\n🏆 Campeón: " + campeon.getNombre());
    } else {
      System.out.println("\n🏆 Campeón: Desconocido");
    }
  }

  /**
   * Metodo recursivo para imprimir visualmente la estructura del torneo en formato de árbol (bracket).
   * Se imprime con indentación jerárquica, diferenciando niveles (final, semifinal, etc.).
   *
   * @param nodo Nodo actual del árbol (partido)
   * @param nivel Nivel de profundidad del nodo en el árbol (0: final, 1: semifinal, etc.)
   * @param izquierdo Indica si la rama actual es izquierda o derecha (afecta el símbolo de impresión)
   */
  private void imprimirLlavesEstiloBracket(NodoPartido nodo, int nivel, boolean izquierdo) {
    if (nodo == null) return;

    Partido p = nodo.getPartido();

    // Calcula la indentación horizontal según el nivel
    String indent = "        ".repeat(nivel); // 8 espacios por nivel
    String flecha = izquierdo ? "└── " : "┌── ";

    // Imprime primero la rama derecha (visualmente arriba)
    imprimirLlavesEstiloBracket(nodo.getDerecho(), nivel + 1, false);

    // Salto de línea entre niveles para claridad visual
    if (nivel > 0) System.out.println();

    // Traducción del nivel a nombre de fase
    String fase = switch (nivel) {
      case 0 -> "Final";
      case 1 -> "Semifinal";
      case 2 -> "Cuartos";
      case 3 -> "8vos";
      case 4 -> "16vos";
      case 5 -> "32vos";
      default -> "Ronda " + (nivel + 1);
    };

    // Obtiene los nombres de los equipos o "BYE" si no están definidos
    String local = (p != null && p.getLocal() != null) ? abreviar(p.getLocal().getNombre()) : "BYE";
    String visitante = (p != null && p.getVisitante() != null) ? abreviar(p.getVisitante().getNombre()) : "BYE";

    // Indica el equipo ganador si existe
    String ganador = (p != null && p.getGanador() != null) ? " → 🏅" + abreviar(p.getGanador().getNombre()) : "";

    // Imprime la línea correspondiente al partido
    System.out.println(indent + flecha + "[" + fase + "] " + local + " vs " + visitante + ganador);

    // Imprime la rama izquierda (abajo)
    imprimirLlavesEstiloBracket(nodo.getIzquierdo(), nivel + 1, true);

    // Espaciado extra para las rondas más importantes (final, semi, cuartos)
    if (nivel <= 2) System.out.println();
  }

  /**
   * Abrevia un nombre a un máximo de 10 caracteres.
   * Si el nombre es igual o menor a 10 caracteres, lo devuelve sin cambios.
   * Usado para mantener formato visual uniforme en tablas o brackets.
   *
   * @param nombre Nombre original del equipo
   * @return Nombre abreviado (máximo 10 caracteres)
   */
  private String abreviar(String nombre) {
    return nombre.length() <= 20 ? nombre : nombre.substring(0, 20);
  }

  /**
   * Muestra en consola la tabla de posiciones de cada grupo del torneo,
   * con estadísticas detalladas por equipo: puntos, partidos jugados, ganados,
   * empatados, perdidos, goles a favor, en contra, diferencia y ELO.
   */
  public void mostrarTablasGrupos() {
    for (int i = 0; i < grupos.size(); i++) {
      // Título de grupo (ej. Grupo 1, Grupo 2, etc.)
      System.out.println("\n=== Grupo " + (i + 1) + " ===");

      // Cabecera de la tabla
      System.out.printf("%-30s %-5s %-5s %-5s %-5s %-5s %-5s %-5s %-5s %-6s%n",
              "Equipo", "Pts", "PJ", "PG", "PE", "PP", "GF", "GC", "DG", "ELO");
      System.out.println("----------------------------------------------------------------------------------");

      // Recorre cada equipo del grupo y muestra sus estadísticas
      for (Equipo equipo : grupos.get(i)) {
        System.out.printf("%-30s %-5d %-5d %-5d %-5d %-5d %-5d %-5d %-5d %-6d%n",
                equipo.getNombre(),                // Nombre completo del equipo
                getPuntos(equipo),                // Puntos acumulados (metodo heredado o calculado)
                equipo.getPartidosJugados(),      // PJ
                equipo.getPartidosGanados(),      // PG
                equipo.getPartidosEmpatados(),    // PE
                equipo.getPartidosPerdidos(),     // PP
                equipo.getGolesAFavor(),          // GF
                equipo.getGolesEnContra(),        // GC
                equipo.getDiferenciaGoles(),      // DG = GF - GC
                equipo.getElo());                 // Valor ELO
      }
    }
  }

  /**
   * Construye recursivamente un árbol binario de eliminación directa para un torneo.
   * Cada nodo representa un partido y sus hijos representan las fases anteriores.
   * En caso de encontrar un "BYE", el otro equipo avanza automáticamente de ronda.
   *
   * @param equipos Lista de equipos clasificados para la fase de eliminación.
   *                Se asume que su tamaño es potencia de 2 y están ordenados.
   * @return Nodo raíz del árbol de eliminación (final del torneo).
   * @throws TorneoException Si ocurre un error inesperado durante la construcción.
   */
  private NodoPartido construirArbolEliminacion(List<Equipo> equipos) throws TorneoException {
    // Caso base: si queda un solo equipo, se retorna un nodo sin partido (campeón directo)
    if (equipos.size() == 1) return new NodoPartido(null);

    List<NodoPartido> nodos = new ArrayList<>();
    List<Equipo> ganadores = new ArrayList<>();

    // Recorre la lista de equipos de a pares
    for (int i = 0; i < equipos.size(); i += 2) {
      Equipo e1 = equipos.get(i);
      Equipo e2 = equipos.get(i + 1);
      Partido partido;
      NodoPartido nodo;

      if (e1.getNombre().equals("BYE")) {
        // Avanza e2 automáticamente si e1 es un BYE
        nodo = new NodoPartido(null);
        nodo.setGanador(e2);
      } else if (e2.getNombre().equals("BYE")) {
        // Avanza e1 automáticamente si e2 es un BYE
        nodo = new NodoPartido(null);
        nodo.setGanador(e1);
      } else {
        // Simula partido entre e1 y e2
        partido = new Partido(e1, e2);
        nodo = new NodoPartido(partido);
        nodo.setGanador(partido.getGanadorConDesempate());  // resuelve empates si es necesario
      }

      nodos.add(nodo);
      ganadores.add(nodo.getGanador());  // Avanzan a la siguiente ronda
    }

    // Si ya hay un ganador único, se devuelve ese nodo
    if (ganadores.size() == 1) return nodos.get(0);

    // Construye recursivamente la siguiente ronda con los ganadores
    return construirArbolEliminacion(ganadores);
  }

  /**
   * Muestra de forma interactiva todos los partidos jugados del torneo mixto:
   * primero los de la fase de grupos, luego los de la fase eliminatoria.
   *
   * Permite al usuario recorrer jornada por jornada mediante entrada por consola.
   *
   * @param scanner Scanner para entrada del usuario.
   */
  @Override
  public void mostrarPartidosTorneo(Scanner scanner) {
    if (grupos == null || grupos.isEmpty()) {
      System.out.println("No hay grupos cargados.");
      return;
    }

    // =================== FASE DE GRUPOS ===================
    for (int g = 0; g < grupos.size(); g++) {
      List<Equipo> grupo = grupos.get(g);
      List<Partido> partidosGrupo = filtrarPartidosPorGrupo(grupo); // Extrae solo los partidos entre equipos del grupo
      List<List<Partido>> jornadas = generarJornadasGrupo(grupo, partidosGrupo); // Agrupa partidos por jornadas

      if (jornadas.isEmpty()) continue;

      ListIterator<List<Partido>> iterador = jornadas.listIterator();
      int nroJornada = 0;

      System.out.println("\n=== Fase de Grupos - Grupo " + (g + 1) + " ===");

      // Muestra primera jornada
      if (iterador.hasNext()) {
        List<Partido> primera = iterador.next();
        nroJornada++;
        mostrarJornadaFormatoLiga(primera, nroJornada);
      }

      // Menú interactivo por grupo
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
                iterador.next(); // Reposiciona correctamente
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

    // =================== FASE ELIMINATORIA ===================
    System.out.println("\n=== Fase Eliminatoria ===");

    // Filtra los partidos de grupos para luego excluirlos
    Set<Partido> partidosGrupos = new HashSet<>();
    for (List<Equipo> grupo : grupos) {
      partidosGrupos.addAll(filtrarPartidosPorGrupo(grupo));
    }

    // Obtiene partidos de eliminación directa (no están en partidosGrupos)
    List<Partido> partidosEliminacion = getPartidos().stream()
            .filter(p -> !partidosGrupos.contains(p))
            .toList();

    if (partidosEliminacion.isEmpty()) {
      System.out.println("No hay partidos de eliminación directa registrados.");
      return;
    }

    // Agrupa partidos en jornadas visuales (de 4 partidos cada una)
    List<List<Partido>> jornadasEliminacion = new ArrayList<>();
    int cantidadPorJornada = 4;
    for (int i = 0; i < partidosEliminacion.size(); i += cantidadPorJornada) {
      int fin = Math.min(i + cantidadPorJornada, partidosEliminacion.size());
      jornadasEliminacion.add(partidosEliminacion.subList(i, fin));
    }

    ListIterator<List<Partido>> it = jornadasEliminacion.listIterator();
    int jornada = 0;

    // Muestra primera jornada
    if (it.hasNext()) {
      List<Partido> primera = it.next();
      jornada++;
      mostrarJornadaFormatoLiga(primera, jornada);
    }

    // Menú interactivo para fase eliminatoria
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

  /**
   * Genera las jornadas (fechas) visuales de partidos para un grupo de equipos,
   * utilizando el algoritmo de rotación round-robin.
   *
   * Se emparejan los equipos en cada jornada de modo que todos se enfrenten entre sí una vez.
   * Los partidos ya jugados entre los equipos del grupo se asignan a sus jornadas correspondientes.
   *
   * @param grupo Lista de equipos que pertenecen a un mismo grupo.
   * @param partidosGrupo Lista de partidos que se jugaron entre los equipos de este grupo.
   * @return Lista de jornadas, donde cada jornada es una lista de partidos jugados.
   */
  private List<List<Partido>> generarJornadasGrupo(List<Equipo> grupo, List<Partido> partidosGrupo) {
    List<List<Partido>> jornadas = new ArrayList<>();
    int totalEquipos = grupo.size();

    // En un round-robin de ida, hay (n - 1) jornadas si n es par
    int totalJornadas = totalEquipos - 1;

    // Copia de los equipos para poder rotarlos sin afectar el grupo original
    List<Equipo> rotables = new ArrayList<>(grupo);

    for (int j = 0; j < totalJornadas; j++) {
      List<Partido> jornada = new ArrayList<>();

      // Emparejamiento de equipos para esta jornada
      for (int i = 0; i < totalEquipos / 2; i++) {
        Equipo local = rotables.get(i);
        Equipo visitante = rotables.get(totalEquipos - 1 - i);

        // Busca el partido correspondiente entre los dos equipos
        for (Partido p : partidosGrupo) {
          if ((p.getLocal().equals(local) && p.getVisitante().equals(visitante)) ||
                  (p.getLocal().equals(visitante) && p.getVisitante().equals(local))) {
            jornada.add(p);
            break;
          }
        }
      }

      // Añade la jornada generada a la lista principal
      jornadas.add(jornada);

      // Rota los equipos para la siguiente jornada (excepto el primero)
      Collections.rotate(rotables.subList(1, totalEquipos), 1);
    }
    return jornadas;
  }

  /**
   * Filtra los partidos jugados únicamente entre los equipos de un grupo determinado.
   *
   * @param grupo Lista de equipos que componen el grupo.
   * @return Lista de partidos donde ambos equipos pertenecen al grupo.
   */
  private List<Partido> filtrarPartidosPorGrupo(List<Equipo> grupo) {
    return getPartidos().stream()
            .filter(p -> grupo.contains(p.getLocal()) && grupo.contains(p.getVisitante()))
            .toList();
  }

  /**
   * Muestra por consola la lista de equipos que clasificaron a la fase eliminatoria.
   */
  public void mostrarEquiposClasificados() {
    System.out.println("\n=== Equipos Clasificados ===");
    for (Equipo equipo : equiposClasificados) {
      System.out.println("- " + equipo.getNombre());
    }
  }

  /**
   * Simula un torneo mixto completo con fase de grupos y fase eliminatoria.
   *
   * El procedimiento incluye:
   * - Distribuir equipos aleatoriamente entre los grupos.
   * - Simular los partidos de la fase de grupos.
   * - Mostrar las tablas y equipos clasificados.
   * - Simular la fase de eliminación directa con los equipos clasificados.
   *
   * @throws TorneoException si no hay suficientes equipos para continuar.
   */
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

  /**
   * Calcula los puntos totales obtenidos por un equipo,
   * considerando 3 puntos por victoria y 1 punto por empate.
   *
   * @param equipo Equipo del cual se desea obtener la puntuación.
   * @return Total de puntos del equipo.
   */
  private int getPuntos(Equipo equipo) {
    return equipo.getPartidosGanados() * 3 + equipo.getPartidosEmpatados();
  }

  /**
   * Calcula la diferencia de goles de un equipo (goles a favor menos goles en contra).
   *
   * @param equipo Equipo del cual se desea obtener la diferencia de goles.
   * @return Diferencia de goles del equipo.
   */
  private int getDiferenciaGoles(Equipo equipo) {
    return equipo.getGolesAFavor() - equipo.getGolesEnContra();
  }
}
