package SimuladorTorneosFutbol_ProyectoFinal.TDA;

public interface ArbolBinario {
  void insertar(int dato);

  boolean buscar(int dato);

  void eliminar(int dato);

  boolean estaVacio();

  int altura();

  int cantidadNodos();

  void recorrerInorden(); // izquierda - raíz - derecha

  void recorrerPreorden(); // raíz - izquierda - derecha

  void recorrerPostorden(); // izquierda - derecha - raíz

  void mostrar();
}