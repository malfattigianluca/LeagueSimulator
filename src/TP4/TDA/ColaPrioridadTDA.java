package TP4.TDA;

public interface ColaPrioridadTDA {
    void InicializarCola();
    // siempre que la cola esté inicializada
    void AcolarPrioridad(int x, int prioridad);
    // siempre que la cola esté inicializada y no esté vacía
    void Desacolar();
    // siempre que la cola esté inicializada y no esté vacía
    int Primero();
    // siempre que la cola esté inicializada
    boolean ColaVacia();
    // siempre que la cola esté inicializada y no esté vacía
    int Prioridad();
}