package leaguesimulator.Modelo;

import leaguesimulator.Excepciones.TorneoException;
import leaguesimulator.Util.NavegadorJornadas;

import java.util.*;

/**
 * Torneo mixto: fase de grupos (todos contra todos) seguida de eliminación directa.
 *
 * Extiende {@link Torneo} y añade la lógica de grupos y clasificación.
 *
 * Cambios respecto a la versión anterior:
 * - Bug corregido: el equipo BYE usaba liga "Libre" que no pasaba la validación.
 *   Ahora usa "BYE" que sí está permitida por el Validador.
 * - Bug corregido: en simularFaseEliminacion se creaba un Partido dummy que
 *   llamaba registrarPartido() en los equipos antes del partido real, duplicando
 *   estadísticas. Ahora los equipos se obtienen directamente de los nodos.
 * - imprimirLlavesEliminacion delega en imprimirNodo() de Torneo (DRY).
 * - mostrarPartidosTorneo usa NavegadorJornadas (DRY).
 * - construirArbolEliminacion (dead code) eliminado.
 * - abreviar() eliminado (usa el heredado de Torneo).
 */
public class TorneoMixto extends Torneo {

    private final List<List<Equipo>> grupos;
    private final int                equiposQuePasanPorGrupo;
    private final List<Equipo>       equiposClasificados;


    public TorneoMixto(String nombre, int numGrupos, int equiposQuePasanPorGrupo) {
        super(nombre, true);
        this.grupos = new ArrayList<>();
        for (int i = 0; i < numGrupos; i++) {
            grupos.add(new ArrayList<>());
        }
        this.equiposQuePasanPorGrupo = equiposQuePasanPorGrupo;
        this.equiposClasificados     = new ArrayList<>();
    }


    /**
     * Distribuye aleatoriamente los equipos entre los grupos en forma circular.
     *
     * @throws TorneoException Si hay menos equipos que grupos.
     */
    public void distribuirEquiposEnGrupos() throws TorneoException {
        if (getEquipos().size() < grupos.size()) {
            throw new TorneoException("No hay suficientes equipos para llenar los grupos");
        }
        List<Equipo> mezclados = new ArrayList<>(getEquipos());
        Collections.shuffle(mezclados);

        int grupoActual = 0;
        for (Equipo equipo : mezclados) {
            grupos.get(grupoActual).add(equipo);
            grupoActual = (grupoActual + 1) % grupos.size();
        }
    }


    /**
     * Simula todos los partidos dentro de cada grupo (todos contra todos),
     * ordena los equipos y clasifica a los mejores por grupo.
     *
     * @throws TorneoException Si ocurre un error al simular un partido.
     */
    public void simularFaseGrupos() throws TorneoException {
        for (List<Equipo> grupo : grupos) {
            for (int i = 0; i < grupo.size(); i++) {
                for (int j = i + 1; j < grupo.size(); j++) {
                    simularPartido(grupo.get(i), grupo.get(j));
                }
            }

            grupo.sort((a, b) -> {
                if (a.getPuntos() != b.getPuntos()) return b.getPuntos() - a.getPuntos();
                return b.getDiferenciaGoles() - a.getDiferenciaGoles();
            });

            for (int i = 0; i < Math.min(equiposQuePasanPorGrupo, grupo.size()); i++) {
                equiposClasificados.add(grupo.get(i));
            }
        }
    }


