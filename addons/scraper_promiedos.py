import requests
from bs4 import BeautifulSoup
import time
from urllib.parse import urljoin
import random
import re

# Lista de User-Agents para rotación
user_agents = [
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36',
    'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.212 Safari/537.36',
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:88.0) Gecko/20100101 Firefox/88.0',
    'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1 Safari/605.1.15',
]

# Diccionario con las ligas y sus equipos
leagues = {
#     "Premier League": {
#         "url": "https://www.transfermarkt.es/premier-league/startseite/wettbewerb/GB1",
#         "teams":  [
#     # "Arsenal FC",
#     # "Aston Villa",
#     # "AFC Bournemouth",
#     # "Brentford FC",
#     # "Brighton & Hove Albion",
#     # "Chelsea FC",
#     # "Crystal Palace",
#     # "Everton FC",
#     # "Fulham FC",
#     # "Ipswich Town",
#     # "Leicester City",
#     # "Liverpool FC",
#     # "Manchester City",
#     # "Manchester United",
#     # "Newcastle United",
#     # "Nottingham Forest",
#     # "Southampton FC",
#     # "Tottenham Hotspur",
#     # "West Ham United",
#     # "Wolverhampton Wanderers"
# ]},
    # "LaLiga": {
    #     "url": "https://www.transfermarkt.es/laliga/startseite/wettbewerb/ES1",
    #     "teams": [
    #         "Athletic Club",
    #         "Atlético de Madrid",
    #         "CA Osasuna",
    #         "CD Leganés",
    #         "Deportivo Alavés",
    #         "FC Barcelona",
    #         "Getafe CF",
    #         "Girona FC",
    #         "RCD Espanyol",
    #         "RCD Mallorca",
    #         "RC Celta de Vigo",
    #         "Rayo Vallecano",
    #         "Real Betis Balompié",
    #         "Real Madrid CF",
    #         "Real Sociedad",
    #         "Real Valladolid CF",
    #         "Sevilla FC",
    #         "UD Las Palmas",
    #         "Valencia CF",
    #         "Villarreal CF"
    #     ]
    # },

    # "Serie A": {
    #     "url": "https://www.transfermarkt.es/serie-a/startseite/wettbewerb/IT1",
    #     "teams": [
    #         "AC Milan",
    #         "AC Monza",
    #         "AS Roma",
    #         "Atalanta de Bérgamo",
    #         "Bolonia",
    #         "Cagliari",
    #         "Como 1907",
    #         "Empoli FC",
    #         "Fiorentina",
    #         "Génova",
    #         "Hellas Verona",
    #         "Inter de Milán",
    #         "Juventus de Turín",
    #         "Parma",
    #         "SSC Nápoles",
    #         "SS Lazio",
    #         "Torino FC",
    #         "Udinese",
    #         "US Lecce",
    #         "Venezia FC"
    #     ]
    # },

    # "Liga Argentina": {
    #     "url": "https://www.transfermarkt.es/torneo-apertura/startseite/wettbewerb/ARG1",
    #     "teams": [
    #         "AA Argentinos Juniors",
    #         "CA Aldosivi",
    #         "CA Banfield",
    #         "CA Barracas Central",
    #         "CA Belgrano",
    #         "CA Boca Juniors",
    #         "CA Central Córdoba (SdE)",
    #         "CSD Defensa y Justicia",
    #         "Club de Gimnasia y Esgrima La Plata",
    #         "CA Huracán",
    #         "CA Independiente",
    #         "CS Independiente Rivadavia",
    #         "Instituto ACC",
    #         "CA Lanús",
    #         "CA Newell's Old Boys",
    #         "CA Platense",
    #         "CA River Plate",
    #         "CA Rosario Central",
    #         "CA San Lorenzo de Almagro",
    #         "CA San Martín (San Juan)",
    #         "CA Sarmiento (Junín)",
    #         "CA Talleres",
    #         "CA Tigre",
    #         "CA Unión (Santa Fe)",
    #         "CA Vélez Sarsfield",
    #         "CD Godoy Cruz Antonio Tomba",
    #         "Club Atlético Tucumán",
    #         "Club Deportivo Riestra",
    #         "Club Estudiantes de La Plata",
    #         "Racing Club"
    #         ]
    # },

    "Ligue 1": {
        "url": "https://www.transfermarkt.es/ligue-1/startseite/wettbewerb/FR1",
        "teams": [
            # "AJ Auxerre",
            # "Angers SCO",
            # "AS Mónaco",
            # "AS Saint-Étienne",
            # "FC Nantes",
            # "Le Havre AC",
            # "LOSC Lille",
            # "Montpellier HSC",
            "OGC Niza",
            # "Olympique de Lyon",
            "Olympique de Marsella",
            # "París Saint-Germain FC",
            # "Racing Club de Estrasburgo",
            # "RC Lens",
            "Stade Brestois 29",
            # "Stade de Reims",
            # "Stade Rennais FC",
            # "Toulouse FC"
        ]
    },

}

# Función para extraer la edad desde el string, capturando números entre paréntesis
def extract_age(birth_date_str):
    try:
        birth_date_str = birth_date_str.strip()
        match = re.search(r'\((\d+)\)', birth_date_str)
        if match:
            return match.group(1)
        print(f"Formato de edad no encontrado en: '{birth_date_str}'")
        return "Desconocido"
    except (AttributeError, TypeError):
        print(f"Error procesando edad: '{birth_date_str}'")
        return "Desconocido"

