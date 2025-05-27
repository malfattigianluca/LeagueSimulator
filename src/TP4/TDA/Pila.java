package TP4.TDA;

public interface Pila {
  void push(int elemento); // Apilar

  int pop(); // desapilar

  int peek(); // tope

  boolean estaVacia();

  int tamanio();

  void mostrar();
}