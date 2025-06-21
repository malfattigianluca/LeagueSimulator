package SimuladorTorneosFutbol_ProyectoFinal.TDA;

public interface Pila {
  void push(int elemento); // Apilar

  int pop(); // desapilar

  int peek(); // tope

  boolean estaVacia();

  int tamanio();

  void mostrar();
}