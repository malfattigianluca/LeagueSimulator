# LeagueSimulator

Este proyecto en Java implementa un sistema para la simulación de torneos de fútbol. Permite gestionar equipos, jugadores, y simular torneos en diferentes formatos: **liga**, **eliminación directa**, y **torneo mixto** (fase de grupos + eliminación). Fue desarrollado como parte del Proyecto Final de la materia *Programación II*.

---

## 📁 Estructura del Proyecto

LeagueSimulator/
├── src/
│   └── LeagueSimulator/
│       ├── App.java
│       ├── Modelo/
│       ├── Excepciones/
│       └── Util/
├── files/
│   ├── teams.txt
│   └── players.txt

```
LeagueSimulator/
├── App.java                      # Clase principal con menú interactivo
├── teams.txt                    # Archivo de entrada con equipos
├── players.txt                  # Archivo de entrada con jugadores
├── Modelo/                      # Lógica del sistema (Equipo, Jugador, Partido, Torneo, etc.)
├── Implementacion/              # Estructuras de datos personalizadas (colas, árboles, diccionarios)
├── Util/                        # Utilidades (validaciones, constantes, consola)
├── Excepciones/                 # Definición de excepciones personalizadas
├── Propuesta de Trabajo Final...txt  # Enunciado del TP
```

---

## ▶️ Cómo Ejecutar

1. **Requisitos**: JDK 17+ instalado en el sistema.

2. **Compilar**:
   - Windows (recomendado):
     ```bat
     .\compile.bat
     ```
   - Manual (cross-platform):
     ```sh
     javac -d bin -sourcepath src \
       src/leaguesimulator/*.java \
       src/leaguesimulator/Modelo/*.java \
       src/leaguesimulator/Util/*.java \
       src/leaguesimulator/TDA/*.java \
       src/leaguesimulator/Excepciones/*.java \
       src/leaguesimulator/Implementacion/*.java
     ```

3. **Ejecutar**:
   ```sh
   java -cp bin leaguesimulator.App
   ```

4. Si estás usando un entorno como IntelliJ o Eclipse, basta con abrir el proyecto y ejecutar `App.java`.

---

## 🧠 Funcionalidades

- Simulación de torneos en tres modalidades:
  - **Liga**: todos contra todos, ida o ida y vuelta.
  - **Eliminación directa**: con llaves generadas automáticamente (tipo bracket).
  - **Mixto**: fase de grupos y luego eliminación.
- Gestión de:
  - Equipos (nombre, país, ELO, etc.)
  - Jugadores (nombre, posición, estadísticas)
- Registro y visualización de:
  - Partidos y resultados
  - Goleadores, asistencias y tarjetas
  - Tablas de posiciones
  - Árbol de eliminación directa

---

## 🔍 Mecánica de la simulación (estadísticas y probabilidades)

### 🧠 Cómo se determinan los resultados de los partidos

- Los resultados (goles local/visitante) se simulan usando una **distribución de Poisson**.
- La media (λ) de la Poisson se calcula con:
  - Probabilidades basadas en el ELO relativo de cada equipo:
    - `pa = 1 / (1 + 10^{(Elo_visitante - Elo_local) / 2000})`
    - `pb = 1 - pa`
  - Ajuste por ventaja de local:
    - `lambdaLocal  ≈ pa * 1.1 * ajuste`
    - `lambdaVisitante ≈ pb * 0.9 * ajuste`
  - Se aplica variación aleatoria (±10%) y se recortan los valores entre **0.4 y 2.5** para evitar goleadas extremas.

### 🏆 ELO (rating) y desempates

- Cada equipo tiene un **ELO** (1000–3000) cargado desde `teams.txt`.
- El ELO sirve para:
  - Decidir el ganador en caso de empate (`getGanadorConDesempate()`): gana el equipo con mayor ELO.
  - Ajustar la probabilidad de anotar en la simulación de goles.
  - Actualizar el ELO tras cada partido, usando la fórmula clásica:
    - `rating_nuevo = rating_viejo + K * (resultado - expectativa)`
    - Donde `K` = 100, `resultado` = 1/0.5/0 según victoria/empate/derrota, y `expectativa` = pa/pb.

### ⚽ Estadísticas de jugadores (goles, asistencias, tarjetas)

