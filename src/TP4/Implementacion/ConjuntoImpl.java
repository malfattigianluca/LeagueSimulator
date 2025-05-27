package TP4.Implementacion;

import TP4.TDA.Conjunto;

public class ConjuntoImpl implements Conjunto {
  private class Nodo {
    int dato;
    Nodo siguiente;

    public Nodo(int dato) {
      this.dato = dato;
      this.siguiente = null;
    }
  }

  private Nodo primero;
  private int tamanio;

  public ConjuntoImpl() {
    this.primero = null;
    this.tamanio = 0;
  }

  @Override
  public void insertar(int elemento) {
    if (!pertenece(elemento)) {
      Nodo nuevo = new Nodo(elemento);
      nuevo.siguiente = primero;
      primero = nuevo;
      tamanio++;
    }
  }

  @Override
  public void eliminar(int elemento) {
    Nodo actual = primero;
    Nodo anterior = null;
    while (actual != null) {
      if (actual.dato == elemento) {
        if (anterior == null) {
          primero = actual.siguiente;
        } else {
          anterior.siguiente = actual.siguiente;
        }
        tamanio--;
        return;
      }
      anterior = actual;
      actual = actual.siguiente;
    }
  }

  @Override
  public boolean pertenece(int elemento) {
    Nodo actual = primero;
    while (actual != null) {
      if (actual.dato == elemento) {
        return true;
      }
      actual = actual.siguiente;
    }
    return false;
  }

  @Override
  public int tamanio() {
    return tamanio;
  }

  @Override
  public boolean estaVacio() {
    return primero == null;
  }

  @Override
  public void mostrar() {
    if (estaVacio()) {
      System.out.println("Conjunto vacío");
      return;
    }
    System.out.print("{ ");
    Nodo actual = primero;
    while (actual != null) {
      System.out.print(actual.dato + " ");
      actual = actual.siguiente;
    }
    System.out.println("}");
  }

  @Override
  public Conjunto union(Conjunto otro) {
    Conjunto resultado = new ConjuntoImpl();
    Nodo actual = this.primero;
    while (actual != null) {
      resultado.insertar(actual.dato);
      actual = actual.siguiente;
    }
    actual = ((ConjuntoImpl) otro).primero;
    while (actual != null) {
      resultado.insertar(actual.dato);
      actual = actual.siguiente;
    }
    return resultado;
  }

  @Override
  public Conjunto interseccion(Conjunto otro) {
    Conjunto resultado = new ConjuntoImpl();
    Nodo actual = this.primero;
    while (actual != null) {
      if (otro.pertenece(actual.dato)) {
        resultado.insertar(actual.dato);
      }
      actual = actual.siguiente;
    }
    return resultado;
  }
}