package TP4.Modelo;

import TP4.Excepciones.TorneoException;
import java.util.*;

public class NodoPartido {
    private Partido partido;
    private NodoPartido izquierdo;
    private NodoPartido derecho;
    private Equipo ganador;

    public NodoPartido(Partido partido) {
        this.partido = partido;
    }

    public Partido getPartido() {
        return partido;
    }

    public void setPartido(Partido partido) {
        this.partido = partido;
    }

    public NodoPartido getIzquierdo() {
        return izquierdo;
    }

    public void setIzquierdo(NodoPartido izquierdo) {
        this.izquierdo = izquierdo;
    }

    public NodoPartido getDerecho() {
        return derecho;
    }

    public void setDerecho(NodoPartido derecho) {
        this.derecho = derecho;
    }

    public Equipo getGanador() {
        return ganador;
    }

    public void setGanador(Equipo ganador) {
        this.ganador = ganador;
    }
}