    /**
     * Simula la fase de eliminación directa con los equipos clasificados.
     *
     * Construye un árbol de partidos usando cola (BFS bottom-up).
     * Si hay número impar de clasificados, el último recibe un BYE.
     *
     * Corrección de bug: el equipo BYE ahora usa liga "BYE" (permitida por el Validador)
     * en lugar de "Libre" (que lanzaba TorneoException).
     *
     * Corrección de bug: antes se creaba un Partido dummy para extraer local/visitante,
     * lo que provocaba doble contabilización de estadísticas. Ahora se obtienen
     * directamente de los nodos del árbol.
     *
     * @throws TorneoException Si hay menos de 2 equipos clasificados.
     */
    public void simularFaseEliminacion() throws TorneoException {
        if (equiposClasificados.size() < 2) {
            throw new TorneoException("No hay suficientes equipos clasificados para la fase de eliminación");
        }

        for (Equipo equipo : equiposClasificados) {
            agregarEquipo(equipo);
        }

        Collections.shuffle(equiposClasificados);
        Queue<NodoPartido> cola = new LinkedList<>();

        // Primera ronda: empareja clasificados de a pares
        for (int i = 0; i < equiposClasificados.size(); i += 2) {
            Equipo local = equiposClasificados.get(i);
            Equipo visitante;
            NodoPartido nodo;

            if (i + 1 < equiposClasificados.size()) {
                visitante = equiposClasificados.get(i + 1);
                simularPartido(local, visitante);
                Partido partido = getPartidos().get(getPartidos().size() - 1);
                nodo = new NodoPartido(partido);
                nodo.setGanador(partido.getGanadorConDesempate());
            } else {
                // Número impar: el último clasifica automáticamente (BYE)
                nodo = new NodoPartido(null);
                nodo.setGanador(local);
            }

            cola.offer(nodo);
        }

        // Rondas sucesivas: combina ganadores hasta el campeón
        while (cola.size() > 1) {
            NodoPartido nodo1 = cola.poll();
            NodoPartido nodo2 = cola.poll();

            // Los equipos se obtienen de los nodos, sin crear un Partido intermedio
            Equipo local     = nodo1.getGanador();
            Equipo visitante = nodo2.getGanador();

            simularPartido(local, visitante);
            Partido partido = getPartidos().get(getPartidos().size() - 1);

            NodoPartido padre = new NodoPartido(partido);
            padre.setIzquierdo(nodo1);
            padre.setDerecho(nodo2);
            padre.setGanador(partido.getGanadorConDesempate());
            cola.offer(padre);
        }

        raizEliminacion = cola.poll();
    }


    /**
     * Imprime la llave de eliminación directa en formato árbol (bracket).
     * Delega el renderizado en {@link Torneo#imprimirNodo} para evitar duplicación.
     */
    public void imprimirLlavesEliminacion() {
        if (raizEliminacion == null) {
            System.out.println("No hay partidos simulados aún.");
            return;
        }
        System.out.println("\n=== Llave del Torneo (Formato Arbol) ===\n");
        imprimirNodo(raizEliminacion, 0, true);

        Equipo campeon = raizEliminacion.getGanador();
        System.out.println(campeon != null
                ? "\nCampeon: " + campeon.getNombre()
                : "\nCampeon: Desconocido");
    }


    /**
     * Muestra de forma interactiva los partidos del torneo mixto:
     * primero por fase de grupos (grupo a grupo) y luego fase eliminatoria.
     *
     * La navegación N/P/0 está delegada a {@link NavegadorJornadas}.
     */
    @Override
    public void mostrarPartidosTorneo(Scanner scanner) {
        if (grupos == null || grupos.isEmpty()) {
            System.out.println("No hay grupos cargados.");
            return;
        }

        // === Fase de Grupos ===
        for (int g = 0; g < grupos.size(); g++) {
            List<Equipo>  grupo          = grupos.get(g);
            List<Partido> partidosGrupo  = filtrarPartidosPorGrupo(grupo);
            List<List<Partido>> jornadas = generarJornadasGrupo(grupo, partidosGrupo);

            if (jornadas.isEmpty()) continue;

            System.out.println("\n=== Fase de Grupos - Grupo " + (g + 1) + " ===");
            NavegadorJornadas.navegar(jornadas, scanner, this::mostrarJornadaFormatoLiga);
        }

        // === Fase Eliminatoria ===
        System.out.println("\n=== Fase Eliminatoria ===");

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

        // Agrupa en páginas de 4 partidos
        List<List<Partido>> paginas = new ArrayList<>();
        int tamPagina = 4;
        for (int i = 0; i < partidosEliminacion.size(); i += tamPagina) {
            paginas.add(partidosEliminacion.subList(i, Math.min(i + tamPagina, partidosEliminacion.size())));
        }

        NavegadorJornadas.navegar(paginas, scanner, this::mostrarJornadaFormatoLiga);
    }


