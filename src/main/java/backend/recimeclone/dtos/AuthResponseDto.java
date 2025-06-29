package backend.recimeclone.dtos;

public record AuthResponseDto(
   String message,
   String token,
   int statusCode
) {}
