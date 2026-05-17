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

    private final WebClient webClientUsuario;

    private final WebClient webClientLibro;

    private SancionResponseDTO mapToDOTO(Sancion sancion){
        if (sancion.getPagado().equals(true)){
            String pagado = "Pagado";
            return new SancionResponseDTO(
                    sancion.getIdSancion(),
                    sancion.getFecIniSancion(),
                    sancion.getMontoMulta(),
                    sancion.getMotivo(),
                    pagado,
                    sancion.getIdUsuario(),
                    sancion.getIdLibro()
            );
        } else {
            String pagado = "No pagado";
            return new SancionResponseDTO(
                    sancion.getIdSancion(),
                    sancion.getFecIniSancion(),
                    sancion.getMontoMulta(),
                    sancion.getMotivo(),
                    pagado,
                    sancion.getIdUsuario(),
                    sancion.getIdLibro()
            );
        }

    }


    private void validarUsuario(Long idUsuario) {
        try {
            webClientUsuario.get()
                    .uri("/api/bibliotecaam/usuario/{id}", idUsuario)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info(">>> Usuario {} validado correctamente (WebClient)", idUsuario);

        } catch (WebClientResponseException.NotFound e) {
            throw new RuntimeException(
                    "El usuario con id " + idUsuario + " no existe en la BBDD de Usuario.");
        } catch (Exception e) {
            throw new RuntimeException(
                    "ERROR - No se puede conectar con la BBDD de Usuario: " + e.getMessage());
        }
    }

    private void validarLibro(Long idLibro) {
        try {
            webClientLibro.get()
                    .uri("/api/bibliotecaam/libro/{id}", idLibro)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info(">>> Libro {} validado correctamente (WebClient)", idLibro);

        } catch (WebClientResponseException.NotFound e) {
            throw new RuntimeException(
                    "El libro con id " + idLibro + " no existe en la BBDD de Libro.");
        } catch (Exception e) {
            throw new RuntimeException(
                    "ERROR - No se puede conectar con la BBDD de Libro: " + e.getMessage());
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
        validarUsuario(dto.getIdUsuario());

        validarLibro(dto.getIdLibro());

        Sancion m = new Sancion(
                null,
                dto.getFecIniSancion(),
                dto.getMontoMulta(),
                dto.getMotivo(),
                dto.getPagado(),
                dto.getIdUsuario(),
                dto.getIdLibro());
        return mapToDOTO(sancionRepository.save(m));
    }

    public Optional<SancionResponseDTO> actualizar(Long id, SancionRequestDTO dto) {
        return sancionRepository.findById(id).map(existente -> {
            validarUsuario(dto.getIdUsuario());

            validarLibro(dto.getIdLibro());

            existente.setFecIniSancion(dto.getFecIniSancion());
            existente.setMontoMulta(dto.getMontoMulta());
            existente.setMotivo(dto.getMotivo());
            existente.setPagado(dto.getPagado());
            existente.setIdUsuario(dto.getIdUsuario());
            existente.setIdLibro(dto.getIdLibro());
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