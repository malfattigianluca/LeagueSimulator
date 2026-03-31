package leaguesimulator.Modelo;

import leaguesimulator.Excepciones.TorneoException;
import leaguesimulator.Util.NavegadorJornadas;
import leaguesimulator.Util.SimuladorPartido;

import java.util.*;

/**
 * Representa un torneo de fútbol en modalidad de eliminación directa o liga simple.
 *
 * En modalidad eliminación directa construye un árbol binario de partidos
 * (NodoPartido) que refleja el bracket completo del torneo.
 *
 * La lógica de simulación de cada partido está delegada a {@link SimuladorPartido}.
 * La navegación interactiva por partidos está delegada a {@link NavegadorJornadas}.
 */
public class Torneo {

    private final String           nombre;
    private final List<Equipo>     equipos;
    private final List<Partido>    partidos;
    private final boolean          eliminacionDirecta;

    // Raíz del árbol de eliminación (null si la modalidad es liga).
    // Protected para que TorneoMixto pueda leerla sin exponer campo público.
    protected NodoPartido raizEliminacion;


    public Torneo(String nombre, boolean eliminacionDirecta) {
        this.nombre             = nombre;
        this.equipos            = new ArrayList<>();
        this.partidos           = new ArrayList<>();
        this.eliminacionDirecta = eliminacionDirecta;
    }


    /**
     * Agrega un equipo al torneo si no fue agregado previamente.
     */
    public void agregarEquipo(Equipo equipo) {
        if (!equipos.contains(equipo)) {
            equipos.add(equipo);
        }
    }


    /**
     * Simula un partido entre local y visitante usando ELO + Poisson.
     * Registra el partido en la lista interna y actualiza el ELO de ambos equipos.
     *
     * @throws TorneoException Si alguno de los equipos no pertenece al torneo.
     */
    public void simularPartido(Equipo local, Equipo visitante) throws TorneoException {
        if (local == null || visitante == null) {
            throw new TorneoException("Uno de los equipos es null");
        }
        if (!equipos.contains(local) || !equipos.contains(visitante)) {
            throw new TorneoException("Los equipos deben pertenecer al torneo");
        }
        Partido partido = SimuladorPartido.simular(local, visitante);
        partidos.add(partido);
    }


    /**
     * Inicia la simulación completa del torneo.
     *
     * - Eliminación directa: construye el árbol bracket por rondas recursivas.
     * - Liga: todos contra todos (round-robin simple).
     *
     * @throws TorneoException Si hay menos de 2 equipos.
     */
    public void simularTorneo() throws TorneoException {
        if (equipos.size() < 2) {
            throw new TorneoException("Se necesitan al menos 2 equipos para simular un torneo");
        }

        if (eliminacionDirecta) {
            List<Equipo> disponibles = new ArrayList<>(equipos);
            Collections.shuffle(disponibles);
            this.raizEliminacion = simularRonda(disponibles);
            imprimirBracket();
        } else {
            for (int i = 0; i < equipos.size(); i++) {
                for (int j = i + 1; j < equipos.size(); j++) {
                    simularPartido(equipos.get(i), equipos.get(j));
                }
            }
        }
    }


    /**
     * Imprime el cuadro completo del torneo en formato bracket ASCII.
     * Muestra al campeón al finalizar.
     */
    public void imprimirBracket() {
        if (raizEliminacion == null) {
            System.out.println("No hay partidos simulados aún.");
            return;
        }
        System.out.println("\n=== Cuadro del Torneo (Formato Bracket) ===\n");
        imprimirNodo(raizEliminacion, 0, true);

        Equipo campeon = raizEliminacion.getGanador();
        System.out.println(campeon != null
                ? "\nCampeon: " + campeon.getNombre()
                : "\nCampeon: Desconocido");
    }


    /**
     * Muestra los partidos jugados del torneo en páginas de 4, navegables con N/P/0.
     *
     * @param scanner Scanner para leer la entrada del usuario.
     */
    public void mostrarPartidosTorneo(Scanner scanner) {
        if (partidos.isEmpty()) {
            System.out.println("No se han jugado partidos aún.");
            return;
        }

        // Agrupa en páginas de 4 partidos
        List<List<Partido>> paginas = new ArrayList<>();
        int tamPagina = 4;
        for (int i = 0; i < partidos.size(); i += tamPagina) {
            paginas.add(partidos.subList(i, Math.min(i + tamPagina, partidos.size())));
        }

        NavegadorJornadas.navegar(paginas, scanner, this::mostrarJornadaFormatoLiga);
    }


    /** Imprime los partidos de una jornada en formato tabla. */
    protected void mostrarJornadaFormatoLiga(List<Partido> jornada, int numero) {
        System.out.printf("\n=== Jornada %d ===\n", numero);
        System.out.printf("%-28s %3s - %-3s %-28s%n", "Equipo Local", "G", "G", "Equipo Visitante");
        System.out.println("---------------------------------------------------------------");
        for (Partido partido : jornada) {
            String local     = partido.getLocal()     != null ? partido.getLocal().getNombre()     : "BYE";
            String visitante = partido.getVisitante() != null ? partido.getVisitante().getNombre() : "BYE";
            System.out.printf("%-28s %3d - %-3d %-28s%n",
                    local, partido.getGolesLocal(), partido.getGolesVisitante(), visitante);
        }
    }


