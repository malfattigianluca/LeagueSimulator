package leaguesimulator.Modelo;

import leaguesimulator.Excepciones.TorneoException;
import leaguesimulator.Util.Validador;
import leaguesimulator.TDA.ConjuntoGenericoTDA;
import leaguesimulator.Implementacion.ConjuntoGenericoImpl;

import java.util.List;
import java.util.Objects;

/**
 * Representa un equipo de fútbol dentro del sistema de gestión de torneos.
 * Contiene información básica como nombre, liga, país, ELO y estadísticas deportivas.
 *
 * También mantiene un conjunto de jugadores asociados y permite operaciones como
 * agregar/eliminar jugadores, registrar partidos, y reiniciar estadísticas.
 *
 * Cada equipo tiene una capacidad máxima de jugadores definida por {@code MAX_JUGADORES}.
 *
 * Las estadísticas del equipo incluyen:
 * - Goles a favor y en contra.
 * - Partidos jugados, ganados, empatados y perdidos.
 * - Cálculo de puntos acumulados.
 *
 * Los métodos de esta clase garantizan validaciones de reglas básicas para mantener
 * la coherencia de los datos (por ejemplo, no duplicar camisetas ni exceder la cantidad máxima de jugadores).
 *
 *
 */
public class Equipo {
  // Identificador único del equipo
  private int id;

  // Nombre del equipo (ej. "Boca Juniors")
  private String nombre;

  // Nombre de la liga a la que pertenece (ej. "LaLiga", "Serie A")
  private String liga;

  // Ruta o identificador visual del escudo del equipo
  private String escudo;

  // País del equipo (ej. "España", "Italia")
  private String pais;

  // Valoración ELO del equipo para determinar su nivel relativo
  private int elo;

  // Estadísticas acumuladas
  private int golesAFavor;
  private int golesEnContra;
  private int partidosJugados;
  private int partidosGanados;
  private int partidosEmpatados;
  private int partidosPerdidos;

  // Conjunto de jugadores del equipo (implementación genérica)
  private ConjuntoGenericoTDA<Jugador> jugadores;

  // Cantidad máxima de jugadores permitidos por equipo
  private static final int MAX_JUGADORES = 40;


  /**
   * Crea una nueva instancia de Equipo con los datos proporcionados.
   *
   * @param id             Identificador único del equipo.
   * @param nombreEquipo   Nombre del equipo.
   * @param elo            Valoración ELO del equipo.
   * @param liga           Nombre de la liga a la que pertenece.
   * @param escudo         Ruta o identificador del escudo del equipo.
   * @param pais           País al que pertenece el equipo.
   * @throws TorneoException Si la liga no es válida según el validador.
   */
  public Equipo(int id, String nombreEquipo, int elo, String liga, String escudo, String pais) throws TorneoException {
    this.id = id;
    this.nombre = nombreEquipo;
    Validador.validarLiga(liga); // Verifica que la liga sea válida
    this.elo = elo;
    this.liga = liga;
    this.escudo = escudo;
    this.pais = pais;
    this.golesAFavor = 0;
    this.golesEnContra = 0;
    this.partidosJugados = 0;
    this.partidosGanados = 0;
    this.partidosEmpatados = 0;
    this.partidosPerdidos = 0;
    this.jugadores = new ConjuntoGenericoImpl<>();
  }

  /**
   * Agrega un jugador al equipo si cumple con las restricciones:
   * - No se excede el máximo de jugadores.
   * - El jugador no es nulo.
   * - El jugador no pertenece ya a otro equipo.
   * - El número de camiseta no está repetido en el equipo.
   *
   * @param jugador Jugador a agregar.
   * @throws TorneoException Si no se cumplen las condiciones anteriores.
   */
  public void agregarJugador(Jugador jugador) throws TorneoException {
    if (cantidadJugadores() >= MAX_JUGADORES) {
      throw new TorneoException("El equipo ya tiene el máximo de jugadores permitidos");
    }
    if (jugador == null) {
      throw new TorneoException("El jugador no puede ser nulo");
    }
    if (jugador.getEquipo() != null) {
      throw new TorneoException("El jugador ya pertenece a un equipo");
    }
    if (tieneNumeroCamiseta(jugador.getNumeroCamiseta())) {
      throw new TorneoException("El número de camiseta " + jugador.getNumeroCamiseta() + " ya está en uso");
    }

    jugadores.insertar(jugador); // Agrega al conjunto
    jugador.unirseAEquipo(this); // Enlaza el jugador con este equipo
  }

