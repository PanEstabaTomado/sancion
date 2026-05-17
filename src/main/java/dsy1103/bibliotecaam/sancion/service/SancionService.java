package dsy1103.bibliotecaam.sancion.service;

import dsy1103.bibliotecaam.sancion.dto.SancionRequestDTO;
import dsy1103.bibliotecaam.sancion.dto.SancionResponseDTO;
import dsy1103.bibliotecaam.sancion.model.Sancion;
import dsy1103.bibliotecaam.sancion.repository.SancionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SancionService {
    private final SancionRepository sancionRepository;

    private final WebClient webClient;

    private SancionResponseDTO mapToDOTO(Sancion sancion){
        if (sancion.getPagado().equals(true)){
            String pagado = "Pagado";
            return new SancionResponseDTO(
                    sancion.getIdSancion(),
                    sancion.getFecIniSancion(),
                    sancion.getMontoMulta(),
                    sancion.getMotivo(),
                    pagado,
                    sancion.getIdPrestamo()
            );
        } else {
            String pagado = "No pagado";
            return new SancionResponseDTO(
                    sancion.getIdSancion(),
                    sancion.getFecIniSancion(),
                    sancion.getMontoMulta(),
                    sancion.getMotivo(),
                    pagado,
                    sancion.getIdPrestamo()
            );
        }

    }


    private void validarPrestamo(Long idPrestamo) {
        try {
            webClient.get()
                    .uri("/api/bibliotecaam/prestamo/{id}", idPrestamo)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info(">>> Prestamo {} validado correctamente (WebClient)", idPrestamo);

        } catch (WebClientResponseException.NotFound e) {
            throw new RuntimeException(
                    "El prestamo con id " + idPrestamo + " no existe en la BBDD de Prestamo.");
        } catch (Exception e) {
            throw new RuntimeException(
                    "ERROR - No se puede conectar con la BBDD de Prestamo: " + e.getMessage());
        }
    }



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
        validarPrestamo(dto.getIdPrestamo());
        Sancion m = new Sancion(
                null,
                dto.getFecIniSancion(),
                dto.getMontoMulta(),
                dto.getMotivo(),
                dto.getPagado(),
                dto.getIdPrestamo());
                // dto.getEspecialidadId());
        return mapToDOTO(sancionRepository.save(m));
    }

    public Optional<SancionResponseDTO> actualizar(Long id, SancionRequestDTO dto) {
        return sancionRepository.findById(id).map(existente -> {
            validarPrestamo(dto.getIdPrestamo());
            existente.setFecIniSancion(dto.getFecIniSancion());
            existente.setMontoMulta(dto.getMontoMulta());
            existente.setMotivo(dto.getMotivo());
            existente.setPagado(dto.getPagado());
            existente.setIdPrestamo(dto.getIdPrestamo());
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