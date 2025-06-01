package TP4.Modelo;

import TP4.TDA.ConjuntoGenericoTDA;
import TP4.Implementacion.ConjuntoGenericoImpl;
import TP4.Excepciones.TorneoException;

public class GestorJugadores {
  private static GestorJugadores instancia;
  private ConjuntoGenericoTDA<Jugador> jugadores;
  private int ultimoId;

  public GestorJugadores() {
    this.jugadores = new ConjuntoGenericoImpl<>();
    this.ultimoId = 0;
  }

  public static GestorJugadores getInstancia() {
    if (instancia == null) {
      instancia = new GestorJugadores();
    }
    return instancia;
  }

  public Jugador crearJugador(String nombre, int numeroCamiseta, String posicion) throws TorneoException {
    // Validar que el nombre no esté vacío
    if (nombre == null || nombre.trim().isEmpty()) {
      throw new TorneoException("El nombre del jugador no puede estar vacío");
    }

    // Validar que el número de camiseta sea válido
    if (numeroCamiseta <= 1 || numeroCamiseta >= 99) {
      throw new TorneoException("El número de camiseta debe estar entre 1 y 99");
    }

    // Validar que la posición no esté vacía
    if (posicion == null || posicion.trim().isEmpty()) {
      throw new TorneoException("La posición del jugador no puede estar vacía");
    }

    Jugador nuevoJugador = new Jugador(++ultimoId, nombre, numeroCamiseta, posicion);
    jugadores.insertar(nuevoJugador);
    return nuevoJugador;
  }

  public void eliminarJugador(Jugador jugador) {
    if (jugador != null && jugador.getEquipo() == null) {
      jugadores.eliminar(jugador);
    }
  }

  public void mostrarJugadores() {
    for (Jugador j : jugadores.getVertices()) {
      System.out.println("ID: " + j.getId() + " | Nombre: " + j.getNombre() +
              " | Nº Camiseta: " + j.getNumeroCamiseta() +
              " | Posición: " + j.getPosicion() +
              (j.getEquipo() != null ? " | Equipo: " + j.getEquipo().getNombre() : ""));
    }
  }

  public Jugador buscarJugadorPorNombre(String nombre) {
    for (Jugador j : jugadores.getVertices()) {
      if (j.getNombre().equalsIgnoreCase(nombre)) {
        return j;
      }
    }
    return null;
  }

  public boolean existeJugador(Jugador jugador) {
    return jugadores.pertenece(jugador);
  }

  public int cantidadJugadores() {
    return jugadores.tamanio();
  }

  public ConjuntoGenericoTDA<Jugador> getJugadores() {
    return jugadores;
  }
}