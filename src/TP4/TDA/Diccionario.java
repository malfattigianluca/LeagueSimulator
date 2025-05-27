package TP4.TDA;

public interface Diccionario {
  void insertar(int clave, String valor);

  void eliminar(int clave);

  boolean pertenece(int clave);

  String obtenerValor(int clave);

  int tamanio();

  boolean estaVacio();

  void mostrar();
}