package dsy1103.bibliotecaam.sancion.controller;

import dsy1103.bibliotecaam.sancion.dto.SancionRequestDTO;
import dsy1103.bibliotecaam.sancion.dto.SancionResponseDTO;
import dsy1103.bibliotecaam.sancion.service.SancionService;
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
public class SancionController {

    private final SancionService sancionService;

    @GetMapping
    public ResponseEntity<List<SancionResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(sancionService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SancionResponseDTO> obtenerPorId(@PathVariable Long id) {
        return sancionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pagado")
    public ResponseEntity<List<SancionResponseDTO>> obtenerSancionPagada() {
        return ResponseEntity.ok(sancionService.obtenerSancionesPagadas());
    }

    @GetMapping("/nopagado")
    public ResponseEntity<List<SancionResponseDTO>> obtenerSancionNoPagada() {
            return ResponseEntity.ok(sancionService.obtenerSancionesNoPagadas());
        }

    @GetMapping("/porfecha")
    public ResponseEntity<List<SancionResponseDTO>> obtenerPorFecha(LocalDate fecha){
        return ResponseEntity.ok(sancionService.obtenerPorFecha(fecha));
    }

    @PostMapping
    private ResponseEntity<SancionResponseDTO> guardar(@Valid @RequestBody SancionRequestDTO dto){
        return ResponseEntity.status(201).body(sancionService.guardar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SancionResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SancionRequestDTO dto) {
        return sancionService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        sancionService.eliminar(id);
        Map<String, String> borrado = new LinkedHashMap<>();
        borrado.put("¡EXITO! ","¡La sancion fue eliminada con exito!");
        sancionService.eliminar(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(borrado);
    }


}
