package TP4.Implementacion;

import TP4.TDA.Pila;

public class PilaEstatica implements Pila {
  private int[] elementos;
  private int tope;
  private int capacidad;

  public PilaEstatica(int capacidad) {
    this.capacidad = capacidad;
    this.elementos = new int[capacidad];
    this.tope = -1; // Inicialmente vacía
  }

  @Override
  public void push(int elemento) {
    if (tope < capacidad - 1) {
      tope++;
      elementos[tope] = elemento;
    } else {
      System.out.println("Pila llena");
    }
  }

  @Override
  public int pop() {
    if (tope >= 0) {
      int elemento = elementos[tope];
      tope--;
      return elemento;
    } else {
      System.out.println("Pila vacía");
      return -1;
    }
  }

  @Override
  public int peek() {
    if (tope >= 0) {
      return elementos[tope];
    } else {
      System.out.println("Pila vacía");
      return -1;
    }
  }

  @Override
  public boolean estaVacia() {
    return tope == -1;
  }

  @Override
  public int tamanio() {
    return tope + 1;
  }

  @Override
  public void mostrar() {
    if (estaVacia()) {
      System.out.println("Pila vacía");
      return;
    }
    System.out.println("Elementos de la pila (desde el tope):");
    for (int i = tope; i >= 0; i--) {
      System.out.println(elementos[i]);
    }
  }
}