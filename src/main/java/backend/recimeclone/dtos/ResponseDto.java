package backend.recimeclone.dtos;

public record ResponseDto(String message, int statusCode) {
    public ResponseDto {
        if (statusCode < 0) throw new IllegalArgumentException("Status code must be non-negative");
    }
}