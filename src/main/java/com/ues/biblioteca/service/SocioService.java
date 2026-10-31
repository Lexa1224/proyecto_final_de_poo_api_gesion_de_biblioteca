package com.ues.biblioteca.service;

import com.ues.biblioteca.dto.SocioRequest;
import com.ues.biblioteca.dto.SocioResponse;
import com.ues.biblioteca.entity.Socio;
import com.ues.biblioteca.exception.ConflictException;
import com.ues.biblioteca.exception.ResourceNotFoundException;
import com.ues.biblioteca.repository.PrestamoRepository;
import com.ues.biblioteca.repository.SocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SocioService {

    private final SocioRepository socioRepository;
    private final PrestamoRepository prestamoRepository;

    @Transactional(readOnly = true)
    public List<SocioResponse> listar() {
        return socioRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SocioResponse obtener(Integer id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public SocioResponse crear(SocioRequest request) {
        validarDuplicados(request, null);

        Socio socio = new Socio();
        aplicarDatos(socio, request);
        return toResponse(socioRepository.save(socio));
    }

    @Transactional
    public SocioResponse actualizar(Integer id, SocioRequest request) {
        Socio socio = buscar(id);
        validarDuplicados(request, id);

        aplicarDatos(socio, request);
        return toResponse(socioRepository.save(socio));
    }

    @Transactional
    public void eliminar(Integer id) {
        Socio socio = buscar(id);
        if (prestamoRepository.existsBySocioId(id)) {
            throw new ConflictException("No se puede eliminar el socio '" + socio.getNombre()
                    + "' porque tiene prestamos registrados");
        }
        socioRepository.delete(socio);
    }

    private void validarDuplicados(SocioRequest request, Integer id) {
        boolean numeroDuplicado = id == null
                ? socioRepository.existsByNumeroSocio(request.numeroSocio().trim())
                : socioRepository.existsByNumeroSocioAndIdNot(request.numeroSocio().trim(), id);
        if (numeroDuplicado) {
            throw new ConflictException("Ya existe un socio con el numero de socio " + request.numeroSocio().trim());
        }

        boolean emailDuplicado = id == null
                ? socioRepository.existsByEmail(request.email().trim())
                : socioRepository.existsByEmailAndIdNot(request.email().trim(), id);
        if (emailDuplicado) {
            throw new ConflictException("Ya existe un socio con el email " + request.email().trim());
        }
    }

    private Socio buscar(Integer id) {
        return socioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un socio con id " + id));
    }

    private void aplicarDatos(Socio socio, SocioRequest request) {
        socio.setNombre(request.nombre().trim());
        socio.setNumeroSocio(request.numeroSocio().trim());
        socio.setEmail(request.email().trim().toLowerCase());
    }

    private SocioResponse toResponse(Socio socio) {
        return new SocioResponse(
                socio.getId(),
                socio.getNombre(),
                socio.getNumeroSocio(),
                socio.getEmail()
        );
    }
}
