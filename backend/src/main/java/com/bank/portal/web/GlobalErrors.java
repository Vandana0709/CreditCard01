package com.bank.portal.web;
import java.util.Map;
import org.springframework.http.*;
@org.springframework.web.bind.annotation.RestControllerAdvice public class GlobalErrors {
 @org.springframework.web.bind.annotation.ExceptionHandler({IllegalArgumentException.class,IllegalStateException.class}) public ResponseEntity<Map<String,String>> bad(RuntimeException e){return ResponseEntity.badRequest().body(Map.of("message",e.getMessage()));}
 @org.springframework.web.bind.annotation.ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class) public ResponseEntity<Map<String,String>> invalid(org.springframework.web.bind.MethodArgumentNotValidException e){String m=e.getBindingResult().getFieldErrors().stream().findFirst().map(x->x.getField()+": "+x.getDefaultMessage()).orElse("Invalid request");return ResponseEntity.badRequest().body(Map.of("message",m));}
}
