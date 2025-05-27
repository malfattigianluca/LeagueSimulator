package TP4.Modelo;

import TP4.Excepciones.TorneoException;
import TP4.Util.Validador;

public class Partido {
  private Equipo equipoLocal;
  private Equipo equipoVisitante;
  private int golesLocal;
  private int golesVisitante;
  private boolean jugado;

  public Partido(Equipo equipoLocal, Equipo equipoVisitante) {
    this.equipoLocal = equipoLocal;
    this.equipoVisitante = equipoVisitante;
    this.golesLocal = 0;
    this.golesVisitante = 0;
    this.jugado = false;
  }

  public void registrarResultado(int golesLocal, int golesVisitante) throws TorneoException {
    Validador.validarGoles(golesLocal);
    Validador.validarGoles(golesVisitante);

    this.golesLocal = golesLocal;
    this.golesVisitante = golesVisitante;
    this.jugado = true;

    equipoLocal.registrarPartido(golesLocal, golesVisitante);
    equipoVisitante.registrarPartido(golesVisitante, golesLocal);
  }

  public Equipo getGanador() {
    if (!jugado)
      return null;
    if (golesLocal > golesVisitante)
      return equipoLocal;
    if (golesVisitante > golesLocal)
      return equipoVisitante;
    return null; // Empate
  }

  public boolean esEmpate() {
    return jugado && golesLocal == golesVisitante;
  }

  // Getters
  public Equipo getEquipoLocal() {
    return equipoLocal;
  }

  public Equipo getEquipoVisitante() {
    return equipoVisitante;
  }

  public int getGolesLocal() {
    return golesLocal;
  }

  public int getGolesVisitante() {
    return golesVisitante;
  }

  public boolean isJugado() {
    return jugado;
  }

  @Override
  public String toString() {
    if (!jugado) {
      return equipoLocal + " vs " + equipoVisitante + " (No jugado)";
    }
    return equipoLocal + " " + golesLocal + " - " + golesVisitante + " " + equipoVisitante;
  }
}