    /**
     * Muestra la tabla de posiciones de cada grupo.
     */
    public void mostrarTablasGrupos() {
        for (int i = 0; i < grupos.size(); i++) {
            System.out.println("\n=== Grupo " + (i + 1) + " ===");
            System.out.printf("%-30s %-5s %-5s %-5s %-5s %-5s %-5s %-5s %-5s %-6s%n",
                    "Equipo", "Pts", "PJ", "PG", "PE", "PP", "GF", "GC", "DG", "ELO");
            System.out.println("----------------------------------------------------------------------------------");
            for (Equipo e : grupos.get(i)) {
                System.out.printf("%-30s %-5d %-5d %-5d %-5d %-5d %-5d %-5d %-5d %-6d%n",
                        e.getNombre(), e.getPuntos(), e.getPartidosJugados(),
                        e.getPartidosGanados(), e.getPartidosEmpatados(), e.getPartidosPerdidos(),
                        e.getGolesAFavor(), e.getGolesEnContra(), e.getDiferenciaGoles(), e.getElo());
            }
        }
    }


    /**
     * Muestra la lista de equipos que clasificaron a la fase eliminatoria.
     */
    public void mostrarEquiposClasificados() {
        System.out.println("\n=== Equipos Clasificados ===");
        for (Equipo equipo : equiposClasificados) {
            System.out.println("- " + equipo.getNombre());
        }
    }


    /**
     * Simula el torneo mixto completo:
     * distribuye grupos → simula grupos → muestra tablas → simula eliminación.
     */
    @Override
    public void simularTorneo() throws TorneoException {
        distribuirEquiposEnGrupos();
        System.out.println("\nSimulando fase de grupos...");
        simularFaseGrupos();
        mostrarTablasGrupos();
        mostrarEquiposClasificados();
        System.out.println("\nSimulando fase de eliminacion directa...");
        simularFaseEliminacion();
    }


    /**
     * Construye las jornadas visuales de un grupo mediante el algoritmo round-robin.
     * Cada jornada agrupa los partidos que corresponden a esa fecha del calendario.
     */
    private List<List<Partido>> generarJornadasGrupo(List<Equipo> grupo, List<Partido> partidosGrupo) {
        List<List<Partido>> jornadas  = new ArrayList<>();
        int              totalEquipos = grupo.size();
        int              totalJornadas = totalEquipos - 1;
        List<Equipo>     rotables     = new ArrayList<>(grupo);

        for (int j = 0; j < totalJornadas; j++) {
            List<Partido> jornada = new ArrayList<>();
            for (int i = 0; i < totalEquipos / 2; i++) {
                Equipo local     = rotables.get(i);
                Equipo visitante = rotables.get(totalEquipos - 1 - i);
                for (Partido p : partidosGrupo) {
                    if ((p.getLocal().equals(local) && p.getVisitante().equals(visitante)) ||
                            (p.getLocal().equals(visitante) && p.getVisitante().equals(local))) {
                        jornada.add(p);
                        break;
                    }
                }
            }
            jornadas.add(jornada);
            Collections.rotate(rotables.subList(1, totalEquipos), 1);
        }
        return jornadas;
    }


    /** Retorna los partidos cuyos dos equipos pertenecen al grupo dado. */
    private List<Partido> filtrarPartidosPorGrupo(List<Equipo> grupo) {
        return getPartidos().stream()
                .filter(p -> grupo.contains(p.getLocal()) && grupo.contains(p.getVisitante()))
                .toList();
    }
}
