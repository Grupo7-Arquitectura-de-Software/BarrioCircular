package com.barriocircular.backend.logistica.interfaces.rest;

import com.barriocircular.backend.logistica.aplicacion.excepciones.SinReservasElegiblesException;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.StaleStateException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(SinReservasElegiblesException.class)
  public ResponseEntity<Map<String, String>> handleSinReservasElegibles(
      SinReservasElegiblesException ex) {
    Map<String, String> body = new HashMap<>();
    body.put("error", ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException ex) {
    Map<String, String> body = new HashMap<>();
    body.put("error", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
    Map<String, String> body = new HashMap<>();
    body.put("error", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
  }

  @ExceptionHandler({
    ObjectOptimisticLockingFailureException.class,
    OptimisticLockingFailureException.class,
    StaleStateException.class
  })
  public ResponseEntity<Map<String, String>> handleOptimisticLocking(Exception ex) {
    Map<String, String> body = new HashMap<>();
    body.put("error", "Conflict: concurrent modification detected.");
    body.put("detail", ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
  }
}
