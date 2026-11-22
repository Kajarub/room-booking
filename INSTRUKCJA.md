# Przewodnik laboratoryjny: Pierwsze REST API w Spring Boot (Zjazd 1)

## 1. Cel zajęć

Na dzisiejszych laboratoriach zbudujesz od podstaw kompletną warstwę **REST API** w ekosystemie **Spring Boot** na przykładzie systemu rezerwacji sal konferencyjnych i dydaktycznych (**Room Booking Service**).

Po ukończeniu ćwiczeń będziesz potrafić:
1. Wyjaśnić, jak **Spring Boot** uruchamia serwer HTTP i zarządza obiektami aplikacji (kontener **IoC** i **Dependency Injection**).
2. Zaprojektować aplikację w **architekturze warstwowej** (`Controller` → `Service` → `Repository`).
3. Oddzielić model domenowy od kontraktów API za pomocą obiektów **DTO** (rekordów Javy).
4. Zaimplementować pełny zestaw operacji **CRUD** (`GET`, `POST`, `PUT`, `DELETE`) ze świadomym doborem kodów statusu HTTP (`200`, `201`, `204`, `400`, `404`).
5. Przetestować stworzone endpointy w **Swagger UI** oraz za pomocą pliku z żądaniami HTTP (`api-tests.http`).

---

## 2. Jak działa aplikacja webowa w Spring Boot?

### 2.1. Przepływ żądania HTTP

Gdy klient (przeglądarka, Swagger UI, Postman lub aplikacja mobilna) wysyła żądanie `GET http://localhost:8080/api/rooms`, w aplikacji zachodzą kolejno następujące kroki:

1. **Wbudowany serwer Tomcat** odbiera połączenie sieciowe na porcie `8080` i przydziela do niego wątek roboczy.
2. **`DispatcherServlet`** (centralny router Spring MVC) sprawdza ścieżkę `/api/rooms` oraz metodę `GET`, a następnie dopasowuje odpowiednią metodę w klasie oznaczonej adnotacją `@RestController`.
3. **Kontroler (`RoomController`)** odczytuje parametry żądania, po czym deleguje wykonanie zadania do warstwy logiki biznesowej (`RoomService`).
4. **Serwis (`RoomService`)** realizuje logikę aplikacji, pobiera dane z repozytorium (`InMemoryRoomRepository`) i przekształca obiekty domenowe (`Room`) w obiekty odpowiedzi (`RoomResponse`).
5. **Biblioteka Jackson** automatycznie serializuje zwrócony obiekt Javy do formatu **JSON**, a Spring wysyła odpowiedź HTTP z odpowiednim kodem statusu.

### 2.2. Odwrócenie sterowania (IoC) i wstrzykiwanie zależności (DI)

W tradycyjnym kodzie Javy klasa sama tworzy swoje zależności za pomocą operatora `new`:
```java
// Podejście ciasno powiązane (utrudnia testowanie i podmianę implementacji)
private final InMemoryRoomRepository repository = new InMemoryRoomRepository();
```

W Springu stosujemy **odwrócenie sterowania (Inversion of Control)**:
- Klasy oznaczone adnotacjami `@RestController`, `@Service`, `@Repository` lub `@Configuration` stają się tzw. **ziarnami (Beans)** zarządzanymi przez kontener Springa.
- Kontener sam tworzy ich instancje przy starcie aplikacji i przekazuje je przez konstruktor tam, gdzie są potrzebne (**Dependency Injection**):

```java
@Service
public class RoomService {

    private final InMemoryRoomRepository roomRepository;

    // Spring automatycznie odnajduje ziarno InMemoryRoomRepository i przekazuje je tutaj
    public RoomService(InMemoryRoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }
}
```

> **Dlaczego to ważne w naszym projekcie?** Na kolejnych zajęciach zastąpimy repozytorium pamięciowe bazą danych (Spring Data JPA). Dzięki separacji warstw i wstrzykiwaniu zależności kontroler `RoomController` nie będzie wymagał żadnych zmian!

### 2.3. Dlaczego używamy obiektów DTO zamiast zwracać bezpośrednio model `Room`?

