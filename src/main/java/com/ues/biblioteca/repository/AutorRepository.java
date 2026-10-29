package com.ues.biblioteca.repository;

import com.ues.biblioteca.entity.Autor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutorRepository extends JpaRepository<Autor, Integer> {
}
