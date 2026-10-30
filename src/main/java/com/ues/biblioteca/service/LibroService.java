package com.ues.biblioteca.service;

import com.ues.biblioteca.dto.LibroRequest;
import com.ues.biblioteca.dto.LibroResponse;
import com.ues.biblioteca.entity.Autor;
import com.ues.biblioteca.entity.Libro;
import com.ues.biblioteca.exception.ConflictException;
import com.ues.biblioteca.exception.ResourceNotFoundException;
import com.ues.biblioteca.repository.AutorRepository;
import com.ues.biblioteca.repository.LibroRepository;
import com.ues.biblioteca.repository.PrestamoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibroService {

    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;
    private final PrestamoRepository prestamoRepository;

    @Transactional(readOnly = true)
    public List<LibroResponse> listar(Boolean disponible, Integer autorId, String titulo) {
        String filtroTitulo = titulo == null ? null : titulo.trim().toLowerCase();

        return libroRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .filter(libro -> disponible == null || libro.getDisponible().equals(disponible))
                .filter(libro -> autorId == null || libro.getAutor().getId().equals(autorId))
                .filter(libro -> filtroTitulo == null
                        || libro.getTitulo().toLowerCase().contains(filtroTitulo))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LibroResponse obtener(Integer id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public LibroResponse crear(LibroRequest request) {
        if (libroRepository.existsByIsbn(request.isbn().trim())) {
            throw new ConflictException("Ya existe un libro registrado con el ISBN " + request.isbn().trim());
        }

        Libro libro = new Libro();
        libro.setDisponible(true);
        aplicarDatos(libro, request);
        return toResponse(libroRepository.save(libro));
    }

    @Transactional
    public LibroResponse actualizar(Integer id, LibroRequest request) {
        Libro libro = buscar(id);

        if (libroRepository.existsByIsbnAndIdNot(request.isbn().trim(), id)) {
            throw new ConflictException("Ya existe otro libro registrado con el ISBN " + request.isbn().trim());
        }

        aplicarDatos(libro, request);
        return toResponse(libroRepository.save(libro));
    }

    @Transactional
    public void eliminar(Integer id) {
        Libro libro = buscar(id);
        if (prestamoRepository.existsByLibroId(id)) {
            throw new ConflictException("No se puede eliminar el libro '" + libro.getTitulo()
                    + "' porque tiene prestamos registrados");
        }
        libroRepository.delete(libro);
    }

    private Libro buscar(Integer id) {
        return libroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un libro con id " + id));
    }

    private void aplicarDatos(Libro libro, LibroRequest request) {
        Autor autor = autorRepository.findById(request.autorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un autor con id " + request.autorId()));

        libro.setTitulo(request.titulo().trim());
        libro.setIsbn(request.isbn().trim());
        libro.setAnioPublicacion(request.anioPublicacion());
        libro.setEditorial(request.editorial().trim());
        libro.setAutor(autor);
    }

    private LibroResponse toResponse(Libro libro) {
        return new LibroResponse(
                libro.getId(),
                libro.getTitulo(),
                libro.getIsbn(),
                libro.getAnioPublicacion(),
                libro.getEditorial(),
                libro.getDisponible(),
                libro.getAutor().getId(),
                libro.getAutor().getNombre()
        );
    }
}
