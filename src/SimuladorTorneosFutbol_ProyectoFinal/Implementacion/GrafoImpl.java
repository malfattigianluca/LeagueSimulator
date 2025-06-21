package SimuladorTorneosFutbol_ProyectoFinal.Implementacion;

import SimuladorTorneosFutbol_ProyectoFinal.TDA.Grafo;
import java.util.*;

public class GrafoImpl<T> implements Grafo<T> {
  private Map<T, Integer> vertices;
  private int[][] matrizAdyacencia;
  private int cantidadVertices;
  private int cantidadAristas;

  public GrafoImpl() {
    this.vertices = new HashMap<>();
    this.matrizAdyacencia = new int[10][10]; // Tamaño inicial
    this.cantidadVertices = 0;
    this.cantidadAristas = 0;
  }

  private void redimensionarMatriz() {
    int nuevoTamano = matrizAdyacencia.length * 2;
    int[][] nuevaMatriz = new int[nuevoTamano][nuevoTamano];

    for (int i = 0; i < matrizAdyacencia.length; i++) {
      System.arraycopy(matrizAdyacencia[i], 0, nuevaMatriz[i], 0, matrizAdyacencia.length);
    }

    matrizAdyacencia = nuevaMatriz;
  }

  @Override
  public void agregarVertice(T vertice) {
    if (!existeVertice(vertice)) {
      if (cantidadVertices >= matrizAdyacencia.length) {
        redimensionarMatriz();
      }
      vertices.put(vertice, cantidadVertices++);
    }
  }

  @Override
  public void eliminarVertice(T vertice) {
    if (existeVertice(vertice)) {
      int indice = vertices.get(vertice);

      // Eliminar aristas
      for (int i = 0; i < cantidadVertices; i++) {
        if (matrizAdyacencia[indice][i] != 0) {
          cantidadAristas--;
        }
        if (matrizAdyacencia[i][indice] != 0) {
          cantidadAristas--;
        }
      }

      // Reorganizar matriz
      for (int i = indice; i < cantidadVertices - 1; i++) {
        for (int j = 0; j < cantidadVertices; j++) {
          matrizAdyacencia[i][j] = matrizAdyacencia[i + 1][j];
          matrizAdyacencia[j][i] = matrizAdyacencia[j][i + 1];
        }
      }

      vertices.remove(vertice);
      cantidadVertices--;
    }
  }

  @Override
  public void agregarArista(T origen, T destino, int peso) {
    if (existeVertice(origen) && existeVertice(destino)) {
      int indiceOrigen = vertices.get(origen);
      int indiceDestino = vertices.get(destino);

      if (matrizAdyacencia[indiceOrigen][indiceDestino] == 0) {
        cantidadAristas++;
      }

      matrizAdyacencia[indiceOrigen][indiceDestino] = peso;
    }
  }

  @Override
  public void eliminarArista(T origen, T destino) {
    if (existeArista(origen, destino)) {
      int indiceOrigen = vertices.get(origen);
      int indiceDestino = vertices.get(destino);

      matrizAdyacencia[indiceOrigen][indiceDestino] = 0;
      cantidadAristas--;
    }
  }

  @Override
  public boolean existeVertice(T vertice) {
    return vertices.containsKey(vertice);
  }

  @Override
  public boolean existeArista(T origen, T destino) {
    if (existeVertice(origen) && existeVertice(destino)) {
      int indiceOrigen = vertices.get(origen);
      int indiceDestino = vertices.get(destino);
      return matrizAdyacencia[indiceOrigen][indiceDestino] != 0;
    }
    return false;
  }

  @Override
  public int getPesoArista(T origen, T destino) {
    if (existeArista(origen, destino)) {
      int indiceOrigen = vertices.get(origen);
      int indiceDestino = vertices.get(destino);
      return matrizAdyacencia[indiceOrigen][indiceDestino];
    }
    return 0;
  }

  @Override
  public List<T> getVertices() {
    return new ArrayList<>(vertices.keySet());
  }

  @Override
  public List<T> getAdyacentes(T vertice) {
    List<T> adyacentes = new ArrayList<>();
    if (existeVertice(vertice)) {
      int indice = vertices.get(vertice);
      for (Map.Entry<T, Integer> entry : vertices.entrySet()) {
        if (matrizAdyacencia[indice][entry.getValue()] != 0) {
          adyacentes.add(entry.getKey());
        }
      }
    }
    return adyacentes;
  }

  @Override
  public int cantidadVertices() {
    return cantidadVertices;
  }

  @Override
  public int cantidadAristas() {
    return cantidadAristas;
  }

  @Override
  public boolean estaVacio() {
    return cantidadVertices == 0;
  }

  @Override
  public void mostrar() {
    System.out.println("Grafo con " + cantidadVertices + " vértices y " + cantidadAristas + " aristas:");
    for (T vertice : vertices.keySet()) {
      System.out.print(vertice + " -> ");
      List<T> adyacentes = getAdyacentes(vertice);
      if (adyacentes.isEmpty()) {
        System.out.println("sin adyacentes");
      } else {
        System.out.println(adyacentes);
      }
    }
  }
}