    /**
     * Simula recursivamente las rondas de eliminación directa.
     * Construye el árbol binario de NodoPartido desde la lista inicial de equipos.
     *
     * El árbol se construye en un único recorrido combinando emparejamiento y
     * construcción de nodos, evitando la duplicación entre simularRonda y
     * simularRondaDesdeNodos que existía en la versión anterior.
     *
     * @param equiposRonda Lista de equipos que participan en esta ronda.
     * @return NodoPartido raíz del árbol de eliminación.
     * @throws TorneoException Si ocurre un error al simular algún partido.
     */
    protected NodoPartido simularRonda(List<Equipo> equiposRonda) throws TorneoException {
        if (equiposRonda.size() == 1) {
            NodoPartido nodo = new NodoPartido(null);
            nodo.setGanador(equiposRonda.get(0));
            return nodo;
        }

        List<NodoPartido> nodosRonda = new ArrayList<>();
        for (int i = 0; i < equiposRonda.size(); i += 2) {
            Equipo e1 = equiposRonda.get(i);
            Equipo e2 = (i + 1 < equiposRonda.size()) ? equiposRonda.get(i + 1) : null;

            if (e2 == null) {
                // Número impar: e1 pasa sin jugar
                NodoPartido nodo = new NodoPartido(null);
                nodo.setGanador(e1);
                nodosRonda.add(nodo);
            } else {
                simularPartido(e1, e2);
                Partido p = partidos.get(partidos.size() - 1);
                Equipo ganador = p.getGanador() != null ? p.getGanador()
                        : (e1.getElo() >= e2.getElo() ? e1 : e2);
                NodoPartido nodo = new NodoPartido(p);
                nodo.setGanador(ganador);
                nodosRonda.add(nodo);
            }
        }

        if (nodosRonda.size() == 1) {
            return nodosRonda.get(0);
        }

        // Construye el siguiente nivel emparejando ganadores de nodos
        return construirNivelSuperior(nodosRonda);
    }


    /**
     * Empareja los nodos de una ronda para construir la siguiente.
     * Se llama recursivamente hasta que queda un único nodo raíz.
     */
    private NodoPartido construirNivelSuperior(List<NodoPartido> nodos) throws TorneoException {
        if (nodos.size() == 1) return nodos.get(0);

        List<NodoPartido> siguienteNivel = new ArrayList<>();
        for (int i = 0; i < nodos.size(); i += 2) {
            NodoPartido izquierdo = nodos.get(i);
            NodoPartido derecho   = (i + 1 < nodos.size()) ? nodos.get(i + 1) : null;

            if (derecho == null) {
                siguienteNivel.add(izquierdo);
                continue;
            }

            Equipo local     = izquierdo.getGanador();
            Equipo visitante = derecho.getGanador();

            if (local == null || visitante == null) {
                NodoPartido pasoLibre = new NodoPartido(null);
                pasoLibre.setGanador(local != null ? local : visitante);
                pasoLibre.setIzquierdo(izquierdo);
                pasoLibre.setDerecho(derecho);
                siguienteNivel.add(pasoLibre);
                continue;
            }

            simularPartido(local, visitante);
            Partido partido = partidos.get(partidos.size() - 1);
            Equipo ganador = partido.getGanador() != null ? partido.getGanador()
                    : (local.getElo() >= visitante.getElo() ? local : visitante);

            NodoPartido padre = new NodoPartido(partido);
            padre.setGanador(ganador);
            padre.setIzquierdo(izquierdo);
            padre.setDerecho(derecho);
            siguienteNivel.add(padre);
        }

        return construirNivelSuperior(siguienteNivel);
    }


    /**
     * Imprime un nodo del árbol bracket de forma recursiva con indentación jerárquica.
     * El hijo derecho se imprime arriba (visualmente) y el izquierdo abajo.
     *
     * @param nodo      Nodo actual.
     * @param nivel     Profundidad (0 = Final).
     * @param izquierdo Indica si es rama izquierda (afecta el símbolo visual).
     */
    protected void imprimirNodo(NodoPartido nodo, int nivel, boolean izquierdo) {
        if (nodo == null) return;

        imprimirNodo(nodo.getDerecho(), nivel + 1, false);

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

        Partido p = nodo.getPartido();
        String indent    = "        ".repeat(nivel);
        String flecha    = izquierdo ? "└── " : "┌── ";
        String localStr  = p != null && p.getLocal()     != null ? String.format("%-22s", abreviar(p.getLocal().getNombre()))     : "BYE                   ";
        String visitStr  = p != null && p.getVisitante() != null ? String.format("%-22s", abreviar(p.getVisitante().getNombre())) : "BYE                   ";
        String ganadorStr= p != null && p.getGanador()   != null ? "-> " + abreviar(p.getGanador().getNombre()) : "";

        System.out.println(indent + flecha + "[" + fase + "] " + localStr + " vs " + visitStr + " " + ganadorStr);

        imprimirNodo(nodo.getIzquierdo(), nivel + 1, true);
        if (nivel <= 2) System.out.println();
    }


    /**
     * Abrevia un nombre a un máximo de 22 caracteres.
     * Centralizado aquí para que TorneoMixto también lo use.
     */
    protected String abreviar(String nombre) {
        return nombre.length() > 22 ? nombre.substring(0, 20) + "…" : nombre;
    }


    public String          getNombre()  { return nombre; }
    public List<Equipo>    getEquipos() { return equipos; }
    public List<Partido>   getPartidos(){ return new ArrayList<>(partidos); }
}
