package TP4.Implementacion;

import TP4.TDA.Pila;

public class PilaDinamica implements Pila {
  private class Nodo {
    int dato;
    Nodo siguiente;

    public Nodo(int dato) {
      this.dato = dato;
      this.siguiente = null;
    }
  }

  private Nodo cima;
  private int tamanio;

  public PilaDinamica() {
    this.cima = null;
    this.tamanio = 0;
  }

  @Override
  public void push(int elemento) {
    Nodo nuevoNodo = new Nodo(elemento);
    nuevoNodo.siguiente = cima;
    cima = nuevoNodo;
    tamanio++;
  }

  @Override
  public int pop() {
    if (cima != null) {
      int elemento = cima.dato;
      cima = cima.siguiente;
      tamanio--;
      return elemento;
    } else {
      System.out.println("Pila vacía");
      return -1;
    }
  }

  @Override
  public int peek() {
    if (cima != null) {
      return cima.dato;
    } else {
      System.out.println("Pila vacía");
      return -1;
    }
  }

  @Override
  public boolean estaVacia() {
    return cima == null;
  }

  @Override
  public int tamanio() {
    return tamanio;
  }

  @Override
  public void mostrar() {
    if (estaVacia()) {
      System.out.println("Pila vacía");
      return;
    }
    System.out.println("Elementos de la pila (desde el tope):");
    Nodo actual = cima;
    while (actual != null) {
      System.out.println(actual.dato);
      actual = actual.siguiente;
    }
  }
}