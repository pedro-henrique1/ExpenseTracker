package com.example.expensetracker.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception exception, WebRequest request) {
        ProblemDetail errorDetail;

        // TODO enviar essa pilha de rastreamento para uma ferramenta de observabilidade
        // Considere usar um framework de log em vez de printStackTrace
        exception.printStackTrace();

        errorDetail = switch (exception) {
            case BadCredentialsException badCredentialsException ->
                    createProblemDetail(HttpStatus.UNAUTHORIZED, exception, "O nome de usuário ou senha está incorreto");
            case AccountStatusException accountStatusException ->
                    createProblemDetail(HttpStatus.FORBIDDEN, exception, "A conta está bloqueada");
            case AccessDeniedException accessDeniedException ->
                    createProblemDetail(HttpStatus.FORBIDDEN, exception, "Você não está autorizado a acessar este recurso");
            case SignatureException signatureException ->
                    createProblemDetail(HttpStatus.FORBIDDEN, exception, "A assinatura do JWT é inválida");
            case ExpiredJwtException expiredJwtException ->
                    createProblemDetail(HttpStatus.FORBIDDEN, exception, "O token JWT expirou");
            default ->
                    createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception, "Erro interno do servidor desconhecido.");
        };

        return ResponseEntity.status(errorDetail.getStatus()).body(errorDetail.getProperties());
    }

    private ProblemDetail createProblemDetail(HttpStatus status, Exception exception, String description) {
        ProblemDetail errorDetail = ProblemDetail.forStatusAndDetail(HttpStatus.valueOf(status.value()), exception.getMessage());
        errorDetail.setProperty("description", description);
        return errorDetail;
    }

    @ExceptionHandler(EmailDuplicateException.class)
    public ResponseEntity handleEmailDuplicateException(EmailDuplicateException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        return new ResponseEntity<>("Erro de integridade de dados: " + e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MissingRequiredFieldException.class)
    public ResponseEntity<String> handlePasswordNull(MissingRequiredFieldException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

}