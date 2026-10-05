package br.com.almoxarifado.security;
import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.*;
public interface UsuarioRepository extends JpaRepository<Usuario,Long> {
 Optional<Usuario> findByUsername(String username);
 @EntityGraph(attributePaths="funcionario") List<Usuario> findAllByOrderByIdAsc();
 @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select u from Usuario u order by u.id") List<Usuario> bloquearUsuarios();
}