W projekcie znajdziesz pakiet `model` (`Room`) oraz pakiet `dto` (`CreateRoomRequest`, `UpdateRoomRequest`, `RoomResponse`):
- **Kontrola nad kontraktem API:** Przy tworzeniu sali (`POST`) klient nie powinien podawać pola `id` (nadaje je serwer) ani `available` (nowa sala jest domyślnie dostępna). Osobny rekord `CreateRoomRequest` wymusza dokładny zestaw wymaganych pól wejściowych.
- **Bezpieczeństwo i ewolucja kodu:** Gdy w bazie danych pojawią się dodatkowe pola techniczne (np. `createdAt`, `internalNotes`), nie wyciekną one przypadkowo w JSON-ie do klienta.
- **Niemutowalność:** Rekordy Javy (`public record ...`) są niemutowalne i zwięzłe — kompilator automatycznie generuje konstruktor, gettery, `equals()`, `hashCode()` oraz `toString()`.

---

## 3. Uruchomienie i weryfikacja projektu startowego

### Krok 1: Otwórz projekt i uruchom aplikację
1. Otwórz sklonowany katalog `room-booking-starter` w swoim IDE (IntelliJ IDEA otworzy go automatycznie jako projekt Maven).
2. Poczekaj, aż Maven pobierze zależności z `pom.xml` (`spring-boot-starter-webmvc` oraz `springdoc-openapi-starter-webmvc-ui`).
3. Uruchom metodę `main()` w klasie `pl.lab.roombooking.RoomBookingApplication` (lub wpisz w terminalu `mvn spring-boot:run`).

### Krok 2: Sprawdź działające endpointy
Otwórz przeglądarkę i wejdź pod poniższe adresy:
1. `http://localhost:8080/api/health` — powinieneś zobaczyć JSON potwierdzający działanie serwera (`"status": "UP"`).
2. `http://localhost:8080/api/rooms` — zobaczysz tablicę JSON z 3 przykładowymi salami zainicjalizowanymi w `InMemoryRoomRepository`.
3. `http://localhost:8080/swagger-ui.html` — interaktywna dokumentacja OpenAPI, z poziomu której możesz wysyłać żądania HTTP bez instalowania dodatkowych narzędzi.

---

## 4. Zadania laboratoryjne do wykonania

W kodzie projektu startowego miejsca do uzupełnienia zostały oznaczone komentarzami `// TODO (Zadanie X)`. Realizuj zadania po kolei, testując każdy nowy endpoint w **Swagger UI** lub w pliku **`api-tests.http`**.

---

### Zadanie 1: Pobieranie pojedynczej sali (`GET /api/rooms/{id}`)

**Cel:** Umożliwić pobranie szczegółów konkretnej sali po jej identyfikatorze oraz poprawnie obsłużyć sytuację, gdy sala nie istnieje.

**Wymagania:**
1. W `InMemoryRoomRepository` zaimplementuj metodę `findById(Long id)` zwracającą `Optional<Room>`.
2. W `RoomService` zaimplementuj metodę `getRoomById(Long id)` zwracającą `Optional<RoomResponse>`.
3. W `RoomController` dodaj metodę obsługującą `GET /api/rooms/{id}`:
   - Jeśli sala istnieje → zwróć status **`200 OK`** oraz obiekt `RoomResponse`.
   - Jeśli sala nie istnieje → zwróć status **`404 Not Found`** (bez ciała odpowiedzi).

**Weryfikacja:**
- `GET http://localhost:8080/api/rooms/1` → `200 OK` i JSON dla `Sala A101`.
- `GET http://localhost:8080/api/rooms/999` → `404 Not Found`.

<details>
<summary>Podpowiedź (rozwiń w razie trudności)</summary>

W `InMemoryRoomRepository`:
```java
public Optional<Room> findById(Long id) {
    return Optional.ofNullable(storage.get(id));
}
```

W `RoomService`:
```java
public Optional<RoomResponse> getRoomById(Long id) {
    return roomRepository.findById(id)
            .map(RoomResponse::from);
}
```

W `RoomController`:
```java
@GetMapping("/{id}")
@Operation(summary = "Pobiera szczegóły sali po identyfikatorze")
public ResponseEntity<RoomResponse> getRoomById(@PathVariable Long id) {
    return roomService.getRoomById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
}
```
</details>

