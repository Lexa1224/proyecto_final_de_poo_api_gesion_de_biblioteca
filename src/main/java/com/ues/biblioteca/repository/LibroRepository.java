package com.ues.biblioteca.repository;

import com.ues.biblioteca.entity.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroRepository extends JpaRepository<Libro, Integer> {

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Integer id);

    boolean existsByAutorId(Integer autorId);
}
