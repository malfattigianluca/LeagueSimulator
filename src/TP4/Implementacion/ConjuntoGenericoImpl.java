package TP4.Implementacion;

import TP4.TDA.ConjuntoGenericoTDA;
import java.util.ArrayList;
import java.util.List;

public class ConjuntoGenericoImpl<T> implements ConjuntoGenericoTDA<T> {
  private class Nodo {
    T elemento;
    Nodo siguiente;

    Nodo(T elemento) {
      this.elemento = elemento;
      this.siguiente = null;
    }
  }

  private Nodo primero;
  private int cantidad;

  public ConjuntoGenericoImpl() {
    this.primero = null;
    this.cantidad = 0;
  }

  @Override
  public void insertar(T elemento) {
    if (!pertenece(elemento)) {
      Nodo nuevo = new Nodo(elemento);
      nuevo.siguiente = primero;
      primero = nuevo;
      cantidad++;
    }
  }

  @Override
  public void eliminar(T elemento) {
    if (primero == null)
      return;

    if (primero.elemento.equals(elemento)) {
      primero = primero.siguiente;
      cantidad--;
      return;
    }

    Nodo actual = primero;
    while (actual.siguiente != null) {
      if (actual.siguiente.elemento.equals(elemento)) {
        actual.siguiente = actual.siguiente.siguiente;
        cantidad--;
        return;
      }
      actual = actual.siguiente;
    }
  }

  @Override
  public boolean pertenece(T elemento) {
    Nodo actual = primero;
    while (actual != null) {
      if (actual.elemento.equals(elemento)) {
        return true;
      }
      actual = actual.siguiente;
    }
    return false;
  }

  @Override
  public int tamanio() {
    return cantidad;
  }

  @Override
  public boolean estaVacio() {
    return cantidad == 0;
  }

  @Override
  public void mostrar() {
    Nodo actual = primero;
    while (actual != null) {
      System.out.println(actual.elemento);
      actual = actual.siguiente;
    }
  }

  @Override
  public List<T> getVertices() {
    List<T> vertices = new ArrayList<>();
    Nodo actual = primero;
    while (actual != null) {
      vertices.add(actual.elemento);
      actual = actual.siguiente;
    }
    return vertices;
  }
}