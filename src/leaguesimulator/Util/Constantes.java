package leaguesimulator.Util;

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

  // === Orden canónico de posiciones en el campo (de portero a delantero) ===
  // Usado tanto para ordenar jugadores visualmente como para calcular probabilidades.
  // Los valores reflejan la jerarquía posicional: menor = más defensivo.

  public static final java.util.Map<String, Double> ORDEN_POSICIONES;
  static {
    java.util.Map<String, Double> m = new java.util.LinkedHashMap<>();
    m.put("portero",              0.001);
    m.put("defensa central",      0.010);
    m.put("lateral derecho",      0.020);
    m.put("lateral izquierdo",    0.020);
    m.put("pivote",               0.030);
    m.put("mediocentro",          0.050);
    m.put("interior derecho",     0.080);
    m.put("interior izquierdo",   0.080);
    m.put("mediocentro ofensivo", 0.100);
    m.put("mediapunta",           0.130);
    m.put("extremo derecho",      0.180);
    m.put("extremo izquierdo",    0.180);
    m.put("delantero centro",     0.350);
    ORDEN_POSICIONES = java.util.Collections.unmodifiableMap(m);
  }

  // === Tipos de torneo admitidos en el sistema ===

  /** Clave para identificar torneos con fase de grupos. */
  public static final String TORNEO_GRUPOS = "GRUPOS";

  /** Clave para identificar torneos de eliminación directa. */
  public static final String TORNEO_ELIMINACION = "ELIMINACION";

  /** Clave para identificar torneos mixtos (grupos + eliminación). */
  public static final String TORNEO_MIXTO = "MIXTO";
}



