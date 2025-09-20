package com.thomas.medicatieinnameapp.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.time.OffsetDateTime;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex,
                                                                HttpServletRequest req) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> fieldErrors.put(fe.getField(), fe.getDefaultMessage()));
        Map<String, Object> payload = body(HttpStatus.BAD_REQUEST, "Validatiefout", req);
        payload.put("fieldErrors", fieldErrors);
        log.warn("400 Validation error at {}: {}", req.getRequestURI(), fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> handleBind(BindException ex, HttpServletRequest req) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> fieldErrors.put(fe.getField(), fe.getDefaultMessage()));
        Map<String, Object> payload = body(HttpStatus.BAD_REQUEST, "Bind-fout", req);
        payload.put("fieldErrors", fieldErrors);
        log.warn("400 Bind error at {}: {}", req.getRequestURI(), fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex,
                                                                         HttpServletRequest req) {
        Map<String, String> violations = new LinkedHashMap<>();
        for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
            violations.put(String.valueOf(v.getPropertyPath()), v.getMessage());
        }
        Map<String, Object> payload = body(HttpStatus.BAD_REQUEST, "Validatiefout (constraint)", req);
        payload.put("fieldErrors", violations);
        log.warn("400 Constraint violation at {}: {}", req.getRequestURI(), violations);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException ex,
                                                                HttpServletRequest req) {
        Map<String, Object> payload = body(HttpStatus.BAD_REQUEST, "Ongeldige of onleesbare JSON body", req);
        log.warn("400 Unreadable body at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException ex,
                                                                  HttpServletRequest req) {
        Map<String, Object> payload = body(HttpStatus.BAD_REQUEST,
                "Ontbrekende parameter: " + ex.getParameterName(), req);
        log.warn("400 Missing param at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                                  HttpServletRequest req) {
        String name = ex.getName();
        String type = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "onbekend";
        Map<String, Object> payload = body(HttpStatus.BAD_REQUEST,
                "Parameter '" + name + "' heeft onjuist type. Verwacht: " + type, req);
        log.warn("400 Type mismatch at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                        HttpServletRequest req) {
        Map<String, Object> payload = body(HttpStatus.METHOD_NOT_ALLOWED, "Methode niet toegestaan", req);
        log.warn("405 Method not supported at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(payload);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMediaType(HttpMediaTypeNotSupportedException ex,
                                                               HttpServletRequest req) {
        Map<String, Object> payload = body(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Content-Type niet ondersteund", req);
        log.warn("415 Media type not supported at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(payload);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUpload(MaxUploadSizeExceededException ex,
                                                               HttpServletRequest req) {
        Map<String, Object> payload = body(HttpStatus.PAYLOAD_TOO_LARGE, "Bestand te groot", req);
        log.warn("413 Payload too large at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(payload);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrity(DataIntegrityViolationException ex,
                                                                   HttpServletRequest req) {
        String message = "Conflict: database-constraint geschonden.";
        Throwable root = NestedExceptionUtils.getMostSpecificCause(ex);
        String rootMsg = root != null && root.getMessage() != null ? root.getMessage().toLowerCase() : "";

        if (rootMsg.contains("foreign key") || rootMsg.contains("constraint fails")
                || rootMsg.contains("a foreign key constraint fails")) {
            message = "Actie niet toegestaan wegens gekoppelde gegevens (foreign key).";
        } else if (rootMsg.contains("duplicate") || rootMsg.contains("unique")
                || rootMsg.contains("uk_") || rootMsg.contains("duplicate entry")) {
            message = "Unieke waarde bestaat al (bijv. e-mail in gebruik).";
        }

        Map<String, Object> payload = body(HttpStatus.CONFLICT, message, req);
        log.warn("409 Data integrity at {}: {}", req.getRequestURI(), rootMsg);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(payload);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleRse(ResponseStatusException ex, HttpServletRequest req) {
        HttpStatusCode sc = ex.getStatusCode();
        HttpStatus status = (sc instanceof HttpStatus hs) ? hs : HttpStatus.valueOf(sc.value());
        String reason = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        Map<String, Object> payload = body(status, reason, req);
        logAt(status).accept(String.format("%d %s at %s: %s", status.value(), status, req.getRequestURI(), reason));
        return ResponseEntity.status(status).body(payload);
    }

    @ExceptionHandler({NoSuchElementException.class, EntityNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException ex, HttpServletRequest req) {
        Map<String, Object> payload = body(HttpStatus.NOT_FOUND,
                ex.getMessage() != null ? ex.getMessage() : "Niet gevonden", req);
        log.warn("404 Not found at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArg(IllegalArgumentException ex, HttpServletRequest req) {
        Map<String, Object> payload = body(HttpStatus.BAD_REQUEST,
                ex.getMessage() != null ? ex.getMessage() : "Ongeldige aanvraag", req);
        log.warn("400 Illegal argument at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        Map<String, Object> payload = body(HttpStatus.FORBIDDEN,
                ex.getMessage() != null ? ex.getMessage() : "Access Denied", req);
        log.warn("403 Forbidden at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(payload);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuth(AuthenticationException ex, HttpServletRequest req) {
        Map<String, Object> payload = body(HttpStatus.UNAUTHORIZED, "Authentication required", req);
        log.warn("401 Unauthorized at {}: {}", req.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(payload);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOther(Exception ex, HttpServletRequest req) {
        Map<String, Object> payload = body(HttpStatus.INTERNAL_SERVER_ERROR, "Er is iets misgegaan", req);
        log.error("500 Internal error at {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payload);
    }

    private Map<String, Object> body(HttpStatus status, String message, HttpServletRequest req) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("timestamp", OffsetDateTime.now().toString());
        m.put("status", status.value());
        m.put("error", status.toString()); // bv. "404 NOT_FOUND"
        m.put("message", message);
        if (req != null) {
            m.put("path", req.getRequestURI());
        }
        return m;
    }

    private java.util.function.Consumer<String> logAt(HttpStatus status) {
        if (status.is5xxServerError()) return msg -> log.error(msg);
        if (status.is4xxClientError()) return msg -> log.warn(msg);
        return msg -> log.info(msg);
    }
}
