package SimuladorTorneosFutbol_ProyectoFinal.TDA;

public interface Cola {
  void enqueue(int elemento); // encolar (agregar elemento)

  int dequeue(); // desencolar (quitar elemento)

  int peek(); // front (mostrar el primer elemento)

  boolean estaVacia();

  int tamanio();

  void mostrar();
}