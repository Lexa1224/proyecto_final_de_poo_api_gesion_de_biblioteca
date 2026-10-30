package com.ues.biblioteca.service;

import com.ues.biblioteca.dto.AutorRequest;
import com.ues.biblioteca.dto.AutorResponse;
import com.ues.biblioteca.entity.Autor;
import com.ues.biblioteca.exception.ConflictException;
import com.ues.biblioteca.exception.ResourceNotFoundException;
import com.ues.biblioteca.repository.AutorRepository;
import com.ues.biblioteca.repository.LibroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AutorService {

    private final AutorRepository autorRepository;
    private final LibroRepository libroRepository;

    @Transactional(readOnly = true)
    public List<AutorResponse> listar() {
        return autorRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AutorResponse obtener(Integer id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public AutorResponse crear(AutorRequest request) {
        Autor autor = new Autor();
        aplicarDatos(autor, request);
        return toResponse(autorRepository.save(autor));
    }

    @Transactional
    public AutorResponse actualizar(Integer id, AutorRequest request) {
        Autor autor = buscar(id);
        aplicarDatos(autor, request);
        return toResponse(autorRepository.save(autor));
    }

    @Transactional
    public void eliminar(Integer id) {
        Autor autor = buscar(id);
        if (libroRepository.existsByAutorId(id)) {
            throw new ConflictException("No se puede eliminar el autor '" + autor.getNombre()
                    + "' porque tiene libros registrados");
        }
        autorRepository.delete(autor);
    }

    private Autor buscar(Integer id) {
        return autorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un autor con id " + id));
    }

    private void aplicarDatos(Autor autor, AutorRequest request) {
        autor.setNombre(request.nombre().trim());
        autor.setNacionalidad(request.nacionalidad().trim());
    }

    private AutorResponse toResponse(Autor autor) {
        return new AutorResponse(autor.getId(), autor.getNombre(), autor.getNacionalidad());
    }
}
