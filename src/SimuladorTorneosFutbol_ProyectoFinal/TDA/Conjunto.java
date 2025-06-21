package SimuladorTorneosFutbol_ProyectoFinal.TDA;

public interface Conjunto {
  void insertar(int elemento);

  void eliminar(int elemento);

  boolean pertenece(int elemento);

  int tamanio();

  boolean estaVacio();

  void mostrar();

  Conjunto union(Conjunto otro);

  Conjunto interseccion(Conjunto otro);
}