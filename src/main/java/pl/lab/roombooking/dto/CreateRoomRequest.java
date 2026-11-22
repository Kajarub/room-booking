package pl.lab.roombooking.dto;

/**
 * Obiekt DTO odbierany w ciele żądania POST /api/rooms podczas tworzenia nowej sali.
 * Nie zawiera pola 'id' (nadaje je serwer) ani 'available' (nowa sala jest domyślnie dostępna).
 */
public record CreateRoomRequest(
        String name,
        String building,
        int floor,
        int capacity,
        boolean hasProjector
) {
}
