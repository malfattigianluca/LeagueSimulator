@echo off
echo Compilando clases en orden...

javac -d . -sourcepath src src/leaguesimulator/Excepciones/TorneoException.java
javac -d . -sourcepath src src/leaguesimulator/Util/Validador.java
javac -d . -sourcepath src src/leaguesimulator/TDA/ConjuntoGenericoTDA.java
javac -d . -sourcepath src src/leaguesimulator/Implementacion/ConjuntoGenericoImpl.java
javac -d . -sourcepath src src/leaguesimulator/Modelo/Jugador.java
javac -d . -sourcepath src src/leaguesimulator/Modelo/Equipo.java
javac -d . -sourcepath src src/leaguesimulator/Modelo/GestorEquipos.java
javac -d . -sourcepath src src/leaguesimulator/Modelo/GestorJugadores.java
javac -d . -sourcepath src src/leaguesimulator/Modelo/Partido.java
javac -d . -sourcepath src src/leaguesimulator/Modelo/SimuladorLiga.java
javac -d . -sourcepath src src/leaguesimulator/Modelo/Torneo.java
javac -d . -sourcepath src src/leaguesimulator/App.java

echo Compilacion completada.
pause 
