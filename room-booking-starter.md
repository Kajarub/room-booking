# Dokumentacja techniczna projektu startowego (`room-booking-starter`)

Niniejszy dokument opisuje szczegółowo budowę projektu startowego `room-booking-starter` przygotowanego na **Zjazd 1** przedmiotu **Programowanie aplikacji webowych w Javie**. Wyjaśnia rolę poszczególnych plików konfiguracyjnych, warstw aplikacji oraz gotowych klas pomocniczych.

---

## 1. Konfiguracja projektu i zależności (`pom.xml` oraz `application.yml`)

### 1.1. Plik `pom.xml` (Maven)
Projekt korzysta ze **Spring Boot 4.1.3** oraz **Javy 21** i zawiera celowo minimalny zestaw zależności, dzięki czemu pobiera się szybko nawet przy wolniejszym łączu w sali laboratoryjnej:

* **`spring-boot-starter-webmvc`** — podstawowy starter do budowy aplikacji webowych i REST API. Dostarcza:
  * wbudowany serwer HTTP **Apache Tomcat**,
  * silnik **Spring MVC** (`DispatcherServlet`, mapowanie żądań `@RestController`, `@GetMapping` itd.),
  * bibliotekę **Jackson** odpowiadającą za automatyczną konwersję obiektów Javy na JSON i odwrotnie.
* **`springdoc-openapi-starter-webmvc-ui` (wersja `3.2.0`)** — automatycznie analizuje kontrolery i rekordy DTO w projekcie, generuje specyfikację OpenAPI 3 i udostępnia graficzny interfejs **Swagger UI** pod adresem `/swagger-ui.html`.
* **`spring-boot-starter-webmvc-test` (zakres `test`)** — zestaw bibliotek testowych (JUnit 5, AssertJ, MockMvc) wykorzystywany na późniejszym etapie kursu.

### 1.2. Plik `src/main/resources/application.yml`
Plik konfiguracyjny aplikacji w formacie YAML ustawia:
* `spring.application.name: room-booking-service` — nazwę aplikacji widoczną w logach startowych.
* `server.port: 8080` — port nasłuchiwania wbudowanego serwera Tomcat.
* `springdoc.swagger-ui.*` — ścieżkę dostępu `/swagger-ui.html` oraz sortowanie endpointów alfabetycznie po tagach i metodach HTTP.

---

## 2. Szczegółowe omówienie pakietów i klas

### 2.1. Punkt startowy (`pl.lab.roombooking.RoomBookingApplication`)
Klasa oznaczona adnotacją `@SpringBootApplication` (łączącą `@Configuration`, `@EnableAutoConfiguration` oraz `@ComponentScan`). Wywołanie `SpringApplication.run(...)` uruchamia kontekst IoC Springa, skanuje wszystkie podpakiety `pl.lab.roombooking.*` w poszukiwaniu komponentów i startuje serwer Tomcat.

### 2.2. Konfiguracja OpenAPI (`pl.lab.roombooking.config.OpenApiConfig`)
Klasa `@Configuration` definiująca ziarno (`@Bean`) typu `OpenAPI`. Odpowiada za wyświetlenie czytelnego tytułu (*Room Booking API*), opisu oraz numeru wersji (`v1.0`) w nagłówku interfejsu Swagger UI.

### 2.3. Model domenowy (`pl.lab.roombooking.model.Room`)
Zwykła klasa Javy (POJO) reprezentująca salę w systemie. Zawiera pola:
* `id` (`Long`) — unikalny identyfikator nadawany przez repozytorium,
* `name` (`String`) — nazwa lub numer sali (np. `"Sala A101"`),
* `building` (`String`) — nazwa budynku (np. `"Budynek A"`),
* `floor` (`int`) — numer piętra,
* `capacity` (`int`) — maksymalna liczba miejsc,
* `hasProjector` (`boolean`) — informacja, czy sala jest wyposażona w projektor,
* `available` (`boolean`) — flaga określająca, czy sala jest obecnie dostępna do rezerwacji.

