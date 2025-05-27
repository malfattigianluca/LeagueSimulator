package TP4.Implementacion;

import TP4.TDA.Cola;

public class ColaEstatica implements Cola {
  private int[] elementos;
  private int frente;
  private int finalCola;
  private int capacidad;
  private int cantidad;

  public ColaEstatica(int capacidad) {
    this.capacidad = capacidad;
    this.elementos = new int[capacidad];
    this.frente = 0;
    this.finalCola = -1;
    this.cantidad = 0;
  }

  @Override
  public void enqueue(int elemento) {
    if (cantidad < capacidad) {
      finalCola = (finalCola + 1) % capacidad;
      elementos[finalCola] = elemento;
      cantidad++;
    } else {
      System.out.println("Cola llena");
    }
  }

  @Override
  public int dequeue() {
    if (cantidad > 0) {
      int elemento = elementos[frente];
      frente = (frente + 1) % capacidad;
      cantidad--;
      return elemento;
    } else {
      System.out.println("Cola vacía");
      return -1;
    }
  }

  @Override
  public int peek() {
    if (cantidad > 0) {
      return elementos[frente];
    } else {
      System.out.println("Cola vacía");
      return -1;
    }
  }

  @Override
  public boolean estaVacia() {
    return cantidad == 0;
  }

  @Override
  public int tamanio() {
    return cantidad;
  }

  @Override
  public void mostrar() {
    if (estaVacia()) {
      System.out.println("Cola vacía");
      return;
    }
    System.out.println("Elementos de la cola (desde el frente):");
    int i = frente;
    int cont = 0;
    while (cont < cantidad) {
      System.out.println(elementos[i]);
      i = (i + 1) % capacidad;
      cont++;
    }
  }
}