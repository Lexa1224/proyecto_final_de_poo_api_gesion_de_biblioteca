package com.ues.biblioteca.repository;

import com.ues.biblioteca.entity.Socio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocioRepository extends JpaRepository<Socio, Integer> {

    boolean existsByNumeroSocio(String numeroSocio);

    boolean existsByNumeroSocioAndIdNot(String numeroSocio, Integer id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Integer id);
}
