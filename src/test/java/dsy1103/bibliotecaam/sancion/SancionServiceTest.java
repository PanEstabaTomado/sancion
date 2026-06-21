package dsy1103.bibliotecaam.sancion;

import dsy1103.bibliotecaam.sancion.dto.SancionRequestDTO;
import dsy1103.bibliotecaam.sancion.dto.SancionResponseDTO;
import dsy1103.bibliotecaam.sancion.model.Sancion;
import dsy1103.bibliotecaam.sancion.repository.SancionRepository;
import dsy1103.bibliotecaam.sancion.service.SancionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest(classes = SancionService.class)
@ActiveProfiles("test")
@DisplayName("Tests Unitarios - SancionService")
class SancionServiceTest {

    @Autowired
    private SancionService sancionService;

    @MockitoBean
    private SancionRepository sancionRepository;

    @MockitoBean(name = "webClientUsuario")
    private WebClient webClientUsuario;

    @MockitoBean(name = "webClientLibro")
    private WebClient webClientLibro;

    // Estructuras auxiliares para simular la API fluida de WebClient
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;
    private WebClient.ResponseSpec responseSpecMock;

    @BeforeEach
    void setUp() {
        requestHeadersUriSpecMock = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);
        responseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);
    }

    @SuppressWarnings("unchecked")
    private void mockWebClientSuccess(WebClient webClientMock, String uri, Long id) {
        Mockito.when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        Mockito.when(requestHeadersUriSpecMock.uri(eq(uri), eq(id))).thenReturn(requestHeadersSpecMock);
        Mockito.when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        Mockito.when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.just("OK"));
    }

    @SuppressWarnings("unchecked")
    private void mockWebClientException(WebClient webClientMock, String uri, Long id, Throwable exception) {
        Mockito.when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        Mockito.when(requestHeadersUriSpecMock.uri(eq(uri), eq(id))).thenReturn(requestHeadersSpecMock);
        Mockito.when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        Mockito.when(responseSpecMock.bodyToMono(String.class)).thenReturn(Mono.error(exception));
    }

    @Test
    @DisplayName("GIVEN: Existen sanciones WHEN: obtenerTodas THEN: Retorna lista con strings de pago bien mapeados")
    void shouldReturnAllSanciones() {
        List<Sancion> mockSanciones = Arrays.asList(
                new Sancion(1L, LocalDate.now(), 5000, "Retraso", true, 10L, 100L),
                new Sancion(2L, LocalDate.now(), 2500, "Daño menor", false, 11L, 101L)
        );
        Mockito.when(sancionRepository.findAll()).thenReturn(mockSanciones);

        List<SancionResponseDTO> resultado =  sancionService.obtenerTodas();

        assertEquals(2, resultado.size());
        assertEquals("Pagado", resultado.get(0).getPagado());
        assertEquals("No pagado", resultado.get(1).getPagado());
    }

    @Test
    @DisplayName("GIVEN: ID existente WHEN: obtenerPorId THEN: Retorna Optional con el DTO mapeado")
    void shouldReturnSancionById() {
        Long id = 1L;
        Sancion sancion = new Sancion(id, LocalDate.now(), 4000, "Perdida de hoja", false, 10L, 100L);
        Mockito.when(sancionRepository.findById(id)).thenReturn(Optional.of(sancion));

        Optional<SancionResponseDTO> resultado = sancionService.obtenerPorId(id);

        assertTrue(resultado.isPresent());
        assertEquals("No pagado", resultado.get().getPagado());
        assertEquals(4000, resultado.get().getMontoMulta());
    }

    @Test
    @DisplayName("GIVEN: Request válido WHEN: guardar THEN: Valida entidades remotas y persiste en BBDD")
    void shouldSaveSancionSuccessfully() {
        SancionRequestDTO request = new SancionRequestDTO(LocalDate.now(), 3000, "Atraso de 3 dias", true, 10L, 100L);
        Sancion guardada = new Sancion(1L, LocalDate.now(), 3000, "Atraso de 3 dias", true, 10L, 100L);

        mockWebClientSuccess(webClientUsuario, "/api/bibliotecaam/usuario/{id}", 10L);
        mockWebClientSuccess(webClientLibro, "/api/bibliotecaam/libro/{id}", 100L);
        Mockito.when(sancionRepository.save(any(Sancion.class))).thenReturn(guardada);

        SancionResponseDTO resultado = sancionService.guardar(request);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdSancion());
        assertEquals("Pagado", resultado.getPagado());
        Mockito.verify(sancionRepository, Mockito.times(1)).save(any(Sancion.class));
    }

    @Test
    @DisplayName("GIVEN: ID de usuario inexistente WHEN: guardar THEN: Lanza RuntimeException de negocio y detiene flujo")
    void shouldThrowExceptionWhenUsuarioNotFound() {
        SancionRequestDTO request = new SancionRequestDTO(LocalDate.now(), 1500, "Incidencia", false, 99L, 100L);

        WebClientResponseException.NotFound notFoundEx = Mockito.mock(WebClientResponseException.NotFound.class);
        mockWebClientException(webClientUsuario, "/api/bibliotecaam/usuario/{id}", 99L, notFoundEx);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> sancionService.guardar(request));
        assertTrue(exception.getMessage().contains("El usuario con id 99 no existe en la BBDD de Usuario."));
        Mockito.verify(sancionRepository, Mockito.never()).save(any(Sancion.class));
    }

    @Test
    @DisplayName("GIVEN: ID de libro inexistente WHEN: guardar THEN: Valida usuario con éxito, pero falla al validar libro")
    void shouldThrowExceptionWhenLibroNotFound() {
        SancionRequestDTO request = new SancionRequestDTO(LocalDate.now(), 1500, "Incidencia", false, 10L, 999L);

        mockWebClientSuccess(webClientUsuario, "/api/bibliotecaam/usuario/{id}", 10L);
        WebClientResponseException.NotFound notFoundEx = Mockito.mock(WebClientResponseException.NotFound.class);
        mockWebClientException(webClientLibro, "/api/bibliotecaam/libro/{id}", 999L, notFoundEx);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> sancionService.guardar(request));
        assertTrue(exception.getMessage().contains("El libro con id 999 no existe en la BBDD de Libro."));
        Mockito.verify(sancionRepository, Mockito.never()).save(any(Sancion.class));
    }

    @Test
    @DisplayName("GIVEN: ID y DTO válidos WHEN: actualizar THEN: Modifica el registro existente")
    void shouldUpdateSancionSuccessfully() {
        Long id = 1L;
        Sancion existente = new Sancion(id, LocalDate.now(), 1000, "Mala entrega", false, 10L, 100L);
        SancionRequestDTO request = new SancionRequestDTO(LocalDate.now(), 1500, "Mala entrega definitiva", true, 10L, 100L);
        Sancion modificada = new Sancion(id, LocalDate.now(), 1500, "Mala entrega definitiva", true, 10L, 100L);

        Mockito.when(sancionRepository.findById(id)).thenReturn(Optional.of(existente));
        mockWebClientSuccess(webClientUsuario, "/api/bibliotecaam/usuario/{id}", 10L);
        mockWebClientSuccess(webClientLibro, "/api/bibliotecaam/libro/{id}", 100L);
        Mockito.when(sancionRepository.save(any(Sancion.class))).thenReturn(modificada);

        Optional<SancionResponseDTO> resultado = sancionService.actualizar(id, request);

        assertTrue(resultado.isPresent());
        assertEquals("Pagado", resultado.get().getPagado());
        assertEquals(1500, resultado.get().getMontoMulta());
        assertEquals("Mala entrega definitiva", resultado.get().getMotivo());
    }

    @Test
    @DisplayName("GIVEN: ID de sanción WHEN: eliminar THEN: Ejecuta deleteById en repositorio")
    void shouldDeleteSancion() {
        Long id = 1L;
        Mockito.doNothing().when(sancionRepository).deleteById(id);

        assertDoesNotThrow(() -> sancionService.eliminar(id));
        Mockito.verify(sancionRepository, Mockito.times(1)).deleteById(id);
    }

    @Test
    @DisplayName("GIVEN: Sanciones pendientes WHEN: obtenerSancionesNoPagadas THEN: Retorna registros mapeados a 'No pagado'")
    void shouldReturnSancionesNoPagadas() {
        List<Sancion> mockNoPagadas = Arrays.asList(
                new Sancion(3L, LocalDate.now(), 8000, "Atraso grave", false, 12L, 105L)
        );
        Mockito.when(sancionRepository.findSancionesNoPagadas()).thenReturn(mockNoPagadas);

        List<SancionResponseDTO> resultado = sancionService.obtenerSancionesNoPagadas();

        assertEquals(1, resultado.size());
        assertEquals("No pagado", resultado.get(0).getPagado());
    }

    @Test
    @DisplayName("GIVEN: Sanciones solventadas WHEN: obtenerSancionesPagadas THEN: Retorna registros mapeados a 'Pagado'")
    void shouldReturnSancionesPagadas() {
        List<Sancion> mockPagadas = Arrays.asList(
                new Sancion(4L, LocalDate.now(), 5000, "Atraso solventado", true, 14L, 109L)
        );
        Mockito.when(sancionRepository.findSancionesPagadas()).thenReturn(mockPagadas);

        List<SancionResponseDTO> resultado = sancionService.obtenerSancionesPagadas();

        assertEquals(1, resultado.size());
        assertEquals("Pagado", resultado.get(0).getPagado());
    }

    @Test
    @DisplayName("GIVEN: Una fecha concreta WHEN: obtenerPorFecha THEN: Retorna sanciones asociadas a ese dia")
    void shouldReturnSancionesByFecha() {
        LocalDate fechaFiltro = LocalDate.now();
        List<Sancion> mockPorFecha = Arrays.asList(
                new Sancion(1L, fechaFiltro, 2000, "Incidencia", true, 10L, 100L)
        );
        Mockito.when(sancionRepository.findSancionesPorFecha(fechaFiltro)).thenReturn(mockPorFecha);

        List<SancionResponseDTO> resultado = sancionService.obtenerPorFecha(fechaFiltro);

        assertEquals(1, resultado.size());
        assertEquals(fechaFiltro, resultado.get(0).getFecIniSancion());
    }
}