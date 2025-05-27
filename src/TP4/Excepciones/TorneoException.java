package TP4.Excepciones;

public class TorneoException extends Exception {
  public TorneoException(String mensaje) {
    super(mensaje);
  }

  public TorneoException(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }
}