- Una vez que el partido tiene goles definidos, se genera una simulación de eventos individuales:
  1. **Goleadores**: por cada gol, se elige un jugador titular según una probabilidad que depende de su posición.
     - Ejemplo de probabilidades de **anotar** (simplificadas):
       - Delantero centro: 35%
       - Extremo: 18%
       - Mediocentro ofensivo: 10%
       - Mediocentro/mediapunta: 5–13%
       - Defensas: 1–2%
       - Portero: 0.1%
  2. **Asistencias**: por cada gol, hay un 70% de chance de que haya asistente.
     - La selección del asistente también depende de su posición (p.ej., mediocentro ofensivo: 32%, extremo 25%).
  3. **Tarjetas amarillas**: cada jugador titular tiene una probabilidad de recibir amarilla según su posición.
  4. **Tarjetas rojas**: cada jugador titular tiene una probabilidad menor (1–1.5% para defensas, 0.1–0.2% para delanteros, etc.).

- Estas probabilidades se encuentran en `Partido.simularEstadisticasJugadores()` y se aplican de forma independiente para cada jugador titular.

### � Cómo funciona internamente (paso a paso)

1. **Carga de datos inicial**
   - Al iniciar, el sistema lee `teams.txt` y `players.txt`.
   - Los equipos se organizan por liga y se les asigna un ELO inicial.
   - Los jugadores se asocian a su equipo y se definen como titulares o suplentes.

2. **Generación del calendario (liga)**
   - Para una liga, se genera un calendario de enfrentamientos con el método `SimuladorLiga.generarCalendario()`.
   - Se usa un algoritmo de rotación que asegura que cada equipo juegue contra todos los demás.
   - Si se juega ida y vuelta, se invierten las localías en la segunda fase.

3. **Simulación de una jornada**
   - Cuando el usuario selecciona "Simular liga", el método `SimuladorLiga.simularJornada()`:
     1. Selecciona los partidos de la jornada actual.
     2. Calcula probabilidades de gol (ELO + ventaja de local).
     3. Genera goles usando Poisson (`simularGoles(lambda)`).
     4. Crea el objeto `Partido` y actualiza estadísticas de equipos.
     5. Ajusta ELO según resultado (fórmula clásica).

4. **Simulación de eventos individuales (jugadores)**
   - Al crear un `Partido`, se llaman métodos en `Partido.simularEstadisticasJugadores()`.
   - Para cada gol se elige un goleador y, con probabilidad 70%, un asistente.
   - Las tarjetas amarillas/rojas se asignan por probabilidad según la posición del jugador.

5. **Resultados y estadísticas
   - Se actualizan tablas de posiciones y se almacena el historial de partidos.
   - Se puede revisar la tabla de la liga con `SimuladorLiga.mostrarTabla()`.
   - Los mejores goleadores/asistentes se calculan en `GestorEquipos` y `GestorJugadores`.

### 📁 Recorrido rápido por el código (archivos clave)

- **`App.java`**: Punto de entrada y menú principal.
- **`GestorEquipos.java`**: Maneja equipos, clasificación, búsqueda y reporte de estadísticas.
- **`GestorJugadores.java`**: Gestiona jugadores, búsqueda y listado.
- **`SimuladorLiga.java`**: Genera calendario, simula jornadas y actualiza ELO.
- **`Partido.java`**: Simula el desarrollo de un partido y genera estadísticas de jugadores.
- **`Equipo.java`**: Contiene el estado de cada equipo (ELO, goles, puntos, lista de jugadores).

---

## 🧪 Datos de Entrada

### teams.txt (equipos)
Cada línea representa un equipo y se carga automáticamente al iniciar el sistema.
Formato (separador `;`):
```
liga;codigo;nombre;pais;escudo;elo
```
- `liga`: nombre de la liga (ej. `LaLiga`, `Serie A`).
- `codigo`: identificador interno del equipo (no usado en la simulación, solo para lectura).
- `nombre`: nombre del club.
- `pais`: país del club.
- `escudo`: cadena para mostrar el escudo (ej. emojis o texto).
- `elo`: rating de fuerza (1000–3000), usado para comparar equipos y para generar probabilidades.

### players.txt (jugadores)
Cada línea representa un jugador y se asocia a un equipo según la liga y nombre del equipo.
Formato (separador `;`):
```
liga;equipo;nombre;posicion;numero;edad;altura;goles;asistencias;amarillas;rojas;titular
```
- `posicion`: controla las probabilidades de anotar/asistir/recibir tarjetas.
- `titular` (`true/false`): determina si participa en la simulación de partidos.
- `goles`, `asistencias`, `amarillas`, `rojas`: valores iniciales (normalmente 0) que se acumulan durante la simulación.

---

## 🛠️ Dependencias

Este proyecto **no utiliza librerías externas**. Todas las estructuras de datos necesarias (colas, árboles, diccionarios) han sido implementadas manualmente.

---

## 👨‍💻 Autores

Trabajo desarrollado por **Malfatti Gianluca y Casalla Lazaro**, para la cátedra de *Programación II* - UADE.

---

## 📄 Licencia

Uso académico y educativo.

