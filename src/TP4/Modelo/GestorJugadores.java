package TP4.Modelo;

import TP4.TDA.ConjuntoGenericoTDA;
import TP4.Implementacion.ConjuntoGenericoImpl;
import TP4.Excepciones.TorneoException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Clase GestorJugadores.
 * Se encarga de gestionar la carga, creación, asociación y búsqueda de jugadores.
 */
public class GestorJugadores {
  // Instancia singleton de GestorJugadores para asegurar una única instancia.
  private static GestorJugadores instancia;

  // Conjunto genérico que almacena los jugadores registrados.
  private ConjuntoGenericoTDA<Jugador> jugadores;

  // Identificador único para cada jugador.
  public int idJugador;

  // Referencia al gestor de equipos para la asociación de jugadores a equipos.
  private GestorEquipos gestorEquipos;

  /**
   * Constructor de la clase GestorJugadores.
   * Inicializa el conjunto de jugadores, el identificador y establece el gestor de equipos.
   *
   * @param gestorEquipos Instancia del gestor de equipos para asociar jugadores.
   */
  public GestorJugadores(GestorEquipos gestorEquipos) {
    this.jugadores = new ConjuntoGenericoImpl<>();
    this.idJugador = 1;
    this.gestorEquipos = gestorEquipos;
  }

  /**
   * Devuelve la instancia singleton de GestorJugadores.
   * Si no existe, la crea utilizando un nuevo GestorEquipos.
   *
   * @return Instancia única de GestorJugadores.
   */
  public static GestorJugadores getInstancia() {
    if (instancia == null) {
      instancia = new GestorJugadores(new GestorEquipos());
    }
    return instancia;
  }

