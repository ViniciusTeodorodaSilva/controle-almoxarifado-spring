package br.com.almoxarifado.repository;

import br.com.almoxarifado.model.Solicitacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitacaoRepository extends JpaRepository<Solicitacao, Integer> {
}
