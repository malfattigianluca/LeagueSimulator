package leaguesimulator.Implementacion;

import leaguesimulator.TDA.ArbolBinario;

public class ArbolBinarioImpl implements ArbolBinario {
  private class Nodo {
    int dato;
    Nodo izquierdo;
    Nodo derecho;

    public Nodo(int dato) {
      this.dato = dato;
      this.izquierdo = null;
      this.derecho = null;
    }
  }

  private Nodo raiz;

  public ArbolBinarioImpl() {
    this.raiz = null;
  }

  @Override
  public void insertar(int dato) {
    raiz = insertarRecursivo(raiz, dato);
  }

  private Nodo insertarRecursivo(Nodo nodo, int dato) {
    if (nodo == null) {
      return new Nodo(dato);
    }
    if (dato < nodo.dato) {
      nodo.izquierdo = insertarRecursivo(nodo.izquierdo, dato);
    } else if (dato > nodo.dato) {
      nodo.derecho = insertarRecursivo(nodo.derecho, dato);
    }
    return nodo;
  }

  @Override
  public boolean buscar(int dato) {
    return buscarRecursivo(raiz, dato) != null;
  }

  private Nodo buscarRecursivo(Nodo nodo, int dato) {
    if (nodo == null) {
      return null;
    }
    if (dato == nodo.dato) {
      return nodo;
    } else if (dato < nodo.dato) {
      return buscarRecursivo(nodo.izquierdo, dato);
    } else {
      return buscarRecursivo(nodo.derecho, dato);
    }
  }

  @Override
  public void eliminar(int dato) {
    raiz = eliminarRecursivo(raiz, dato);
  }

  private Nodo eliminarRecursivo(Nodo nodo, int dato) {
    if (nodo == null) {
      return null;
    }
    if (dato == nodo.dato) {
      if (nodo.izquierdo == null) {
        return nodo.derecho;
      } else if (nodo.derecho == null) {
        return nodo.izquierdo;
      }
      Nodo sucesor = encontrarMinimo(nodo.derecho);
      nodo.dato = sucesor.dato;
      nodo.derecho = eliminarRecursivo(nodo.derecho, sucesor.dato);
    } else if (dato < nodo.dato) {
      nodo.izquierdo = eliminarRecursivo(nodo.izquierdo, dato);
    } else {
      nodo.derecho = eliminarRecursivo(nodo.derecho, dato);
    }
    return nodo;
  }

  private Nodo encontrarMinimo(Nodo nodo) {
    while (nodo.izquierdo != null) {
      nodo = nodo.izquierdo;
    }
    return nodo;
  }

  @Override
  public boolean estaVacio() {
    return raiz == null;
  }

  @Override
  public int altura() {
    return alturaRecursiva(raiz);
  }

  private int alturaRecursiva(Nodo nodo) {
    if (nodo == null) {
      return -1;
    }
    int alturaIzq = alturaRecursiva(nodo.izquierdo);
    int alturaDer = alturaRecursiva(nodo.derecho);
    return Math.max(alturaIzq, alturaDer) + 1;
  }

  @Override
  public int cantidadNodos() {
    return cantidadNodosRecursiva(raiz);
  }

  private int cantidadNodosRecursiva(Nodo nodo) {
    if (nodo == null) {
      return 0;
    }
    return 1 + cantidadNodosRecursiva(nodo.izquierdo) + cantidadNodosRecursiva(nodo.derecho);
  }

  @Override
  public void recorrerInorden() {
    System.out.println("Recorrido inorden (izquierda - raíz - derecha):");
    recorrerInordenRecursivo(raiz);
    System.out.println();
  }

  private void recorrerInordenRecursivo(Nodo nodo) {
    if (nodo != null) {
      recorrerInordenRecursivo(nodo.izquierdo);
      System.out.print(nodo.dato + " ");
      recorrerInordenRecursivo(nodo.derecho);
    }
  }

  @Override
  public void recorrerPreorden() {
    System.out.println("Recorrido preorden (raíz - izquierda - derecha):");
    recorrerPreordenRecursivo(raiz);
    System.out.println();
  }

  private void recorrerPreordenRecursivo(Nodo nodo) {
    if (nodo != null) {
      System.out.print(nodo.dato + " ");
      recorrerPreordenRecursivo(nodo.izquierdo);
      recorrerPreordenRecursivo(nodo.derecho);
    }
  }

  @Override
  public void recorrerPostorden() {
    System.out.println("Recorrido postorden (izquierda - derecha - raíz):");
    recorrerPostordenRecursivo(raiz);
    System.out.println();
  }

  private void recorrerPostordenRecursivo(Nodo nodo) {
    if (nodo != null) {
      recorrerPostordenRecursivo(nodo.izquierdo);
      recorrerPostordenRecursivo(nodo.derecho);
      System.out.print(nodo.dato + " ");
    }
  }

  @Override
  public void mostrar() {
    if (estaVacio()) {
      System.out.println("Árbol vacío");
      return;
    }
    System.out.println("Árbol binario:");
    mostrarRecursivo(raiz, 0);
  }

  private void mostrarRecursivo(Nodo nodo, int nivel) {
    if (nodo != null) {
      mostrarRecursivo(nodo.derecho, nivel + 1);
      for (int i = 0; i < nivel; i++) {
        System.out.print("    ");
      }
      System.out.println(nodo.dato);
      mostrarRecursivo(nodo.izquierdo, nivel + 1);
    }
  }
}


