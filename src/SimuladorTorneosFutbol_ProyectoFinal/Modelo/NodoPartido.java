package SimuladorTorneosFutbol_ProyectoFinal.Modelo;

/**
 * Clase NodoPartido.
 * Representa un nodo dentro del árbol binario de un torneo de eliminación directa.
 * Cada nodo puede contener un partido entre dos equipos y referencias a sus nodos hijos (partidos previos).
 *
 * Es utilizado para construir el árbol del torneo, donde cada nodo representa una instancia de enfrentamiento
 * y se conecta recursivamente con los partidos anteriores que llevaron a ese encuentro.
 */
public class NodoPartido {
    private Partido partido;             // Partido correspondiente a este nodo (puede ser null si es un nodo vacío)
    private NodoPartido izquierdo;       // Subárbol izquierdo (representa el partido previo del equipo 1)
    private NodoPartido derecho;         // Subárbol derecho (representa el partido previo del equipo 2)
    private Equipo ganador;              // Equipo que resultó ganador de este partido (puede ser asignado posteriormente)

    /**
     * Constructor que inicializa el nodo con un partido.
     *
     * @param partido Partido que se juega en este nodo del árbol.
     */
    public NodoPartido(Partido partido) {
        this.partido = partido;
    }

    /**
     * Devuelve el partido asociado a este nodo.
     *
     * @return Partido de este nodo.
     */
    public Partido getPartido() {
        return partido;
    }

    /**
     * Establece el partido para este nodo.
     *
     * @param partido Partido a asignar.
     */
    public void setPartido(Partido partido) {
        this.partido = partido;
    }

    /**
     * Devuelve el nodo hijo izquierdo.
     *
     * @return Nodo izquierdo del árbol.
     */
    public NodoPartido getIzquierdo() {
        return izquierdo;
    }

    /**
     * Establece el nodo hijo izquierdo.
     *
     * @param izquierdo Nodo que representa el partido previo del equipo de la izquierda.
     */
    public void setIzquierdo(NodoPartido izquierdo) {
        this.izquierdo = izquierdo;
    }

    /**
     * Devuelve el nodo hijo derecho.
     *
     * @return Nodo derecho del árbol.
     */
    public NodoPartido getDerecho() {
        return derecho;
    }

    /**
     * Establece el nodo hijo derecho.
     *
     * @param derecho Nodo que representa el partido previo del equipo de la derecha.
     */
    public void setDerecho(NodoPartido derecho) {
        this.derecho = derecho;
    }

    /**
     * Devuelve el equipo ganador del partido.
     *
     * @return Equipo que ganó este partido.
     */
    public Equipo getGanador() {
        return ganador;
    }

    /**
     * Establece el equipo ganador del partido.
     *
     * @param ganador Equipo que se consagró ganador en este nodo.
     */
    public void setGanador(Equipo ganador) {
        this.ganador = ganador;
    }
}
