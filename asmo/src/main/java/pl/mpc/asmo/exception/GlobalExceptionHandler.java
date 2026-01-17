package pl.mpc.asmo.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException e) {
        String aiInstruction = String.format(
                "INSTRUKCJA SYSTEMOWA: Żądane dane nie zostały znalezione (Szczegóły: %s). " +
                        "Poinformuj użytkownika w przyjazny, nietechniczny sposób, że nie udało się znaleźć tego, czego szukał. " +
                        "Zakończ frazą: Spróbuj ponownie lub sprawdź poprawność danych.", e.getMessage());

        return createErrorResponse(HttpStatus.NOT_FOUND, "Not Found", aiInstruction);
    }

    @ExceptionHandler(BotSecurityException.class)
    public ResponseEntity<Map<String, Object>> handleBotSecurityException(BotSecurityException e) {
        String aiInstruction = String.format(
                "INSTRUKCJA SYSTEMOWA: Odmowa dostępu (Szczegóły: %s). " +
                        "Wyjaśnij użytkownikowi w naturalnym języku, że nie ma do tego uprawnień. " +
                        "Zakończ frazą: Spróbuj ponownie, gdy uzyskasz odpowiednie uprawnienia.", e.getMessage());

        return createErrorResponse(HttpStatus.FORBIDDEN, "Access Denied", aiInstruction);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        String aiInstruction = String.format(
                "INSTRUKCJA SYSTEMOWA: Podano nieprawidłowe dane wejściowe (Szczegóły: %s). " +
                        "Wyjaśnij prostymi słowami, co poszło nie tak. " +
                        "Zakończ frazą: Spróbuj jeszcze raz, poprawiając parametry.", e.getMessage());

        return createErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", aiInstruction);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception e) {
        String aiInstruction = String.format(
                "INSTRUKCJA SYSTEMOWA: Wystąpił błąd techniczny serwera (Szczegóły: %s). " +
                        "Przeproś użytkownika i powiedz, że coś poszło nie tak po naszej stronie. " +
                        "Zakończ frazą: Spróbuj ponownie za chwilę.", e.getMessage());

        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", aiInstruction);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", error);
        errorResponse.put("message", message);

        return new ResponseEntity<>(errorResponse, status);
    }
}