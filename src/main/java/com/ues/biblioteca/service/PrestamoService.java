package com.ues.biblioteca.service;

import com.ues.biblioteca.dto.PrestamoRequest;
import com.ues.biblioteca.dto.PrestamoResponse;
import com.ues.biblioteca.entity.Libro;
import com.ues.biblioteca.entity.Prestamo;
import com.ues.biblioteca.entity.Socio;
import com.ues.biblioteca.exception.BusinessException;
import com.ues.biblioteca.exception.ConflictException;
import com.ues.biblioteca.exception.ResourceNotFoundException;
import com.ues.biblioteca.repository.LibroRepository;
import com.ues.biblioteca.repository.PrestamoRepository;
import com.ues.biblioteca.repository.SocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final SocioRepository socioRepository;

    @Transactional(readOnly = true)
    public List<PrestamoResponse> listar(String estado, Integer socioId, Integer libroId) {
        Boolean devuelto = parsearEstado(estado);

        return prestamoRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .filter(prestamo -> devuelto == null || prestamo.getDevuelto().equals(devuelto))
                .filter(prestamo -> socioId == null || prestamo.getSocio().getId().equals(socioId))
                .filter(prestamo -> libroId == null || prestamo.getLibro().getId().equals(libroId))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PrestamoResponse obtener(Integer id) {
        return toResponse(buscar(id));
    }

    /**
     * Registra un prestamo. El libro queda marcado como no disponible.
     */
    @Transactional
    public PrestamoResponse crear(PrestamoRequest request) {
        Libro libro = libroRepository.findById(request.libroId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un libro con id " + request.libroId()));

        Socio socio = socioRepository.findById(request.socioId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un socio con id " + request.socioId()));

        if (!Boolean.TRUE.equals(libro.getDisponible())
                || prestamoRepository.existsByLibroIdAndDevueltoFalse(libro.getId())) {
            throw new ConflictException("El libro '" + libro.getTitulo()
                    + "' no esta disponible para prestamo");
        }

        Prestamo prestamo = new Prestamo();
        prestamo.setLibro(libro);
        prestamo.setSocio(socio);
        prestamo.setFechaPrestamo(request.fechaPrestamo() != null
                ? request.fechaPrestamo()
                : LocalDate.now());
        prestamo.setFechaDevolucion(null);
        prestamo.setDevuelto(false);

        libro.setDisponible(false);

        return toResponse(prestamoRepository.save(prestamo));
    }

    /**
     * Registra la devolucion de un prestamo y vuelve a dejar el libro disponible.
     */
    @Transactional
    public PrestamoResponse devolver(Integer id, LocalDate fechaDevolucion) {
        Prestamo prestamo = buscar(id);

        if (Boolean.TRUE.equals(prestamo.getDevuelto())) {
            throw new ConflictException("El prestamo con id " + id + " ya fue registrado como devuelto");
        }

        LocalDate fecha = fechaDevolucion != null ? fechaDevolucion : LocalDate.now();

        if (fecha.isBefore(prestamo.getFechaPrestamo())) {
            throw new BusinessException("La fecha de devolucion no puede ser anterior a la fecha del prestamo");
        }
        if (fecha.isAfter(LocalDate.now())) {
            throw new BusinessException("La fecha de devolucion no puede ser una fecha futura");
        }

        prestamo.setDevuelto(true);
        prestamo.setFechaDevolucion(fecha);
        prestamo.getLibro().setDisponible(true);

        return toResponse(prestamo);
    }

    @Transactional
    public void eliminar(Integer id) {
        Prestamo prestamo = buscar(id);
        if (!Boolean.TRUE.equals(prestamo.getDevuelto())) {
            throw new ConflictException("No se puede eliminar un prestamo activo. "
                    + "Registre primero la devolucion con PUT /api/prestamos/" + id + "/devolucion");
        }
        prestamoRepository.delete(prestamo);
    }

    private Prestamo buscar(Integer id) {
        return prestamoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un prestamo con id " + id));
    }

    private Boolean parsearEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return null;
        }
        return switch (estado.trim().toLowerCase()) {
            case "activo" -> false;
            case "devuelto" -> true;
            default -> throw new BusinessException(
                    "El estado debe ser 'activo' o 'devuelto'");
        };
    }

    private PrestamoResponse toResponse(Prestamo prestamo) {
        return new PrestamoResponse(
                prestamo.getId(),
                prestamo.getFechaPrestamo(),
                prestamo.getFechaDevolucion(),
                prestamo.getDevuelto(),
                prestamo.getLibro().getId(),
                prestamo.getLibro().getTitulo(),
                prestamo.getSocio().getId(),
                prestamo.getSocio().getNombre()
        );
    }
}
