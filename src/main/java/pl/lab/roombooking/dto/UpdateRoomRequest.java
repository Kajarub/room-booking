package pl.lab.roombooking.dto;

/**
 * Obiekt DTO odbierany w ciele żądania PUT /api/rooms/{id} podczas pełnej aktualizacji sali.
 */
public record UpdateRoomRequest(
        String name,
        String building,
        int floor,
        int capacity,
        boolean hasProjector,
        boolean available
) {
}
