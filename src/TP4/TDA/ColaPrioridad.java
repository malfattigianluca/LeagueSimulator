package TP4.TDA;

public interface ColaPrioridad {
  void encolar(int dato, int prioridad);

  int desencolar();

  int frente();

  boolean estaVacia();

  int tamanio();

  void mostrar();
}