package TP4.Modelo;

import TP4.Excepciones.TorneoException;
import TP4.Util.Validador;

public class Partido {
  private Equipo local;
  private Equipo visitante;
  private int golesLocal;
  private int golesVisitante;

  public Partido(Equipo local, Equipo visitante, int golesLocal, int golesVisitante) throws TorneoException {
    if (local == null || visitante == null) {
      throw new TorneoException("Los equipos no pueden ser nulos");
    }
    Validador.validarGoles(golesLocal);
    Validador.validarGoles(golesVisitante);

    this.local = local;
    this.visitante = visitante;
    this.golesLocal = golesLocal;
    this.golesVisitante = golesVisitante;

    // Registrar el partido en los equipos
    local.registrarPartido(golesLocal, golesVisitante);
    visitante.registrarPartido(golesVisitante, golesLocal);
  }

  public Equipo getLocal() {
    return local;
  }

  public Equipo getVisitante() {
    return visitante;
  }

  public int getGolesLocal() {
    return golesLocal;
  }

  public int getGolesVisitante() {
    return golesVisitante;
  }

  public Equipo getGanador() {
    if (golesLocal > golesVisitante) {
      return local;
    } else if (golesVisitante > golesLocal) {
      return visitante;
    }
    return null; // Empate
  }

  @Override
  public String toString() {
    return String.format("%s %d - %d %s",
        local.getNombre(), golesLocal, golesVisitante, visitante.getNombre());
  }
}