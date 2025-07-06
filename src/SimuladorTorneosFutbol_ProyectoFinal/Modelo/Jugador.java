package SimuladorTorneosFutbol_ProyectoFinal.Modelo;

import SimuladorTorneosFutbol_ProyectoFinal.Excepciones.TorneoException;



/**
 * Representa a un jugador de fútbol dentro del sistema de torneos.
 * Contiene información personal y estadística relevante como nombre, número de camiseta,
 * posición, edad, altura, y registros de rendimiento (goles, asistencias, tarjetas).
 *
 * Cada jugador puede estar asociado a un único {@link Equipo}, aunque su pertenencia
 * puede cambiar o quedar nula si es removido de un equipo.
 *
 * Los jugadores también pueden guardar la referencia de la liga a la que pertenecen,
 * lo cual puede usarse para filtrado u organización adicional.
 *
 * Esta clase permite ser utilizada en estructuras de datos como conjuntos o grafos.
 *
 * @author
 */
public class Jugador {
  // Identificador único del jugador
  private int id;

  // Nombre completo del jugador (ej. "Lionel Messi")
  private String nombre;

  // Número de camiseta que utiliza dentro del equipo (debe ser único en su equipo)
  private int numeroCamiseta;

  // Posición en la cancha (ej. "Delantero", "Defensor", "Arquero")
  private String posicion;

  // Referencia al equipo al que pertenece actualmente (puede ser null)
  private Equipo equipo;

  // Edad del jugador (en años)
  private int edad;

  // Altura del jugador (en formato String para flexibilidad, ej. "1.75m")
  private String altura;

  // Cantidad total de goles anotados por el jugador
  private int goles;

  // Cantidad total de asistencias realizadas
  private int asistencias;

  // Cantidad de tarjetas amarillas acumuladas
  private int amarillas;

  // Cantidad de tarjetas rojas acumuladas
  private int rojas;

  // Liga a la que pertenece el jugador (puede coincidir con la liga del equipo)
  private String liga;

    // Indica si el jugador es titular en su equipo (por defecto es false)
  private boolean esTitular;



  /**
   * Crea una nueva instancia de Jugador con sus datos personales, de juego y estadísticas.
   * Realiza validaciones sobre campos obligatorios y valores dentro de rangos permitidos.
   *
   * @param id            Identificador único del jugador.
   * @param nombre        Nombre completo del jugador. No puede ser nulo ni vacío.
   * @param numeroCamiseta Número de camiseta (entre 1 y 99, o -1 si no está asignado).
   * @param posicion      Posición en la cancha (ej. "Defensor", "Delantero"). No puede ser nula ni vacía.
   * @param liga          Nombre de la liga en la que participa.
   * @param altura        Altura en formato decimal con dos decimales (ej. "1.85") o "N/A".
   * @param edad          Edad del jugador.
   * @param goles         Goles anotados.
   * @param asistencias   Asistencias realizadas.
   * @param amarillas     Tarjetas amarillas recibidas.
   * @param rojas         Tarjetas rojas recibidas.
   * @param esTitular    Indica si el jugador es titular en su equipo (por defecto es false).
   *
   * @throws TorneoException Si algún valor no cumple con las restricciones de validación.
   */
  public Jugador(int id, String nombre, int numeroCamiseta, String posicion, String liga, String altura, int edad,
                 int goles, int asistencias, int amarillas, int rojas, boolean esTitular) throws TorneoException {
    // Validación del número de camiseta (excepto si es -1)
    if ((numeroCamiseta != -1) && (numeroCamiseta < 1 || numeroCamiseta > 99)) {
      throw new TorneoException("El número de camiseta debe estar entre 1 y 99");
    }

    // Validación de nombre obligatorio
    if (nombre == null || nombre.trim().isEmpty()) {
      throw new TorneoException("El nombre del jugador no puede estar vacío");
    }

    // Validación de posición obligatoria
    if (posicion == null || posicion.trim().isEmpty()) {
      throw new TorneoException("La posición del jugador no puede estar vacía");
    }

    // Validación de formato de altura
    if (altura != null && !altura.equalsIgnoreCase("N/A") && !altura.matches("\\d+\\.\\d{2}")) {
      throw new TorneoException("La altura debe ser 'N/A' o un valor numérico (ej. 1.85)");
    }

    // Asignación de valores
    this.id = id;
    this.nombre = nombre;
    this.numeroCamiseta = numeroCamiseta;
    this.posicion = posicion;
    this.liga = liga;
    this.altura = altura != null ? altura : "N/A";
    this.edad = edad;
    this.equipo = null; // No pertenece a un equipo al ser creado
    this.goles = goles;
    this.asistencias = asistencias;
    this.amarillas = amarillas;
    this.rojas = rojas;
    this.esTitular = esTitular;
  }



