package TP4.TDA;

public interface ListaEnlazadaSimple {
  void insertarInicio(int elemento);

  void insertarFin(int elemento);

  void insertarEnPos(int pos, int elemento);

  void eliminarElemento(int elemento);

  boolean contiene(int elemento);

  int obtenerEnPos(int pos);

  int tamanio();

  boolean estaVacia();

  void mostrar();
}