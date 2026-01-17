# ASMO - Discord AI Bot Platform

Zaawansowana platforma łącząca **Discord**, **Spring Boot** i **Azure OpenAI** do budowy inteligentnych botów conversacyjnych z obsługą subskrypcji i zaplanowanych zadań.

## Funkcjonalności

- **Integracja Discord** - Natywna komunikacja przez Discord Slash Commands
- **AI/ML** - Azure OpenAI GPT-4o Mini dla zaawansowanej obróbki tekstu
- **Bezpieczeństwo** - OAuth2 integracja z Discordem i aspekty bezpieczeństwa
- **Baza danych** - PostgreSQL (Supabase) do przechowywania użytkowników i subskrypcji
- **Zaplanowanie** - Automatyczne zadania oraz webhoki
- **Skalowanie** - Docker & Docker Compose dla łatwego deploymentu

## Wymagania

- **Java 21+** (Spring Boot 3.4.0)
- **PostgreSQL** (Supabase lub lokalnie)
- **Docker & Docker Compose** (do deploymentu)
- **Discord Server** (do testowania)
- **Azure OpenAI API Key** (do funkcjonalności AI)

## Konfiguracja

### 1. Zmienne środowiskowe

Utwórz plik `.env` w katalogu głównym lub w `asmo/`:

```env
# Database
DB_USER=your_db_user
DB_PASSWORD=your_db_password

# Discord OAuth2
DISCORD_CLIENT_ID=your_discord_client_id
DISCORD_CLIENT_SECRET=your_discord_client_secret

# Azure OpenAI
AI_ENDPOINT=https://your-resource.openai.azure.com/
MCP_BEARER_TOKEN=your_azure_openai_key
```

### 2. Struktura projektu

```
asmo/
├── src/
│   ├── main/
│   │   ├── java/pl/mpc/asmo/
│   │   │   ├── config/          # Konfiguracja Spring, Discord, Security
│   │   │   ├── controller/      # REST endpoints
│   │   │   ├── service/         # Logika biznesowa
│   │   │   ├── listener/        # Discord event listeners
│   │   │   ├── model/           # Entity classes
│   │   │   ├── repository/      # JPA repositories
│   │   │   ├── aspect/          # Security aspects
│   │   │   └── exception/       # Exception handling
│   │   └── resources/
│   │       └── application.properties
│   └── test/                    # Testy jednostkowe
├── Dockerfile                   # Obraz Docker
├── docker-compose.yml          # Orkestracja kontenerów
├── build.gradle                # Gradle build configuration
└── gradlew                      # Gradle wrapper
```

## Instalacja i Uruchomienie

### Opcja 1: Lokalnie (bez Docker)

```bash
cd asmo
./gradlew build
./gradlew bootRun
```

Aplikacja będzie dostępna na: `http://localhost:8080`

### Opcja 2: Docker Compose (Zalecane)

```bash
cd asmo
docker-compose up --build
```

Usługa będzie dostępna na porcie skonfigurowanym w `docker-compose.yml`.

## Klucze API i Sekrety

- **KEYS.txt** - Przechowuj tutaj ważne klucze (nie commit do git)
- **asmo-ai_key.pem** - Klucz prywatny do AI (nie commit do git)

> **Bezpieczeństwo**: Dodaj te pliki do `.gitignore` przed commitem do repozytorium publicznego.

## Główne Zależności

| Zależność | Wersja | Cel |
|-----------|--------|-----|
| Spring Boot | 3.4.0 | Framework aplikacji |
| Spring AI | 1.0.0-M5 | Integracja Azure OpenAI |
| JDA (Discord) | 5.2.1 | Discord Bot Library |
| PostgreSQL Driver | - | Baza danych |
| Lombok | - | Redukcja boilerplate'u |
| Spring Security | 3.4.0 | Autentykacja & Autoryzacja |

## Główne Komponenty

### Controllers
- **BotController** - Endpoint dla operacji bota
- **UserController** - Zarządzanie użytkownikami
- **RedirectController** - Redirect logowania OAuth2

### Services
- **BotService** - Główna logika bota
- **DiscordService** - Komunikacja z Discord API
- **SubscriptionService** - Zarządzanie subskrypcjami
- **UserServiceDb** - Operacje na bazie danych

### Models
- **User** - Użytkownik Discord
- **Subscription** - Subskrypcja usługi
- **Prompt** - Historia promptów
- **SubscribeType** - Typy subskrypcji

## CI/CD

Projekt zawiera GitHub Actions workflow do automatycznego deploymentu:

```yaml
.github/workflows/deploy.yml
```

**Trigger**: Push na branch `mcp-spring-automatic-deployment`

**Proces**:
1. Pobranie kodu ze strony serwera
2. Aktualizacja Docker imagen
3. Restart usługi Docker Compose

## API Endpoints

### Autentykacja
- `GET /login/oauth2/authorization/discord` - Discord OAuth2 login
- `GET /login/oauth2/code/discord` - OAuth2 callback

### Bot
- `POST /api/bot/send` - Wysłanie wiadomości
- `GET /api/bot/status` - Status bota

### Użytkownik
- `GET /api/user/{id}` - Dane użytkownika
- `POST /api/user/subscribe` - Subskrypcja

## Bezpieczeństwo

- **OAuth2** - Discord OAuth2 do autentykacji
- **Spring Security** - Role-based access control
- **@RequiresBotAccess** - Custom annotation dla bezpiecznych endpointów
- **BotSecurityAspect** - AOP aspect dla dodatkowych sprawdzeń bezpieczeństwa

## Baza Danych

Projekt używa **Supabase PostgreSQL** (na AWS EU-West-1):

```sql
-- Hibernated auto-creation (ddl-auto=update)
-- Główne tabele:
- users (użytkownicy Discord)
- subscriptions (subskrypcje)
- prompts (historia AI)
```

## Troubleshooting

### Błąd: "Cannot find DISCORD_CLIENT_ID"
→ Upewnij się, że `.env` jest w katalogu `asmo/` i `Dotenv` jest zainicjalizowany.

### Błąd: "PostgreSQL connection refused"
→ Sprawdź `application.properties` i zmienne `DB_USER`, `DB_PASSWORD`.

### Błąd: "Azure OpenAI endpoint invalid"
→ Ustaw poprawnie `AI_ENDPOINT` i `MCP_BEARER_TOKEN`.

## Support

Aby zaraportować błędy, utwórz issue w repozytorium.

## Licencja

Projekt jest własnością zespołu ASMO. Wszystkie prawa zastrzeżone.

---

**Ostatnia aktualizacja**: Styczeń 2026
