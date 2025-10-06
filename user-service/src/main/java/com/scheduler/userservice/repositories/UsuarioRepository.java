package com.scheduler.userservice.repositories;

import com.scheduler.userservice.entities.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findById(Long id);
}