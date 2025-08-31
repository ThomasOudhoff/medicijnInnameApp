package com.thomas.medicatieinnameapp.exception;

import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    // 404 – voor je huidige IllegalArgumentException (bijv. "Gebruiker niet gevonden")
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body(
                404, "Not Found", ex.getMessage()
        ));
    }

    // 400 – bean validation (@Valid) fouten met veldmeldingen
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                fieldErrors.put(fe.getField(), fe.getDefaultMessage())
        );

        Map<String, Object> payload = body(400, "Bad Request", "Validatiefout");
        payload.put("fieldErrors", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
    }

    // Respecteer eventueel gegooide ResponseStatusException (bv. 409/404/400 uit service)
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleRse(ResponseStatusException ex) {
        HttpStatusCode status = ex.getStatusCode();
        String reason = ex.getReason() != null ? ex.getReason() : status.toString();
        return ResponseEntity.status(status).body(body(status.value(), status.toString(), reason));
    }

    // 409 – unieke constraint of foreign key constraint
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex) {
        String message = "Conflict: gegevens kunnen niet worden opgeslagen/verwijderd vanwege een database-constraint.";

        // Probeer oorzaak te herkennen (unique vs foreign key) op basis van driver-boodschap
        Throwable root = NestedExceptionUtils.getMostSpecificCause(ex);
        String rootMsg = root != null && root.getMessage() != null ? root.getMessage().toLowerCase() : "";

        if (rootMsg.contains("foreign key") || rootMsg.contains("constraint fails")
                || rootMsg.contains("a foreign key constraint fails")) {
            message = "Gebruiker kan niet verwijderd worden omdat er nog gekoppelde data bestaat.";
        } else if (rootMsg.contains("duplicate") || rootMsg.contains("unique")
                || rootMsg.contains("uk_") || rootMsg.contains("duplicate entry")) {
            message = "Deze e-mail is al in gebruik.";
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body(409, "Conflict", message));
    }

    // (optioneel) Catch-all → 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOther(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body(500, "Internal Server Error", ex.getMessage()));
    }

    // ---------- helpers ----------
    private Map<String, Object> body(int status, String error, String message) {
        Map<String, Object> m = new HashMap<>();
        m.put("timestamp", OffsetDateTime.now().toString());
        m.put("status", status);
        m.put("error", error);
        m.put("message", message);
        return m;
    }
}
