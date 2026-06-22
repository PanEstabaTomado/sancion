package dsy1103.bibliotecaam.sancion;

import dsy1103.bibliotecaam.sancion.assembler.SancionModelAssembler;
import dsy1103.bibliotecaam.sancion.controller.SancionController;
import dsy1103.bibliotecaam.sancion.dto.SancionRequestDTO;
import dsy1103.bibliotecaam.sancion.dto.SancionResponseDTO;
import dsy1103.bibliotecaam.sancion.service.SancionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SancionController.class)
@ActiveProfiles("test")
@DisplayName("Tests Unitarios - SancionController")
class SancionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SancionService sancionService;

    @MockitoBean
    private SancionModelAssembler assembler;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    private SancionResponseDTO s1;
    private SancionResponseDTO s2;

    @BeforeEach
    void setUp() {
        s1 = new SancionResponseDTO(1L, LocalDate.now(), 5000, "Entrega atrasada", "No Pagado", 100L, 500L);
        s2 = new SancionResponseDTO(2L, LocalDate.now(), 3000, "Libro dañado", "Pagado", 101L, 501L);

        Mockito.when(assembler.toModel(eq(s1))).thenReturn(
                EntityModel.of(s1, linkTo(methodOn(SancionController.class).obtenerPorId(1L)).withSelfRel())
        );
        Mockito.when(assembler.toModel(eq(s2))).thenReturn(
                EntityModel.of(s2, linkTo(methodOn(SancionController.class).obtenerPorId(2L)).withSelfRel())
        );
    }

    @Test
    @DisplayName("GIVEN: Existen sanciones WHEN: GET /api/bibliotecaam/sancion THEN: Retorna 200 OK y HAL-JSON con links")
    void shouldReturnAllSanciones() throws Exception {
        List<SancionResponseDTO> lista = Arrays.asList(s1, s2);
        Mockito.when(sancionService.obtenerTodas()).thenReturn(lista);

        mockMvc.perform(get("/api/bibliotecaam/sancion")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("$._embedded.sancionResponseDTOList[0].idSancion").value(1L))
                .andExpect(jsonPath("$._embedded.sancionResponseDTOList[0].pagado").value("No Pagado"))
                .andExpect(jsonPath("$._embedded.sancionResponseDTOList[1].idSancion").value(2L))
                .andExpect(jsonPath("$._embedded.sancionResponseDTOList[1].montoMulta").value(3000))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("GIVEN: ID válido WHEN: GET /api/bibliotecaam/sancion/{id} THEN: Retorna el modelo de la sanción")
    void shouldReturnSancionById() throws Exception {
        Long id = 1L;
        Mockito.when(sancionService.obtenerPorId(id)).thenReturn(Optional.of(s1));

        mockMvc.perform(get("/api/bibliotecaam/sancion/{id}", id)
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSancion").value(id))
                .andExpect(jsonPath("$.motivo").value("Entrega atrasada"))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("GIVEN: ID inexistente WHEN: GET /api/bibliotecaam/sancion/{id} THEN: Retorna 404 Not Found")
    void shouldReturnNotFoundWhenSancionDoesNotExist() throws Exception {
        Long id = 99L;
        Mockito.when(sancionService.obtenerPorId(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/bibliotecaam/sancion/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GIVEN: Sanciones pagadas WHEN: GET /api/bibliotecaam/sancion/pagado THEN: Retorna la colección correspondiente")
    void shouldReturnSancionesPagadas() throws Exception {
        Mockito.when(sancionService.obtenerSancionesPagadas()).thenReturn(Arrays.asList(s2));

        mockMvc.perform(get("/api/bibliotecaam/sancion/pagado")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.sancionResponseDTOList[0].idSancion").value(2L))
                .andExpect(jsonPath("$._embedded.sancionResponseDTOList[0].pagado").value("Pagado"));
    }

    @Test
    @DisplayName("GIVEN: Sanciones impagadas WHEN: GET /api/bibliotecaam/sancion/nopagado THEN: Retorna la colección correspondiente")
    void shouldReturnSancionesNoPagadas() throws Exception {
        Mockito.when(sancionService.obtenerSancionesNoPagadas()).thenReturn(Arrays.asList(s1));

        mockMvc.perform(get("/api/bibliotecaam/sancion/nopagado")
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.sancionResponseDTOList[0].idSancion").value(1L))
                .andExpect(jsonPath("$._embedded.sancionResponseDTOList[0].pagado").value("No Pagado"));
    }

    @Test
    @DisplayName("GIVEN: Una fecha WHEN: GET /api/bibliotecaam/sancion/porfecha THEN: Filtra pasándola como Query Param")
    void shouldReturnSancionesByFecha() throws Exception {
        LocalDate fechaBúsqueda = LocalDate.now();
        Mockito.when(sancionService.obtenerPorFecha(fechaBúsqueda)).thenReturn(Arrays.asList(s1));

        mockMvc.perform(get("/api/bibliotecaam/sancion/porfecha")
                        .param("fecha", fechaBúsqueda.toString())
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.sancionResponseDTOList.length()").value(1));
    }

    @Test
    @DisplayName("GIVEN: Request válido WHEN: POST /api/bibliotecaam/sancion THEN: Crea registro y retorna 201 Created")
    void shouldCreateSancion() throws Exception {
        SancionRequestDTO request = new SancionRequestDTO(LocalDate.now(), 5000, "Entrega atrasada", false, 100L, 500L);
        Mockito.when(sancionService.guardar(any(SancionRequestDTO.class))).thenReturn(s1);

        mockMvc.perform(post("/api/bibliotecaam/sancion")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idSancion").value(1L))
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    @Test
    @DisplayName("GIVEN: ID y Request válidos WHEN: PUT /api/bibliotecaam/sancion/{id} THEN: Modifica exitosamente")
    void shouldUpdateSancion() throws Exception {
        Long id = 1L;
        SancionRequestDTO request = new SancionRequestDTO(LocalDate.now(), 5000, "Entrega atrasada", true, 100L, 500L);
        Mockito.when(sancionService.actualizar(eq(id), any(SancionRequestDTO.class))).thenReturn(Optional.of(s1));

        mockMvc.perform(put("/api/bibliotecaam/sancion/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaTypes.HAL_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idSancion").value(id));
    }

    @Test
    @DisplayName("GIVEN: ID existente WHEN: DELETE /api/bibliotecaam/sancion/{id} THEN: Elimina y retorna JSON con éxito en 204 No Content")
    void shouldDeleteSancionSuccessfully() throws Exception {
        Long id = 1L;
        Mockito.when(sancionService.obtenerPorId(id)).thenReturn(Optional.of(s1));
        Mockito.doNothing().when(sancionService).eliminar(id);

        mockMvc.perform(delete("/api/bibliotecaam/sancion/{id}", id))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.['¡EXITO! ']").value("¡La sancion fue eliminada con exito!"));
    }

    @Test
    @DisplayName("GIVEN: ID inexistente WHEN: DELETE /api/bibliotecaam/sancion/{id} THEN: No elimina y retorna JSON de error en 204 No Content")
    void shouldReturnErrorWhenDeletingNonExistentSancion() throws Exception {
        Long id = 99L;
        Mockito.when(sancionService.obtenerPorId(id)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/bibliotecaam/sancion/{id}", id))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.['¡ERROR! ']").value("¡La sancion con id 99 no fue encontrada!"));
    }
}