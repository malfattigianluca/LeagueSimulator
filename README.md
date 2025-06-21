# Sistema de Gestión de Torneos Deportivos

Este proyecto en Java implementa un sistema para la simulación de torneos de fútbol. Permite gestionar equipos, jugadores, y simular torneos en diferentes formatos: **liga**, **eliminación directa**, y **torneo mixto** (fase de grupos + eliminación). Fue desarrollado como parte del Proyecto Final de la materia *Programación II*.

---

## 📁 Estructura del Proyecto

```
SimuladorTorneosFutbol_ProyectoFinal/
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

2. **Compilar desde terminal**:
   ```bash
   javac -encoding UTF-8 -cp . SimuladorTorneosFutbol_ProyectoFinal/App.java
   ```

3. **Ejecutar**:
   ```bash
   java SimuladorTorneosFutbol_ProyectoFinal.App
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

## 🧪 Datos de Entrada

- **`teams.txt`**: lista de equipos con su nombre, país, ELO y otros datos.
- **`players.txt`**: lista de jugadores asociados a equipos.

---

## 🛠️ Dependencias

Este proyecto **no utiliza librerías externas**. Todas las estructuras de datos necesarias (colas, árboles, diccionarios) han sido implementadas manualmente.

---

## 👨‍💻 Autores

Trabajo desarrollado por **Malfatti Gianluca y Casalla Lazaro**, para la cátedra de *Programación II* - UADE.

---

## 📄 Licencia

Uso académico y educativo.
