package dsy1103.bibliotecaam.sancion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SancionRequestDTO {
    @NotNull(message = "La fecha de vencimiento del prestamo no puede estar vacia.")
    private LocalDate fecIniSancion;

    @Positive(message = "El monto de la multa no puede ser negativo.")
    @NotNull(message = "El monto de la multa no puede estar vacio.")
    private Integer montoMulta;

    @NotBlank(message = "Debe haber una descripcion que detalle las razones de la sancion.")
    private String motivo;

    @NotNull(message = "Se debe saber si la sancion fue pagada o no.")
    private Boolean pagado;

    @NotNull(message = "El idPrestamo es obligatorio")
    private Long idPrestamo;

}