---

### Zadanie 2: Dodawanie nowej sali (`POST /api/rooms`)

**Cel:** Umożliwić rejestrację nowej sali poprzez przesłanie danych w formacie JSON zgodnie ze standardem REST.

**Wymagania:**
1. W `RoomService` zaimplementuj metodę `createRoom(CreateRoomRequest request)`:
   - Utwórz nowy obiekt `Room` z `id = null` (repozytorium nada kolejny numer automatycznie) oraz `available = true`.
   - Zapisz obiekt w repozytorium i zwróć `RoomResponse`.
2. W `RoomController` dodaj metodę obsługującą `POST /api/rooms`:
   - Odbierz dane z ciała żądania za pomocą adnotacji `@RequestBody`.
   - Zgodnie ze specyfikacją HTTP zwróć status **`201 Created`**, nagłówek **`Location`** wskazujący na adres nowego zasobu (`/api/rooms/{id}`) oraz w ciele odpowiedzi utworzony obiekt `RoomResponse`.

**Weryfikacja:**
- Wyślij `POST /api/rooms` z poprawnym JSON-em (np. z pliku `api-tests.http`).
- Sprawdź, czy odpowiedź ma kod `201 Created`, nagłówek `Location: /api/rooms/4` oraz czy `GET /api/rooms` zwraca już 4 sale.

<details>
<summary>Podpowiedź (rozwiń w razie trudności)</summary>

W `RoomController`:
```java
@PostMapping
@Operation(summary = "Tworzy nową salę")
public ResponseEntity<RoomResponse> createRoom(@RequestBody CreateRoomRequest request) {
    RoomResponse created = roomService.createRoom(request);
    URI location = URI.create("/api/rooms/" + created.id());
    return ResponseEntity.created(location).body(created);
}
```
*(Pamiętaj o imporcie `java.net.URI`.)*
</details>

---

### Zadanie 3: Pełna aktualizacja sali (`PUT /api/rooms/{id}`)

**Cel:** Umożliwić nadpisanie danych istniejącej sali.

**Wymagania:**
1. W `RoomService` zaimplementuj metodę `updateRoom(Long id, UpdateRoomRequest request)`:
   - Wyszukaj salę w repozytorium (`findById(id)`).
   - Jeśli istnieje: zaktualizuj jej pola (`name`, `building`, `floor`, `capacity`, `hasProjector`, `available`), zapisz ją w repozytorium i zwróć `Optional.of(RoomResponse.from(saved))`.
   - Jeśli nie istnieje: zwróć `Optional.empty()`.
2. W `RoomController` dodaj metodę obsługującą `PUT /api/rooms/{id}`:
   - Zwróć **`200 OK`** z zaktualizowanym `RoomResponse` lub **`404 Not Found`**, jeśli sala o podanym ID nie istnieje.

**Weryfikacja:**
- Zaktualizuj salę o `id = 1`, a następnie pobierz ją przez `GET /api/rooms/1` i upewnij się, że zmiany zostały zapisane.
- Wyślij `PUT /api/rooms/999` → oczekuj `404 Not Found`.

---

### Zadanie 4: Usuwanie sali (`DELETE /api/rooms/{id}`)

**Cel:** Umożliwić usunięcie sali z systemu.

**Wymagania:**
1. W `InMemoryRoomRepository` zaimplementuj metodę `deleteById(Long id)` zwracającą `true`, jeśli wpis istniał w mapie i został usunięty (`storage.remove(id) != null`), lub `false` w przeciwnym razie.
2. W `RoomService` zaimplementuj metodę `deleteRoom(Long id)` delegującą wywołanie do repozytorium.
3. W `RoomController` dodaj metodę obsługującą `DELETE /api/rooms/{id}`:
   - Jeśli sala została usunięta → zwróć **`204 No Content`** (`ResponseEntity.noContent().build()`).
   - Jeśli sala nie istniała → zwróć **`404 Not Found`**.

**Weryfikacja:**
- Pierwsze wywołanie `DELETE /api/rooms/2` → `204 No Content`.
- Drugie wywołanie `DELETE /api/rooms/2` (idempotentność) → `404 Not Found`.

---

### Zadanie 5: Filtrowanie listy sal po parametrach zapytania (`@RequestParam`)

