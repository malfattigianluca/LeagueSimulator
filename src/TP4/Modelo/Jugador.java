package TP4.Modelo;

import TP4.Excepciones.TorneoException;

public class Jugador {
  private int id;
  private String nombre;
  private int numeroCamiseta;
  private String posicion;
  private Equipo equipo;
  private int edad;
  private String altura;
  private int goles;
  private int asistencias;
  private int amarillas;
  private int rojas;
  private String liga;

  public Jugador(int id, String nombre, int numeroCamiseta, String posicion, String liga, String altura, int edad,
                 int goles, int asistencias, int amarillas, int rojas) throws TorneoException {
    if ((numeroCamiseta!=-1) && (numeroCamiseta < 1 || numeroCamiseta > 99)) {
      throw new TorneoException("El número de camiseta debe estar entre 1 y 99");
    }
    if (nombre == null || nombre.trim().isEmpty()) {
      throw new TorneoException("El nombre del jugador no puede estar vacío");
    }
    if (posicion == null || posicion.trim().isEmpty()) {
      throw new TorneoException("La posición del jugador no puede estar vacía");
    }
    if (altura != null && !altura.equalsIgnoreCase("N/A") && !altura.matches("\\d+\\.\\d{2}")) {
      throw new TorneoException("La altura debe ser 'N/A' o un valor numérico (ej. 1.85)");
    }
    this.id = id;
    this.nombre = nombre;
    this.numeroCamiseta = numeroCamiseta;
    this.posicion = posicion;
    this.liga = liga;
    this.altura = altura != null ? altura : "N/A";
    this.edad = edad;
    this.equipo = null;
    this.goles = goles;
    this.asistencias = asistencias;
    this.amarillas = amarillas;
    this.rojas = rojas;
  }

  public void unirseAEquipo(Equipo nuevoEquipo) throws TorneoException {
    if (nuevoEquipo == null) {
      throw new TorneoException("El equipo no puede ser nulo");
    }
    if (this.equipo != null) {
      throw new TorneoException("El jugador ya pertenece a un equipo");
    }
    this.equipo = nuevoEquipo;
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

  public void registrarAmarilla() {
    this.amarillas++;
  }

  public void registrarRoja(){ this.rojas++;}

  public int getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public int getNumeroCamiseta() {
    return numeroCamiseta;
  }

  public String getLiga(){
    return liga;
  }

  public int getEdad(){
    return edad;
  }

  public String getAltura(){
    return altura;
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

  public int getAmarillas() {
    return amarillas;
  }

  public int getRojas(){
    return rojas;
  }

  public void setNumeroCamiseta(int numeroCamiseta) throws TorneoException {
    if ((numeroCamiseta!=-1) && numeroCamiseta < 1 || numeroCamiseta > 99) {
      throw new TorneoException("El número de camiseta debe estar entre 1 y 99");
    }
    this.numeroCamiseta = numeroCamiseta;
  }

  @Override
  public String toString() {
    String equipoStr = (equipo != null) ? " - " + equipo.getNombre() : " (Sin equipo)";
    return nombre + " #" + numeroCamiseta + " (" + posicion + ")" + equipoStr;
  }
}