  /**
   * Carga los jugadores desde un archivo especificado.
   * Cada línea del archivo debe tener 11 datos separados por punto y coma (;).
   *
   * @param rutaArchivo Ruta del archivo con la información de los jugadores.
   * @throws TorneoException Si ocurre algún error al leer o procesar el archivo.
   */
  public void cargarJugadores(String rutaArchivo) throws TorneoException {
    try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
      String linea;
      int numeroLinea = 0;
      // Se lee el archivo línea por línea.
      while ((linea = reader.readLine()) != null) {
        numeroLinea++;
        // Se omiten las líneas vacías.
        if (linea.trim().isEmpty()) {
          continue;
        }
        // Se separa la línea en datos usando ";" como separador.
        String[] datos = linea.split(";");
        // Se valida que la línea contenga exactamente 11 datos.
        if (datos.length == 11) {
          try {
            // Se extraen y limpian los datos.
            String liga = datos[0].trim();
            String nombreEquipo = datos[1].trim();
            String nombreJugador = datos[2].trim();
            String posicion = datos[3].trim();
            int numeroCamiseta;
            // Se asigna -1 cuando no hay dorsal asignado.
            if (datos[4].equals("-")) {
              numeroCamiseta = -1;
            } else {
              numeroCamiseta = Integer.parseInt(datos[4].trim());
            }
            int edad = Integer.parseInt(datos[5].trim());
            String altura = datos[6].trim();
            // Si la altura es "Desconocido", se establece como "N/A".
            if (altura.equalsIgnoreCase("Desconocido")) {
              altura = "N/A";
            } else {
              // Se normaliza el valor de la altura.
              altura = altura.replace(",", ".").replace("m", "");
            }
            int goles = Integer.parseInt(datos[7].trim());
            int asistencias = Integer.parseInt(datos[8].trim());
            int rojas = Integer.parseInt(datos[9].trim());
            int amarillas = Integer.parseInt(datos[10].trim());

            // Creación del jugador.
            Jugador jugador;
            try {
              jugador = new Jugador(idJugador++, nombreJugador, numeroCamiseta, posicion, liga, altura, edad,
                      goles, asistencias, amarillas, rojas);
            } catch (TorneoException e) {
              System.err.println(
                      "Error en la línea " + numeroLinea + ": " + e.getMessage() + " para el jugador " + nombreJugador);
              continue; // Se omite el jugador si ocurre un error (por ejemplo, número de camiseta inválido).
            }
            // Se inserta el jugador en el conjunto.
            jugadores.insertar(jugador);

            // Asociación del jugador con su equipo.
            boolean equipoEncontrado = false;
            for (List<Equipo> equipos : gestorEquipos.getEquiposPorLiga().values()) {
              for (Equipo equipo : equipos) {
                // Se verifica que el equipo coincida por nombre y liga.
                if (equipo.getNombre().equalsIgnoreCase(nombreEquipo) && equipo.getLiga().equalsIgnoreCase(liga)) {
                  try {
                    int intentos = 0;
                    boolean anadido = false;
                    // Se intenta asignar un número de camiseta único.
                    while (!anadido && intentos < 100) {
                      try {
                        equipo.agregarJugador(jugador);
                        anadido = true;
                        equipoEncontrado = true;
                      } catch (TorneoException e) {
                        // Si el número de camiseta ya está en uso, se incrementa y se intenta de nuevo.
                        if (e.getMessage().contains("ya está en uso")) {
                          numeroCamiseta++;
                          try {
                            jugador.setNumeroCamiseta(numeroCamiseta);
                          } catch (TorneoException ex) {
                            System.err.println("Error al asignar camiseta para " + nombreJugador + ": " + ex.getMessage());
                            break;
                          }
                          intentos++;
                        } else {
                          throw e;
                        }
                      }
                    }
                    // Si tras varios intentos no se pudo asignar, se elimina el jugador.
                    if (!anadido) {
                      System.err.println("No se pudo asignar un número de camiseta único para " + nombreJugador
                              + " en la línea " + numeroLinea);
                      idJugador--;
                      jugadores.eliminar(jugador);
                    }
                  } catch (TorneoException e) {
                    System.err.println("Error en la línea " + numeroLinea + ": " + e.getMessage());
                    idJugador--;
                    jugadores.eliminar(jugador);
                  }
                  break;
                }
              }
              if (equipoEncontrado)
                break;
            }
            // Si no se encontró el equipo, se elimina el jugador y se informa el error.
            if (!equipoEncontrado) {
              System.err.println("Equipo no encontrado para el jugador en la línea " + numeroLinea + ": " + nombreEquipo);
              idJugador--;
              jugadores.eliminar(jugador);
            }
          } catch (NumberFormatException e) {
            System.err.println(
                    "Error en la línea " + numeroLinea + ": Formato inválido en los datos numéricos para " + linea);
            continue;
          }
        } else {
          System.err.println("Formato inválido en la línea " + numeroLinea + ": " + linea);
          throw new TorneoException("Formato inválido en la línea " + numeroLinea);
        }
      }
    } catch (IOException e) {
      throw new TorneoException("Error al leer el archivo de jugadores: " + e.getMessage());
    }
  }

  /**
   * Muestra en consola información básica de todos los jugadores.
   */
  public void mostrarJugadores() {
    for (Jugador j : jugadores.getVertices()) {
      System.out.println("ID: " + j.getId() + " | Nombre: " + j.getNombre() +
              " | Nº Camiseta: " + j.getNumeroCamiseta() +
              " | Posición: " + j.getPosicion() +
              (j.getEquipo() != null ? " | Equipo: " + j.getEquipo().getNombre() : ""));
    }
  }

  /**
   * Busca jugadores cuyo nombre contenga el fragmento especificado (búsqueda parcial).
   *
   * @param fragmento Fragmento del nombre a buscar.
   * @return Lista de jugadores que coinciden con el fragmento.
   */
  public List<Jugador> buscarJugadoresPorNombreParcial(String fragmento) {
    List<Jugador> resultados = new ArrayList<>();
    String fragmentoLower = fragmento.toLowerCase();
    for (Jugador jugador : jugadores.getVertices()) {
      String nombre = jugador.getNombre();
      if (nombre != null && nombre.toLowerCase().contains(fragmentoLower)) {
        resultados.add(jugador);
      }
    }
    return resultados;
  }

  /**
   * Muestra en consola una lista de jugadores, utilizando la función que imprime la información detallada.
   *
   * @param jugadores Lista de jugadores a mostrar.
   */
  public void mostrarListaDeJugadores(List<Jugador> jugadores) {
    if (jugadores.isEmpty()) {
      System.out.println("No se encontraron jugadores.");
      return;
    }
    for (Jugador j : jugadores) {
      mostrarInformacionJugador(j);
    }
  }

  /**
   * Muestra información detallada de un jugador.
   *
   * @param j Jugador del cual se mostrará la información.
   */
  private void mostrarInformacionJugador(Jugador j) {
    System.out.println("\n=== Información del Jugador ===");
    System.out.println("Nombre: " + j.getNombre());
    System.out.println("Número: " + (j.getNumeroCamiseta() == -1 ? "Sin dorsal" : j.getNumeroCamiseta()));
    System.out.println("Posición: " + j.getPosicion());
    System.out.println("Edad: " + j.getEdad());
    System.out.println("Altura: " + j.getAltura());
    System.out.println("Equipo: " + (j.getEquipo() != null ? j.getEquipo().getNombre() : "Sin equipo"));
    System.out.println("Liga: " + j.getLiga());
    System.out.println("Estadísticas:");
    System.out.println("  - Goles: " + j.getGoles());
    System.out.println("  - Asistencias: " + j.getAsistencias());
    System.out.println("  - Tarjetas amarillas: " + j.getAmarillas());
    System.out.println("  - Tarjetas rojas: " + j.getRojas());
    System.out.println("=============================");
  }

  /**
   * Retorna el conjunto que contiene todos los jugadores.
   *
   * @return Conjunto de jugadores.
   */
  public ConjuntoGenericoTDA<Jugador> getJugadores() {
    return jugadores;
  }


  /**
   *   Los siguientes metodos permiten crear y eliminar jugadores, pero no estan invocados en el main. Se dejan para
   *    futuras versiones o pruebas unitarias.
   *
   *
   * /


  /**
   * Crea un nuevo jugador y lo inserta en el conjunto.
   *
   * @param nombre          Nombre del jugador.
   * @param numeroCamiseta  Número de camiseta del jugador.
   * @param posicion        Posición en el campo.
   * @param liga            Liga a la que pertenece.
   * @param altura          Altura del jugador.
   * @param edad            Edad del jugador.
   * @param goles           Número de goles.
   * @param asistencias     Número de asistencias.
   * @param amarillas       Tarjetas amarillas.
   * @param rojas           Tarjetas rojas.
   * @return El jugador creado.
   * @throws TorneoException Si ocurre algún error durante la creación.
   */
  public Jugador crearJugador(String nombre, int numeroCamiseta, String posicion, String liga, String altura, int edad,
                              int goles, int asistencias, int amarillas, int rojas) throws TorneoException {
    Jugador nuevoJugador = new Jugador(++idJugador, nombre, numeroCamiseta, posicion, liga, altura, edad, goles,
            asistencias, amarillas, rojas);
    jugadores.insertar(nuevoJugador);
    return nuevoJugador;
  }

  /**
   * Elimina un jugador del conjunto, siempre y cuando no esté asociado a ningún equipo.
   *
   * @param jugador Jugador a eliminar.
   */
  public void eliminarJugador(Jugador jugador) {
    if (jugador != null && jugador.getEquipo() == null) {
      jugadores.eliminar(jugador);
    }
  }

  /**
   * Metodo para agregar un jugador manualmente mediante entrada por consola.
   * Se solicitan, validan y procesan los datos del jugador.
   * Además, se asocia el jugador al equipo correspondiente según la liga y el nombre del equipo.
   *
   * @param scanner Scanner para leer la entrada por consola.
   * @throws TorneoException Si ocurre algún error en la validación o al asociar el jugador al equipo.
   */
  public void agregarJugadorManualmente(Scanner scanner) throws TorneoException {
    int goles = 0, asistencias = 0, amarillas = 0, rojas = 0;
    String altura;

    System.out.println("\n--- Agregar Jugador Manualmente ---");

    // Se solicita el nombre del jugador.
    System.out.print("Ingrese el nombre del jugador: ");
    String nombreJugador = scanner.nextLine().trim();

    // Se solicita y valida el número de camiseta.
    System.out.print("Ingrese el número de camiseta (1-99): ");
    int numeroCamiseta;
    String camisetaInput = scanner.nextLine().trim();
    if (camisetaInput.equals("-")) {
      throw new TorneoException("El número de camiseta no puede ser '-'." );
    }
    try {
      numeroCamiseta = Integer.parseInt(camisetaInput);
    } catch (NumberFormatException e) {
      throw new TorneoException("Número de camiseta inválido.");
    }

    // Se solicita la posición del jugador.
    System.out.print("Ingrese la posición: ");
    String posicion = scanner.nextLine().trim();

    // Se solicita la liga.
    System.out.print("Ingrese la liga: ");
    String liga = scanner.nextLine().trim();

    // Se solicita el nombre del equipo.
    System.out.print("Ingrese el nombre del equipo: ");
    String equipoNombre = scanner.nextLine().trim();

    // Se solicita y valida la edad.
    System.out.print("Ingrese la edad: ");
    int edad;
    String edadInput = scanner.nextLine().trim();
    try {
      edad = Integer.parseInt(edadInput);
    } catch (NumberFormatException e) {
      throw new TorneoException("Edad inválida.");
    }

    // Se solicita y valida la altura.
    System.out.print("Ingrese la altura (en metros, ej. 1.85, o 'N/A'): ");
    altura = scanner.nextLine().trim();
    if (altura.equalsIgnoreCase("Desconocido")) {
      altura = "N/A";
    } else if (!altura.equalsIgnoreCase("N/A")) {
      altura = altura.replace(",", ".").replace("m", "");
      if (!altura.matches("\\d+\\.\\d{2}")) {
        throw new TorneoException("La altura debe ser 'N/A' o un valor numérico (ej. 1.85)");
      }
    }

    // Creación del jugador con los datos ingresados.
    Jugador jugador = new Jugador(idJugador++, nombreJugador, numeroCamiseta, posicion, liga, altura, edad,
            goles, asistencias, amarillas, rojas);

    // Se inserta el jugador en el conjunto de jugadores.
    jugadores.insertar(jugador);

    // Se busca y asocia el jugador con el equipo correspondiente.
    boolean equipoEncontrado = false;
    for (List<Equipo> equipos : gestorEquipos.getEquiposPorLiga().values()) {
      for (Equipo equipo : equipos) {
        if (equipo.getNombre().equalsIgnoreCase(equipoNombre) && equipo.getLiga().equalsIgnoreCase(liga)) {
          try {
            equipo.agregarJugador(jugador);
            equipoEncontrado = true;
          } catch (TorneoException e) {
            System.err.println("Error al agregar jugador: " + e.getMessage());
            idJugador--;
            jugadores.eliminar(jugador);
            throw e;
          }
          break;
        }
      }
      if (equipoEncontrado)
        break;
    }
    // Si no se encontró el equipo, se revierte la inserción del jugador.
    if (!equipoEncontrado) {
      idJugador--;
      jugadores.eliminar(jugador);
      throw new TorneoException("Equipo no encontrado: " + equipoNombre + " en la liga " + liga);
    }

    System.out.println("Jugador agregado exitosamente: " + jugador);
  }

  /**
   * Devuelve la cantidad de jugadores registrados.
   *
   * @return Número total de jugadores en el conjunto.
   */
  public int cantidadJugadores() {
    return jugadores.tamanio();
  }
}
