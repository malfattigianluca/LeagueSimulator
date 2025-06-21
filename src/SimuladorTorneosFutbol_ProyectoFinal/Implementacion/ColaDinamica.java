package SimuladorTorneosFutbol_ProyectoFinal.Implementacion;

import SimuladorTorneosFutbol_ProyectoFinal.TDA.Cola;

public class ColaDinamica implements Cola {
  private class Nodo {
    int dato;
    Nodo siguiente;

    public Nodo(int dato) {
      this.dato = dato;
      this.siguiente = null;
    }
  }

  private Nodo frente;
  private Nodo finalCola;
  private int tamanio;

  public ColaDinamica() {
    this.frente = null;
    this.finalCola = null;
    this.tamanio = 0;
  }

  @Override
  public void enqueue(int elemento) {
    Nodo nuevoNodo = new Nodo(elemento);
    if (estaVacia()) {
      frente = nuevoNodo;
      finalCola = nuevoNodo;
    } else {
      finalCola.siguiente = nuevoNodo;
      finalCola = nuevoNodo;
    }
    tamanio++;
  }

  @Override
  public int dequeue() {
    if (!estaVacia()) {
      int elemento = frente.dato;
      frente = frente.siguiente;
      if (frente == null) {
        finalCola = null;
      }
      tamanio--;
      return elemento;
    } else {
      System.out.println("Cola vacía");
      return -1;
    }
  }

  @Override
  public int peek() {
    if (!estaVacia()) {
      return frente.dato;
    } else {
      System.out.println("Cola vacía");
      return -1;
    }
  }

  @Override
  public boolean estaVacia() {
    return frente == null;
  }

  @Override
  public int tamanio() {
    return tamanio;
  }

  @Override
  public void mostrar() {
    if (estaVacia()) {
      System.out.println("Cola vacía");
      return;
    }
    System.out.println("Elementos de la cola (desde el frente):");
    Nodo actual = frente;
    while (actual != null) {
      System.out.println(actual.dato);
      actual = actual.siguiente;
    }
  }
}