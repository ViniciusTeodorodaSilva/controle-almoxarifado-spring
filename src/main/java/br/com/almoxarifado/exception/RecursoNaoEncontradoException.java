package br.com.almoxarifado.exception;

public class RecursoNaoEncontradoException extends IllegalArgumentException {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
