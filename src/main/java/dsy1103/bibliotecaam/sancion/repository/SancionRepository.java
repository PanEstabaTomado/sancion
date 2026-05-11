package dsy1103.bibliotecaam.sancion.repository;

import dsy1103.bibliotecaam.sancion.model.Sancion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SancionRepository extends JpaRepository<Sancion, Long> {

    @Query("SELECT s FROM Sancion s WHERE s.fecIniSancion <= :fecha")
    List<Sancion> findSancionesPorFecha(@Param("fecha") LocalDate fecha);

    @Query("SELECT s FROM Sancion s WHERE s.pagado = false")
    List<Sancion> findSancionesNoPagadas();

    @Query("SELECT s FROM Sancion s WHERE s.pagado = true")
    List<Sancion> findSancionesPagadas();
}
