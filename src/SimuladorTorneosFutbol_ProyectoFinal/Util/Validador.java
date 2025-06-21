package SimuladorTorneosFutbol_ProyectoFinal.Util;

import SimuladorTorneosFutbol_ProyectoFinal.Excepciones.TorneoException;

/**
 * Clase utilitaria encargada de realizar validaciones centrales dentro del sistema de torneos.
 *
 * Provee métodos estáticos que lanzan {@link TorneoException} cuando una condición no se cumple,
 * asegurando que los datos utilizados en el sistema sean válidos antes de continuar con el flujo de ejecución.
 */
public class Validador {
  /**
   * Constructor privado para evitar instanciación.
   * Esta clase contiene solo métodos estáticos.
   */
  private Validador() {
  }

  /**
   * Valida que la cantidad de goles anotados esté en un rango lógico.
   *
   * @param goles Cantidad de goles por equipo.
   * @throws TorneoException Si el número de goles es irreal o inválido.
   */
  public static void validarGoles(int goles) throws TorneoException {
    if (goles < Constantes.MIN_GOLES || goles > Constantes.MAX_GOLES) {
      throw new TorneoException("La cantidad de goles debe estar entre " +
              Constantes.MIN_GOLES + " y " + Constantes.MAX_GOLES);
    }
  }

  /**
   * Valida que una liga especificada exista entre las ligas definidas.
   *
   * @param liga Nombre de la liga.
   * @throws TorneoException Si la liga no es válida.
   */
  public static void validarLiga(String liga) throws TorneoException {
    // Se permite "BYE" o "Ninguna" como ligas válidas especiales
    if ("BYE".equalsIgnoreCase(liga) || "Ninguna".equalsIgnoreCase(liga)) {
      return;
    }

    boolean ligaValida = false;
    for (String ligaDisponible : Constantes.LIGAS_DISPONIBLES) {
      if (ligaDisponible.equalsIgnoreCase(liga)) {
        ligaValida = true;
        break;
      }
    }

    if (!ligaValida) {
      throw new TorneoException("La liga especificada no es válida");
    }
  }

  /** LOS SIGUIENTES METODOS NO SERAN UTILIZADOS EN LA PRIMER VERSION DEL PROGRAMA*/

  /**
   * Valida que la cantidad de equipos esté dentro del rango permitido.
   *
   * @param cantidad Número de equipos.
   * @throws TorneoException Si la cantidad está fuera del rango definido en {@link Constantes}.
   */
  public static void validarCantidadEquipos(int cantidad) throws TorneoException {
    if (cantidad < Constantes.MIN_EQUIPOS_TORNEO || cantidad > Constantes.MAX_EQUIPOS_TORNEO) {
      throw new TorneoException("La cantidad de equipos debe estar entre " +
              Constantes.MIN_EQUIPOS_TORNEO + " y " + Constantes.MAX_EQUIPOS_TORNEO);
    }
  }

  /**
   * Valida que el tipo de torneo sea uno de los aceptados.
   *
   * @param tipo Tipo de torneo (GRUPOS, ELIMINACION, MIXTO).
   * @throws TorneoException Si el tipo no coincide con los tipos definidos en {@link Constantes}.
   */
  public static void validarTipoTorneo(String tipo) throws TorneoException {
    if (!tipo.equals(Constantes.TORNEO_GRUPOS) &&
            !tipo.equals(Constantes.TORNEO_ELIMINACION) &&
            !tipo.equals(Constantes.TORNEO_MIXTO)) {
      throw new TorneoException("El tipo de torneo especificado no es válido");
    }
  }
}
