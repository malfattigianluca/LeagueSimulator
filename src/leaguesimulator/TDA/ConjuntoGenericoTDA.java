package leaguesimulator.TDA;

import java.util.List;

public interface ConjuntoGenericoTDA<T> {
  void insertar(T elemento);

  void eliminar(T elemento);

  boolean pertenece(T elemento);

  int tamanio();

  boolean estaVacio();

  void mostrar();

  List<T> getVertices();

}


