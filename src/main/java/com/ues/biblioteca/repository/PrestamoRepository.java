package com.ues.biblioteca.repository;

import com.ues.biblioteca.entity.Prestamo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrestamoRepository extends JpaRepository<Prestamo, Integer> {

    boolean existsByLibroId(Integer libroId);

    boolean existsBySocioId(Integer socioId);

    boolean existsByLibroIdAndDevueltoFalse(Integer libroId);

    Optional<Prestamo> findByLibroIdAndDevueltoFalse(Integer libroId);
}
