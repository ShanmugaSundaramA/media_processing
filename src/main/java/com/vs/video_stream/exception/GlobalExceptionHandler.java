package com.vs.video_stream.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ExpiredJwtException.class)
  public ResponseEntity<String> handleExpiredJwtException(ExpiredJwtException ex) {
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body("Token expired: " + ex.getMessage());
  }

  @SuppressWarnings("deprecation")
  @ExceptionHandler(SignatureException.class)
  public ResponseEntity<String> handleSignatureException(SignatureException ex) {
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body("Invalid token signature: " + ex.getMessage());
  }

  @ExceptionHandler(MalformedJwtException.class)
  public ResponseEntity<String> handleMalformedJwtException(MalformedJwtException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body("Malformed token: " + ex.getMessage());
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
    return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body("User not found: " + ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGeneralException(Exception ex) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("An unexpected error occurred: " + ex.getMessage());
  }
}
