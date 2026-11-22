# Room Booking Service — Projekt startowy (Zjazd 1)

Repozytorium startowe do przedmiotu **Programowanie aplikacji webowych w Javie** (Zjazd 1: architektura Spring Boot i budowa REST API).

## Wymagania wstępne

- **Java JDK 25** (najnowsza wersja LTS)
- **Apache Maven 3.9+** (lub wbudowany Maven w IntelliJ IDEA)
- Środowisko programistyczne: **IntelliJ IDEA**, **VS Code** (z *Extension Pack for Java*) lub **Eclipse**

## Uruchomienie projektu

1. Sklonuj repozytorium i otwórz folder projektu w swoim IDE (jako projekt Maven).
2. Uruchom klasę `pl.lab.roombooking.RoomBookingApplication` z poziomu IDE **lub** wykonaj w terminalu:
   ```bash
   mvn spring-boot:run
   ```
3. Po uruchomieniu aplikacji otwórz w przeglądarce:
   - **Testy połączenia (Healthcheck):** http://localhost:8080/api/health
   - **Lista startowych sal (JSON):** http://localhost:8080/api/rooms
   - **Dokumentacja i konsola testowa Swagger UI:** http://localhost:8080/swagger-ui.html

## Dokumentacja i materiały w repozytorium

- **[INSTRUKCJA.md](./INSTRUKCJA.md)** — przewodnik laboratoryjny krok po kroku dla studentów wraz z opisem architektury, zadaniami (Zadania 1–6 + zadanie dodatkowe), podpowiedziami oraz checklistą odbioru.
- **[room-booking-starter.md](./room-booking-starter.md)** — szczegółowa dokumentacja techniczna szkieletu projektu startowego (omówienie pliku `pom.xml`, konfiguracji `application.yml`, wszystkich warstw aplikacji, rekordów DTO oraz pliku testowego `api-tests.http`).
- **[SCENARIUSZ_ZAJEC.md](./SCENARIUSZ_ZAJEC.md)** — konspekt i harmonogram prowadzenia 3-godzinnych (180 min) zajęć laboratoryjnych minuta po minucie (dla prowadzącego: plan bloków, live coding, checkpointy oraz najczęstsze problemy techniczne).

## Struktura projektu

```text
src/main/java/pl/lab/roombooking/
├── RoomBookingApplication.java        # Punkt startowy aplikacji Spring Boot
├── config/
│   └── OpenApiConfig.java             # Konfiguracja tytułu i opisu w Swagger UI
├── controller/
│   ├── HealthController.java          # Endpoint diagnostyczny GET /api/health
│   └── RoomController.java            # Kontroler REST dla zasobu /api/rooms (TODO)
├── dto/
│   ├── CreateRoomRequest.java         # DTO żądania utworzenia sali (POST)
│   ├── UpdateRoomRequest.java         # DTO żądania aktualizacji sali (PUT)
│   └── RoomResponse.java              # DTO odpowiedzi zwracanej klientowi
├── model/
│   └── Room.java                      # Model domenowy sali
├── repository/
│   └── InMemoryRoomRepository.java    # Repozytorium w pamięci RAM (ConcurrentHashMap)
└── service/
    └── RoomService.java               # Logika biznesowa i mapowanie Model <-> DTO
```
