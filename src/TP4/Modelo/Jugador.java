package TP4.Modelo;

import TP4.Excepciones.TorneoException;

public class Jugador {
  private int id;
  private String nombre;
  private int numeroCamiseta;
  private String posicion;
  private Equipo equipo;
  private int goles;
  private int asistencias;
  private int partidosJugados;

  public Jugador(int id, String nombre, int numeroCamiseta, String posicion) {
    this.id = id;
    this.nombre = nombre;
    this.numeroCamiseta = numeroCamiseta;
    this.posicion = posicion;
    this.equipo = null;
    this.goles = 0;
    this.asistencias = 0;
    this.partidosJugados = 0;
  }

  public void unirseAEquipo(Equipo nuevoEquipo) throws TorneoException {
    if (nuevoEquipo == null) {
      throw new TorneoException("El equipo no puede ser nulo");
    }

    if (this.equipo != null) {
      throw new TorneoException("El jugador ya pertenece a un equipo");
    }

    if (nuevoEquipo.tieneNumeroCamiseta(this.numeroCamiseta)) {
      throw new TorneoException("El número de camiseta " + this.numeroCamiseta + " ya está en uso en el equipo");
    }

    this.equipo = nuevoEquipo;
    nuevoEquipo.agregarJugador(this);
  }

  public void salirDelEquipo() throws TorneoException {
    if (this.equipo == null) {
      throw new TorneoException("El jugador no pertenece a ningún equipo");
    }

    this.equipo.eliminarJugador(this);
    this.equipo = null;
  }

  public void registrarGol() {
    this.goles++;
  }

  public void registrarAsistencia() {
    this.asistencias++;
  }

  public void registrarPartido() {
    this.partidosJugados++;
  }

  public int getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public int getNumeroCamiseta() {
    return numeroCamiseta;
  }

  public String getPosicion() {
    return posicion;
  }

  public Equipo getEquipo() {
    return equipo;
  }

  public int getGoles() {
    return goles;
  }

  public int getAsistencias() {
    return asistencias;
  }

  public int getPartidosJugados() {
    return partidosJugados;
  }

  @Override
  public String toString() {
    String equipoStr = (equipo != null) ? " - " + equipo.getNombre() : " (Sin equipo)";
    return nombre + " #" + numeroCamiseta + " (" + posicion + ")" + equipoStr;
  }
}