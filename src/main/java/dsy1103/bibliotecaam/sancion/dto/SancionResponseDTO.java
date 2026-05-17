package dsy1103.bibliotecaam.sancion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SancionResponseDTO {
    private Long idSancion;

    private LocalDate fecIniSancion;

    private Integer montoMulta;

    private String motivo;

    private String pagado;

    private Long idUsuario;

    private Long idLibro;

}
