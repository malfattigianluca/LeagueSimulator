package TP4.Util;

import TP4.Excepciones.TorneoException;

public class Validador {

  public static void validarCantidadEquipos(int cantidad) throws TorneoException {
    if (cantidad < Constantes.MIN_EQUIPOS_TORNEO || cantidad > Constantes.MAX_EQUIPOS_TORNEO) {
      throw new TorneoException("La cantidad de equipos debe estar entre " +
          Constantes.MIN_EQUIPOS_TORNEO + " y " + Constantes.MAX_EQUIPOS_TORNEO);
    }
  }

  public static void validarGoles(int goles) throws TorneoException {
    if (goles < Constantes.MIN_GOLES || goles > Constantes.MAX_GOLES) {
      throw new TorneoException("La cantidad de goles debe estar entre " +
          Constantes.MIN_GOLES + " y " + Constantes.MAX_GOLES);
    }
  }

  public static void validarLiga(String liga) throws TorneoException {
    boolean ligaValida = false;
    for (String ligaDisponible : Constantes.LIGAS_DISPONIBLES) {
      if (ligaDisponible.equals(liga)) {
        ligaValida = true;
        break;
      }
    }
    if (!ligaValida) {
      throw new TorneoException("La liga especificada no es válida");
    }
  }

  public static void validarTipoTorneo(String tipo) throws TorneoException {
    if (!tipo.equals(Constantes.TORNEO_GRUPOS) &&
        !tipo.equals(Constantes.TORNEO_ELIMINACION) &&
        !tipo.equals(Constantes.TORNEO_MIXTO)) {
      throw new TorneoException("El tipo de torneo especificado no es válido");
    }
  }

  private Validador() {
  }
}