package br.com.almoxarifado.security;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AuditoriaRepository extends JpaRepository<EventoAuditoria,Long> {}
