package SimuladorTorneosFutbol_ProyectoFinal.TDA;

public interface ColaTDA {
    void InicializarCola();
    // siempre que la cola esté inicializada
    void Acolar(int x);
    // siempre que la cola esté inicializada y no esté vacía
    void Desacolar();
    // siempre que la cola esté inicializada
    boolean ColaVacia();
    // siempre que la cola esté inicializada y no esté vacía
    int Primero();
    // siempre que la cola esté inicializada
    boolean isFull();
    // siempre que la cola esté inicializada
    void display();
}
