package br.com.almoxarifado.exception;

public class ConflitoException extends IllegalArgumentException {
    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
