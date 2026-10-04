package br.com.almoxarifado.dto;

import java.time.Instant;

public record ErroResposta(Instant timestamp, int status, String erro, String mensagem, String path) {
}
