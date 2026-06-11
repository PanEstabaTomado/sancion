package dsy1103.bibliotecaam.sancion.controller;

import dsy1103.bibliotecaam.sancion.dto.SancionRequestDTO;
import dsy1103.bibliotecaam.sancion.dto.SancionResponseDTO;
import dsy1103.bibliotecaam.sancion.model.Sancion;
import dsy1103.bibliotecaam.sancion.service.SancionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bibliotecaam/sancion")
@RequiredArgsConstructor
@Tag(name = "Sancion", description = "Operaciones asociadas a sanciones.")
public class SancionController {

    private final SancionService sancionService;

    @GetMapping
    @Operation(summary = "Obtener todas las sancion", description = "Obtiene una lista de todas las sancinoes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacion exitosa"),
            @ApiResponse(responseCode = "404", description = "Sancion no encontrada")
    })
    public ResponseEntity<List<SancionResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(sancionService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener sancion por id", description = "Obtiene una sancion acorde a una id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "404", description = "Sancion no encontrada")
    })
    public ResponseEntity<SancionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return sancionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pagado")
    @Operation(summary = "Obtener sanciones acorde a si estan pagadas", description = "Obtiene una sancion acorde a si esta pagada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "404", description = "Sancion no encontrada")
    })
    public ResponseEntity<List<SancionResponseDTO>> obtenerSancionPagada() {
        return ResponseEntity.ok(sancionService.obtenerSancionesPagadas());
    }

    @GetMapping("/nopagado")
    @Operation(summary = "Obtener sanciones acorde a si no estan pagadas", description = "Obtiene una sancion acorde a si no esta pagada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "404", description = "Sancion no encontrada")
    })
    public ResponseEntity<List<SancionResponseDTO>> obtenerSancionNoPagada() {
            return ResponseEntity.ok(sancionService.obtenerSancionesNoPagadas());
        }

    @GetMapping("/porfecha")
    @Operation(summary = "Obtener sanciones acorde a una fecha", description = "Obtiene una sancion acorde a una fecha.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa"),
            @ApiResponse(responseCode = "404", description = "Sancion no encontrada")
    })
    public ResponseEntity<List<SancionResponseDTO>> obtenerPorFecha(LocalDate fecha){
        return ResponseEntity.ok(sancionService.obtenerPorFecha(fecha));
    }

    @PostMapping
    @Operation(summary = "Guardar una sancion", description = "Guarda una sancion acorde a lo ingresado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operación exitosa."),
            @ApiResponse(responseCode = "400", description = "Error al ingresar parametros. Revise si ingreso todos los parametros solicitados."),
            @ApiResponse(responseCode = "403", description = "No tienes permiso para hacer el cambio.")
    })
    private ResponseEntity<SancionResponseDTO> guardar(@Valid @RequestBody SancionRequestDTO dto){
        return ResponseEntity.status(201).body(sancionService.guardar(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar sancion", description = "Actualiza una sancion acorde a una id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sancion actualizada",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Sancion.class))),
            @ApiResponse(responseCode = "404", description = "El id de la sancion no existe.")
    })
    public ResponseEntity<SancionResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SancionRequestDTO dto) {
        return sancionService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar sancion", description = "Elimina una sancion acorde a una id.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "¡Sancion eliminada con exito!"),
            @ApiResponse(responseCode = "404",description = "ERROR: ¡El id de la sancion ingresada no existe!")
    })
    public ResponseEntity<Map<String,String>> eliminar(@PathVariable Long id) {
        if (sancionService.obtenerPorId(id).isEmpty()){
            Map<String, String> borrado = new LinkedHashMap<>();
            borrado.put("¡ERROR! ", "¡La sancion con id "+id+" no fue encontrada!");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(borrado);
        }else {
            sancionService.eliminar(id);
            Map<String, String> borrado = new LinkedHashMap<>();
            borrado.put("¡EXITO! ", "¡La sancion fue eliminada con exito!");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(borrado);
        }
    }


}
