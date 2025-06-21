package SimuladorTorneosFutbol_ProyectoFinal.TDA;

public interface ListaDoblementeEnlazada {
  void insertarInicio(int elemento);

  void insertarFin(int elemento);

  int eliminarInicio();

  int eliminarFin();

  void eliminarElemento(int elemento);

  boolean contiene(int elemento);

  int obtenerEnPos(int pos);

  int tamanio();

  boolean estaVacia();

  void mostrarDesdeInicio();

  void mostrarDesdeFin();
}