> **Wskazówka architektoniczna:** Na Zjeździe 2 ta klasa zostanie przekształcona w encję JPA poprzez dodanie adnotacji `@Entity`, `@Id` oraz `@GeneratedValue`.

### 2.4. Obiekty transferu danych (`pl.lab.roombooking.dto.*`)
Zamiast wystawiać bezpośrednio klasę `Room` w API, projekt rozdziela kontrakty wejściowe i wyjściowe za pomocą **rekordów Javy (`record`)**:
* **`CreateRoomRequest`** — kontrakt dla żądania `POST /api/rooms`. Zawiera wyłącznie pola podawane przez użytkownika podczas tworzenia sali (`name`, `building`, `floor`, `capacity`, `hasProjector`). Pomija `id` oraz `available`.
* **`UpdateRoomRequest`** — kontrakt dla żądania `PUT /api/rooms/{id}`. Zawiera pełen zestaw edytowalnych pól sali (łącznie z flagą `available`).
* **`RoomResponse`** — kontrakt odpowiedzi zwracanej klientowi w formacie JSON. Udostępnia statyczną metodę fabrykującą `RoomResponse.from(Room room)`, która zamienia obiekt domenowy na DTO.

### 2.5. Warstwa dostępu do danych (`pl.lab.roombooking.repository.InMemoryRoomRepository`)
Klasa oznaczona adnotacją `@Repository`, symulująca bazę danych w pamięci RAM:
* Wykorzystuje **`ConcurrentHashMap<Long, Room>`** oraz **`AtomicLong`**, aby zapewnić bezpieczeństwo wielowątkowe (Tomcat obsługuje równoległe żądania HTTP w osobnych wątkach).
* Metoda **`initSampleData()`** oznaczona adnotacją `@PostConstruct` uruchamia się automatycznie tuż po utworzeniu ziarna i wypełnia repozytorium trzema salami startowymi (`Sala A101`, `Aula B204`, `Laboratorium C012`).
* Zawiera gotowe metody `findAll()`, `save(Room)`, `existsById(Long)` oraz miejsca `TODO` do uzupełnienia w Zadaniu 1 (`findById`) i Zadaniu 4 (`deleteById`).

### 2.6. Warstwa logiki biznesowej (`pl.lab.roombooking.service.RoomService`)
Klasa oznaczona adnotacją `@Service`, wstrzykująca `InMemoryRoomRepository` przez konstruktor. Odpowiada za:
* wywoływanie operacji na repozytorium,
* mapowanie obiektów `Room` na `RoomResponse`,
* filtrowanie wyników w metodzie `searchRooms(...)` (Zadanie 5).

### 2.7. Warstwa prezentacji HTTP (`pl.lab.roombooking.controller.*`)
* **`HealthController`** — udostępnia gotowy endpoint `GET /api/health`, zwracający status `"UP"` i aktualny znacznik czasu (`Instant.now()`). Służy do natychmiastowej weryfikacji, czy serwer działa poprawnie.
* **`RoomController`** — główny kontroler REST zmapowany pod ścieżkę bazową `/api/rooms`. Zawiera działający endpoint `GET /api/rooms` (obsługujący opcjonalne parametry zapytania `minCapacity` oraz `hasProjector`) oraz komentarze `TODO` wyznaczające miejsca na kolejne metody CRUD (`GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}`).

---

## 3. Plik testowy `api-tests.http`

W katalogu głównym repozytorium znajduje się plik **`api-tests.http`**, zawierający 9 gotowych żądań HTTP (od healthchecku, przez tworzenie, aktualizację i filtrowanie sal, aż po usuwanie i testy błędnych danych).
* W **IntelliJ IDEA** (Ultimate) każde żądanie można uruchomić bezpośrednio z edytora klikając zieloną ikonę strzałki na marginesie.
* W **VS Code** plik działa z darmowym rozszerzeniem *REST Client* (`humao.rest-client`).