# Función para realizar una solicitud con reintentos
def make_request(url, max_retries=5):
    for attempt in range(max_retries):
        try:
            headers = {'User-Agent': random.choice(user_agents)}
            response = requests.get(url, headers=headers, timeout=20)
            response.raise_for_status()
            return response
        except requests.exceptions.HTTPError as e:
            if response.status_code == 429:
                print(f"Error 429: Demasiadas solicitudes. Reintentando en {2 ** attempt + random.uniform(5, 10)} segundos...")
                time.sleep(2 ** attempt + random.uniform(5, 10))
            else:
                print(f"Error HTTP al acceder a {url}: {e}")
                return None
        except requests.exceptions.Timeout:
            print(f"Timeout al acceder a {url}. Reintentando en {2 ** attempt + random.uniform(5, 10)} segundos...")
            time.sleep(2 ** attempt + random.uniform(5, 10))
        except requests.exceptions.RequestException as e:
            print(f"Error al acceder a {url}: {e}")
            return None
    print(f"Falló después de {max_retries} intentos para {url}")
    return None

# Función para obtener los datos de un equipo
def scrape_team_data(team_url, team_name, league_name):
    try:
        print(f"Procesando equipo: {team_name} ({league_name})")
        response = make_request(team_url)
        if not response:
            return []

        soup = BeautifulSoup(response.text, 'html.parser')
        expanded_tab = soup.find('a', class_='tm-tab', string=lambda text: text and 'Ampliado' in text)
        if not expanded_tab or 'href' not in expanded_tab.attrs:
            print(f"No se encontró la vista 'Ampliado' para {team_name} ({league_name})")
            return []

        expanded_url = urljoin(team_url, expanded_tab['href'])
        response = make_request(expanded_url)
        if not response:
            return []

        soup = BeautifulSoup(response.text, 'html.parser')
        table = soup.find('table', class_='items')
        if not table:
            print(f"No se encontró la tabla de jugadores para {team_name} ({league_name})")
            return []

        players_data = []
        rows = table.find_all('tr', class_=['odd', 'even'])
        for row in rows:
            cols = row.find_all('td')
            if len(cols) < 8:  # Asegurarse de que hay suficientes columnas
                print(f"Fila incompleta en {team_name} ({league_name}): {[col.text.strip() for col in cols]}")
                continue

            # Extraer número de dorsal
            dorsal = cols[0].text.strip() if cols[0].text.strip() else "Sin dorsal"

            # Extraer nombre desde la subtabla
            name_cell = cols[1]
            name = None
            inline_table = name_cell.find('table', class_='inline-table')
            if inline_table:
                link = inline_table.find('a')
                if link and link.text.strip():
                    name = link.text.strip()
            if not name:
                print(f"Nombre no encontrado en {team_name} ({league_name}): {name_cell.prettify()}")
                continue

            # Extraer posición
            position = cols[4].text.strip() if len(cols) > 4 and cols[4].text.strip() else "Desconocido"
            if position == name:
                print(f"Posición igual al nombre en {team_name} ({league_name}) para {name}: {cols[4].prettify()}")
                position = "Desconocido"

            # Extraer edad desde el string
            birth_date = cols[5].text.strip() if len(cols) > 5 and cols[5].text.strip() else "Desconocido"
            age = extract_age(birth_date) if birth_date != "Desconocido" else "Desconocido"

            # Extraer altura
            height = cols[7].text.strip() if len(cols) > 7 and cols[7].text.strip() and cols[7].text.strip().endswith('m') else "Desconocido"
            if height == "Desconocido":
                print(f"Altura no encontrada en {team_name} ({league_name}) para {name}: {[col.text.strip() for col in cols]}")

            # Limpiar campos
            name = name.strip()
            position = position.strip()
            dorsal = dorsal.strip()
            age = age.strip()
            height = height.strip()

            players_data.append({
                'Liga': league_name.strip(),
                'Equipo': team_name.strip(),
                'Dorsal': dorsal,
                'Nombre': name,
                'Posición': position,
                'Edad': age,
                'Altura': height
            })

        return players_data

    except Exception as e:
        print(f"Error al procesar {team_name} ({league_name}): {e}")
        return []

# Función principal
def scrape_leagues():
    try:
        all_players_data = []
        for league_name, league_info in leagues.items():
            league_url = league_info["url"]
            teams = league_info["teams"]

            response = make_request(league_url)
            if not response:
                continue

            soup = BeautifulSoup(response.text, 'html.parser')
            available_teams = [a['title'] for a in soup.find_all('a', title=True) if 'title' in a.attrs]
            for team_name in teams:
                if team_name not in available_teams:
                    print(f"Advertencia: El equipo '{team_name}' no se encontró en la página de {league_name}. Equipos disponibles: {available_teams}")

            for team_name in teams:
                team_link = soup.find('a', title=team_name, href=True)
                if not team_link:
                    print(f"No se encontró el enlace para {team_name} ({league_name})")
                    continue

                team_url = urljoin(league_url, team_link['href'])
                team_data = scrape_team_data(team_url, team_name, league_name)
                all_players_data.extend(team_data)
                time.sleep(random.uniform(15, 20))

        with open('players.txt', 'w', encoding='utf-8') as f:
            for player in all_players_data:
                line = f"{player['Liga']};{player['Equipo']};{player['Nombre']};{player['Posición']};{player['Dorsal']};{player['Edad']};{player['Altura']};"
                f.write(line + '\n')

        print("Datos guardados en 'players.txt'")

    except Exception as e:
        print(f"Error al procesar las ligas: {e}")

# Ejecutar el script
if __name__ == "__main__":
    scrape_leagues()