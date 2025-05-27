package TP4.Implementacion;

import TP4.TDA.ListaDoblementeEnlazada;

public class ListaDoblementeEnlazadaImpl implements ListaDoblementeEnlazada {
  private class Nodo {
    int dato;
    Nodo siguiente;
    Nodo anterior;

    public Nodo(int dato) {
      this.dato = dato;
      this.siguiente = null;
      this.anterior = null;
    }
  }

  private Nodo cabeza;
  private Nodo cola;
  private int tamanio;

  public ListaDoblementeEnlazadaImpl() {
    this.cabeza = null;
    this.cola = null;
    this.tamanio = 0;
  }

  @Override
  public void insertarInicio(int elemento) {
    Nodo nuevoNodo = new Nodo(elemento);
    if (cabeza == null) {
      cabeza = nuevoNodo;
      cola = nuevoNodo;
    } else {
      nuevoNodo.siguiente = cabeza;
      cabeza.anterior = nuevoNodo;
      cabeza = nuevoNodo;
    }
    tamanio++;
  }

  @Override
  public void insertarFin(int elemento) {
    Nodo nuevoNodo = new Nodo(elemento);
    if (cola == null) {
      cabeza = nuevoNodo;
      cola = nuevoNodo;
    } else {
      nuevoNodo.anterior = cola;
      cola.siguiente = nuevoNodo;
      cola = nuevoNodo;
    }
    tamanio++;
  }

  @Override
  public int eliminarInicio() {
    if (cabeza == null) {
      System.out.println("Lista vacía");
      return -1;
    }
    int elemento = cabeza.dato;
    if (cabeza == cola) {
      cabeza = null;
      cola = null;
    } else {
      cabeza = cabeza.siguiente;
      cabeza.anterior = null;
    }
    tamanio--;
    return elemento;
  }

  @Override
  public int eliminarFin() {
    if (cola == null) {
      System.out.println("Lista vacía");
      return -1;
    }
    int elemento = cola.dato;
    if (cabeza == cola) {
      cabeza = null;
      cola = null;
    } else {
      cola = cola.anterior;
      cola.siguiente = null;
    }
    tamanio--;
    return elemento;
  }

  @Override
  public void eliminarElemento(int elemento) {
    if (cabeza == null) {
      return;
    }
    Nodo actual = cabeza;
    while (actual != null && actual.dato != elemento) {
      actual = actual.siguiente;
    }
    if (actual != null) {
      if (actual == cabeza) {
        eliminarInicio();
      } else if (actual == cola) {
        eliminarFin();
      } else {
        actual.anterior.siguiente = actual.siguiente;
        actual.siguiente.anterior = actual.anterior;
        tamanio--;
      }
    }
  }

  @Override
  public boolean contiene(int elemento) {
    Nodo actual = cabeza;
    while (actual != null) {
      if (actual.dato == elemento) {
        return true;
      }
      actual = actual.siguiente;
    }
    return false;
  }

  @Override
  public int obtenerEnPos(int pos) {
    if (pos < 0 || pos >= tamanio) {
      System.out.println("Posición inválida");
      return -1;
    }
    Nodo actual;
    if (pos < tamanio / 2) {
      actual = cabeza;
      for (int i = 0; i < pos; i++) {
        actual = actual.siguiente;
      }
    } else {
      actual = cola;
      for (int i = tamanio - 1; i > pos; i--) {
        actual = actual.anterior;
      }
    }
    return actual.dato;
  }

  @Override
  public int tamanio() {
    return tamanio;
  }

  @Override
  public boolean estaVacia() {
    return cabeza == null;
  }

  @Override
  public void mostrarDesdeInicio() {
    if (estaVacia()) {
      System.out.println("Lista vacía");
      return;
    }
    System.out.println("Elementos de la lista (desde el inicio):");
    Nodo actual = cabeza;
    while (actual != null) {
      System.out.print(actual.dato + " <-> ");
      actual = actual.siguiente;
    }
    System.out.println("null");
  }

  @Override
  public void mostrarDesdeFin() {
    if (estaVacia()) {
      System.out.println("Lista vacía");
      return;
    }
    System.out.println("Elementos de la lista (desde el fin):");
    Nodo actual = cola;
    while (actual != null) {
      System.out.print(actual.dato + " <-> ");
      actual = actual.anterior;
    }
    System.out.println("null");
  }
}