**Cel:** Umożliwić klientowi wyszukiwanie sal spełniających określone kryteria, np. `GET /api/rooms?minCapacity=25&hasProjector=true`.

**Wymagania:**
1. Kontroler `RoomController` przekazuje już opcjonalne parametry `minCapacity` oraz `hasProjector` do metody `roomService.searchRooms(minCapacity, hasProjector)`.
2. Zaimplementuj filtrowanie strumieniowe (`Stream API`) w `RoomService.searchRooms(...)`:
   - Gdy `minCapacity != null` → zostaw tylko sale, których `capacity >= minCapacity`.
   - Gdy `hasProjector != null` → zostaw tylko sale, których `hasProjector == hasProjector`.
   - Gdy oba parametry są `null` → zwróć wszystkie sale.

**Weryfikacja:**
- `GET /api/rooms?minCapacity=20` → zwraca `Sala A101` (24) i `Aula B204` (120).
- `GET /api/rooms?minCapacity=20&hasProjector=true` → filtruje jednocześnie po obu warunkach.

<details>
<summary>Podpowiedź (rozwiń w razie trudności)</summary>

```java
public List<RoomResponse> searchRooms(Integer minCapacity, Boolean hasProjector) {
    return roomRepository.findAll().stream()
            .filter(room -> minCapacity == null || room.getCapacity() >= minCapacity)
            .filter(room -> hasProjector == null || room.isHasProjector() == hasProjector)
            .map(RoomResponse::from)
            .toList();
}
```
</details>

---

### Zadanie 6: Podstawowa walidacja danych wejściowych (`400 Bad Request`)

**Cel:** Zabezpieczyć endpointy `POST` i `PUT` przed zapisaniem niespójnych danych (np. pustej nazwy sali lub ujemnej liczby miejsc).

*(Uwaga: na kolejnym zjeździe poznamy deklaratywną walidację Bean Validation z adnotacją `@Valid` — dzisiaj napiszemy prostą walidację ręczną, aby zrozumieć mechanizm kodów błędów klienta).*

**Wymagania:**
1. Sprawdź, czy:
   - `name` nie jest `null` ani puste po usunięciu białych znaków (`request.name() == null || request.name().isBlank()`),
   - `building` nie jest `null` ani puste (`request.building() == null || request.building().isBlank()`),
   - `capacity > 0`.
2. Jeśli którykolwiek warunek nie jest spełniony, zwróć odpowiedź ze statusem **`400 Bad Request`** (`ResponseEntity.badRequest().build()`).

---

### Zadanie dodatkowe (dla chętnych): Szybka zmiana dostępności (`PATCH /api/rooms/{id}/availability`)

W architekturze REST metoda `PUT` służy do podmiany całego zasobu, natomiast `PATCH` służy do **częściowej aktualizacji**.
1. Utwórz w pakiecie `dto` rekord `UpdateAvailabilityRequest(boolean available)`.
2. Dodaj endpoint `@PatchMapping("/{id}/availability")`, który zmienia wyłącznie pole `available` wskazanej sali, zwracając `200 OK` (lub `404 Not Found`, gdy sala nie istnieje).

---

## 5. Checklista przed oddaniem zadania

Upewnij się, że Twoje rozwiązanie spełnia wszystkie poniższe punkty:
- [ ] Projekt kompiluje się bez błędów i uruchamia na porcie `8080`.
- [ ] `GET /api/rooms` zwraca listę sal (a z parametrami `?minCapacity=...&hasProjector=...` poprawnie ją filtruje).
- [ ] `GET /api/rooms/{id}` zwraca `200 OK` dla istniejącej sali i `404 Not Found` dla nieistniejącej.
- [ ] `POST /api/rooms` zwraca `201 Created` wraz z nagłówkiem `Location`.
- [ ] `PUT /api/rooms/{id}` aktualizuje istniejącą salę (`200 OK`) lub zwraca `404 Not Found`.
- [ ] `DELETE /api/rooms/{id}` usuwa salę (`204 No Content`), a ponowne wywołanie zwraca `404 Not Found`.
- [ ] Niepoprawne dane w `POST` lub `PUT` skutkują odpowiedzią `400 Bad Request`.
