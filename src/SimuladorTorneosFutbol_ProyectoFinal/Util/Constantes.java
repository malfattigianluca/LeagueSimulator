package SimuladorTorneosFutbol_ProyectoFinal.Util;

/**
 * Clase utilitaria que contiene constantes globales utilizadas en el sistema de gestión de torneos.
 *
 * Esta clase permite centralizar la configuración estática del sistema,
 * facilitando el mantenimiento, la reutilización y la coherencia en los valores utilizados.
 */
public class Constantes {

  // === Límites de equipos para la creación de torneos ===

  /** Mínimo de equipos requerido para iniciar un torneo. */
  public static final int MIN_EQUIPOS_TORNEO = 2;

  /** Máximo de equipos permitido en un torneo. */
  public static final int MAX_EQUIPOS_TORNEO = 32;

  // === Rango de goles posibles en una simulación ===

  /** Goles mínimos posibles por equipo en un partido (puede ser 0). */
  public static final int MIN_GOLES = 0;

  /** Goles máximos posibles por equipo en un partido (límite para evitar resultados absurdos). */
  public static final int MAX_GOLES = 20;

  // === Ligas predefinidas disponibles para seleccionar equipos ===

  /** Listado de ligas disponibles en el sistema, usadas para agrupar equipos. */
  public static final String[] LIGAS_DISPONIBLES = {
          "Premier League",
          "LaLiga",
          "Liga Argentina de Futbol",
          "Serie A",
          "Ligue 1"
  };

  // === Tipos de torneo admitidos en el sistema ===

  /** Clave para identificar torneos con fase de grupos. */
  public static final String TORNEO_GRUPOS = "GRUPOS";

  /** Clave para identificar torneos de eliminación directa. */
  public static final String TORNEO_ELIMINACION = "ELIMINACION";

  /** Clave para identificar torneos mixtos (grupos + eliminación). */
  public static final String TORNEO_MIXTO = "MIXTO";
}
