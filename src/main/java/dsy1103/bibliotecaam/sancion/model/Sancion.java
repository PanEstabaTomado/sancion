package dsy1103.bibliotecaam.sancion.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "sancion")
public class Sancion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSancion;

    @Column(nullable = false)
    private LocalDate fecIniSancion;

    @Column(nullable = false, precision = 8)
    private Integer montoMulta;

    @Column(nullable = false,length = 100)
    private String motivo;

    @Column(nullable = false)
    private Boolean pagado;

    /*
    * * AQUI VAN:
    * La relacion a id Prestamo
     */

    @Column(nullable = false)
    private Long idUsuario;

    @Column(nullable = false)
    private Long idLibro;
}
