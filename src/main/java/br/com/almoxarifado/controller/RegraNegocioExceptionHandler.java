package br.com.almoxarifado.controller;

import br.com.almoxarifado.dto.ErroResposta;
import br.com.almoxarifado.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.time.Instant;

@RestControllerAdvice
public class RegraNegocioExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(RegraNegocioExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> tratarArgumentoInvalido(IllegalArgumentException exception, HttpServletRequest request) {
        return resposta(HttpStatus.BAD_REQUEST, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Object> tratarNaoEncontrado(RecursoNaoEncontradoException exception, HttpServletRequest request) {
        return resposta(HttpStatus.NOT_FOUND, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<Object> tratarConflito(ConflitoException exception, HttpServletRequest request) {
        return resposta(HttpStatus.CONFLICT, exception.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({DataIntegrityViolationException.class, PessimisticLockingFailureException.class})
    public ResponseEntity<Object> tratarConflitoPersistencia(Exception exception, HttpServletRequest request) {
        return resposta(HttpStatus.CONFLICT, "Conflito de integridade ou operação concorrente", request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> tratarInesperado(Exception exception, HttpServletRequest request) {
        LOG.error("Erro interno em {}", request.getRequestURI(), exception);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao processar a requisição", request.getRequestURI());
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        String mensagem = status.is5xxServerError() ? "Erro interno ao processar a requisição"
                : status.value() == 404 ? "Recurso não encontrado" : "Requisição inválida";
        return new ResponseEntity<>(erro(status, mensagem, path), headers, status);
    }

    private ResponseEntity<Object> resposta(HttpStatus status, String mensagem, String path) {
        return ResponseEntity.status(status).body(erro(status, mensagem, path));
    }

    private ErroResposta erro(HttpStatusCode status, String mensagem, String path) {
        HttpStatus httpStatus = HttpStatus.resolve(status.value());
        return new ErroResposta(Instant.now(), status.value(),
                httpStatus == null ? "Erro" : httpStatus.getReasonPhrase(), mensagem, path);
    }
}
