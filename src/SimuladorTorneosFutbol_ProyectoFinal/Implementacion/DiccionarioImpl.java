package SimuladorTorneosFutbol_ProyectoFinal.Implementacion;

import SimuladorTorneosFutbol_ProyectoFinal.TDA.Diccionario;

public class DiccionarioImpl implements Diccionario {
  private class Par {
    int clave;
    String valor;
    Par siguiente;

    public Par(int clave, String valor) {
      this.clave = clave;
      this.valor = valor;
      this.siguiente = null;
    }
  }

  private Par primero;
  private int tamanio;

  public DiccionarioImpl() {
    this.primero = null;
    this.tamanio = 0;
  }

  @Override
  public void insertar(int clave, String valor) {
    Par actual = primero;
    while (actual != null) {
      if (actual.clave == clave) {
        actual.valor = valor;
        return;
      }
      actual = actual.siguiente;
    }
    Par nuevo = new Par(clave, valor);
    nuevo.siguiente = primero;
    primero = nuevo;
    tamanio++;
  }

  @Override
  public void eliminar(int clave) {
    Par actual = primero;
    Par anterior = null;
    while (actual != null) {
      if (actual.clave == clave) {
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
  public boolean pertenece(int clave) {
    Par actual = primero;
    while (actual != null) {
      if (actual.clave == clave) {
        return true;
      }
      actual = actual.siguiente;
    }
    return false;
  }

  @Override
  public String obtenerValor(int clave) {
    Par actual = primero;
    while (actual != null) {
      if (actual.clave == clave) {
        return actual.valor;
      }
      actual = actual.siguiente;
    }
    return null;
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
      System.out.println("Diccionario vacío");
      return;
    }
    System.out.println("Elementos del diccionario:");
    Par actual = primero;
    while (actual != null) {
      System.out.println("Clave: " + actual.clave + ", Valor: " + actual.valor);
      actual = actual.siguiente;
    }
  }
}