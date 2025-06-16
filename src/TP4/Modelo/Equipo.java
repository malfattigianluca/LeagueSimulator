package TP4.Modelo;

import TP4.Excepciones.TorneoException;
import TP4.Util.Validador;
import TP4.TDA.ConjuntoGenericoTDA;
import TP4.Implementacion.ConjuntoGenericoImpl;

import java.util.IllegalFormatConversionException;
import java.util.List;

public class Equipo {
  private int id;
  private String nombre;
  private String liga;
  private String escudo;
  private String pais;
  private int elo;
  private int golesAFavor;
  private int golesEnContra;
  private int partidosJugados;
  private int partidosGanados;
  private int partidosEmpatados;
  private int partidosPerdidos;
  private ConjuntoGenericoTDA<Jugador> jugadores;
  private static final int MAX_JUGADORES = 40;

  public Equipo(int id, String nombreEquipo, int elo, String liga, String escudo, String pais) throws TorneoException {
    this.id = id;
    this.nombre = nombreEquipo;
    Validador.validarLiga(liga);
    this.elo = elo;
    this.liga = liga;
    this.escudo = escudo;
    this.pais = pais;
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

  public void mostrarJugadores() {
    System.out.println("\nEquipo: " + getNombre() + " (" + getLiga() + ")");
    if (jugadores.estaVacio()) {
      System.out.println("No hay jugadores registrados para este equipo.");
      return;
    }

    // Imprimir cabecera
    System.out.printf("%-30s %-25s %-10s %-10s %-10s%n",
        "Nombre", "Posición", "Camiseta", "Edad", "Altura");
    System.out.println("------------------------------------------------------------------------------------");

    // Obtener la lista de jugadores
    List<Jugador> listaJugadores = jugadores.getVertices();
    if (listaJugadores == null) {
      System.out.println("Error: La lista de jugadores es nula.");
      return;
    }

    // Iterar sobre los jugadores
    for (Jugador jugador : listaJugadores) {
      try {
        // Manejar valores null
        String nombreJugador = (jugador.getNombre() != null) ? jugador.getNombre() : "N/A";
        String posicionJugador = (jugador.getPosicion() != null) ? jugador.getPosicion() : "N/A";
        int camisetaJugador = jugador.getNumeroCamiseta() != -1 ? jugador.getNumeroCamiseta() : 0;
        int edadJugador = jugador.getEdad() != -1 ? jugador.getEdad() : 0;
        String alturaJugador = (jugador.getAltura() != null) ? jugador.getAltura() : "N/A";

        System.out.printf("%-30s %-25s %-10d %-10d %-10s%n", nombreJugador, posicionJugador, camisetaJugador,
            edadJugador, alturaJugador);
      } catch (NullPointerException e) {
        System.out.println("Error al procesar jugador: "
            + (jugador != null && jugador.getNombre() != null ? jugador.getNombre() : "Jugador nulo"));
        e.printStackTrace();
      } catch (IllegalFormatConversionException e) {
        System.out.println("Error de formato para jugador: "
            + (jugador != null && jugador.getNombre() != null ? jugador.getNombre() : "Jugador nulo"));
        e.printStackTrace();
      }
    }
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
    if (numeroCamiseta == -1) {
      return false;
    }
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
    } else if (golesAFavor == golesEnContra) {
      this.partidosEmpatados++;
    } else {
      this.partidosPerdidos++;
    }
  }


  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setPuntos(int puntos) {
    if (puntos < 0) {
      throw new IllegalArgumentException("Los puntos no pueden ser negativos");
    }
    this.partidosGanados = puntos / 3;
    this.partidosEmpatados = puntos % 3;
  }

  public String getNombre() {
    return nombre;
  }

  public String getEscudo() {
    return escudo;
  }

  public String getLiga() {
    return liga;
  }

  public String getPais() {
    return pais;
  }

  public int getElo() {
    return elo;
  }

  public int getPuntos() {
    return calcularPuntos();
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

  public ConjuntoGenericoTDA<Jugador> getJugadores() {
    return jugadores;
  }

  public int calcularPuntos() {
    return (this.partidosGanados * 3) + this.partidosEmpatados;
  }


  public void setElo(int nuevoElo) {
    if (nuevoElo < 1000) {
      this.elo = 1000;
    } else if (nuevoElo > 3000) {
      this.elo = 3000;
    } else {
      this.elo = nuevoElo;
    }
  }

  @Override
  public String toString() {
    return String.format("Escudo: %s, Nombre: %s, Elo: %d, Liga: %s",
        escudo, nombre, elo, liga);
  }
}