package pl.lab.roombooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health", description = "Weryfikacja statusu działania aplikacji")
public class HealthController {

    @GetMapping
    @Operation(summary = "Sprawdza, czy serwer poprawnie odpowiada na żądania HTTP")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "room-booking",
                "timestamp", Instant.now().toString()
        );
    }
}