  /**
   * Asocia al jugador a un equipo, si no pertenece ya a otro.
   *
   * @param nuevoEquipo Equipo al que se quiere unir el jugador.
   * @throws TorneoException Si el equipo es nulo o el jugador ya tiene equipo asignado.
   */
  public void unirseAEquipo(Equipo nuevoEquipo) throws TorneoException {
    if (nuevoEquipo == null) {
      throw new TorneoException("El equipo no puede ser nulo");
    }
    if (this.equipo != null) {
      throw new TorneoException("El jugador ya pertenece a un equipo");
    }
    this.equipo = nuevoEquipo;
  }


  /**
   * Elimina la asociación del jugador con su equipo actual.
   *
   * @throws TorneoException Si el jugador no pertenece a ningún equipo.
   */
  public void salirDelEquipo() throws TorneoException {
    if (this.equipo == null) {
      throw new TorneoException("El jugador no pertenece a ningún equipo");
    }
    this.equipo.eliminarJugador(this);
    this.equipo = null;
  }

  /** Incrementa en uno la cantidad de goles del jugador. */
  public void registrarGol() {
    this.goles++;
  }

  /** Incrementa en uno la cantidad de asistencias del jugador. */
  public void registrarAsistencia() {
    this.asistencias++;
  }

  /** Incrementa en uno la cantidad de tarjetas amarillas del jugador. */
  public void registrarAmarilla() {
    this.amarillas++;
  }

  /** Incrementa en uno la cantidad de tarjetas rojas del jugador. */
  public void registrarRoja() {
    this.rojas++;
  }


  /** @return ID del jugador. */
  public int getId() {
    return id;
  }

  /** @return Nombre del jugador. */
  public String getNombre() {
    return nombre;
  }

  /** @return Número de camiseta asignado. */
  public int getNumeroCamiseta() {
    return numeroCamiseta;
  }

  /** @return Liga a la que pertenece. */
  public String getLiga() {
    return liga;
  }

  /** @return Edad del jugador. */
  public int getEdad() {
    return edad;
  }

  /** @return Altura del jugador como cadena. */
  public String getAltura() {
    return altura;
  }

  /** @return Posición en la que juega. */
  public String getPosicion() {
    return posicion;
  }

  /** @return Referencia al equipo actual del jugador, o null si no tiene. */
  public Equipo getEquipo() {
    return equipo;
  }

  /** @return Goles anotados. */
  public int getGoles() {
    return goles;
  }

  /** @return Asistencias realizadas. */
  public int getAsistencias() {
    return asistencias;
  }

  /** @return Tarjetas amarillas acumuladas. */
  public int getAmarillas() {
    return amarillas;
  }

  /** @return Tarjetas rojas acumuladas. */
  public int getRojas() {
    return rojas;
  }

  /** @return true si es titular */
  public boolean isTitular() {
    return esTitular;
  }

  /**
   * Asigna un nuevo número de camiseta al jugador.
   * Permite el valor -1 como indicación de "sin asignar".
   *
   * @param numeroCamiseta Número a asignar (entre 1 y 99, o -1).
   * @throws TorneoException Si el número está fuera de rango válido.
   */
  public void setNumeroCamiseta(int numeroCamiseta) throws TorneoException {
    if ((numeroCamiseta != -1) && (numeroCamiseta < 1 || numeroCamiseta > 99)) {
      throw new TorneoException("El número de camiseta debe estar entre 1 y 99");
    }
    this.numeroCamiseta = numeroCamiseta;
  }


  /**
   * Devuelve una representación en cadena del jugador, incluyendo nombre, número,
   * posición y nombre del equipo (si pertenece a alguno).
   *
   * @return Cadena con formato: "Nombre #Camiseta (Posición) - Equipo" o " (Sin equipo)".
   */
  @Override
  public String toString() {
    String equipoStr = (equipo != null) ? " - " + equipo.getNombre() : " (Sin equipo)";
    return nombre + " #" + numeroCamiseta + " (" + posicion + ")" + equipoStr;
  }
}