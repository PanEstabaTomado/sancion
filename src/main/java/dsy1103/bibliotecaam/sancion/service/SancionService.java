package dsy1103.bibliotecaam.sancion.service;

import dsy1103.bibliotecaam.sancion.dto.SancionRequestDTO;
import dsy1103.bibliotecaam.sancion.dto.SancionResponseDTO;
import dsy1103.bibliotecaam.sancion.model.Sancion;
import dsy1103.bibliotecaam.sancion.repository.SancionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SancionService {
    private final SancionRepository sancionRepository;

    private SancionResponseDTO mapToDOTO(Sancion sancion){
        if (sancion.isPagado()){
            String pagado = "Pagado";
            return new SancionResponseDTO(
                    sancion.getIdSancion(),
                    sancion.getFecIniSancion(),
                    sancion.getMontoMulta(),
                    sancion.getMotivo(),
                    pagado
            );
        } else {
            String pagado = "No pagado";
            return new SancionResponseDTO(
                    sancion.getIdSancion(),
                    sancion.getFecIniSancion(),
                    sancion.getMontoMulta(),
                    sancion.getMotivo(),
                    pagado
            );
        }

    }

    /*
    private void validarPrestamo(Long idPrestamo) {
        try {
            webClient.get()
                    .uri("/api/prestamo/{id}", idPrestamo)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info(">>> Prestamo {} validado correctamente (WebClient)", idPrestamo);

        } catch (WebClientResponseException.NotFound e) {
            throw new RuntimeException(
                    "El prestamo con id " + idPrestamo + " no existe en Prestamo.");
        } catch (Exception e) {
            throw new RuntimeException(
                    "No se puede conectar con prestamo: " + e.getMessage());
        }
    }
     */


    /*
    * * ---------------------- C R U D ---------------------
     */

    public List<SancionResponseDTO> obtenerTodas() {
        return sancionRepository.findAll().stream()
                .map(this::mapToDOTO).collect(Collectors.toList());
    }

    public Optional<SancionResponseDTO> obtenerPorId(Long id) {
        return sancionRepository.findById(id).map(this::mapToDOTO);
    }

    public SancionResponseDTO guardar(SancionRequestDTO dto) {
        // validarEspecialidad(dto.getEspecialidadId());

        Sancion m = new Sancion(
                null,
                dto.getFecIniSancion(),
                dto.getMontoMulta(),
                dto.getMotivo(),
                dto.isPagado());
                // dto.getEspecialidadId());
        return mapToDOTO(sancionRepository.save(m));
    }

    public Optional<SancionResponseDTO> actualizar(Long id, SancionRequestDTO dto) {
        return sancionRepository.findById(id).map(existente -> {
            //validarEspecialidad(dto.getEspecialidadId());
            existente.setFecIniSancion(dto.getFecIniSancion());
            existente.setMontoMulta(dto.getMontoMulta());
            existente.setMotivo(dto.getMotivo());
            existente.setPagado(dto.isPagado());
            return mapToDOTO(sancionRepository.save(existente));
        });
    }

    public void eliminar(Long id) {
        sancionRepository.deleteById(id);
    }

    // ------------------------------------------------------------------------
    // * * Funciones Extras * *
    // ------------------------------------------------------------------------

    public List<SancionResponseDTO> obtenerSancionesNoPagadas() {
        return sancionRepository.findSancionesNoPagadas().stream()
                .map(this::mapToDOTO).collect(Collectors.toList());
    }

    public List<SancionResponseDTO> obtenerSancionesPagadas() {
        return sancionRepository.findSancionesPagadas().stream()
                .map(this::mapToDOTO).collect(Collectors.toList());
    }

    public List<SancionResponseDTO> obtenerPorFecha(LocalDate fecha) {
        return sancionRepository.findSancionesPorFecha(fecha).stream()
                .map(this::mapToDOTO)
                .collect(Collectors.toList());
    }
}