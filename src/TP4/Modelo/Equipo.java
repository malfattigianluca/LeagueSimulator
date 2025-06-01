package TP4.Modelo;

import TP4.Util.Constantes;
import TP4.Excepciones.TorneoException;
import TP4.Util.Validador;
import TP4.TDA.ConjuntoGenericoTDA;
import TP4.Implementacion.ConjuntoGenericoImpl;

public class Equipo {
  private int id;
  private String nombre;
  private String liga;
  private int puntos;
  private int golesAFavor;
  private int golesEnContra;
  private int partidosJugados;
  private int partidosGanados;
  private int partidosEmpatados;
  private int partidosPerdidos;
  private ConjuntoGenericoTDA<Jugador> jugadores;
  private static final int MAX_JUGADORES = 25; 

  public Equipo(int id, String nombreEquipo, String liga) throws TorneoException {
    this.id = id;
    this.nombre = nombreEquipo;
    Validador.validarLiga(liga);
    this.liga = liga;
    this.puntos = 0;
    this.golesAFavor = 0;
    this.golesEnContra = 0;
    this.partidosJugados = 0;
    this.partidosGanados = 0;
    this.partidosEmpatados = 0;
    this.partidosPerdidos = 0;
    this.jugadores = new ConjuntoGenericoImpl<>();
  }

  public void agregarJugador(Jugador jugador) throws TorneoException {
    if (cantidadJugadores() >= MAX_JUGADORES) {
      throw new TorneoException("El equipo ya tiene el máximo de jugadores permitidos");
    }

    if (jugador == null) {
      throw new TorneoException("El jugador no puede ser nulo");
    }

    if (jugador.getEquipo() != null) {
      throw new TorneoException("El jugador ya pertenece a un equipo");
    }

    if (tieneNumeroCamiseta(jugador.getNumeroCamiseta())) {
      throw new TorneoException("El número de camiseta " + jugador.getNumeroCamiseta() + " ya está en uso");
    }

    jugadores.insertar(jugador);
    jugador.unirseAEquipo(this);
  }

  public void eliminarJugador(Jugador jugador) {
    if (jugador != null && jugadores.pertenece(jugador)) {
      jugadores.eliminar(jugador);
      try {
        jugador.salirDelEquipo();
      } catch (TorneoException e) {
      }
    }
  }

  public boolean tieneJugador(Jugador jugador) {
    return jugadores.pertenece(jugador);
  }

  public boolean tieneNumeroCamiseta(int numeroCamiseta) {
    for (Jugador jugador : jugadores.getVertices()) {
      if (jugador.getNumeroCamiseta() == numeroCamiseta) {
        return true;
      }
    }
    return false;
  }

  public int cantidadJugadores() {
    return jugadores.tamanio();
  }

  public void registrarPartido(int golesAFavor, int golesEnContra) throws TorneoException {
    Validador.validarGoles(golesAFavor);
    Validador.validarGoles(golesEnContra);

    this.golesAFavor += golesAFavor;
    this.golesEnContra += golesEnContra;
    this.partidosJugados++;

    if (golesAFavor > golesEnContra) {
      this.partidosGanados++;
      this.puntos += 3;
    } else if (golesAFavor == golesEnContra) {
      this.partidosEmpatados++;
      this.puntos += 1;
    } else {
      this.partidosPerdidos++;
    }
  }

  public int getId() {
    return id;
  }

  public String getNombre() {
    return nombre;
  }

  public String getLiga() {
    return liga;
  }

  public int getPuntos() {
    return puntos;
  }

  public int getGolesAFavor() {
    return golesAFavor;
  }

  public int getGolesEnContra() {
    return golesEnContra;
  }

  public int getPartidosJugados() {
    return partidosJugados;
  }

  public int getPartidosGanados() {
    return partidosGanados;
  }

  public int getPartidosEmpatados() {
    return partidosEmpatados;
  }

  public int getPartidosPerdidos() {
    return partidosPerdidos;
  }

  public int getDiferenciaGoles() {
    return golesAFavor - golesEnContra;
  }

  @Override
  public String toString() {
    return nombre + " (" + liga + ") - Jugadores: " + cantidadJugadores();
  }
}