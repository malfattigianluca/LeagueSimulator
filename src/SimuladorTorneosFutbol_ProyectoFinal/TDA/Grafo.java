package SimuladorTorneosFutbol_ProyectoFinal.TDA;

import java.util.List;

public interface Grafo<T> {
  void agregarVertice(T vertice);

  void eliminarVertice(T vertice);

  void agregarArista(T origen, T destino, int peso);

  void eliminarArista(T origen, T destino);

  boolean existeVertice(T vertice);

  boolean existeArista(T origen, T destino);

  int getPesoArista(T origen, T destino);

  List<T> getVertices();

  List<T> getAdyacentes(T vertice);

  int cantidadVertices();

  int cantidadAristas();

  boolean estaVacio();

  void mostrar();
}