  /**
   * Muestra en consola una tabla con los jugadores del equipo,
   * incluyendo nombre, posición, número de camiseta, edad y altura.
   * Si no hay jugadores, lo indica al usuario.
   */
  public void mostrarJugadores() {
    System.out.println("\nEquipo: " + getNombre() + " (" + getLiga() + ")");

    // Si no hay jugadores, imprimir mensaje y salir
    if (jugadores.estaVacio()) {
      System.out.println("No hay jugadores registrados para este equipo.");
      return;
    }

    // Cabecera de la tabla
    System.out.printf("%-30s %-25s %-10s %-10s %-10s%n",
            "Nombre", "Posición", "Camiseta", "Edad", "Altura");
    System.out.println("------------------------------------------------------------------------------------");

    // Obtener lista de jugadores (puede ser null si hubo error en la implementación)
    List<Jugador> listaJugadores = jugadores.getVertices();
    if (listaJugadores == null) {
      System.out.println("Error: La lista de jugadores es nula.");
      return;
    }

    for (Jugador jugador : listaJugadores) {
      String camiseta = jugador.getNumeroCamiseta() != -1 ? String.valueOf(jugador.getNumeroCamiseta()) : "N/A";
      System.out.printf("%-30s %-25s %-10s %-10d %-10s%n",
              jugador.getNombre(),
              jugador.getPosicion(),
              camiseta,
              jugador.getEdad(),
              jugador.getAltura());
    }
  }

  /**
   * Reinicia todas las estadísticas del equipo:
   * puntos, partidos jugados, ganados, empatados, perdidos y goles.
   * Ideal para comenzar una nueva temporada o reiniciar un torneo.
   */
  public void reiniciarEstadisticas() {
    this.partidosJugados = 0;
    this.partidosGanados = 0;
    this.partidosEmpatados = 0;
    this.partidosPerdidos = 0;
    this.golesAFavor = 0;
    this.golesEnContra = 0;
  }


  /**
   * Verifica si algún jugador del equipo ya utiliza el número de camiseta especificado.
   *
   * @param numeroCamiseta Número de camiseta a verificar.
   * @return true si algún jugador ya tiene asignado ese número; false en caso contrario o si el número es -1.
   */
  public boolean tieneNumeroCamiseta(int numeroCamiseta) {
    if (numeroCamiseta == -1) {
      return false; // No se considera un número válido
    }
    for (Jugador jugador : jugadores.getVertices()) {
      if (jugador.getNumeroCamiseta() == numeroCamiseta) {
        return true;
      }
    }
    return false;
  }


  /**
   * Devuelve la cantidad actual de jugadores registrados en el equipo.
   *
   * @return Número total de jugadores en el conjunto.
   */
  public int cantidadJugadores() {
    return jugadores.tamanio();
  }


  /**
   * Registra el resultado de un partido, actualizando estadísticas del equipo:
   * - Goles a favor y en contra.
   * - Partidos jugados, ganados, empatados o perdidos.
   * - Puntos acumulados (3 por victoria, 1 por empate).
   *
   * @param golesAFavor    Cantidad de goles marcados por el equipo.
   * @param golesEnContra  Cantidad de goles recibidos.
   * @throws TorneoException Si la cantidad de goles ingresada no es válida (valores negativos, etc.).
   */
  public void registrarPartido(int golesAFavor, int golesEnContra) throws TorneoException {
    Validador.validarGoles(golesAFavor);
    Validador.validarGoles(golesEnContra);

    this.golesAFavor += golesAFavor;
    this.golesEnContra += golesEnContra;
    this.partidosJugados++;

    if (golesAFavor > golesEnContra) {
      this.partidosGanados++;
    } else if (golesAFavor == golesEnContra) {
      this.partidosEmpatados++;
    } else {
      this.partidosPerdidos++;
    }
  }


