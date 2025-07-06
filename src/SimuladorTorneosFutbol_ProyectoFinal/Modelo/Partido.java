  package SimuladorTorneosFutbol_ProyectoFinal.Modelo;

  import SimuladorTorneosFutbol_ProyectoFinal.Excepciones.TorneoException;
  import SimuladorTorneosFutbol_ProyectoFinal.Util.Validador;

  import java.util.*;
  import java.util.function.Function;

  /**
   * Representa un partido de fútbol entre dos equipos: local y visitante.
   * Contiene información sobre el resultado del encuentro, incluyendo los goles anotados,
   * los goleadores, asistentes, y los jugadores que recibieron tarjetas.
   *
   * Esta clase permite registrar el desarrollo completo de un partido y sirve como base
   * para estadísticas individuales y de equipos en torneos o simulaciones.
   *
   * Los jugadores que participan en eventos (goles, asistencias, tarjetas) se almacenan
   * por separado según el tipo de acción y el equipo al que pertenecen.
   *
   * Se asume que un partido no puede tener más de un resultado asociado (no hay desempates).
   *
   * @author
   */
  public class Partido {
    // Equipo que juega como local en el partido
    private Equipo local;

    // Equipo que juega como visitante en el partido
    private Equipo visitante;

    // Goles anotados por el equipo local
    private int golesLocal;

    // Goles anotados por el equipo visitante
    private int golesVisitante;

    // Lista de jugadores del equipo local que anotaron goles
    private List<Jugador> goleadoresLocal;

    // Lista de jugadores del equipo visitante que anotaron goles
    private List<Jugador> goleadoresVisitante;

    // Lista de jugadores del equipo local que realizaron asistencias
    private List<Jugador> asistentesLocal;

    // Lista de jugadores del equipo visitante que realizaron asistencias
    private List<Jugador> asistentesVisitante;

    // Lista de jugadores que recibieron tarjeta amarilla (de ambos equipos)
    private List<Jugador> jugadoresAmarillas;

    // Lista de jugadores que recibieron tarjeta roja (de ambos equipos)
    private List<Jugador> jugadoresRojas;

    // Equipo ganador del partido
    private Equipo ganador;


    /**
     * Crea un nuevo partido entre dos equipos, registrando el resultado y generando estadísticas de jugadores.
     * <p>
     * Este constructor:
     * - Valida que los equipos no sean nulos.
     * - Valida los goles anotados por cada equipo.
     * - Inicializa las listas de eventos (goles, asistencias, tarjetas).
     * - Actualiza las estadísticas de los equipos (goles a favor/en contra, puntos, etc.).
     * - Simula estadísticas individuales de los jugadores (goleadores, asistentes, tarjetas).
     *
     * @param local          Equipo que juega como local.
     * @param visitante      Equipo que juega como visitante.
     * @param golesLocal     Goles marcados por el equipo local.
     * @param golesVisitante Goles marcados por el equipo visitante.
     * @throws TorneoException Si los equipos son nulos o los goles tienen valores inválidos.
     */
    public Partido(Equipo local, Equipo visitante, int golesLocal, int golesVisitante) throws TorneoException {
      // Validaciones de entrada
      if (local == null || visitante == null) {
        throw new TorneoException("Los equipos no pueden ser nulos");
      }
      Validador.validarGoles(golesLocal);
      Validador.validarGoles(golesVisitante);

      // Asignación de atributos principales
      this.local = local;
      this.visitante = visitante;
      this.golesLocal = golesLocal;
      this.golesVisitante = golesVisitante;
      this.ganador = determinarGanador(); // Determina el ganador al crear el partido

      // Inicialización de listas de eventos por jugador
      this.goleadoresLocal = new ArrayList<>();
      this.goleadoresVisitante = new ArrayList<>();
      this.asistentesLocal = new ArrayList<>();
      this.asistentesVisitante = new ArrayList<>();
      this.jugadoresAmarillas = new ArrayList<>();
      this.jugadoresRojas = new ArrayList<>();

      // Actualiza las estadísticas del equipo (partidos jugados, puntos, goles, etc.)
      local.registrarPartido(golesLocal, golesVisitante);
      visitante.registrarPartido(golesVisitante, golesLocal);

      // Genera automáticamente estadísticas de jugadores (simulación)
      simularEstadisticasJugadores();
    }

    public Partido(Equipo local, Equipo visitante) throws TorneoException {
      this(local, visitante, 0, 0); // Crea un partido inicializado en 0 a 0
    }

    /**
     * Simula estadísticas de jugadores para el partido actual.
     * <p>
     * Este metodo utiliza generación aleatoria para:
     * - Asignar goleadores a partir de los goles registrados por cada equipo.
     * - Asignar asistentes con un 70% de probabilidad por gol.
     * - Asignar tarjetas amarillas (0 a 3 por equipo) y rojas (0 a 1 por equipo).
     * <p>
     * Las estadísticas actualizan los objetos {@link Jugador} directamente
     * (goles, asistencias, amarillas, rojas) y también se registran en las listas del partido.
     *
     * @throws TorneoException Si ocurre un error en la modificación de los jugadores.
     */
    void simularEstadisticasJugadores() throws TorneoException {
      Random random = new Random();

      // Filtrar jugadores titulares de cada equipo
      List<Jugador> jugadoresLocal = local.getJugadores().getVertices().stream()
              .filter(Jugador::isTitular).toList();
      List<Jugador> jugadoresVisitante = visitante.getJugadores().getVertices().stream()
              .filter(Jugador::isTitular).toList();

      // 🔹 Simular goles
      for (int i = 0; i < golesLocal; i++) {
        Jugador goleador = elegirJugadorPorProbabilidad(jugadoresLocal, this::obtenerProbabilidadGol);
        if (goleador != null) {
          goleador.registrarGol();
          goleadoresLocal.add(goleador);
        }
      }
      for (int i = 0; i < golesVisitante; i++) {
        Jugador goleador = elegirJugadorPorProbabilidad(jugadoresVisitante, this::obtenerProbabilidadGol);
        if (goleador != null) {
          goleador.registrarGol();
          goleadoresVisitante.add(goleador);
        }
      }

      // 🔹 Simular asistencias (70% de probabilidad por gol)
      for (int i = 0; i < golesLocal; i++) {
        if (random.nextDouble() < 0.7) {
          Jugador asistente = elegirJugadorPorProbabilidad(jugadoresLocal, this::obtenerProbabilidadAsistencia);
          if (asistente != null) {
            asistente.registrarAsistencia();
            asistentesLocal.add(asistente);
          }
        }
      }
      for (int i = 0; i < golesVisitante; i++) {
        if (random.nextDouble() < 0.7) {
          Jugador asistente = elegirJugadorPorProbabilidad(jugadoresVisitante, this::obtenerProbabilidadAsistencia);
          if (asistente != null) {
            asistente.registrarAsistencia();
            asistentesVisitante.add(asistente);
          }
        }
      }

      // 🔹 Simular tarjetas amarillas
      for (Jugador jugador : jugadoresLocal) {
        if (random.nextDouble() < obtenerProbabilidadAmarilla(jugador.getPosicion())) {
          jugador.registrarAmarilla();
          jugadoresAmarillas.add(jugador);
        }
      }
      for (Jugador jugador : jugadoresVisitante) {
        if (random.nextDouble() < obtenerProbabilidadAmarilla(jugador.getPosicion())) {
          jugador.registrarAmarilla();
          jugadoresAmarillas.add(jugador);
        }
      }

      // 🔹 Simular tarjetas rojas
      for (Jugador jugador : jugadoresLocal) {
        if (random.nextDouble() < obtenerProbabilidadRoja(jugador.getPosicion())) {
          jugador.registrarRoja();
          jugadoresRojas.add(jugador);
        }
      }
      for (Jugador jugador : jugadoresVisitante) {
        if (random.nextDouble() < obtenerProbabilidadRoja(jugador.getPosicion())) {
          jugador.registrarRoja();
          jugadoresRojas.add(jugador);
        }
      }
    }


    /**
     * Elige un jugador de una lista según una probabilidad basada en su posición.
     * Utiliza una función que recibe la posición del jugador y devuelve una probabilidad.
     *
     * @param jugadores Lista de jugadores a evaluar.
     * @param probabilidadFunc Función que recibe la posición del jugador y devuelve la probabilidad de selección.
     * @return Un jugador seleccionado aleatoriamente según la probabilidad, o null si no hay candidatos.
     */
    private Jugador elegirJugadorPorProbabilidad(List<Jugador> jugadores, Function<String, Double> probabilidadFunc) {
      Random random = new Random();
      List<Jugador> candidatos = new ArrayList<>();
      for (Jugador j : jugadores) {
        double prob = probabilidadFunc.apply(j.getPosicion());
        if (random.nextDouble() < prob) {
          candidatos.add(j);
        }
      }
      return candidatos.isEmpty() ? null : candidatos.get(random.nextInt(candidatos.size()));
    }


    /**
     * Obtiene la probabilidad de que un jugador anote un gol según su posición.
     * Las probabilidades son aproximadas y pueden ajustarse según el contexto del juego.
     *
     * @param posicion Posición del jugador (ej. "portero", "defensa central", etc.).
     * @return Probabilidad de anotar un gol para esa posición.
     */
    private double obtenerProbabilidadGol(String posicion) {
      return switch (posicion.toLowerCase()) {
        case "portero" -> 0.001;
        case "defensa central" -> 0.01;
        case "lateral derecho", "lateral izquierdo" -> 0.02;
        case "pivote" -> 0.03;
        case "mediocentro" -> 0.05;
        case "interior derecho", "interior izquierdo" -> 0.08;
        case "mediocentro ofensivo" -> 0.10;
        case "mediapunta" -> 0.13;
        case "extremo derecho", "extremo izquierdo" -> 0.18;
        case "delantero centro" -> 0.35;
        default -> 0.02;
      };
    }



    /**
     * Obtiene la probabilidad de que un jugador realice una asistencia según su posición.
     * Las probabilidades son aproximadas y pueden ajustarse según el contexto del juego.
     *
     * @param posicion Posición del jugador (ej. "portero", "defensa central", etc.).
     * @return Probabilidad de asistencia para esa posición.
     */
    private double obtenerProbabilidadAsistencia(String posicion) {
      return switch (posicion.toLowerCase()) {
        case "portero" -> 0.001;
        case "defensa central" -> 0.02;
        case "lateral derecho", "lateral izquierdo" -> 0.08;
        case "pivote" -> 0.10;
        case "mediocentro" -> 0.20;
        case "mediocentro ofensivo", "interior derecho", "interior izquierdo" -> 0.32;
        case "mediapunta", "extremo derecho", "extremo izquierdo" -> 0.25;
        case "delantero centro" -> 0.05;
        default -> 0.10;
      };
    }



    /**
     * Obtiene la probabilidad de que un jugador reciba una tarjeta amarilla según su posición.
     * Las probabilidades son aproximadas y pueden ajustarse según el contexto del juego.
     *
     * @param posicion Posición del jugador (ej. "portero", "defensa central", etc.).
     * @return Probabilidad de recibir tarjeta amarilla para esa posición.
     */
    private double obtenerProbabilidadAmarilla(String posicion) {
      return switch (posicion.toLowerCase()) {
        case "portero" -> 0.02;
        case "defensa central" -> 0.15;
        case "lateral derecho", "lateral izquierdo" -> 0.10;
        case "pivote" -> 0.12;
        case "mediocentro" -> 0.08;
        case "mediocentro ofensivo" -> 0.05;
        case "interior derecho", "interior izquierdo" -> 0.06;
        case "mediapunta" -> 0.04;
        case "extremo derecho", "extremo izquierdo" -> 0.03;
        case "delantero centro" -> 0.02;
        default -> 0.05;
      };
    }


    /**
     * Obtiene la probabilidad de que un jugador reciba una tarjeta roja según su posición.
     * Las probabilidades son aproximadas y pueden ajustarse según el contexto del juego.
     *
     * @param posicion Posición del jugador (ej. "portero", "defensa central", etc.).
     * @return Probabilidad de recibir tarjeta roja para esa posición.
     */
    private double obtenerProbabilidadRoja(String posicion) {
      return switch (posicion.toLowerCase()) {
        case "portero" -> 0.002;
        case "defensa central" -> 0.015;
        case "lateral derecho", "lateral izquierdo" -> 0.010;
        case "pivote" -> 0.012;
        case "mediocentro" -> 0.008;
        case "mediocentro ofensivo" -> 0.003;
        case "interior derecho", "interior izquierdo" -> 0.004;
        case "mediapunta" -> 0.002;
        case "extremo derecho", "extremo izquierdo" -> 0.001;
        case "delantero centro" -> 0.001;
        default -> 0.005;
      };
    }


    /**
       * Determina el ganador del partido basándose en los goles anotados.
       * Si el equipo local tiene más goles, es el ganador.
       * Si el equipo visitante tiene más goles, es el ganador.
       * Si ambos equipos tienen la misma cantidad de goles, no hay ganador (empate).
       *
       * @return El equipo ganador o null si hay empate.
       */
    private Equipo determinarGanador() {
        if (local == null || visitante == null) return null;
        if (golesLocal > golesVisitante) return local;
        if (golesVisitante > golesLocal) return visitante;
        return null; // Empate
    }


    /**
     * Obtiene el ganador del partido considerando un desempate por ELO si es necesario.
     * Si ya hay un ganador definido, lo devuelve directamente.
     * Si hay empate, se utiliza el ELO de los equipos para determinar el ganador.
     *
     * @return El equipo ganador considerando el desempate por ELO, o null si no se puede determinar.
     */
    public Equipo getGanadorConDesempate() {
      if (ganador != null) return ganador;
      // Desempate por ELO si hay empate y no se definió aún
      if (local != null && visitante != null) {
        return local.getElo() >= visitante.getElo() ? local : visitante;
      }
      return null;
    }


    /**
     * @return Equipo que ganó el partido, o null si fue un empate.
     */
    public Equipo getGanador() {
      return ganador;
    }


    /**
     * @return Lista de jugadores del equipo local que anotaron goles en el partido.
     */
    public List<Jugador> getGoleadoresLocal() {
      return new ArrayList<>(goleadoresLocal);
    }

    /**
     * @return Lista de jugadores del equipo visitante que anotaron goles en el partido.
     */
    public List<Jugador> getGoleadoresVisitante() {
      return new ArrayList<>(goleadoresVisitante);
    }

    /**
     * @return Lista de jugadores del equipo local que realizaron asistencias.
     */
    public List<Jugador> getAsistentesLocal() {
      return new ArrayList<>(asistentesLocal);
    }

    /**
     * @return Lista de jugadores del equipo visitante que realizaron asistencias.
     */
    public List<Jugador> getAsistentesVisitante() {
      return new ArrayList<>(asistentesVisitante);
    }

    /**
     * @return Lista de jugadores (locales o visitantes) que recibieron tarjetas amarillas.
     */
    public List<Jugador> getJugadoresAmarillas() {
      return new ArrayList<>(jugadoresAmarillas);
    }

    /**
     * @return Lista de jugadores (locales o visitantes) que recibieron tarjetas rojas.
     */
    public List<Jugador> getJugadoresRojas() {
      return new ArrayList<>(jugadoresRojas);
    }

    /**
     * @return Equipo que jugó como local.
     */
    public Equipo getLocal() {
      return local;
    }

    /**
     * @return Equipo que jugó como visitante.
     */
    public Equipo getVisitante() {
      return visitante;
    }

    /**
     * @return Cantidad de goles anotados por el equipo local.
     */
    public int getGolesLocal() {
      return golesLocal;
    }

    /**
     * @return Cantidad de goles anotados por el equipo visitante.
     */
    public int getGolesVisitante() {
      return golesVisitante;
    }


    /**
     * Devuelve una representación detallada del partido en forma de texto.
     *
     * Incluye:
     * - Resultado (nombre de los equipos y goles).
     * - Lista de goleadores y equipo correspondiente.
     * - Lista de asistentes.
     * - Jugadores con tarjetas amarillas y rojas.
     *
     * Este metodo es útil para mostrar el resumen de un partido en consola o informes.
     *
     * @return Cadena con el detalle del partido, incluyendo eventos destacados.
     */
    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();

      // Resultado principal
      sb.append(String.format("%s %d - %d %s%n",
              local.getNombre(), golesLocal, golesVisitante, visitante.getNombre()));

      // Goleadores
      if (!goleadoresLocal.isEmpty() || !goleadoresVisitante.isEmpty()) {
        sb.append("Goles:\n");
        for (Jugador goleador : goleadoresLocal) {
          sb.append("  ").append(goleador.getNombre())
                  .append(" (").append(local.getNombre()).append(")\n");
        }
        for (Jugador goleador : goleadoresVisitante) {
          sb.append("  ").append(goleador.getNombre())
                  .append(" (").append(visitante.getNombre()).append(")\n");
        }
      }

      // Asistentes
      if (!asistentesLocal.isEmpty() || !asistentesVisitante.isEmpty()) {
        sb.append("Asistencias:\n");
        for (Jugador asistente : asistentesLocal) {
          sb.append("  ").append(asistente.getNombre())
                  .append(" (").append(local.getNombre()).append(")\n");
        }
        for (Jugador asistente : asistentesVisitante) {
          sb.append("  ").append(asistente.getNombre())
                  .append(" (").append(visitante.getNombre()).append(")\n");
        }
      }

      // Amarillas
      if (!jugadoresAmarillas.isEmpty()) {
        sb.append("Tarjetas Amarillas:\n");
        for (Jugador jugador : jugadoresAmarillas) {
          sb.append("  ").append(jugador.getNombre())
                  .append(" (").append(jugador.getEquipo().getNombre()).append(")\n");
        }
      }

      // Rojas
      if (!jugadoresRojas.isEmpty()) {
        sb.append("Tarjetas Rojas:\n");
        for (Jugador jugador : jugadoresRojas) {
          sb.append("  ").append(jugador.getNombre())
                  .append(" (").append(jugador.getEquipo().getNombre()).append(")\n");
        }
      }

      return sb.toString();
    }
  }