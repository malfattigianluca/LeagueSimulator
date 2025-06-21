package SimuladorTorneosFutbol_ProyectoFinal.Implementacion;

import SimuladorTorneosFutbol_ProyectoFinal.TDA.ListaEnlazadaSimple;

public class ListaEnlazadaSimpleImpl implements ListaEnlazadaSimple {
  private class Nodo {
    int dato;
    Nodo siguiente;

    public Nodo(int dato) {
      this.dato = dato;
      this.siguiente = null;
    }
  }

  private Nodo cabeza;
  private int tamanio;

  public ListaEnlazadaSimpleImpl() {
    this.cabeza = null;
    this.tamanio = 0;
  }

  @Override
  public void insertarInicio(int elemento) {
    Nodo nuevoNodo = new Nodo(elemento);
    nuevoNodo.siguiente = cabeza;
    cabeza = nuevoNodo;
    tamanio++;
  }

  @Override
  public void insertarFin(int elemento) {
    Nodo nuevoNodo = new Nodo(elemento);
    if (cabeza == null) {
      cabeza = nuevoNodo;
    } else {
      Nodo actual = cabeza;
      while (actual.siguiente != null) {
        actual = actual.siguiente;
      }
      actual.siguiente = nuevoNodo;
    }
    tamanio++;
  }

  @Override
  public void insertarEnPos(int pos, int elemento) {
    if (pos < 0 || pos > tamanio) {
      System.out.println("Posición inválida");
      return;
    }
    if (pos == 0) {
      insertarInicio(elemento);
      return;
    }
    if (pos == tamanio) {
      insertarFin(elemento);
      return;
    }
    Nodo nuevoNodo = new Nodo(elemento);
    Nodo actual = cabeza;
    for (int i = 0; i < pos - 1; i++) {
      actual = actual.siguiente;
    }
    nuevoNodo.siguiente = actual.siguiente;
    actual.siguiente = nuevoNodo;
    tamanio++;
  }

  @Override
  public void eliminarElemento(int elemento) {
    if (cabeza == null) {
      return;
    }
    if (cabeza.dato == elemento) {
      cabeza = cabeza.siguiente;
      tamanio--;
      return;
    }
    Nodo actual = cabeza;
    while (actual.siguiente != null && actual.siguiente.dato != elemento) {
      actual = actual.siguiente;
    }
    if (actual.siguiente != null) {
      actual.siguiente = actual.siguiente.siguiente;
      tamanio--;
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
    Nodo actual = cabeza;
    for (int i = 0; i < pos; i++) {
      actual = actual.siguiente;
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
  public void mostrar() {
    if (estaVacia()) {
      System.out.println("Lista vacía");
      return;
    }
    System.out.println("Elementos de la lista:");
    Nodo actual = cabeza;
    while (actual != null) {
      System.out.print(actual.dato + " -> ");
      actual = actual.siguiente;
    }
    System.out.println("null");
  }
}