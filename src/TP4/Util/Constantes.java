package TP4.Util;

public class Constantes {
  public static final int MIN_EQUIPOS_TORNEO = 2;
  public static final int MAX_EQUIPOS_TORNEO = 32;

  public static final int MIN_GOLES = 0;
  public static final int MAX_GOLES = 10;

  public static final String[] LIGAS_DISPONIBLES = {
      "Primera División Argentina",
      "Premier League",
      "LaLiga",
      "Serie A",
      "Bundesliga"
  };

  public static final String TORNEO_GRUPOS = "Fase de Grupos";
  public static final String TORNEO_ELIMINACION = "Eliminación Directa";
  public static final String TORNEO_MIXTO = "Mixto";

  private Constantes() {
  }
}