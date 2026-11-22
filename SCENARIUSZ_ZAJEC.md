# Scenariusz prowadzenia zajęć laboratoryjnych — Zjazd 1 (3h / 180 min)

**Przedmiot:** Programowanie aplikacji webowych w Javie  
**Temat:** Architektura Spring Boot, kontener IoC/DI oraz budowa REST API  
**Repozytorium startowe:** `room-booking-starter`  

---

## Przygotowanie przed zajęciami (5 minut)

1. Udostępnij studentom link do repozytorium na tablicy lub platformie zajęciowej.
2. Studenci nie potrzebują dodatkowych materiałów — pełna instrukcja krok po kroku znajduje się w pliku **`INSTRUKCJA.md`** wewnątrz repozytorium.

---

## Harmonogram spotkania (180 minut)

### Blok 1: Rozruch środowisk i „wycieczka po kodzie” (0:00 – 0:30 | 30 min)

* **0:00–0:15 | Klonowanie i pierwsze uruchomienie (Studenci + Prowadzący):**
  * Studenci klonują repozytorium, otwierają je jako projekt Maven w IDE, czekają na pobranie zależności i uruchamiają klasę `RoomBookingApplication`.
  * Prowadzący krąży po sali i pomaga rozwiązać ewentualne problemy ze środowiskiem (wersja JDK, port 8080).
  * **Kamień milowy:** każdy student ma otwarte w przeglądarce `http://localhost:8080/api/rooms` (widzi 3 sale w formacie JSON) oraz `http://localhost:8080/swagger-ui.html`.
* **0:15–0:30 | Omówienie architektury na działającym przykładzie (Ekran prowadzącego):**
  * Zamiast prezentacji slajdowej otwórz w IDE plik `RoomController.java` i prześledź z grupą drogę działającego żądania `GET /api/rooms`:
    1. **Kontroler (`RoomController`):** odbiera żądanie HTTP, nie zawiera logiki biznesowej.
    2. **Serwis (`RoomService`):** dostaje repozytorium przez konstruktor (omów ideę IoC/DI) i mapuje model na DTO.
    3. **Repozytorium (`InMemoryRoomRepository`):** dlaczego używamy `ConcurrentHashMap` i `AtomicLong` (wielowątkowość Tomcata) oraz skąd wzięły się 3 sale startowe (`@PostConstruct`).
    4. **DTO (`RoomResponse`, `CreateRoomRequest`):** dlaczego używamy `record` i czemu klient przy `POST` nie wysyła `id` ani `available`.

---

### Blok 2: Live Coding — wspólne wykonanie Zadania 1 (0:30 – 0:55 | 25 min)

Wykonaj **Zadanie 1 (`GET /api/rooms/{id}`)** wspólnie ze studentami pokazać pełny cykl pracy w projekcie:
1. Otwórz `InMemoryRoomRepository` i dopisz `findById` (`Optional.ofNullable(storage.get(id))`).
2. Przejdź do `RoomService` i dopisz `getRoomById` (`.map(RoomResponse::from)`).
3. Przejdź do `RoomController` i napisz metodę z `@GetMapping("/{id}")` oraz `@PathVariable Long id`. Pokaż różnicę między zwróceniem `200 OK` a `404 Not Found` (`ResponseEntity.notFound().build()`).
4. Zrestartuj aplikację i przetestuj na rzutniku dwa przypadki w Swaggerze lub pliku `api-tests.http`: `/api/rooms/1` (`200 OK`) oraz `/api/rooms/999` (`404 Not Found`).

> **Efekt:** Po 55 minutach wszyscy studenci wiedzą, jak edytować poszczególne warstwy, restartować serwer i testować endpointy.

---

### Blok 3: Praca własna studentów — pełny CRUD: Zadania 2, 3 i 4 (0:55 – 1:55 | 60 min)