  /**
   * @return ID único del equipo.
   */
  public int getId() {
    return id;
  }


  /**
   * Establece el ID del equipo.
   *
   * @param id Nuevo identificador único.
   */
  public void setId(int id) {
    this.id = id;
  }



  /**
   * Establece los puntos del equipo y actualiza los partidos ganados y empatados
   * en función del total de puntos. Asume 3 puntos por victoria y 1 por empate.
   *
   * @param puntos Total de puntos a asignar.
   * @throws IllegalArgumentException Si los puntos son negativos.
   */
  /** @return Nombre del equipo. */
  public String getNombre() {
    return nombre;
  }


  /** @return Escudo del equipo. */
  public String getEscudo() {
    return escudo;
  }

  /** @return Liga a la que pertenece el equipo. */
  public String getLiga() {
    return liga;
  }


  /** @return País del equipo. */
  public String getPais() {
    return pais;
  }

  /** @return Valor ELO del equipo. */
  public int getElo() {
    return elo;
  }


  /** @return Total de puntos del equipo (3 por victoria, 1 por empate). */
  public int getPuntos() {
    return (this.partidosGanados * 3) + this.partidosEmpatados;
  }

  /** @return Goles a favor acumulados del equipo. */
  public int getGolesAFavor() {
    return golesAFavor;
  }

  /** @return Goles en contra acumulados del equipo. */
  public int getGolesEnContra() {
    return golesEnContra;
  }

  /** @return Total de partidos jugados. */
  public int getPartidosJugados() {
    return partidosJugados;
  }

  /** @return Total de partidos ganados. */
  public int getPartidosGanados() {
    return partidosGanados;
  }

  /** @return Total de partidos empatados. */
  public int getPartidosEmpatados() {
    return partidosEmpatados;
  }

  /** @return Total de partidos perdidos. */
  public int getPartidosPerdidos() {
    return partidosPerdidos;
  }

  /** @return Diferencia de goles (goles a favor - goles en contra). */
  public int getDiferenciaGoles() {
    return golesAFavor - golesEnContra;
  }

  /** @return Conjunto de jugadores del equipo. */
  public ConjuntoGenericoTDA<Jugador> getJugadores() {
    return jugadores;
  }

  /**
   * Establece un nuevo valor ELO para el equipo, respetando los límites definidos.
   * Si el ELO es menor a 1000, se ajusta a 1000. Si es mayor a 3000, se ajusta a 3000.
   *
   * @param nuevoElo Nuevo valor de ELO a asignar.
   */
  public void setElo(int nuevoElo) {
    if (nuevoElo < 1000) {
      this.elo = 1000;
    } else if (nuevoElo > 3000) {
      this.elo = 3000;
    } else {
      this.elo = nuevoElo;
    }
  }

  /**
   * Representación en forma de string del equipo, mostrando su escudo, nombre, ELO y liga.
   *
   * @return Cadena con información resumida del equipo.
   */
  @Override
  public String toString() {
    return String.format("Escudo: %s, Nombre: %s, Elo: %d, Liga: %s",
            escudo, nombre, elo, liga);
  }

  /**
   * Compara este equipo con otro objeto para determinar si son iguales.
   * La comparación se basa en el ID del equipo, lo que garantiza unicidad.
   *
   * @param o el objeto con el que se va a comparar.
   * @return true si el objeto es de tipo Equipo y tiene el mismo ID; false en caso contrario.
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Equipo equipo = (Equipo) o;
    return id == equipo.id;
  }

  /**
   * Genera un código hash para este equipo, basado en su ID.
   * Esto es consistente con el método equals, lo que permite su uso en estructuras hash.
   *
   * @return el código hash basado en el ID del equipo.
   */
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}


