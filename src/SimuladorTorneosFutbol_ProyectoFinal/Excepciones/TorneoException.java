package SimuladorTorneosFutbol_ProyectoFinal.Excepciones;

/**
 * Excepción personalizada utilizada para manejar errores específicos
 * relacionados con la lógica de torneos dentro del sistema.
 *
 * Esta excepción permite lanzar errores controlados cuando ocurren
 * situaciones como: cantidad inválida de equipos, fallos en simulaciones,
 * datos inconsistentes, entre otros escenarios particulares del dominio.
 */
public class TorneoException extends Exception {

  /**
   * Constructor que permite crear una excepción con un mensaje específico.
   *
   * @param mensaje Descripción del error.
   */
  public TorneoException(String mensaje) {
    super(mensaje);
  }

  /**
   * Constructor que permite crear una excepción con un mensaje y una causa.
   *
   * @param mensaje Descripción del error.
   * @param causa   Causa original del error (otra excepción).
   */
  public TorneoException(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }
}
