package TP4.Implementacion;

import TP4.TDA.ColaPrioridad;

public class ColaPrioridadArreglo implements ColaPrioridad {
  private class ElementoPrioridad {
    int dato;
    int prioridad;

    public ElementoPrioridad(int dato, int prioridad) {
      this.dato = dato;
      this.prioridad = prioridad;
    }
  }

  private ElementoPrioridad[] arreglo;
  private int tamanioMaximo;
  private int cantidad;

  public ColaPrioridadArreglo(int capacidad) {
    this.tamanioMaximo = capacidad;
    this.arreglo = new ElementoPrioridad[capacidad];
    this.cantidad = 0;
  }

  @Override
  public void encolar(int dato, int prioridad) {
    if (cantidad >= tamanioMaximo) {
      System.out.println("Cola llena");
      return;
    }

    ElementoPrioridad nuevo = new ElementoPrioridad(dato, prioridad);
    int i = cantidad - 1;

    while (i >= 0 && arreglo[i].prioridad > prioridad) {
      arreglo[i + 1] = arreglo[i];
      i--;
    }
    arreglo[i + 1] = nuevo;
    cantidad++;
  }

  @Override
  public int desencolar() {
    if (estaVacia()) {
      System.out.println("Cola vacía");
      return -1;
    }

    int valor = arreglo[0].dato;
    for (int i = 0; i < cantidad - 1; i++) {
      arreglo[i] = arreglo[i + 1];
    }
    arreglo[cantidad - 1] = null;
    cantidad--;
    return valor;
  }

  @Override
  public int frente() {
    if (estaVacia()) {
      throw new RuntimeException("Cola vacía");
    }
    return arreglo[0].dato;
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
    System.out.println("Cola con prioridad (arreglo):");
    for (int i = 0; i < cantidad; i++) {
      System.out.println("Dato: " + arreglo[i].dato + " | Prioridad: " + arreglo[i].prioridad);
    }
  }
}