* Studenci pracują z plikiem `INSTRUKCJA.md` i realizują kolejno **Zadanie 2 (`POST`)**, **Zadanie 3 (`PUT`)** i **Zadanie 4 (`DELETE`)**. W razie trudności mogą rozwinąć sekcje `<details>Podpowiedź</details>` w instrukcji.
* **Na co zwracać uwagę podczas obchodu po sali:**
  * **W Zadaniu 2 (`POST`):** sprawdź, czy zwracają **`201 Created` z nagłówkiem `Location`** (`ResponseEntity.created(URI.create(...)).body(...)`) zamiast zwykłego `200 OK`, oraz czy nowa sala dostaje domyślnie `available = true`.
  * **W Zadaniu 3 (`PUT`):** upewnij się, że aktualizują istniejący rekord w mapie (zachowując jego `id`), a dla nieistniejącego `id` zwracają `404 Not Found`.
  * **W Zadaniu 4 (`DELETE`):** zwróć uwagę na idempotentność — pierwsze wywołanie usuwa zasób (`204 No Content`), drugie dla tego samego ID musi zwrócić `404 Not Found`.
* **Checkpoint (~1:25):** Jeśli kilka osób popełnia ten sam błąd (np. brak adnotacji `@RequestBody` przy `POST`), zatrzymaj salę na 2 minuty i omów ten fragment wspólnie na ekranie.

---

### Blok 4: Mini-wykład + filtrowanie i walidacja: Zadania 5 i 6 (1:55 – 2:35 | 40 min)

* **1:55–2:05 | Krótkie wprowadzenie (Ekran prowadzącego):**
  * Pokaż w `RoomController`, że metoda `getAllRooms` przyjmuje już `@RequestParam(required = false) Integer minCapacity` i `Boolean hasProjector`. Wyjaśnij różnicę między `@PathVariable` (`/rooms/1`) a `@RequestParam` (`/rooms?minCapacity=20`).
  * Wyjaśnij, dlaczego brak walidacji kończy się błędem `500` lub niespójnymi danymi, a poprawną reakcją API na błędne dane od użytkownika jest **`400 Bad Request`**.
* **2:05–2:35 | Praca studentów nad Zadaniami 5 i 6:**
  * Studenci implementują filtrowanie strumieniowe w `RoomService.searchRooms(...)` (**Zadanie 5**) oraz walidację w `POST`/`PUT` (**Zadanie 6**).
  * Osoby, które skończą wcześniej, realizują **Zadanie dodatkowe** (`PATCH /api/rooms/{id}/availability`).

---

### Blok 5: Odbiór rozwiązań i zapowiedź Zjazdu 2 (2:35 – 3:00 | 25 min)

* **2:35–2:55 | Szybki odbiór z plikiem `api-tests.http`:**
  * Podejdź do każdego studenta i poproś o uruchomienie po kolei scenariuszy z pliku `api-tests.http` (lub w Swagger UI) według **Checklisty** z końca `INSTRUKCJA.md`. Weryfikacja jednej osoby zajmuje ok. 1,5–2 minuty.
* **2:55–3:00 | Podsumowanie i pomost do Zjazdu 2:**
  * Podkreśl zaletę architektury warstwowej: cała wiedza o tym, *gdzie* zapisywane są dane, znajduje się wyłącznie w `InMemoryRoomRepository`.
  * Zapowiedz, że na **Zjeździe 2** ta jedna klasa zostanie zastąpiona przez **Spring Data JPA + relacyjną bazę danych**, dojdzie druga encja (`Reservation` — rezerwowanie sal w konkretnych przedziałach czasowych), a ręczne `if`-y z Zadania 6 zostaną zastąpione adnotacjami `@Valid` oraz globalnym `@RestControllerAdvice`.

---

## Najczęstsze problemy techniczne u studentów (ściągawka)

1. **„Zmieniłem kod, ale nadal dostaję `500 UnsupportedOperationException`!”**  
   Student dopisał kod w pliku `.java`, ale nie kliknął przycisku **Rerun** w IDE i wciąż wysyła żądania do starej instancji aplikacji.
2. **Błąd wersji Javy przy kompilacji**  
   W `pom.xml` ustawiona jest **Java 25**. Jeśli IDE studenta ma domyślnie ustawione starsze Project SDK (np. 21), należy zmienić je w *Project Structure → SDK*.
3. **Błąd `Port 8080 was already in use`**  
   Jeśli w tle działa inna usługa na porcie 8080, najszybciej zmienić `server.port: 8081` w `src/main/resources/application.yml`.
