package br.com.almoxarifado.dto;
public record AlertaEstoqueResponse(Integer estoqueId, Integer produtoId, String codigo, String produto,
        Integer almoxarifadoId, String almoxarifado, String unidade, double saldoAtual,
        Double estoqueMinimo, Double estoqueMaximo, Double quantidadeSugerida) {}
