package leaguesimulator.Util;

import leaguesimulator.Excepciones.TorneoException;
import leaguesimulator.Modelo.Equipo;
import leaguesimulator.Modelo.Partido;

import java.util.Random;

/**
 * Utilitario que centraliza la lógica de simulación de un partido individual.
 *
 * Antes, esta lógica estaba duplicada en SimuladorLiga y Torneo con valores
 * ligeramente distintos (lambdaTotal 2.7 vs 2.8, ventajaLocal 1.1 vs 1.15).
 * Ahora existe una única fuente de verdad para el algoritmo ELO + Poisson.
 *
 * Responsabilidades:
 * - Calcular probabilidad de victoria por ELO.
 * - Generar goles mediante distribución de Poisson (algoritmo de Knuth).
 * - Actualizar el ELO de ambos equipos tras el partido.
 */
public class SimuladorPartido {

    private static final int    K                    = 100;
    private static final double LAMBDA_TOTAL         = 2.7;
    private static final double VENTAJA_LOCAL        = 1.1;
    private static final double DESVENTAJA_VISITANTE = 0.9;
    private static final double LAMBDA_MIN           = 0.4;
    private static final double LAMBDA_MAX           = 2.5;

    private SimuladorPartido() {}

    /**
     * Simula un partido entre local y visitante usando ELO + Poisson.
     * Actualiza el ELO de ambos equipos como efecto secundario del partido.
     *
     * @param local     Equipo que juega de local.
     * @param visitante Equipo que juega de visitante.
     * @param random    Instancia de Random (inyectable para tests).
     * @return Partido con resultado y estadísticas de jugadores simuladas.
     * @throws TorneoException si los goles generados están fuera del rango permitido.
     */
    public static Partido simular(Equipo local, Equipo visitante, Random random) throws TorneoException {
        double pa = calcularProbabilidadLocal(local, visitante);
        double pb = 1.0 - pa;

        double ajuste          = LAMBDA_TOTAL / (pa * VENTAJA_LOCAL + pb * DESVENTAJA_VISITANTE);
        double lambdaLocal     = acotar(pa * VENTAJA_LOCAL        * ajuste * variacion(random));
        double lambdaVisitante = acotar(pb * DESVENTAJA_VISITANTE * ajuste * variacion(random));

        int golesLocal     = generarGolesPoisson(lambdaLocal,     random);
        int golesVisitante = generarGolesPoisson(lambdaVisitante, random);

        Partido partido = new Partido(local, visitante, golesLocal, golesVisitante);
        actualizarElo(local, visitante, golesLocal, golesVisitante, pa);
        return partido;
    }

    /**
     * Simula un partido creando internamente una instancia Random.
     * Conveniente para uso general; usar la variante con Random para tests.
     */
    public static Partido simular(Equipo local, Equipo visitante) throws TorneoException {
        return simular(local, visitante, new Random());
    }

    /**
     * Calcula la probabilidad de victoria del equipo local según la diferencia de ELO.
     * Formula ELO estándar adaptada al fútbol (divisor 2000 en lugar de 400 del ajedrez).
     *
     * @param local     Equipo local.
     * @param visitante Equipo visitante.
     * @return Probabilidad en [0, 1] de que el local gane.
     */
    public static double calcularProbabilidadLocal(Equipo local, Equipo visitante) {
        return 1.0 / (1.0 + Math.pow(10, (visitante.getElo() - local.getElo()) / 2000.0));
    }

    /**
     * Genera goles usando la distribución de Poisson (algoritmo de Knuth).
     *
     * @param lambda Tasa promedio de goles esperados.
     * @param random Fuente de aleatoriedad.
     * @return Número de goles ≥ 0.
     */
    public static int generarGolesPoisson(double lambda, Random random) {
        double L = Math.exp(-lambda);
        int    k = 0;
        double p = 1.0;
        do {
            k++;
            p *= random.nextDouble();
        } while (p > L);
        return k - 1;
    }

    /**
     * Actualiza el ELO de ambos equipos tras conocer el resultado.
     *
     * @param local          Equipo local.
     * @param visitante      Equipo visitante.
     * @param golesLocal     Goles del local.
     * @param golesVisitante Goles del visitante.
     * @param pa             Probabilidad previa del local (calculada antes del partido).
     */
    public static void actualizarElo(Equipo local, Equipo visitante,
                                     int golesLocal, int golesVisitante, double pa) {
        double ra = golesLocal > golesVisitante ? 1.0 : (golesLocal == golesVisitante ? 0.5 : 0.0);
        local.setElo    ((int) Math.round(local.getElo()     + K * (ra       - pa)));
        visitante.setElo((int) Math.round(visitante.getElo() + K * ((1 - ra) - (1 - pa))));
    }

    // Factor de variación aleatoria ±10% para evitar resultados deterministas.
    private static double variacion(Random random) {
        return 0.9 + random.nextDouble() * 0.2;
    }

    // Acota lambda al rango [LAMBDA_MIN, LAMBDA_MAX].
    private static double acotar(double valor) {
        return Math.max(LAMBDA_MIN, Math.min(valor, LAMBDA_MAX));
    }
}
